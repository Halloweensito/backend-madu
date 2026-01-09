package com.Osiris.backendMadu.DTO.Order;

import com.Osiris.backendMadu.Entity.OrderStatus;

public record OrderFilterRequest(
        OrderStatus status,
        String orderNumber,
        String customerName,
        String customerPhone,
        String startDate,
        String endDate
) {}