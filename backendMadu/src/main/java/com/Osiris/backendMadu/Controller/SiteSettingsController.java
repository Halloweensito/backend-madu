package com.Osiris.backendMadu.Controller;


import com.Osiris.backendMadu.DTO.SiteSettings.PublicSiteSettings;
import com.Osiris.backendMadu.DTO.SiteSettings.UpdateSiteSettingsRequest;
import com.Osiris.backendMadu.Entity.SiteSettings;
import com.Osiris.backendMadu.Service.SiteSettingsService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/site-settings")
@RequiredArgsConstructor
public class SiteSettingsController {

    private final SiteSettingsService siteSettingsService;

    // ===================== STORE (PÚBLICO) =====================

    /**
     * Settings públicos para header, footer, branding, SEO
     */
    @GetMapping("/store")
    public ResponseEntity<PublicSiteSettings> getPublicSettings() {
        return ResponseEntity.ok(siteSettingsService.getPublicSettings());
    }

    // ===================== ADMIN =====================

    /**
     * Settings completos para el panel admin
     */
    @GetMapping("/admin")
    public ResponseEntity<SiteSettings> getAdminSettings() {
        return ResponseEntity.ok(siteSettingsService.getAdminSettings());
    }

    /**
     * Actualización parcial de settings
     */
    @PatchMapping("/admin")
    public ResponseEntity<SiteSettings> updateSettings(
            @Valid @RequestBody UpdateSiteSettingsRequest request
    ) {
        return ResponseEntity.ok(siteSettingsService.updateSettings(request));
    }
}

