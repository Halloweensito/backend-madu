package com.Osiris.backendMadu.DTO.Order;

public record OrderItemRequest(
        Long productId,
        Long variantId,
        Integer quantity
) {}
