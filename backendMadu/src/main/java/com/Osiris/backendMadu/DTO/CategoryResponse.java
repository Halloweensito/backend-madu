package com.Osiris.backendMadu.DTO;

import com.Osiris.backendMadu.Entity.Status;
import lombok.Data;

@Data
public class CategoryResponse {
    private Long id;
    private String name;
    private String slug;
    private String description;
    private String imageUrl;
    private Status status;
    private int sortOrder;
    private Long parentId;
    private String parentName;
}
