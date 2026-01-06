package com.Osiris.backendMadu.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "home_sections")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class HomeSection {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private SectionType type;

    private String title;
    private String subtitle;
    private Integer position;
    private boolean active;

    private String imageUrl;
    private String linkUrl;

    // RELACIÓN SEGURA
    @OneToMany(mappedBy = "section", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("position ASC") // Hibernate ordena la lista automáticamente
    private List<HomeSectionItem> items = new ArrayList<>();

    // Helper para agregar items fácilmente
    public void addItem(HomeSectionItem item) {
        items.add(item);
        item.setSection(this);
    }
}
