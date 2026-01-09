package com.Osiris.backendMadu.Repository;

import com.Osiris.backendMadu.Entity.Order;
import com.Osiris.backendMadu.Entity.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long>, JpaSpecificationExecutor<Order> {

    // Buscar por número de pedido (útil para WhatsApp / soporte)
    Optional<Order> findByOrderNumber(String orderNumber);

    // Panel admin: listar pedidos por estado
    Page<Order> findByStatus(OrderStatus status, Pageable pageable);

    // (opcional) verificar unicidad del número de pedido
    boolean existsByOrderNumber(String orderNumber);
}
