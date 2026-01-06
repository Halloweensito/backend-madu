package com.Osiris.backendMadu.Mapper;


import com.Osiris.backendMadu.DTO.HomeSectionItemResponse;
import com.Osiris.backendMadu.DTO.HomeSectionRequest;
import com.Osiris.backendMadu.DTO.HomeSectionResponse;
import com.Osiris.backendMadu.Entity.HomeSection;
import com.Osiris.backendMadu.Entity.HomeSectionItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(
        componentModel = "spring",
        uses = {ProductMapper.class, CategoryMapper.class} // 🔥 CLAVE: Reutiliza lógica
)
public interface HomeSectionMapper {

    // Mapeo del Padre
    @Mapping(target = "items", source = "items")
    HomeSectionResponse toResponse(HomeSection section);

    // Mapeo de los Hijos
    // MapStruct detectará automáticamente que:
    // - Entity 'product' -> Dto 'product' (ProductSummaryDto) usando ProductMapper
    // - Entity 'category' -> Dto 'category' (CategorySummaryDto) usando CategoryMapper
    @Mapping(target = "product", source = "product")
    @Mapping(target = "category", source = "category")
    HomeSectionItemResponse toItemResponse(HomeSectionItem item);

    @Mapping(target = "id", ignore = true) // El ID no se toca
    @Mapping(target = "items", ignore = true) // ⚠️ CRÍTICO: La lista la manejamos en el Service
    @Mapping(source = "ctaLink", target = "linkUrl")
    void updateEntity(HomeSectionRequest request, @MappingTarget HomeSection entity);


    // ✅ CREACIÓN: Convierte Request -> Entity nueva
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "items", ignore = true)
    @Mapping(source = "ctaLink", target = "linkUrl")
    HomeSection toEntity(HomeSectionRequest request);
}