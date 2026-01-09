package com.Osiris.backendMadu.Controller;

import com.Osiris.backendMadu.DTO.PageContent.PageContentData;
import com.Osiris.backendMadu.DTO.PageContent.PageContentRecord;
import com.Osiris.backendMadu.DTO.PageContent.PublicPageContent;
import com.Osiris.backendMadu.Service.PageContentService;
import jakarta.validation.Valid; // Importante si vuelves a poner validaciones en el DTO
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api") // Prefijo base global
@RequiredArgsConstructor
public class PageContentController {

    private final PageContentService service;

    /* =======================================================
       ZONA PÚBLICA (Acceso libre)
       Rutas: /api/public/pages/...
       ======================================================= */

    @GetMapping("/public/pages")
    public ResponseEntity<List<PublicPageContent>> getAllPublicPages() {
        // Devuelve solo title, content, meta (lo que ve el usuario final)
        List<PublicPageContent> pages = service.getAllPublicPages();
        return ResponseEntity.ok(pages);
    }

    @GetMapping("/public/pages/{slug}")
    public ResponseEntity<PublicPageContent> getPageBySlug(@PathVariable String slug) {
        // Busca por URL amigable. Si no está publicada, dará error (404)
        PublicPageContent page = service.getPublicPageBySlug(slug);
        return ResponseEntity.ok(page);
    }

    /* =======================================================
       ZONA ADMIN (Requiere Autenticación - Futuro)
       Rutas: /api/admin/pages/...
       ======================================================= */

    @GetMapping("/admin/pages")
    public ResponseEntity<List<PageContentRecord>> getAllPagesAdmin() {
        return ResponseEntity.ok(service.getAllPagesForAdmin());
    }

    @GetMapping("/admin/pages/{id}")
    public ResponseEntity<PageContentRecord> getPageByIdAdmin(@PathVariable Long id) {
        return ResponseEntity.ok(service.getPageById(id));
    }

    @PostMapping("/admin/pages")
    public ResponseEntity<PageContentRecord> createPage(@RequestBody @Valid PageContentData data) {
        // Crea la página y devuelve el objeto creado con su nuevo ID y fechas
        PageContentRecord createdPage = service.createPage(data);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdPage);
    }

    @PutMapping("/admin/pages/{id}")
    public ResponseEntity<PageContentRecord> updatePage(
            @PathVariable Long id,
            @RequestBody @Valid PageContentData data) {

        PageContentRecord updatedPage = service.updatePage(id, data);
        return ResponseEntity.ok(updatedPage);
    }

    @DeleteMapping("/admin/pages/{id}")
    public ResponseEntity<Void> deletePage(@PathVariable Long id) {
        service.deletePage(id);
        // Retorna 204 No Content (estándar para borrados exitosos)
        return ResponseEntity.noContent().build();
    }
}