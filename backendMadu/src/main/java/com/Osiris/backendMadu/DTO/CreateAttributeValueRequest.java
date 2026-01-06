package com.Osiris.backendMadu.DTO;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreateAttributeValueRequest {
    @NotBlank(message = "El valor es obligatorio")
    @Size(min = 1, max = 255, message = "El valor debe tener entre 1 y 255 caracteres")
    private String value;

    @Size(max = 255, message = "El slug no puede exceder 255 caracteres")
    private String slug;

    @Size(max = 7, message = "El color hexadecimal debe tener máximo 7 caracteres")
    @Pattern(regexp = "^#[0-9A-Fa-f]{6}$|null", message = "Formato hexadecimal inválido")
    private String hexColor;

    @Min(value = 0, message = "La posición no puede ser negativa")
    private Integer position;
}
