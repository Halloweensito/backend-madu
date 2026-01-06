package com.Osiris.backendMadu.Service;

import com.Osiris.backendMadu.DTO.*;
import com.Osiris.backendMadu.Entity.Category;
import com.Osiris.backendMadu.Entity.Status;
import com.Osiris.backendMadu.Mapper.CategoryMapper;
import com.Osiris.backendMadu.Repository.CategoryRepository;
import com.Osiris.backendMadu.Utils.SlugGenerator;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final SlugGenerator slugGenerator;
    private final CategoryMapper categoryMapper;

    // ===================== READ =====================


    public List<CategoryResponse> findAllAdmin() {
        return categoryRepository.findByStatusNot(Status.ARCHIVED)
                .stream()
                .map(categoryMapper::toResponse)
                .toList();
    }

    public List<CategoryResponse> findAllActive() {
        return categoryRepository.findAllByStatus(Status.ACTIVE)
                .stream()
                .map(categoryMapper::toResponse)
                .toList();
    }

    public CategoryResponse findById(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Category not found with id: " + id));
        return categoryMapper.toResponse(category);
    }

    public CategoryResponse findBySlug(String slug) {
        Category category = categoryRepository.findBySlugAndStatus(slug, Status.ACTIVE)
                .orElseThrow(() -> new EntityNotFoundException("Category not found with slug: " + slug));
        return categoryMapper.toResponse(category);
    }

    // ===================== CREATE =====================

    @Transactional
    public CategoryResponse createCategory(CreateCategoryRequest request) {

        // 1. Validar Slug (Si no viene, se genera. Si viene, se valida unicidad)
        String slugToUse = StringUtils.hasText(request.getSlug())
                ? request.getSlug()
                : slugGenerator.generateSlug(request.getName());

        if (categoryRepository.existsBySlug(slugToUse)) {
            throw new IllegalStateException("Slug already exists: " + slugToUse);
        }

        // 2. Convertir DTO a Entidad usando Mapper
        Category category = categoryMapper.toEntity(request);
        category.setSlug(slugToUse);

        // 3. Asignar Padre (Jerarquía)
        if (request.getParentId() != null) {
            Category parent = categoryRepository
                    .findByIdAndStatus(request.getParentId(), Status.ACTIVE)
                    .orElseThrow(() ->
                            new IllegalStateException("Parent category must be ACTIVE")
                    );
            category.setParent(parent);
        }

        // 4. Guardar
        return categoryMapper.toResponse(categoryRepository.save(category));
    }


    // ===================== UPDATE =====================


    @Transactional
    public CategoryResponse updateCategory(Long id, UpdateCategoryRequest request) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() ->
                        new EntityNotFoundException("Category not found with id: " + id)
                );

        categoryMapper.updateEntityFromDto(request, category);

        if (request.getName() != null && !request.getName().equals(category.getName())) {
            category.setSlug(slugGenerator.generateSlug(request.getName()));
        }
        if (request.getParentId() != null) {

            if (request.getParentId().equals(category.getId())) {
                throw new IllegalArgumentException("A category cannot be its own parent");
            }

            Category newParent = categoryRepository
                    .findByIdAndStatus(request.getParentId(), Status.ACTIVE)
                    .orElseThrow(() ->
                            new IllegalStateException("Parent category must be ACTIVE to be assigned")
                    );

            category.setParent(newParent);
        } else {
            category.setParent(null);
        }

        return categoryMapper.toResponse(categoryRepository.save(category));
    }


    public List<CategoryTreeDTO> getActiveCategoryTree() {
        List<Category> rootCategories = categoryRepository.findByParentIsNullAndStatus(Status.ACTIVE);
        return rootCategories.stream()
                .map(categoryMapper::toTreeDto)
                .toList();
    }

    public List<CategoryTreeAdminDTO> getAdminCategoryTree() {
        return categoryRepository
                .findByParentIsNullAndStatusNot(Status.ARCHIVED)
                .stream()
                .map(categoryMapper::toAdminTreeDto)
                .toList();
    }


    @Transactional
    public void archiveCategory(Long id) {
        log.info("Archiving category {}", id);

        Category category = categoryRepository.findById(id)
                .orElseThrow(() ->
                        new EntityNotFoundException("Category not found with id: " + id)
                );

        archiveRecursively(category);
    }

    private void archiveRecursively(Category category) {
        category.setStatus(Status.ARCHIVED);

        category.getSubCategories()
                .forEach(this::archiveRecursively);
    }

}