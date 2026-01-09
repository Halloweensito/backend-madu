package com.Osiris.backendMadu.DTO.Product;

import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
public class UpdateStockRequest {
    @Min(value = 0, message = "El stock no puede ser negativo")
    private Integer newStock;
}
