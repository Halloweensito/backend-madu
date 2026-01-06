package com.Osiris.backendMadu.Mapper;

import com.Osiris.backendMadu.DTO.ProductRequest;
import com.Osiris.backendMadu.DTO.ProductResponse;
import com.Osiris.backendMadu.DTO.ProductSummaryDTO;
import com.Osiris.backendMadu.Entity.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring", uses = {ProductVariantMapper.class, CategoryMapper.class})
public interface ProductMapper {

    // ================== READ (Entity -> Summary DTO) ==================

    @Mapping(target = "price", source = "displayPrice")      // Usa lógica de la Entidad
    @Mapping(target = "mainImageUrl", source = "mainImageUrl") // Usa lógica de la Entidad

    // ✅ CORRECCIÓN 1: Silenciamos warnings o calculamos valores
    @Mapping(target = "promotionalPrice", ignore = true)     // Ignoramos si no tienes lógica de ofertas aún
    @Mapping(target = "stock", source = ".", qualifiedByName = "calculateTotalStock")
        // Calculamos el stock total
    ProductSummaryDTO toSummaryDto(Product product);


    // ================== READ (Entity -> Full DTO) ==================

    @Mapping(source = "category", target = "category")
    ProductResponse toDto(Product product);


    // ================== CREATE (Request -> Entity) ==================

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "slug", ignore = true)
    @Mapping(target = "variants", ignore = true)
    @Mapping(target = "images", ignore = true)
    @Mapping(target = "status", source = "status", defaultValue = "ACTIVE")
    Product toEntity(ProductRequest dto);


    // ================== HELPERS ==================

    // ✅ CORRECCIÓN 2: Lógica para sumar el stock de todas las variantes
    @Named("calculateTotalStock")
    default Integer calculateTotalStock(Product product) {
        if (product.getVariants() == null || product.getVariants().isEmpty()) {
            return 0;
        }
        return product.getVariants().stream()
                .mapToInt(v -> v.getStock() != null ? v.getStock() : 0)
                .sum();
    }
}