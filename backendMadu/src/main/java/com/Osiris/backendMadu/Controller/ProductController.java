package com.Osiris.backendMadu.Controller;

import com.Osiris.backendMadu.DTO.Product.*;
import com.Osiris.backendMadu.Service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    // ===================== PRODUCTOS =====================


    @GetMapping("/admin")
    public ResponseEntity<Page<ProductResponse>> getAllAdmin(
            @PageableDefault(size = 20) Pageable pageable
    ) {
        return ResponseEntity.ok(productService.findAllAdmin(pageable));
    }

    @GetMapping("/store")
    public ResponseEntity<Page<ProductResponse>> getAllActive(
            @PageableDefault(size = 20) Pageable pageable
    ) {
        return ResponseEntity.ok(productService.findAllActive(pageable));
    }



    @GetMapping("/category/{slug}")
    public ResponseEntity<Page<ProductResponse>> getByCategorySlug(
            @PathVariable String slug,
            @PageableDefault(size = 12) Pageable pageable
    ) {
        return ResponseEntity.ok(productService.findByCategorySlug(slug, pageable));
    }


    @GetMapping("/{slug}")
    public ResponseEntity<ProductResponse> getBySlug(@PathVariable String slug) {
        return ResponseEntity.ok(productService.findBySlug(slug));
    }

    @GetMapping("/search")
    public ResponseEntity<Page<ProductResponse>> searchProducts(
            @RequestParam(required = false) String q, // 'q' es el texto a buscar
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(productService.search(q, pageable));
    }

    // Para edición en admin (por ID)
    @GetMapping("/id/{id}")
    public ResponseEntity<ProductResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(productService.findById(id));
    }


    @PostMapping
    public ResponseEntity<ProductResponse> create(@Valid @RequestBody ProductRequest request) {
        ProductResponse createdProduct = productService.createProduct(request);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{slug}")
                .buildAndExpand(createdProduct.getSlug())
                .toUri();
        return ResponseEntity.created(location).body(createdProduct);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody ProductRequest request) {
        return ResponseEntity.ok(productService.updateProduct(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }

    // ===================== IMÁGENES GENERALES DEL PRODUCTO =====================

    /**
     * Agregar una imagen general al producto (variant_id = null)
     * Estas imágenes se aplican a todas las variantes por defecto
     */
    @PostMapping("/{productId}/images")
    public ResponseEntity<ProductResponse> addGeneralImage(
            @PathVariable Long productId,
            @Valid @RequestBody ImageRequest request) {
        return ResponseEntity.ok(
                productService.addGeneralImageToProduct(
                        productId,
                        request.getUrl(),
                        request.getPosition()
                )
        );
    }

    /**
     * Eliminar una imagen general del producto
     */
    @DeleteMapping("/{productId}/images/{imageId}")
    public ResponseEntity<ProductResponse> removeGeneralImage(
            @PathVariable Long productId,
            @PathVariable Long imageId) {
        return ResponseEntity.ok(
                productService.removeGeneralImageFromProduct(
                        productId,
                        imageId
                )
        );
    }

    /**
     * Reordenar imágenes generales del producto
     * Útil cuando se arrastran imágenes en el frontend
     */
    @PutMapping("/{productId}/images/reorder")
    public ResponseEntity<ProductResponse> reorderProductImages(
            @PathVariable Long productId,
            @Valid @RequestBody List<ImageRequest> images) {
        return ResponseEntity.ok(
                productService.reorderProductImages(
                        productId,
                        images
                )
        );
    }

    // ===================== VARIANTS (SUB-RESOURCE) =====================

    @PatchMapping("/{productId}/variants/{variantId}/stock")
    public ResponseEntity<ProductResponse> updateVariantStock(
            @PathVariable Long productId,
            @PathVariable Long variantId,
            @Valid @RequestBody UpdateStockRequest request) {
        return ResponseEntity.ok(
                productService.updateVariantStock(
                        productId,
                        variantId,
                        request.getNewStock()
                )
        );
    }

    @PatchMapping("/{productId}/variants/{variantId}/price")
    public ResponseEntity<ProductResponse> updateVariantPrice(
            @PathVariable Long productId,
            @PathVariable Long variantId,
            @Valid @RequestBody UpdatePriceRequest request) {
        return ResponseEntity.ok(
                productService.updateVariantPrice(
                        productId,
                        variantId,
                        request.getNewPrice()
                )
        );
    }

    // ===================== IMÁGENES ESPECÍFICAS DE VARIANTE =====================


    @PostMapping("/{productId}/variants/{variantId}/images/{imageId}")
    public ResponseEntity<ProductResponse> linkImageToVariant(
            @PathVariable Long productId,
            @PathVariable Long variantId,
            @PathVariable Long imageId) {

        return ResponseEntity.ok(
                productService.addImageToVariant(productId, variantId, imageId)
        );
    }

    /**
     * Eliminar una imagen específica de una variante
     */
    @DeleteMapping("/{productId}/variants/{variantId}/images/{imageId}")
    public ResponseEntity<ProductResponse> removeVariantImage(
            @PathVariable Long productId,
            @PathVariable Long variantId,
            @PathVariable Long imageId) {
        return ResponseEntity.ok(
                productService.removeImageFromVariant(
                        productId,
                        variantId,
                        imageId
                )
        );
    }


    @DeleteMapping("/{productId}/variants/{variantId}")
    public ResponseEntity<Void> deleteVariant(
            @PathVariable Long productId,
            @PathVariable Long variantId) {
        productService.deleteVariant(productId, variantId);
        return ResponseEntity.noContent().build();
    }
}