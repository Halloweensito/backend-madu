package com.Osiris.backendMadu.Mapper;

import com.Osiris.backendMadu.DTO.AttributeValueResponse;
import com.Osiris.backendMadu.DTO.ImageResponse;
import com.Osiris.backendMadu.DTO.ProductVariantRequest;
import com.Osiris.backendMadu.DTO.ProductVariantResponse;
import com.Osiris.backendMadu.Entity.AttributeValue;
import com.Osiris.backendMadu.Entity.Image;
import com.Osiris.backendMadu.Entity.ProductVariant;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", uses = {
        CategoryMapper.class,
        AttributeMapper.class,
        ImageMapper.class
})
public abstract class ProductVariantMapper {

    // ✅ INYECCIÓN: Nos permite usar el mapper real dentro de los helpers
    @Autowired
    protected AttributeMapper attributeMapper;

    // --- MAPPINGS ---

    @Mapping(source = "attributeValues", target = "attributeValues", qualifiedByName = "mapAttributeValues")
    @Mapping(source = "effectiveImages", target = "images", qualifiedByName = "sortImages")
    public abstract ProductVariantResponse toDto(ProductVariant entity);

    // --- DTO a Entity ---
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "product", ignore = true)
    @Mapping(target = "images", ignore = true)
    @Mapping(target = "attributeValues", ignore = true)
    @Mapping(target = "sku", ignore = true)
    // ✅ CORRECCIÓN: Ignoramos effectiveImages para quitar el warning
    @Mapping(target = "effectiveImages", ignore = true)
    public abstract ProductVariant toEntity(ProductVariantRequest dto);


    // --- HELPERS ---

    @Named("mapAttributeValues")
    protected List<AttributeValueResponse> mapAttributeValues(Set<AttributeValue> values) {
        if (values == null) return List.of();

        return values.stream()
                .sorted(Comparator.comparingInt(av ->
                        av.getAttribute().getPosition() != null ? av.getAttribute().getPosition() : 0))
                .map(this::toAttributeValueDto) // Llama al helper de abajo
                .collect(Collectors.toList());
    }

    // Helper optimizado usando el Mapper inyectado
    protected AttributeValueResponse toAttributeValueDto(AttributeValue av) {
        // 1. Usamos el mapper real (nada de 'new Object()')
        AttributeValueResponse response = attributeMapper.toValueDto(av);

        // 2. Aplicamos lógica de negocio extra sobre el DTO ya creado
        if (response.getValue() == null || response.getValue().isEmpty()) {
            response.setValue(av.getSlug() != null ? av.getSlug() : "Valor " + av.getId());
        }
        return response;
    }

    @Named("sortImages")
    protected List<ImageResponse> sortImages(List<Image> images) {
        if (images == null) return List.of();

        return images.stream()
                .sorted(Comparator.comparingInt(img -> img.getPosition() != null ? img.getPosition() : 0))
                .map(this::toImageResponse) // MapStruct implementará esto usando ImageMapper
                .collect(Collectors.toList());
    }

    public abstract ImageResponse toImageResponse(Image image);
}