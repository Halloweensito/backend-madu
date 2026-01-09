package com.Osiris.backendMadu.DTO.Product;

import lombok.Data;

@Data
public class ReorderRequest {
    private Long id;
    private Integer position;
}