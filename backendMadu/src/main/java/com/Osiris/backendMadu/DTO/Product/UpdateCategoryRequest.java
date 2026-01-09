package com.Osiris.backendMadu.DTO.Product;

import com.Osiris.backendMadu.Entity.Status;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateCategoryRequest {


    @NotBlank(message = "El nombre es obligatorio")
    @Size(min = 3, max = 50)
    private String name;

    @Size(max = 2000)
    private String description;


    private Status status;

    private Integer sortOrder;

    private String imageUrl;

    private Long parentId;
}
