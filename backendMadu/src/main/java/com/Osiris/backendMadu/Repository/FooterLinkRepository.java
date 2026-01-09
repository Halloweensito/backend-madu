package com.Osiris.backendMadu.Repository;

import com.Osiris.backendMadu.Entity.FooterLink;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FooterLinkRepository extends JpaRepository<FooterLink, Long> {

    /* ===== Público (Optimizado) ===== */
    // Trae links activos de secciones activas, con sus relaciones cargadas
    @Query("""
        SELECT fl
        FROM FooterLink fl
        JOIN FETCH fl.section s
        LEFT JOIN FETCH fl.page p
        WHERE fl.active = true
          AND s.active = true
        ORDER BY s.position ASC, fl.position ASC
    """)
    List<FooterLink> findPublicLinks();

    /* ===== Admin (Optimizado) ===== */
    @Query("""
        SELECT fl
        FROM FooterLink fl
        JOIN FETCH fl.section
        LEFT JOIN FETCH fl.page
        ORDER BY fl.section.position ASC, fl.position ASC
    """)
    List<FooterLink> findAllForAdmin();

    /* ===== Helpers para Reordenamiento ===== */

    List<FooterLink> findBySectionIdOrderByPositionAsc(Long sectionId);

    /* ===== CRÍTICO: Cálculo de Posición ===== */
    // Esta es la query que le falta a tu código para que el Service funcione bien.
    // Permite calcular la siguiente posición disponible sin usar .size()
    @Query("SELECT MAX(l.position) FROM FooterLink l WHERE l.section.id = :sectionId")
    Integer findMaxPositionBySectionId(@Param("sectionId") Long sectionId);
}
