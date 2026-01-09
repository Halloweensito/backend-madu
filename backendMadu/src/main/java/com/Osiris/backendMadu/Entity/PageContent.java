package com.Osiris.backendMadu.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageContent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * URL-friendly identifier
     * Ej: "contacto", "acerca-de", "terminos"
     */
    @Column(nullable = false, length = 100)
    private String slug;

    /**
     * Título visible de la página
     */
    @Column(nullable = false, length = 150)
    private String title;

    /**
     * Contenido principal
     * HTML o Markdown renderizado
     */
    @Lob
    @Column(nullable = false)
    private String content;

    /**
     * Control de publicación
     */
    @Column(nullable = false)
    private boolean published = false;

    /**
     * SEO básico (opcional pero recomendado)
     */
    @Column(length = 150)
    private String metaTitle;

    @Column(length = 255)
    private String metaDescription;

    /**
     * Pensado para evolución futura
     * SIMPLE hoy, SECTIONS mañana
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PageLayout layout = PageLayout.SIMPLE;

    /**
     * Auditoría
     */
    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
