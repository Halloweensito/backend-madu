package com.Osiris.backendMadu.Entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "site_settings")
public class SiteSettings {

    @Id
    private Long id = 1L;

    // Branding
    private String siteName;

    @Column(length = 500)
    private String siteDescription;      // 👈 Para el footer "Lencería de diseño..."
    private String logoUrl;
    private String logoMobileUrl;
    private String faviconUrl;

    // Theme
    private String primaryColor;
    private String secondaryColor;
    private String accentColor;

    // SEO
    @Column(length = 255)
    private String metaTitle;
    @Column(length = 500)
    private String metaDescription;

    // Contact & Social
    private String email;                 // 👈 Email de contacto
    private String phone;                 // 👈 Teléfono (opcional)
    private String instagramUrl;
    private String facebookUrl;           // 👈 Falta Facebook
    private String whatsappUrl;
    private String tiktokUrl;             // 👈 TikTok (opcional)

    // Footer
    @Column(length = 500)
    private String footerText;            // Copyright o texto adicional
    private String developerName;         // 👈 "Osiris M. Corrales"
    private String developerUrl;          // 👈 GitHub URL

    // Behavior
    private Boolean maintenanceMode;
}
