package com.Osiris.backendMadu.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FooterSection {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Título de la columna
     * Ej: "Ayuda", "Información", "Legal"
     */
    @Column(nullable = false, length = 100)
    private String title;

    /**
     * Orden de la sección (columna)
     */
    @Column(nullable = false)
    private Integer position;

    /**
     * Visible / no visible
     */
    @Column(nullable = false)
    private Boolean active = true;
}
