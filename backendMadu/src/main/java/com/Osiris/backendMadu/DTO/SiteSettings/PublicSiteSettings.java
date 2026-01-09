package com.Osiris.backendMadu.DTO.SiteSettings;

public record PublicSiteSettings(
        String siteName,
        String logoUrl,
        String logoMobileUrl,
        String faviconUrl,

        String primaryColor,
        String secondaryColor,
        String accentColor,

        String email,
        String phone,
        String instagramUrl,
        String facebookUrl,
        String whatsappUrl,
        String tiktokUrl,


        String footerText,
        Boolean maintenanceMode
) {}