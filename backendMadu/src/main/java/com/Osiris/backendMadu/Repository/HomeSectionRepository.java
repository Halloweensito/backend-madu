package com.Osiris.backendMadu.Repository;

import com.Osiris.backendMadu.Entity.HomeSection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HomeSectionRepository extends JpaRepository<HomeSection, Long> {

    // ==========================================
    // 🚀 PARA LA TIENDA (PÚBLICO)
    // ==========================================

    /**
     * Trae TODA la home activa de un solo golpe.
     * Usa LEFT JOIN FETCH para llenar los objetos hijos y evitar N+1 queries.
     * * DISTINCT: Necesario porque al hacer Join con una lista, Hibernate duplica el padre en memoria.
     */
    @Query("""
                SELECT DISTINCT s FROM HomeSection s
                LEFT JOIN FETCH s.items i
                LEFT JOIN FETCH i.product p
                LEFT JOIN FETCH i.category c
                WHERE s.active = true
                ORDER BY s.position ASC, i.position ASC
            """)
    List<HomeSection> findAllActiveWithDetails();

    // ==========================================
    // 🛠️ PARA EL ADMIN (PANEL)
    // ==========================================

    // Para la lista de gestión (no necesitamos traer todos los productos pesados aquí)
    List<HomeSection> findAllByOrderByPositionAsc();

}
