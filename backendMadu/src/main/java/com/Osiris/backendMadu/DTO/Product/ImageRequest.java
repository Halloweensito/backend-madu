package com.Osiris.backendMadu.DTO.Product;

import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
public class ImageRequest {

    private String tempId;   // 👈 CLAVE
    private String url;

    @Min(value = 0, message = "La posición debe ser mayor o igual a 0")
    private Integer position;
}
