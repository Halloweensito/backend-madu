package com.Osiris.backendMadu.DTO;

import com.Osiris.backendMadu.Entity.Status;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreateCategoryRequest {
    @NotBlank(message = "El nombre de la categoría es obligatorio")
    @Size(min = 1, max = 255, message = "El nombre debe tener entre 1 y 255 caracteres")
    private String name;

    private String description;
    private int sortOrder;

    @Size(max = 255, message = "El slug no puede exceder 255 caracteres")
    private String slug;

    private Status status;

    private Long parentId;

    @Size(max = 500, message = "La URL de la imagen no puede exceder 500 caracteres")
    private String imageUrl;
}
