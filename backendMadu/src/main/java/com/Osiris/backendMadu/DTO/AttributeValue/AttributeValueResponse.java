package com.Osiris.backendMadu.DTO.AttributeValue;

import com.Osiris.backendMadu.DTO.Attribute.AttributeInfo;
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