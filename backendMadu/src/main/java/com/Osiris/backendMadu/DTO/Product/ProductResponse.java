package com.Osiris.backendMadu.DTO.Product;

import com.Osiris.backendMadu.DTO.Category.CategoryResponse;
import com.Osiris.backendMadu.Entity.Status;
import lombok.Data;

import java.util.List;

@Data
public class ProductResponse {

    private Long id;
    private String name;
    private String slug;
    private String description;
    private CategoryResponse category;
    private Status status;
    private List<ProductVariantResponse> variants;
    private List<ImageResponse> images;
}