package com.Osiris.backendMadu.Service;

import com.Osiris.backendMadu.DTO.Product.ImageRequest;
import com.Osiris.backendMadu.DTO.Product.ProductRequest;
import com.Osiris.backendMadu.DTO.Product.ProductResponse;
import com.Osiris.backendMadu.DTO.Product.ProductVariantRequest;
import com.Osiris.backendMadu.Entity.*;
import com.Osiris.backendMadu.Mapper.ProductMapper;
import com.Osiris.backendMadu.Repository.CategoryRepository;
import com.Osiris.backendMadu.Repository.ProductRepository;
import com.Osiris.backendMadu.Utils.SlugGenerator;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ProductVariantService variantService;
    private final ProductMapper productMapper;
    private final SlugGenerator slugGenerator;

    // ===================== CRUD BÁSICO =====================

    public Page<ProductResponse> findAllAdmin(Pageable pageable) {
        return productRepository.findByStatusNot(Status.ARCHIVED, pageable)
                .map(productMapper::toDto);
    }

    public Page<ProductResponse> findAllActive(Pageable pageable) {
        return productRepository.findByStatus(Status.ACTIVE, pageable)
                .map(productMapper::toDto);
    }

    public ProductResponse findById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Product not found with id: " + id));
        return productMapper.toDto(product);
    }

    public ProductResponse findBySlug(String slug) {
        Product product = productRepository.findBySlugAndStatus(slug, Status.ACTIVE)
                .orElseThrow(() -> new EntityNotFoundException("Product not found with slug: " + slug));
        return productMapper.toDto(product);
    }


    public Page<ProductResponse> search(String query, Pageable pageable) {
        // 1. Declaramos una variable para guardar el RESULTADO de la base de datos (Entidades)
        Page<Product> products;

        // 2. Decidimos qué buscar
        if (query == null || query.isBlank()) {
            products = productRepository.findByStatus(Status.ACTIVE, pageable);
        } else {
            // Buscamos por nombre O descripción
            products = productRepository.search(query, Status.ACTIVE, pageable);
        }

        return products.map(productMapper::toDto);
    }

    public Page<ProductResponse> findByCategorySlug(String slug, Pageable pageable) {
        // 1. Buscamos la categoría principal
        Category rootCategory = categoryRepository.findBySlugAndStatus(slug, Status.ACTIVE)
                .orElseThrow(() -> new EntityNotFoundException("Category not found with slug: " + slug));

        // 2. Recolectamos el ID de esa categoría Y de todos sus hijos/nietos
        List<Long> allCategoryIds = getAllCategoryIdsRecursively(rootCategory);

        // 3. Pasamos el 'pageable' al repositorio y mapeamos el resultado sin usar stream() ni toList()
        return productRepository.findAllByCategoryIdInAndStatus(allCategoryIds, Status.ACTIVE, pageable)
                .map(productMapper::toDto);
    }

    private List<Long> getAllCategoryIdsRecursively(Category category) {
        List<Long> ids = new ArrayList<>();

        // Agregamos el ID actual (ej: ID de "Ropa")
        ids.add(category.getId());

        // Si tiene hijos, los recorremos y nos llamamos a nosotros mismos (Recursión)
        if (category.getSubCategories() != null && !category.getSubCategories().isEmpty()) {
            for (Category child : category.getSubCategories()) {
                ids.addAll(getAllCategoryIdsRecursively(child));
            }
        }

        return ids;
    }


    @Transactional
    public ProductResponse createProduct(ProductRequest request) {
        Category category = categoryRepository
                .findByIdAndStatus(request.getCategoryId(), Status.ACTIVE)
                .orElseThrow(() -> new IllegalStateException("Category is not active"));

        Product product = new Product();
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setSlug(generateUniqueSlug(request));
        product.setCategory(category);
        product.setStatus(Status.ACTIVE);

        if (request.getGeneralImages() != null) {
            for (ImageRequest imgReq : request.getGeneralImages()) {
                Image image = new Image();
                image.setUrl(imgReq.getUrl());
                image.setPosition(imgReq.getPosition());
                image.setTempId(imgReq.getTempId());
                image.setProduct(product);
                product.getImages().add(image);
            }
        }

        Product savedProduct = productRepository.save(product);


        if (request.getVariants() == null || request.getVariants().isEmpty()) {

            ProductVariantRequest defaultVariant = new ProductVariantRequest();
            defaultVariant.setPrice(request.getPrice());
            defaultVariant.setStock(request.getStock());
            defaultVariant.setAttributeValueIds(Collections.emptyList());

            if (savedProduct.getImages() != null && !savedProduct.getImages().isEmpty()) {

                // 1. Extraemos los IDs de las imágenes recién guardadas
                List<Long> allImageIds = savedProduct.getImages().stream()
                        .map(Image::getId)
                        .toList();

                // 2. Se los asignamos a la variante default
                // (Asegúrate de que tu DTO usa 'selectedImageIds' como acordamos)
                defaultVariant.setSelectedImageIds(allImageIds);
            }

            // Creamos la variante (el servicio vinculará las imágenes por ID)
            ProductVariant variant = variantService.createVariant(savedProduct, defaultVariant);
            savedProduct.getVariants().add(variant);
        } else {
            for (ProductVariantRequest variantReq : request.getVariants()) {
                ProductVariant variant = variantService.createVariant(savedProduct, variantReq);
                savedProduct.getVariants().add(variant);
            }
        }


        return productMapper.toDto(savedProduct);
    }

    @Transactional
    public ProductResponse updateProduct(Long productId, ProductRequest request) {

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new EntityNotFoundException("Product not found with id: " + productId));

        // 1. Actualizar campos básicos
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setStatus(request.getStatus());

        if (request.getCategoryId() != null) {
            Category newCategory = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new EntityNotFoundException("Category not found"));
            product.setCategory(newCategory);
        }
        if (request.getGeneralImages() != null) {
            syncMasterImages(product, request.getGeneralImages());

            productRepository.saveAndFlush(product);

            Map<String, String> urlToTempIdMap = request.getGeneralImages().stream()
                    .filter(img -> img.getTempId() != null)
                    .collect(Collectors.toMap(ImageRequest::getUrl, ImageRequest::getTempId, (existing, replacement) -> existing));

            for (Image img : product.getImages()) {
                if (urlToTempIdMap.containsKey(img.getUrl())) {
                    img.setTempId(urlToTempIdMap.get(img.getUrl()));
                }
            }
        }

        boolean isNoVariantsList = request.getVariants() == null || request.getVariants().isEmpty();

        boolean isExplicitDefaultVariant = request.getVariants() != null
                && request.getVariants().size() == 1
                && (request.getVariants().getFirst().getAttributeValueIds() == null || request.getVariants().getFirst().getAttributeValueIds().isEmpty());

        if (isNoVariantsList || isExplicitDefaultVariant) {
            // CASO A y B: Manejar como Producto Simple (Variante Default)
            handleDefaultVariantUpdate(product, request);
        } else {
            // CASO C: Manejar como Producto con Múltiples Variantes
            handleComplexVariantsUpdate(product, request);
        }


        return productMapper.toDto(productRepository.save(product));
    }

    private void syncMasterImages(Product product, List<ImageRequest> incomingImages) {
        // 1. Identificar qué imágenes se van a borrar (las que ya no vienen en el request)
        List<Image> imagesToDelete = product.getImages().stream()
                .filter(existingImg -> incomingImages.stream()
                        .noneMatch(req -> req.getUrl().equals(existingImg.getUrl())))
                .toList();

        // 2. 🔥 Limpiar referencias en variantes para las imágenes que se van a borrar
        if (!imagesToDelete.isEmpty() && product.getVariants() != null) {
            Set<Long> idsToDelete = imagesToDelete.stream().map(Image::getId).collect(Collectors.toSet());

            for (ProductVariant variant : product.getVariants()) {
                variant.getImages().removeIf(img -> idsToDelete.contains(img.getId()));
            }
        }

        // 3. Ahora es seguro eliminar de la lista maestra
        product.getImages().removeAll(imagesToDelete);

        // 4. ACTUALIZAR O INSERTAR (Lógica que ya tenías)
        for (ImageRequest imgReq : incomingImages) {
            Optional<Image> existingImg = product.getImages().stream()
                    .filter(img -> img.getUrl().equals(imgReq.getUrl()))
                    .findFirst();

            if (existingImg.isPresent()) {
                existingImg.get().setPosition(imgReq.getPosition());
                existingImg.get().setTempId(imgReq.getTempId());
            } else {
                Image newImage = new Image();
                newImage.setUrl(imgReq.getUrl());
                newImage.setPosition(imgReq.getPosition());
                newImage.setTempId(imgReq.getTempId());
                newImage.setProduct(product);
                product.getImages().add(newImage);
            }
        }
    }

    @Transactional
    public void deleteProduct(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() ->
                        new EntityNotFoundException("Product not found with id: " + productId)
                );
        product.setStatus(Status.ARCHIVED);
    }

    @Transactional
    public ProductResponse addGeneralImageToProduct(Long productId, String imageUrl, Integer position) {
        Product product = getProductOrThrow(productId);
        Image image = new Image();
        image.setUrl(imageUrl);
        image.setPosition(position);
        image.setProduct(product);
        product.getImages().add(image);
        return productMapper.toDto(productRepository.save(product));
    }


    @Transactional
    public ProductResponse removeGeneralImageFromProduct(Long productId, Long imageId) {
        Product product = getProductOrThrow(productId);

        Image imageToRemove = product.getImages().stream()
                .filter(img -> img.getId().equals(imageId))
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException("Image not found with id: " + imageId));

        if (product.getVariants() != null) {
            for (ProductVariant variant : product.getVariants()) {
                variant.getImages().removeIf(img -> img.getId().equals(imageId));
            }
        }

        product.getImages().remove(imageToRemove);

        return productMapper.toDto(productRepository.save(product));
    }

    @Transactional
    public ProductResponse reorderProductImages(Long productId, List<ImageRequest> images) {
        Product product = getProductOrThrow(productId);
        for (ImageRequest imgReq : images) {
            product.getImages().stream()
                    .filter(img -> img.getUrl().equals(imgReq.getUrl()))
                    .findFirst()
                    .ifPresent(img -> img.setPosition(imgReq.getPosition()));
        }
        return productMapper.toDto(productRepository.save(product));
    }

    // ===================== OPERACIONES DE VARIANTES =====================

    @Transactional
    public ProductResponse updateVariantStock(Long productId, Long variantId, int newStock) {
        if (newStock < 0) throw new IllegalArgumentException("Stock cannot be negative");
        Product product = getProductOrThrow(productId);
        getVariantOrThrow(product, variantId).setStock(newStock);
        return productMapper.toDto(productRepository.save(product));
    }

    @Transactional
    public ProductResponse updateVariantPrice(Long productId, Long variantId, BigDecimal newPrice) {
        if (newPrice.compareTo(BigDecimal.ZERO) < 0) throw new IllegalArgumentException("Price cannot be negative");
        Product product = getProductOrThrow(productId);
        getVariantOrThrow(product, variantId).setPrice(newPrice);
        return productMapper.toDto(productRepository.save(product));
    }

    @Transactional
    public ProductResponse addImageToVariant(Long productId, Long variantId, Long imageId) {
        Product product = getProductOrThrow(productId);
        ProductVariant variant = getVariantOrThrow(product, variantId);

        Image image = product.getImages().stream()
                .filter(img -> img.getId().equals(imageId))
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException("Image not found in product master list"));

        variant.getImages().add(image);
        return productMapper.toDto(productRepository.save(product));
    }

    @Transactional
    public ProductResponse removeImageFromVariant(Long productId, Long variantId, Long imageId) {
        Product product = getProductOrThrow(productId);
        ProductVariant variant = getVariantOrThrow(product, variantId);

        variant.getImages().removeIf(img -> img.getId().equals(imageId));

        return productMapper.toDto(productRepository.save(product));
    }

    @Transactional
    public void deleteVariant(Long productId, Long variantId) {
        Product product = getProductOrThrow(productId);
        ProductVariant variant = getVariantOrThrow(product, variantId);

        cleanupVariantResources(variant);
        product.getVariants().remove(variant);

        productRepository.save(product);
    }

    // ===================== MÉTODOS PRIVADOS =====================

    private String generateUniqueSlug(ProductRequest request) {
        String baseSlug = (request.getSlug() != null && !request.getSlug().isBlank())
                ? slugGenerator.generateSlug(request.getSlug())
                : slugGenerator.generateSlug(request.getName());
        String slug = baseSlug;
        int counter = 1;
        while (productRepository.existsBySlug(slug)) {
            slug = baseSlug + "-" + counter;
            counter++;
        }
        return slug;
    }

    private Product getProductOrThrow(Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new EntityNotFoundException("Product not found"));
    }

    private ProductVariant getVariantOrThrow(Product product, Long variantId) {
        return product.getVariants().stream()
                .filter(v -> v.getId().equals(variantId))
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException("Variant not found"));
    }

    private void cleanupVariantResources(ProductVariant variant) {
        variant.getAttributeValues().clear();
        variant.getImages().clear();
    }


    private void handleDefaultVariantUpdate(Product product, ProductRequest request) {
        // 1. Limpiar variantes complejas si existían
        if (product.getVariants().size() > 1) {
            product.getVariants().clear();
        }

        // 2. Obtener o Crear la Variante Default
        ProductVariant defaultVariant;
        if (product.getVariants().isEmpty()) {
            defaultVariant = new ProductVariant();
            defaultVariant.setProduct(product);
            product.getVariants().add(defaultVariant);
        } else {
            defaultVariant = product.getVariants().getFirst();
        }

        defaultVariant.setPrice(request.getPrice());
        defaultVariant.setStock(request.getStock());

        // 4. Limpieza
        defaultVariant.getAttributeValues().clear(); // Asegurar que no tenga atributos
        defaultVariant.getImages().clear(); // Productos simples usan las imágenes del padre visualmente
    }

    private void handleComplexVariantsUpdate(Product product, ProductRequest request) {
        // HE MOVIDO TODA LA LÓGICA QUE TENÍAS EN EL IF GIGANTE AQUÍ

        // A. Borrar variantes removidas
        Set<Long> incomingIds = request.getVariants().stream()
                .map(ProductVariantRequest::getId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        product.getVariants().removeIf(v -> {
            if (!incomingIds.contains(v.getId())) {
                cleanupVariantResources(v);
                return true;
            }
            return false;
        });

        // B. Procesar Variantes (Update o Create)
        for (ProductVariantRequest variantReq : request.getVariants()) {
            ProductVariant variant;

            if (variantReq.getId() != null && variantReq.getId() > 0) {
                // UPDATE EXISTENTE
                variant = product.getVariants().stream()
                        .filter(v -> v.getId().equals(variantReq.getId()))
                        .findFirst()
                        .orElseThrow(() -> new EntityNotFoundException("Variant not found"));

                if (variantReq.getSku() != null && !variant.getSku().equals(variantReq.getSku())) {
                    variantService.validateSkuUniqueness(variant.getId(), variantReq.getSku());
                    variant.setSku(variantReq.getSku());
                }
                variant.setPrice(variantReq.getPrice());
                variant.setStock(variantReq.getStock());
                variantService.updateVariantAttributeValues(variant, variantReq.getAttributeValueIds());

            } else {
                // CREAR NUEVA VARIANTE COMPLEJA
                variant = variantService.createVariant(product, variantReq);
                product.getVariants().add(variant);
            }

            // C. Sincronizar Imágenes de la variante
            if (variantReq.getSelectedImageIds() != null || variantReq.getSelectedImageTempIds() != null) {
                variant.getImages().clear();

                // Imágenes existentes por ID
                if (variantReq.getSelectedImageIds() != null && !variantReq.getSelectedImageIds().isEmpty()) {
                    List<Image> matchedImages = product.getImages().stream()
                            .filter(img -> img.getId() != null &&
                                    variantReq.getSelectedImageIds().contains(img.getId()))
                            .toList();
                    variant.getImages().addAll(matchedImages);
                }

                // Imágenes nuevas por TempID
                if (variantReq.getSelectedImageTempIds() != null && !variantReq.getSelectedImageTempIds().isEmpty()) {
                    List<Image> matchedImages = product.getImages().stream()
                            .filter(img -> img.getTempId() != null &&
                                    variantReq.getSelectedImageTempIds().contains(img.getTempId()))
                            .toList();
                    variant.getImages().addAll(matchedImages);
                }
            } else {
                variant.getImages().clear();
            }
        }
    }


}