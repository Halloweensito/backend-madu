package com.Osiris.backendMadu.DTO.SiteSettings;

import lombok.Data;

@Data
public class UpdateSiteSettingsRequest {

    // Branding
    private String siteName;
    private String siteDescription;
    private String logoUrl;
    private String logoMobileUrl;
    private String faviconUrl;

    // Theme
    private String primaryColor;
    private String secondaryColor;
    private String accentColor;

    // SEO
    private String metaTitle;
    private String metaDescription;

    // Contact & Social
    private String email;
    private String phone;
    private String instagramUrl;
    private String facebookUrl;
    private String whatsappUrl;
    private String tiktokUrl;

    // Footer
    private String footerText;
    private String developerName;
    private String developerUrl;

    // Behavior
    private Boolean maintenanceMode;
}
