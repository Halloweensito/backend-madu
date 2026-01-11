package com.Osiris.backendMadu.Controller;

import com.Osiris.backendMadu.DTO.Order.OrderFilterRequest;
import com.Osiris.backendMadu.DTO.Order.OrderRequest;
import com.Osiris.backendMadu.DTO.Order.OrderResponse;
import com.Osiris.backendMadu.DTO.Order.OrderSummaryResponse;
import com.Osiris.backendMadu.Entity.OrderStatus;
import com.Osiris.backendMadu.Service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class OrderController {

    private final OrderService orderService;

    // ===================== PUBLIC =====================
    @PostMapping("/public/orders")
    public ResponseEntity<OrderResponse> createOrder(
            @Valid @RequestBody OrderRequest request
    ) {
        // El servicio ya devuelve el DTO listo, el controlador solo lo pasa.
        // Código mucho más limpio.
        return ResponseEntity.ok(orderService.createOrder(request));
    }

// ===================== ADMIN =====================

    @GetMapping("/admin/orders/{id}")
    public ResponseEntity<OrderResponse> getOrderById(@PathVariable Long id) {
        // Igual aquí: el servicio se encargó de buscar y mapear.
        return ResponseEntity.ok(orderService.findById(id));
    }

    @PatchMapping("/admin/orders/{id}/status")
    public ResponseEntity<Void> updateOrderStatus(
            @PathVariable Long id,
            @RequestParam OrderStatus status
    ) {
        orderService.updateStatus(id, status);
        return ResponseEntity.noContent().build();
    }


    @GetMapping("/admin/orders")
    public ResponseEntity<Page<OrderSummaryResponse>> getOrders(
            @RequestParam(required = false) OrderStatus status,
            @RequestParam(required = false) String orderNumber,
            @RequestParam(required = false) String customerName,
            @RequestParam(required = false) String customerPhone,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        OrderFilterRequest filters = new OrderFilterRequest(
                status, orderNumber, customerName, customerPhone, startDate, endDate
        );
        return ResponseEntity.ok(orderService.findOrdersFiltered(filters, pageable));
    }

}