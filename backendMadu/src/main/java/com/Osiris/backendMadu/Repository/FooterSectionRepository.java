package com.Osiris.backendMadu.Repository;

import com.Osiris.backendMadu.Entity.FooterSection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FooterSectionRepository extends JpaRepository<FooterSection, Long> {

    /* ===== Público (Para el usuario final) ===== */
    // Trae solo las activas y ordenadas
    List<FooterSection> findByActiveTrueOrderByPositionAsc();

    /* ===== Admin (Listado completo) ===== */
    List<FooterSection> findAllByOrderByPositionAsc();

    /* ===== Validaciones (Creación) ===== */
    // Verifica si el título ya existe (para new FooterSection)
    boolean existsByTitleIgnoreCase(String title);

    /* ===== Validaciones (Actualización) - CRÍTICO ===== */
    // Verifica si el título existe EN OTRO ID diferente al actual.
    // Sin esto, no podrás guardar cambios en una sección sin cambiarle el nombre.
    boolean existsByTitleIgnoreCaseAndIdNot(String title, Long id);

    /* ===== Utilidades (Posicionamiento) - CRÍTICO ===== */
    // Busca el número de posición más alto actualmente en uso.
    // Usamos esto en lugar de count() para evitar errores si borras secciones intermedias.
    @Query("SELECT MAX(s.position) FROM FooterSection s")
    Integer findMaxPosition();
}
