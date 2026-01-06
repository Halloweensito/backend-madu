package com.Osiris.backendMadu.Mapper;

import com.Osiris.backendMadu.DTO.*;
import com.Osiris.backendMadu.Entity.Attribute;
import com.Osiris.backendMadu.Entity.AttributeValue;
import org.mapstruct.*;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AttributeMapper {
    // ==========================================
    // 1. LECTURA (Entity -> DTO)
    // ==========================================

    @Mapping(source = "position", target = "position")
    @Mapping(source = "values", target = "values")
    AttributeResponse toDto(Attribute attribute);

    @Mapping(source = "attribute", target = "attribute")
    @Mapping(target = "hexColor", source = "hexColor")
    @Mapping(source = "position", target = "position")
    AttributeValueResponse toValueDto(AttributeValue value);

    // MapStruct implementa esto automáticamente (Enum -> String)
    AttributeInfo toInfo(Attribute attribute);


    // ==========================================
    // 2. ESCRITURA (DTO -> Entity)
    // ==========================================

    // --- Crear Atributo ---
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "slug", ignore = true)
    @Mapping(target = "values", ignore = true)
    @Mapping(target = "position", source = "position")
    // 🆕 Agregar esto
    Attribute toEntity(CreateAttributeRequest dto);

    // --- Actualizar Atributo ---
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "slug", ignore = true)
    @Mapping(target = "type", ignore = true)
    @Mapping(target = "values", ignore = true)
    void updateEntityFromDto(UpdateAttributeRequest dto, @MappingTarget Attribute attribute);

    // --- Crear Valor ---
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "slug", ignore = true)
    @Mapping(target = "attribute", ignore = true)
    @Mapping(target = "hexColor", source = "hexColor")
    @Mapping(target = "position", source = "position")
    // Se mapeará solo si no es null
    AttributeValue toValueEntity(CreateAttributeValueRequest dto);

    // --- 🆕 Actualizar Valor (La pieza que faltaba) ---
    // @BeanMapping con IGNORE: Si el campo en el DTO es null, NO toca la entidad.
    // Esto es vital para updates parciales (ej: solo cambiar el color, mantener el nombre).
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "slug", ignore = true)      // El slug no se suele cambiar en update simple
    @Mapping(target = "attribute", ignore = true)
    // No cambiamos el padre
    void updateValueFromDto(UpdateAttributeValueRequest dto, @MappingTarget AttributeValue value);
}
