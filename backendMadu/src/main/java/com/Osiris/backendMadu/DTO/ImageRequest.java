package com.Osiris.backendMadu.DTO;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ImageRequest {

    private String tempId;   // 👈 CLAVE
    @NotBlank(message = "La URL de la imagen es obligatoria")
    private String url;

    @Min(value = 0, message = "La posición debe ser mayor o igual a 0")
    private Integer position;
}
