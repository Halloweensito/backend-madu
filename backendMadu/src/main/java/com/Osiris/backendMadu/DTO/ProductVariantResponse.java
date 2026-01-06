package com.Osiris.backendMadu.DTO;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class ProductVariantResponse {
    private Long id;
    private String sku;
    private BigDecimal price;
    private Integer stock;
    private List<ImageResponse> images;
    private List<AttributeValueResponse> attributeValues;
}