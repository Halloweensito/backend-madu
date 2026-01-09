package com.Osiris.backendMadu.Repository;

import com.Osiris.backendMadu.Entity.PageContent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PageContentRepository extends JpaRepository<PageContent, Long> {

    /* ==========================================
       MÉTODOS DE BÚSQUEDA (LECTURA)
       ========================================== */

    /**
     * VISTA PÚBLICA:
     * Busca una página por su URL (slug) PERO solo si está publicada.
     * Si existe el slug pero published=false, devuelve vacío (404).
     */
    Optional<PageContent> findBySlugAndPublishedTrue(String slug);

    /**
     * VISTA ADMIN / PREVIEW:
     * Busca por slug sin importar si está publicada o no.
     * Útil para editar o previsualizar borradores.
     */
    Optional<PageContent> findBySlug(String slug);

    /**
     * LISTADO PÚBLICO:
     * Devuelve todas las páginas publicadas.
     * Útil para generar menús dinámicos o sitemaps.
     */
    List<PageContent> findByPublishedTrue();

    /* ==========================================
       MÉTODOS DE VALIDACIÓN (Útiles para Service)
       ========================================== */

    /**
     * CREACIÓN:
     * Verifica si ya existe una página con ese slug.
     * Se usa antes de guardar para lanzar excepción si hay duplicado.
     */
    boolean existsBySlug(String slug);

    /**
     * EDICIÓN:
     * Verifica si el slug ya existe en CUALQUIER OTRA página
     * que NO sea la que estoy editando actualmente (id != id).
     */
    boolean existsBySlugAndIdNot(String slug, Long id);
}