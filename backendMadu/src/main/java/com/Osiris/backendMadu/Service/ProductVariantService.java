package com.Osiris.backendMadu.Service;

import com.Osiris.backendMadu.DTO.Product.ProductVariantRequest;
import com.Osiris.backendMadu.Entity.AttributeValue;
import com.Osiris.backendMadu.Entity.Image;
import com.Osiris.backendMadu.Entity.Product;
import com.Osiris.backendMadu.Entity.ProductVariant;
import com.Osiris.backendMadu.Mapper.ProductVariantMapper;
import com.Osiris.backendMadu.Repository.AttributeValueRepository;
import com.Osiris.backendMadu.Repository.ProductVariantRepository;
import com.Osiris.backendMadu.Utils.AttributeValueValidator;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.Normalizer;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductVariantService {

    private final ProductVariantRepository variantRepository;
    private final AttributeValueValidator attributeValueValidator;
    private final AttributeValueRepository attributeValueRepository;
    private final ProductVariantMapper variantMapper;


    @Transactional
    public ProductVariant createVariant(Product product, ProductVariantRequest request) {
        if (request.getId() != null) {
            throw new IllegalArgumentException("Cannot create variant with existing id.");
        }

        ProductVariant variant = variantMapper.toEntity(request);
        variant.setProduct(product);

        // Atributos
        if (request.getAttributeValueIds() != null && !request.getAttributeValueIds().isEmpty()) {
            List<AttributeValue> values = attributeValueValidator.validateAndRetrieve(request.getAttributeValueIds());
            variant.setAttributeValues(new HashSet<>(values));
        }

        // SKU
        String skuToUse = (request.getSku() == null || request.getSku().trim().isEmpty())
                ? generateSku(variant)
                : request.getSku();

        if (request.getSku() != null) validateSkuUniqueness(null, request.getSku());
        variant.setSku(skuToUse);


        // =====================================================================
        // 1. LINKEO DE IMÁGENES POR ID (Imágenes que YA existen en la BD)
        // =====================================================================
        if (request.getSelectedImageIds() != null && !request.getSelectedImageIds().isEmpty()) {
            List<Image> matchingImages = product.getImages().stream()
                    .filter(img -> img.getId() != null &&
                            request.getSelectedImageIds().contains(img.getId()))
                    .toList();
            variant.getImages().addAll(matchingImages);
        }

        // =====================================================================
        // 2. 👇 NUEVO: LINKEO POR TEMP_ID (Imágenes NUEVAS en memoria)
        // =====================================================================
        if (request.getSelectedImageTempIds() != null && !request.getSelectedImageTempIds().isEmpty()) {

            // Buscamos en la lista de imágenes del "product" (que nos pasó el Controller)
            // aquellas que tengan el tempId que pide esta variante.
            List<Image> newMatchingImages = product.getImages().stream()
                    .filter(img -> img.getTempId() != null &&
                            request.getSelectedImageTempIds().contains(img.getTempId()))
                    .toList();

            variant.getImages().addAll(newMatchingImages);
        }

        return variantRepository.save(variant);
    }

    /**
     * ✅ Genera un SKU único basado en los AttributeValues de la variante
     */
    private String generateSku(ProductVariant variant) {
        if (variant.getAttributeValues() == null || variant.getAttributeValues().isEmpty()) {
            // Si no tiene AttributeValues, generar SKU genérico
            return generateGenericSku();
        }

        // Obtener los primeros 3 caracteres de cada AttributeValue
        List<String> skuParts = variant.getAttributeValues().stream()
                // Ajuste en ProductVariantService.java -> generateSku
                .sorted(Comparator.comparing(av -> av.getAttribute().getPosition() != null ? av.getAttribute().getPosition() : 0))
                .map(av -> {
                    String value = av.getValue() != null ? av.getValue() : av.getSlug();
                    if (value == null || value.trim().isEmpty()) {
                        return "VAL";
                    }
                    // Limpiar y tomar primeros 3 caracteres alfanuméricos
                    // ✅ Usar Normalizer de java.text para eliminar acentos
                    String normalized = Normalizer.normalize(value, Normalizer.Form.NFD);
                    String withoutAccents = normalized.replaceAll("\\p{InCombiningDiacriticalMarks}", ""); // Eliminar acentos
                    String onlyAlphanumeric = withoutAccents
                            .toUpperCase()
                            .replaceAll("[^A-Z0-9]", ""); // Solo letras y números

                    // ✅ Corregir substring: tomar primeros 3 caracteres o todos si son menos
                    if (onlyAlphanumeric.isEmpty()) {
                        return "VAL";
                    }

                    return onlyAlphanumeric.length() >= 3
                            ? onlyAlphanumeric.substring(0, 3)
                            : onlyAlphanumeric;
                })
                .collect(Collectors.toList());

        if (skuParts.isEmpty()) {
            return generateGenericSku();
        }

        // Generar parte base del SKU
        String baseSku = "SKU-" + String.join("-", skuParts);

        // Buscar un SKU único agregando un sufijo numérico si es necesario
        String uniqueSku = baseSku;
        int suffix = 1;
        while (variantRepository.findBySku(uniqueSku).isPresent()) {
            uniqueSku = baseSku + "-" + suffix;
            suffix++;
            // Prevenir loop infinito (aunque es muy improbable)
            if (suffix > 9999) {
                uniqueSku = baseSku + "-" + System.currentTimeMillis();
                break;
            }
        }

        return uniqueSku;
    }

    /**
     * Genera un SKU genérico cuando no hay AttributeValues
     */
    private String generateGenericSku() {
        String baseSku = "SKU-GEN";
        String uniqueSku = baseSku;
        int suffix = 1;
        while (variantRepository.findBySku(uniqueSku).isPresent()) {
            uniqueSku = baseSku + "-" + suffix;
            suffix++;
            if (suffix > 9999) {
                uniqueSku = baseSku + "-" + System.currentTimeMillis();
                break;
            }
        }
        return uniqueSku;
    }

    /**
     * ✅ Valida que el SKU sea único (globalmente)
     * Usado antes de INSERT o cuando se cambia el SKU en UPDATE
     *
     * @param variantId ID de la variante actual (null si es nueva variante)
     * @param sku       SKU a validar
     */
    public void validateSkuUniqueness(Long variantId, String sku) {
        if (sku == null || sku.trim().isEmpty()) {
            return; // SKU vacío se generará automáticamente
        }

        Optional<ProductVariant> existing = variantRepository.findBySku(sku);
        if (existing.isPresent()) {
            ProductVariant existingVariant = existing.get();

            // ✅ Si es la misma variante (UPDATE sin cambio de SKU), está bien
            if (existingVariant.getId().equals(variantId)) {
                return; // Es la misma variante, está bien
            }

            // ✅ Es otra variante diferente, error
            throw new IllegalArgumentException(
                    "SKU already exists: " + sku +
                            ". Cannot create duplicate SKU."
            );
        }
    }


    /**
     * ✅ Actualiza los AttributeValues de una variante existente
     */
    @Transactional
    public void updateVariantAttributeValues(ProductVariant variant, List<Long> attributeValueIds) {
        Set<AttributeValue> newAttributeValues = new HashSet<>();

        for (Long attrValueId : attributeValueIds) {
            AttributeValue attrValue = attributeValueRepository.findById(attrValueId)
                    .orElseThrow(() -> new EntityNotFoundException(
                            "AttributeValue con ID " + attrValueId + " no encontrado"));
            newAttributeValues.add(attrValue);
        }

        variant.setAttributeValues(newAttributeValues);
    }
}
