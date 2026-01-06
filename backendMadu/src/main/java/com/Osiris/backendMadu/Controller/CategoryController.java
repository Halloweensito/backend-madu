package com.Osiris.backendMadu.Controller;

import com.Osiris.backendMadu.DTO.*;
import com.Osiris.backendMadu.Service.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;


    @GetMapping("/store")
    public ResponseEntity<List<CategoryResponse>> getAllActive() {
        return ResponseEntity.ok(categoryService.findAllActive());
    }

    @GetMapping("/store/tree")
    public ResponseEntity<List<CategoryTreeDTO>> getActiveTree() {
        return ResponseEntity.ok(categoryService.getActiveCategoryTree());
    }

    // Endpoint helpful for Admin Panel or direct ID lookups
    @GetMapping("/{id}")
    public ResponseEntity<CategoryResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(categoryService.findById(id));
    }

    @GetMapping("/slug/{slug}") // Changed to /slug/{slug} to avoid conflict with /{id}
    public ResponseEntity<CategoryResponse> getBySlug(@PathVariable String slug) {
        return ResponseEntity.ok(categoryService.findBySlug(slug));
    }

    // =====================(Admin) =====================

    @GetMapping("/admin")
    public ResponseEntity<List<CategoryResponse>> getAllAdmin() {
        return ResponseEntity.ok(categoryService.findAllAdmin());
    }

    @GetMapping("/admin/tree")
    public ResponseEntity<List<CategoryTreeAdminDTO>> getAdminTree() {
        return ResponseEntity.ok(categoryService.getAdminCategoryTree());
    }

    @PostMapping
    public ResponseEntity<CategoryResponse> create(@Valid @RequestBody CreateCategoryRequest dto) {
        CategoryResponse createdCategory = categoryService.createCategory(dto);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(createdCategory.getId())
                .toUri();

        return ResponseEntity.created(location).body(createdCategory);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CategoryResponse> update(@PathVariable Long id, @Valid @RequestBody UpdateCategoryRequest dto) {
        return ResponseEntity.ok(categoryService.updateCategory(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        categoryService.archiveCategory(id);
        return ResponseEntity.noContent().build();
    }
}