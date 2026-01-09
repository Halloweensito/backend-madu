package com.Osiris.backendMadu.DTO.Attribute;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateAttributeRequest {
    @Size(min = 1, max = 50, message = "El nombre debe tener entre 1 y 50 caracteres")
    private String name;

    private Integer position; //
}
