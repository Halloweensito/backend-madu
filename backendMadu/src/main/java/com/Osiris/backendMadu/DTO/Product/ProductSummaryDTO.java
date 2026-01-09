package com.Osiris.backendMadu.DTO.Product;


import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductSummaryDTO {
    private Long id;
    private String name;
    private String slug;
    private String mainImageUrl; // Solo la primera imagen o la portada
    private BigDecimal price;    // O un String "priceRange" si varía
    private BigDecimal promotionalPrice; // Si tienes ofertas
    private Integer stock;       // Para poner cartel "Sin Stock" si es 0
}