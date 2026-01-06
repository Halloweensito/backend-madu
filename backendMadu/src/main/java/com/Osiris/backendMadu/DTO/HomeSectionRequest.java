package com.Osiris.backendMadu.DTO;


import com.Osiris.backendMadu.Entity.SectionType;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class HomeSectionRequest {
    @NotNull
    private SectionType type;

    // Campos para BANNER, TEXT_BLOCK y Títulos de secciones
    private String title;
    private String subtitle;
    private String description; // Para TEXT_BLOCK
    private String imageUrl;    // Para BANNER
    private String ctaText;     // Para BANNER / TEXT_BLOCK
    private String ctaLink;     // Para BANNER / TEXT_BLOCK

    private Integer position;
    private boolean active;

    // Campos para HERO, CATEGORIES, FEATURED_PRODUCTS
    private List<HomeSectionItemRequest> items;
}