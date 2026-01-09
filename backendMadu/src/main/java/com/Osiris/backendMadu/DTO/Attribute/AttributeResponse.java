package com.Osiris.backendMadu.DTO.Attribute;

import com.Osiris.backendMadu.DTO.AttributeValue.AttributeValueResponse;
import com.Osiris.backendMadu.Entity.AttributeType;
import lombok.Data;

import java.util.List;

@Data
public class AttributeResponse {
    private Long id;
    private String name;
    private String slug;
    private AttributeType type;
    private Integer position;
    private List<AttributeValueResponse> values;
}