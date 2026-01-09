package com.Osiris.backendMadu.DTO.Category;

import lombok.Data;

import java.util.List;

@Data
public class CategoryTreeDTO {
    private Long id;
    private String name;
    private String slug;
    private String imageUrl;
    private List<CategoryTreeDTO> children;
}
