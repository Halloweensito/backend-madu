package com.Osiris.backendMadu.DTO.Order;

import com.Osiris.backendMadu.Entity.PaymentMethod;
import com.Osiris.backendMadu.Entity.ShippingMethod;

import java.util.List;

public record OrderRequest(
        List<OrderItemRequest> items,
        String customerName,
        String customerPhone,
        String customerNote,
        ShippingMethod shippingMethod,
        String shippingAddress,
        String shippingNote,
        PaymentMethod paymentMethod
        ) {}
