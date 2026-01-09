package com.Osiris.backendMadu.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "order_items")
@Getter
@Setter
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    // Referencias (opcionales, para trazabilidad)
    private Long productId;
    private Long variantId;

    // Snapshot
    @Column(nullable = false)
    private String productName;

    private String sku;

    @Column(nullable = false)
    private BigDecimal price;

    @Column(nullable = false)
    private Integer quantity;

    @Column(length = 500)
    private String attributes;
    // Ej: "Color: Negro, Talle: M"
}
