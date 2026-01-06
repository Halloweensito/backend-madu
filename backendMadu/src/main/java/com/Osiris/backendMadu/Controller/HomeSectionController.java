package com.Osiris.backendMadu.Controller;

import com.Osiris.backendMadu.DTO.HomeSectionRequest;
import com.Osiris.backendMadu.DTO.HomeSectionResponse;
import com.Osiris.backendMadu.DTO.ReorderRequest;
import com.Osiris.backendMadu.Service.HomeSectionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/home")
@RequiredArgsConstructor
public class HomeSectionController {

    private final HomeSectionService homeSectionService;

    // ==========================================
    // 🌍 PUBLIC (Tienda)
    // ==========================================

    @GetMapping("/store")
    public ResponseEntity<List<HomeSectionResponse>> getStoreFront() {
        return ResponseEntity.ok(homeSectionService.getStoreFrontSections());
    }

    // ==========================================
    // 👮 ADMIN (Gestión)
    // ==========================================

    @GetMapping("/admin")
    public ResponseEntity<List<HomeSectionResponse>> getAllForAdmin() {
        return ResponseEntity.ok(homeSectionService.getAllSectionsForAdmin());
    }

    @GetMapping("/{id}")
    public ResponseEntity<HomeSectionResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(homeSectionService.getSectionById(id));
    }

    @PostMapping
    public ResponseEntity<HomeSectionResponse> create(@Valid @RequestBody HomeSectionRequest request) {
        HomeSectionResponse response = homeSectionService.createSection(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<HomeSectionResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody HomeSectionRequest request) {
        return ResponseEntity.ok(homeSectionService.updateSection(id, request));
    }

    @PutMapping("/reorder")
    public ResponseEntity<Void> reorderSections(@RequestBody List<ReorderRequest> reorderList) {
        homeSectionService.reorderSections(reorderList);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> archive(@PathVariable Long id) {
        homeSectionService.archiveSection(id);
        return ResponseEntity.noContent().build();
    }
}