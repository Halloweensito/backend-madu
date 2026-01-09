package com.Osiris.backendMadu.Controller;

import com.Osiris.backendMadu.DTO.Section.FooterSectionAdmin;
import com.Osiris.backendMadu.DTO.Section.FooterSectionRequest;
import com.Osiris.backendMadu.Service.FooterSectionAdminService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/footer-sections")
@RequiredArgsConstructor
public class FooterSectionAdminController {

    private final FooterSectionAdminService sectionService;

    @GetMapping
    public ResponseEntity<List<FooterSectionAdmin>> findAll() {
        return ResponseEntity.ok(sectionService.findAll());
    }

    @PostMapping
    public ResponseEntity<FooterSectionAdmin> create(
            @RequestBody @Valid FooterSectionRequest dto // <--- Validación activada
    ) {
        FooterSectionAdmin created = sectionService.create(dto);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<FooterSectionAdmin> update(
            @PathVariable Long id,
            @RequestBody @Valid FooterSectionRequest dto // <--- Validación activada
    ) {
        return ResponseEntity.ok(sectionService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        sectionService.delete(id);
        return ResponseEntity.noContent().build();
    }

    /* ===== REORDER (Drag & Drop de Columnas) ===== */

    @PutMapping("/reorder")
    public ResponseEntity<Void> reorder(@RequestBody List<Long> orderedIds) {
        sectionService.reorder(orderedIds);
        return ResponseEntity.ok().build();
    }
}