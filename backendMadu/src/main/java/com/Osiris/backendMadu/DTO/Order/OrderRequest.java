package com.Osiris.backendMadu.DTO.Order;

import java.util.List;

public record OrderRequest(
        List<OrderItemRequest> items,
        String customerName,
        String customerPhone,
        String customerNote
) {}
