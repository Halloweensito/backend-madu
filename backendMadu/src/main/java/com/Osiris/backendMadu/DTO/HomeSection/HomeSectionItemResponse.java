package com.Osiris.backendMadu.DTO.HomeSection;


import com.Osiris.backendMadu.DTO.Category.CategorySummaryDTO;
import com.Osiris.backendMadu.DTO.Product.ProductSummaryDTO;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL) // No envía nulos al JSON
public class HomeSectionItemResponse {
    private Long id;
    private Integer position;
    private String imageUrl;
    private String redirectUrl;
    private String title;
    private ProductSummaryDTO product;
    private CategorySummaryDTO category;
}