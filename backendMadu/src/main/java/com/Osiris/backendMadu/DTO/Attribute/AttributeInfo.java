package com.Osiris.backendMadu.DTO.Attribute;

import lombok.Data;

@Data
public class AttributeInfo {
    private Long id;
    private String name;
    private String slug;
    private String type;
    private Integer position;
}
