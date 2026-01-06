package com.Osiris.backendMadu.DTO;

import com.Osiris.backendMadu.Entity.Status;
import lombok.Data;

import java.util.List;

@Data
public class CategoryTreeAdminDTO {
    private Long id;
    private String name;
    private String slug;
    private String imageUrl;
    private Status status;
    private List<CategoryTreeAdminDTO> children;
}
