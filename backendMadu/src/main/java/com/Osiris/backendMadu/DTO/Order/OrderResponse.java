package com.Osiris.backendMadu.DTO.Order;

import com.Osiris.backendMadu.Entity.OrderStatus;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record OrderResponse(
        Long id,
        String orderNumber,
        OrderStatus status,
        String customerName,
        String customerPhone,
        String customerNote,
        BigDecimal subtotal,
        BigDecimal total,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        List<OrderItemResponse> items // <--- La lista completa
) {}