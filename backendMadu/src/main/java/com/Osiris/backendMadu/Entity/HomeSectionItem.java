package com.Osiris.backendMadu.Entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "home_section_items")
@Data
public class HomeSectionItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer position;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "section_id")
    private HomeSection section;

    private String imageUrl;
    private String redirectUrl;
    private String title;


    // Referencias opcionales (Solo una estará llena)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;
}
