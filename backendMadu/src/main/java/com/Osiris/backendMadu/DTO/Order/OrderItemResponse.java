package com.Osiris.backendMadu.DTO.Order;

import java.math.BigDecimal;

public record OrderItemResponse(
        Long id,
        Long productId,     // Útil para hacer link al producto original
        Long variantId,
        String productName,
        String sku,
        String attributes,  // Ej: "Color: Negro, Talle: M"
        BigDecimal price,   // Precio unitario
        Integer quantity,
        BigDecimal total    // Calculado (price * quantity)
) {}