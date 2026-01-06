package com.Osiris.backendMadu.DTO;


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