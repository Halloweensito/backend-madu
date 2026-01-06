package com.Osiris.backendMadu.Entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "product")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(unique = true, nullable = false)
    private String slug;

    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    @JsonIgnoreProperties({"products"})
    private Category category;


    @Enumerated(EnumType.STRING)
    private Status status;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("position ASC")
    private List<Image> images = new ArrayList<>();

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("id ASC")
    private List<ProductVariant> variants = new ArrayList<>();

    public BigDecimal getDisplayPrice() {
        if (variants == null || variants.isEmpty()) return BigDecimal.ZERO;

        return variants.stream()
                .map(ProductVariant::getPrice)
                .filter(Objects::nonNull)
                .min(Comparator.naturalOrder())
                .orElse(BigDecimal.ZERO);
    }

    public String getMainImageUrl() {
        if (images == null || images.isEmpty()) return null;
        // Lógica para devolver la imagen con position 0 o la primera
        return images.stream()
                .sorted(Comparator.comparing(Image::getPosition))
                .map(Image::getUrl)
                .findFirst()
                .orElse(null);
    }
}