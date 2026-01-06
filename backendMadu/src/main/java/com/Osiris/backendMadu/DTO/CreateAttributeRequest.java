package com.Osiris.backendMadu.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreateAttributeRequest {
    @NotBlank(message = "El nombre del atributo es obligatorio")
    @Size(min = 1, max = 255, message = "El nombre debe tener entre 1 y 255 caracteres")
    private String name;

    @Size(max = 255, message = "El slug no puede exceder 255 caracteres")
    private String slug;

    @NotBlank(message = "El tipo de atributo es obligatorio")
    @Pattern(regexp = "SELECT|TEXT", message = "El tipo debe ser SELECT o TEXT")
    private String type;

    private Integer position;
}
