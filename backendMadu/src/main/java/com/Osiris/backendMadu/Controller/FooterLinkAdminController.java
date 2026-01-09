package com.Osiris.backendMadu.Controller;

import com.Osiris.backendMadu.DTO.Section.FooterLinkAdmin;
import com.Osiris.backendMadu.DTO.Section.FooterLinkRequest;
import com.Osiris.backendMadu.Service.FooterLinkAdminService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/footer-links")
@RequiredArgsConstructor
public class FooterLinkAdminController {

    private final FooterLinkAdminService linkService;

    @GetMapping
    public ResponseEntity<List<FooterLinkAdmin>> findAll() {
        return ResponseEntity.ok(linkService.findAll());
    }

    @PostMapping
    public ResponseEntity<FooterLinkAdmin> create(
            @RequestBody @Valid FooterLinkRequest dto // <--- Validación activada
    ) {
        FooterLinkAdmin created = linkService.create(dto);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<FooterLinkAdmin> update(
            @PathVariable Long id,
            @RequestBody @Valid FooterLinkRequest dto // <--- Validación activada
    ) {
        return ResponseEntity.ok(linkService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        linkService.delete(id);
        return ResponseEntity.noContent().build();
    }

    /* ===== REORDER ===== */

    @PutMapping("/reorder/{sectionId}")
    public ResponseEntity<Void> reorder(
            @PathVariable Long sectionId,
            @RequestBody List<Long> orderedIds
    ) {
        linkService.reorder(sectionId, orderedIds);
        return ResponseEntity.ok().build();
    }
}