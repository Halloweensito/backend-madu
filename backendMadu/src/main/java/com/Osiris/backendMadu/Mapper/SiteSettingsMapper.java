package com.Osiris.backendMadu.Mapper;

import com.Osiris.backendMadu.DTO.SiteSettings.PublicSiteSettings;
import com.Osiris.backendMadu.DTO.SiteSettings.UpdateSiteSettingsRequest;
import com.Osiris.backendMadu.Entity.SiteSettings;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface SiteSettingsMapper {

    // ===== PUBLIC SETTINGS =====
    @Mapping(target = "siteName", source = "siteName")
    @Mapping(target = "logoUrl", source = "logoUrl")
    @Mapping(target = "logoMobileUrl", source = "logoMobileUrl")
    @Mapping(target = "faviconUrl", source = "faviconUrl")

    @Mapping(target = "primaryColor", source = "primaryColor")
    @Mapping(target = "secondaryColor", source = "secondaryColor")
    @Mapping(target = "accentColor", source = "accentColor")

    @Mapping(target = "instagramUrl", source = "instagramUrl")
    @Mapping(target = "facebookUrl", source = "facebookUrl")
    @Mapping(target = "whatsappUrl", source = "whatsappUrl")

    @Mapping(target = "footerText", source = "footerText")
    @Mapping(target = "maintenanceMode", source = "maintenanceMode")
    PublicSiteSettings toPublicDto(SiteSettings entity);

    // ===== UPDATE SETTINGS =====
    @Mapping(target = "id", ignore = true)
    void updateEntity(UpdateSiteSettingsRequest request,
                      @MappingTarget SiteSettings entity);
}