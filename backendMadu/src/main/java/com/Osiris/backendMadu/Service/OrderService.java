package com.Osiris.backendMadu.Service;

import com.Osiris.backendMadu.DTO.Order.OrderFilterRequest;
import com.Osiris.backendMadu.DTO.Order.OrderItemRequest;
import com.Osiris.backendMadu.DTO.Order.OrderRequest;
import com.Osiris.backendMadu.DTO.Order.OrderSummaryResponse; // <--- Importante
import com.Osiris.backendMadu.Entity.*;
import com.Osiris.backendMadu.Mapper.OrderMapper; // <--- Importante
import com.Osiris.backendMadu.Repository.OrderRepository;
import com.Osiris.backendMadu.Repository.ProductRepository;
import com.Osiris.backendMadu.Repository.ProductVariantRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.hibernate.Hibernate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final ProductVariantRepository variantRepository;
    private final OrderMapper orderMapper; // <--- 1. Inyectamos el Mapper

    // ================= CREACIÓN DE ORDEN =================
    public Order createOrder(OrderRequest request) {
        Order order = new Order();
        order.setOrderNumber(generateOrderNumber());
        order.setStatus(OrderStatus.SENT);
        order.setCustomerName(request.customerName());
        order.setCustomerPhone(request.customerPhone());
        order.setCustomerNote(request.customerNote());
        order.setCreatedAt(LocalDateTime.now());

        BigDecimal subtotal = BigDecimal.ZERO;

        for (OrderItemRequest itemReq : request.items()) {
            if (itemReq.quantity() <= 0) {
                throw new IllegalArgumentException("La cantidad debe ser mayor a 0");
            }

            Product product = productRepository.findById(itemReq.productId())
                    .orElseThrow(() -> new EntityNotFoundException("Producto no encontrado ID: " + itemReq.productId()));

            OrderItem item = new OrderItem();
            item.setOrder(order);
            item.setProductId(product.getId());
            item.setProductName(product.getName());
            item.setQuantity(itemReq.quantity());

            ProductVariant variant;

            if (itemReq.variantId() != null) {
                variant = variantRepository.findById(itemReq.variantId())
                        .orElseThrow(() -> new EntityNotFoundException("Variante no encontrada ID: " + itemReq.variantId()));

                if (!variant.getProduct().getId().equals(product.getId())) {
                    throw new IllegalArgumentException("La variante no pertenece al producto especificado");
                }
            } else {
                if (product.getVariants() == null || product.getVariants().isEmpty()) {
                    throw new EntityNotFoundException("El producto " + product.getName() + " no tiene variantes configuradas.");
                }
                variant = product.getVariants().getFirst();
            }

            // --- CONTROL DE STOCK ---
            if (variant.getStock() < itemReq.quantity()) {
                throw new IllegalArgumentException("Stock insuficiente para: " + product.getName());
            }
            variant.setStock(variant.getStock() - itemReq.quantity());
            variantRepository.save(variant);
            // ------------------------

            item.setVariantId(variant.getId());
            item.setSku(variant.getSku());
            item.setPrice(variant.getPrice());
            item.setAttributes(buildAttributesSnapshot(variant));

            BigDecimal itemTotal = item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
            subtotal = subtotal.add(itemTotal);

            order.getItems().add(item);
        }

        order.setSubtotal(subtotal);
        order.setTotal(subtotal);

        return orderRepository.save(order);
    }

    // ================= ADMIN: LISTAR (SOLUCIONADO) =================
    @Transactional(readOnly = true)
    // 2. Cambiamos el retorno a Page<OrderSummaryResponse> para evitar LazyInitializationException
    public Page<OrderSummaryResponse> findAll(Pageable pageable) {
        Page<Order> orders = orderRepository.findAll(pageable);
        // La conversión se hace AQUÍ, mientras la conexión a la BD sigue abierta
        return orders.map(orderMapper::toSummaryResponse);
    }

    // ================= ADMIN: ACTUALIZAR ESTADO =================
    public void updateStatus(Long id, OrderStatus newStatus) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Orden no encontrada"));

        if (order.getStatus() == newStatus) {
            return;
        }

        if (order.getStatus() == OrderStatus.CANCELLED) {
            throw new IllegalStateException("No se puede cambiar el estado de una orden cancelada.");
        }

        if (newStatus == OrderStatus.CANCELLED) {
            restoreStock(order);
        }

        order.setStatus(newStatus);
        orderRepository.save(order);
    }

    // ================= BUSCAR POR ID =================
    public Order findById(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Orden no encontrada ID: " + id));

        // 3. Truco para forzar la carga de los items antes de cerrar la transacción
        // Esto evita que falle cuando el Controller intente leer los items
        Hibernate.initialize(order.getItems());

        return order;
    }



    @Transactional(readOnly = true) // Mantiene la sesión de DB abierta
    public Page<OrderSummaryResponse> findOrdersFiltered(
            OrderFilterRequest filters,
            Pageable pageable
    ) {
        if (filters == null) {
            return orderRepository.findAll(pageable)
                    .map(orderMapper::toSummaryResponse); // <--- Mapeo crítico aquí
        }

        // 1. Lógica de Fechas
        LocalDateTime startDate = null;
        LocalDateTime endDate = null;

        try {
            if (filters.startDate() != null && !filters.startDate().isBlank()) {
                startDate = LocalDate.parse(filters.startDate()).atStartOfDay();
            }
            if (filters.endDate() != null && !filters.endDate().isBlank()) {
                endDate = LocalDate.parse(filters.endDate()).atTime(23, 59, 59);
            }
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Formato de fecha inválido, use YYYY-MM-DD");
        }

        final LocalDateTime finalStart = startDate;
        final LocalDateTime finalEnd = endDate;

        // 2. Ejecutar Query
        Page<Order> orders = orderRepository.findAll((root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (filters.status() != null) {
                predicates.add(cb.equal(root.get("status"), filters.status()));
            }

            if (filters.orderNumber() != null && !filters.orderNumber().isBlank()) {
                predicates.add(cb.like(cb.lower(root.get("orderNumber")), "%" + filters.orderNumber().toLowerCase() + "%"));
            }

            if (filters.customerName() != null && !filters.customerName().isBlank()) {
                predicates.add(cb.like(cb.lower(root.get("customerName")), "%" + filters.customerName().toLowerCase() + "%"));
            }

            if (filters.customerPhone() != null && !filters.customerPhone().isBlank()) {
                predicates.add(cb.like(root.get("customerPhone"), "%" + filters.customerPhone() + "%"));
            }

            if (finalStart != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("createdAt"), finalStart));
            }

            if (finalEnd != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("createdAt"), finalEnd));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        }, pageable);

        // 3. RETORNO CORRECTO: Convertimos a DTO aquí mismo
        // Al hacerlo aquí, Hibernate puede contar los items porque la transacción sigue viva.
        return orders.map(orderMapper::toSummaryResponse);
    }
    // ================= HELPERS =================

    private void restoreStock(Order order) {
        for (OrderItem item : order.getItems()) {
            variantRepository.findById(item.getVariantId()).ifPresent(variant -> {
                variant.setStock(variant.getStock() + item.getQuantity());
                variantRepository.save(variant);
            });
        }
    }

    private String generateOrderNumber() {
        long timestamp = System.currentTimeMillis();
        String randomPart = UUID.randomUUID().toString().substring(0, 4).toUpperCase();
        return "ORD-" + timestamp + "-" + randomPart;
    }

    private String buildAttributesSnapshot(ProductVariant variant) {
        if (variant.getAttributeValues() == null || variant.getAttributeValues().isEmpty()) {
            return null;
        }
        return variant.getAttributeValues().stream()
                .map(av -> av.getAttribute().getName() + ": " + av.getValue())
                .collect(Collectors.joining(", "));
    }


}