package com.Osiris.backendMadu.DTO.HomeSection;


import lombok.Data;

import java.util.List;

@Data
public class HomeSectionResponse {
    private Long id;
    private String type; // String del Enum
    private String title;
    private String subtitle;
    private Integer position;
    private boolean active;

    // Configuración visual del contenedor
    private String imageUrl;
    private String linkUrl;

    // Lista de items hijos
    private List<HomeSectionItemResponse> items;
}