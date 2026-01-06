package com.Osiris.backendMadu.DTO;

import lombok.Data;

@Data
public class CategorySummaryDTO {
    private Long id;
    private String name;
    private String slug;
    private String imageUrl;
}