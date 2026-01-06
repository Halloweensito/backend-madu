package com.Osiris.backendMadu.Mapper;

import com.Osiris.backendMadu.DTO.*;
import com.Osiris.backendMadu.Entity.Category;
import com.Osiris.backendMadu.Entity.Status;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public abstract class CategoryMapper {

    // ===================== READ (DTOs de Respuesta) =====================

    // 1. Respuesta Completa (Admin / Detalle)
    @Mapping(source = "parent.id", target = "parentId")
    @Mapping(source = "parent.name", target = "parentName")
    public abstract CategoryResponse toResponse(Category category);

    // 2. Respuesta Resumida (Home / Listados / Menús)
    // ✅ MapStruct mapea automáticamente: id, name, slug, imageUrl
    public abstract CategorySummaryDTO toSummaryDto(Category category);

    // 3. Árboles de Categorías
    @Mapping(target = "children", expression = "java(mapActiveChildren(category))")
    public abstract CategoryTreeDTO toTreeDto(Category category);

    @Mapping(target = "children", expression = "java(mapAllChildren(category))")
    public abstract CategoryTreeAdminDTO toAdminTreeDto(Category category);


    // ===================== WRITE (Entity Mapping) =====================

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "slug", ignore = true)
    @Mapping(target = "parent", ignore = true)
    @Mapping(target = "subCategories", ignore = true)
    @Mapping(target = "status", source = "status", defaultValue = "ACTIVE")
    public abstract Category toEntity(CreateCategoryRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "slug", ignore = true)
    @Mapping(target = "subCategories", ignore = true)
    @Mapping(target = "parent", ignore = true)
    public abstract void updateEntityFromDto(UpdateCategoryRequest request, @MappingTarget Category entity);


    // ===================== HELPERS (Java Logic) =====================

    protected List<CategoryTreeDTO> mapActiveChildren(Category category) {
        if (category.getSubCategories() == null) {
            return Collections.emptyList();
        }
        return category.getSubCategories().stream()
                .filter(c -> c.getStatus() == Status.ACTIVE)
                .map(this::toTreeDto) // Llamada recursiva
                .collect(Collectors.toList());
    }

    protected List<CategoryTreeAdminDTO> mapAllChildren(Category category) {
        if (category.getSubCategories() == null) {
            return Collections.emptyList();
        }
        return category.getSubCategories().stream()
                .map(this::toAdminTreeDto) // Llamada recursiva
                .collect(Collectors.toList());
    }
}