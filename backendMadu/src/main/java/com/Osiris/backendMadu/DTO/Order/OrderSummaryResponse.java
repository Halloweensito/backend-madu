package com.Osiris.backendMadu.DTO.Order;

import com.Osiris.backendMadu.Entity.OrderStatus;
import com.Osiris.backendMadu.Entity.PaymentMethod;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record OrderSummaryResponse(
        Long id,
        String orderNumber,
        OrderStatus status,
        BigDecimal total,
        String customerName,
        PaymentMethod paymentMethod,
        Integer itemsCount,
        LocalDateTime createdAt
) {}
