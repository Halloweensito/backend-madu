package com.Osiris.backendMadu.DTO.Order;

import com.Osiris.backendMadu.Entity.OrderStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record OrderSummaryResponse(
        Long id,
        String orderNumber,
        OrderStatus status,
        BigDecimal total,
        String customerName,
        Integer itemsCount, // <--- Este es el que causa el error de compilación
        LocalDateTime createdAt
) {}
