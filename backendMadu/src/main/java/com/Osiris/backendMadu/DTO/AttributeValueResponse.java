package com.Osiris.backendMadu.DTO;

import lombok.Data;

@Data
public class AttributeValueResponse {
    private Long id;
    private String value;
    private String slug;
    private String hexColor;
    private Integer position;
    private AttributeInfo attribute;
}