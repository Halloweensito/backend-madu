package com.Osiris.backendMadu.Entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FooterLink {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Texto visible del link
     */
    @Column(nullable = false, length = 100)
    private String label;

    /**
     * URL externa (solo si page == null)
     */
    @Column(length = 255)
    private String url;

    /**
     * Página interna (solo si url == null)
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "page_id")
    private PageContent page;

    /**
     * Sección a la que pertenece
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "section_id")
    private FooterSection section;


    @Column(nullable = false)
    private Integer position;

    /**
     * Visible / no visible
     */
    @Column(nullable = false)
    private Boolean active = true;
}
