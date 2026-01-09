package com.Osiris.backendMadu.DTO.AttributeValue;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateAttributeValueRequest {
    @Size(min = 1, max = 50, message = "El valor debe tener entre 1 y 50 caracteres")
    private String value;

    @Pattern(regexp = "^#([A-Fa-f0-9]{6}|[A-Fa-f0-9]{3})$", message = "Formato de color inválido (ej: #FF0000)")
    private String hexColor;

    private Integer position;
}
