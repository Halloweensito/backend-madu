package com.Osiris.backendMadu.Service;

import com.Osiris.backendMadu.DTO.HomeSectionItemRequest;
import com.Osiris.backendMadu.DTO.HomeSectionRequest;
import com.Osiris.backendMadu.DTO.HomeSectionResponse;
import com.Osiris.backendMadu.DTO.ReorderRequest;
import com.Osiris.backendMadu.Entity.Category;
import com.Osiris.backendMadu.Entity.HomeSection;
import com.Osiris.backendMadu.Entity.HomeSectionItem;
import com.Osiris.backendMadu.Entity.Product;
import com.Osiris.backendMadu.Mapper.HomeSectionMapper;
import com.Osiris.backendMadu.Repository.CategoryRepository;
import com.Osiris.backendMadu.Repository.HomeSectionRepository;
import com.Osiris.backendMadu.Repository.ProductRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HomeSectionService {

    private final HomeSectionRepository sectionRepository;
    private final ProductRepository productRepository;  // ✅ Necesario para buscar productos
    private final CategoryRepository categoryRepository; // ✅ Necesario para buscar categorías
    private final HomeSectionMapper mapper;


    // ===================== LECTURA =====================

    // @Cacheable(value = "home_sections")
    public List<HomeSectionResponse> getStoreFrontSections() {
        return sectionRepository.findAllActiveWithDetails().stream()
                .map(mapper::toResponse)
                .toList();
    }

    public List<HomeSectionResponse> getAllSectionsForAdmin() {
        return sectionRepository.findAllByOrderByPositionAsc().stream()
                .map(mapper::toResponse)
                .toList();
    }

    public HomeSectionResponse getSectionById(Long id) {
        HomeSection section = sectionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Section not found"));
        return mapper.toResponse(section);
    }

    // ===================== ESCRITURA (Admin) =====================


    @Transactional
    public HomeSectionResponse createSection(HomeSectionRequest request) {
        // (Title, Subtitle, Type, Position, ImageUrl, Active...)
        HomeSection section = mapper.toEntity(request);

        // Aquí es donde buscamos los Productos/Categorías en la DB
        if (request.getItems() != null && !request.getItems().isEmpty()) {
            processAndAddItems(section, request.getItems());
        }

        // 3. Guardar
        HomeSection savedSection = sectionRepository.save(section);
        return mapper.toResponse(savedSection);
    }


    @Transactional
    public HomeSectionResponse updateSection(Long id, HomeSectionRequest request) {
        HomeSection section = sectionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Section not found"));

        // 1. ✅ MAGIA: El Mapper actualiza título, subtítulo, active, imágenes, links...
        mapper.updateEntity(request, section);

        // 2. Manejo de la Lista (Items)
        // Lo hacemos aquí para asegurar que usamos la referencia correcta de Hibernate
        if (request.getItems() != null) {
            // A. Limpiar (Hibernate agendará los DELETEs por orphanRemoval)
            section.getItems().clear();

            // B. Reconstruir (Hibernate agendará los INSERTs)
            processAndAddItems(section, request.getItems());
        }

        return mapper.toResponse(sectionRepository.save(section));
    }

    // @CacheEvict(value = "home_sections", allEntries = true)
    @Transactional
    public void reorderSections(List<ReorderRequest> reorderList) {
        for (ReorderRequest item : reorderList) {
            sectionRepository.findById(item.getId()).ifPresent(section -> {
                section.setPosition(item.getPosition());
            });
        }
    }

    // @CacheEvict(value = "home_sections", allEntries = true)
    @Transactional
    public void archiveSection(Long id) {
        HomeSection section = sectionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Section not found"));

        // Soft Delete: Desactivamos para que no salga en el StoreFront
        section.setActive(false);
        sectionRepository.save(section);
    }

    // ===================== MÉTODOS PRIVADOS (Helpers) =====================


    private void processAndAddItems(HomeSection section, List<HomeSectionItemRequest> itemRequests) {

        // 1. Recolectar todos los IDs para hacer UNA sola consulta
        List<Long> productIds = itemRequests.stream()
                .map(HomeSectionItemRequest::getProductId)
                .filter(Objects::nonNull)
                .toList();

        List<Long> categoryIds = itemRequests.stream()
                .map(HomeSectionItemRequest::getCategoryId)
                .filter(Objects::nonNull)
                .toList();

        // Map<Long, Product> para acceso rápido
        Map<Long, Product> productsMap = new HashMap<>();
        if (!productIds.isEmpty()) {
            productsMap = productRepository.findAllById(productIds).stream()
                    .collect(Collectors.toMap(Product::getId, p -> p));
        }

        Map<Long, Category> categoriesMap = new HashMap<>();
        if (!categoryIds.isEmpty()) {
            categoriesMap = categoryRepository.findAllById(categoryIds).stream()
                    .collect(Collectors.toMap(Category::getId, c -> c));
        }

        // 3. Iterar y asignar desde memoria (Sin ir a la DB)
        for (HomeSectionItemRequest itemReq : itemRequests) {
            HomeSectionItem item = new HomeSectionItem();
            item.setPosition(itemReq.getPosition());

            // A. Manual
            item.setImageUrl(itemReq.getImageUrl());
            item.setRedirectUrl(itemReq.getRedirectUrl());
            item.setTitle(itemReq.getTitle());

            // B. Producto (Buscamos en el Mapa, no en la DB)
            if (itemReq.getProductId() != null) {
                Product product = productsMap.get(itemReq.getProductId());
                if (product == null) {
                    // Opción A: Lanzar error
                    throw new EntityNotFoundException("Product ID not found: " + itemReq.getProductId());
                    // Opción B: Ignorar item (continue;) -> Más seguro para que no falle toda la home
                }
                item.setProduct(product);
            }

            // C. Categoría
            if (itemReq.getCategoryId() != null) {
                Category category = categoriesMap.get(itemReq.getCategoryId());
                if (category == null) {
                    throw new EntityNotFoundException("Category ID not found: " + itemReq.getCategoryId());
                }
                item.setCategory(category);
            }

            section.addItem(item);
        }
    }
}