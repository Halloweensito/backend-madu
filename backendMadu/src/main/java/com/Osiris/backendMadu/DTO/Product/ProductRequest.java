package com.Osiris.backendMadu.DTO.Product;

import com.Osiris.backendMadu.Entity.Status;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class ProductRequest {

    @NotBlank(message = "El nombre del producto es obligatorio")
    @Size(min = 1, max = 255, message = "El nombre debe tener entre 1 y 255 caracteres")
    private String name;

    @Size(max = 255, message = "El slug no puede exceder 255 caracteres")
    private String slug;

    @Size(max = 2000, message = "La descripción no puede exceder 2000 caracteres")
    private String description;

    @NotNull(message = "La categoría es obligatoria")
    @Positive(message = "El ID de categoría debe ser positivo")
    private Long categoryId;

    private List<ImageRequest> generalImages;

    private List<ProductVariantRequest> variants;

    private Status status;
    @PositiveOrZero(message = "El stock no puede ser negativo")
    private Integer stock;

    @Positive(message = "El precio debe ser mayor a 0")
    private BigDecimal price;
}