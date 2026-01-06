package com.Osiris.backendMadu.DTO;


import lombok.Data;

@Data
public class HomeSectionItemRequest {
    private Long id;
    private Integer position;

    // Solo para HERO
    private String imageUrl;
    private String redirectUrl;
    private String title;
    private Long productId;   // Antes: ProductSummaryDTO
    private Long categoryId;  // Antes: CategorySummaryDTO
}