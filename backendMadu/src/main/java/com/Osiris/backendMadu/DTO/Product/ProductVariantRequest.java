package com.Osiris.backendMadu.DTO.Product;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class ProductVariantRequest {

    private Long id;

    @Size(max = 100, message = "El SKU debe tener entre 1 y 100 caracteres")
    private String sku;

    @NotNull(message = "El precio es obligatorio")
    @DecimalMin(value = "0.01", message = "El precio debe ser mayor a 0")
    private BigDecimal price;

    @NotNull(message = "El stock es obligatorio")
    @Min(value = 0, message = "El stock no puede ser negativo")
    private Integer stock;

    @NotEmpty(message = "Debe tener al menos un valor de atributo")
    private List<Long> attributeValueIds;


    private List<Long> selectedImageIds;

    private List<String> selectedImageTempIds;
}