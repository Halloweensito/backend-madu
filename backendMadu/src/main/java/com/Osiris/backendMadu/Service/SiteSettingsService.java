package com.Osiris.backendMadu.Service;

import com.Osiris.backendMadu.DTO.SiteSettings.PublicSiteSettings;
import com.Osiris.backendMadu.DTO.SiteSettings.UpdateSiteSettingsRequest;
import com.Osiris.backendMadu.Entity.SiteSettings;
import com.Osiris.backendMadu.Mapper.SiteSettingsMapper;
import com.Osiris.backendMadu.Repository.SiteSettingsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SiteSettingsService {

    private static final Long SETTINGS_ID = 1L;

    private final SiteSettingsRepository repository;
    private final SiteSettingsMapper mapper;

    // ===================== PUBLIC =====================

    /**
     * Settings públicos para storefront
     */
    @Cacheable("public_site_settings")
    public PublicSiteSettings getPublicSettings() {
        SiteSettings settings = getOrCreate();
        return mapper.toPublicDto(settings);
    }

    // ===================== ADMIN =====================

    /**
     * Settings completos para panel admin
     */
    public SiteSettings getAdminSettings() {
        return getOrCreate();
    }

    /**
     * Actualización parcial desde el panel admin
     */
    @Transactional
    @CacheEvict(value = "public_site_settings", allEntries = true)
    public SiteSettings updateSettings(UpdateSiteSettingsRequest request) {
        SiteSettings settings = getOrCreate();
        mapper.updateEntity(request, settings);
        return repository.save(settings);
    }

    // ===================== INTERNAL =====================

    /**
     * Garantiza que siempre exista una fila
     */
    private SiteSettings getOrCreate() {
        return repository.findById(SETTINGS_ID)
                .orElseGet(() -> repository.save(new SiteSettings()));
    }
}
