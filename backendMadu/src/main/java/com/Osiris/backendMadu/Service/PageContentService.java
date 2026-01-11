package com.Osiris.backendMadu.Service;

import com.Osiris.backendMadu.DTO.PageContent.PageContentData;
import com.Osiris.backendMadu.DTO.PageContent.PageContentRecord;
import com.Osiris.backendMadu.DTO.PageContent.PublicPageContent;
import com.Osiris.backendMadu.Entity.PageContent;
import com.Osiris.backendMadu.Mapper.PageContentMapper;
import com.Osiris.backendMadu.Repository.FooterLinkRepository;
import com.Osiris.backendMadu.Repository.PageContentRepository;
import com.Osiris.backendMadu.Utils.SlugGenerator;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PageContentService {

    private final PageContentRepository repository;
    private final PageContentMapper mapper;
    private final SlugGenerator slugGenerator;
    private final FooterLinkRepository footerLinkRepository;

    /* =======================================================
       MÉTODOS PÚBLICOS (Para la Web)
       ======================================================= */

    /**
     * Obtiene una página para mostrarla en el frontend.
     * Solo devuelve si 'published = true'.
     */
    public PublicPageContent getPublicPageBySlug(String slug) {
        return repository.findBySlugAndPublishedTrue(slug)
                .map(mapper::toPublicRecord)
                .orElseThrow(() -> new EntityNotFoundException("Página no encontrada o no publicada: " + slug));
    }

    /**
     * Obtiene el menú de páginas (solo las publicadas).
     * Ideal para navbar o footer.
     */
    public List<PublicPageContent> getAllPublicPages() {
        return repository.findByPublishedTrue().stream()
                .map(mapper::toPublicRecord)
                .toList();
    }

    /* =======================================================
       MÉTODOS ADMIN (Creación y Edición)
       ======================================================= */

    /**
     * Obtiene lista completa para el panel de administración (incluye borradores).
     */
    public List<PageContentRecord> getAllPagesForAdmin() {
        return repository.findAll().stream()
                .map(mapper::toAdminRecord)
                .toList();
    }

    public PageContentRecord getPageById(Long id) {
        return repository.findById(id)
                .map(mapper::toAdminRecord)
                .orElseThrow(() -> new EntityNotFoundException("Página no encontrada con ID: " + id));
    }

    @Transactional
    public PageContentRecord createPage(PageContentData data) {
        // 1. Generar slug si no viene
        String slug = (data.slug() == null || data.slug().isBlank())
                ? slugGenerator.generateSlug(data.title())
                : slugGenerator.generateSlug(data.slug());

        // 2. Asegurar unicidad (intentos hasta encontrar un slug libre)
        int attempt = 0;
        String uniqueSlug = slug;
        while (repository.existsBySlug(uniqueSlug)) {
            attempt++;
            uniqueSlug = slugGenerator.generateUniqueSlug(slug, attempt);
        }

        // 3. Mapear data y setear slug final
        PageContentData dataWithSlug = new PageContentData(
                uniqueSlug,
                data.title(),
                data.content(),
                data.published(),
                data.metaTitle(),
                data.metaDescription()
        );

        PageContent entity = mapper.toEntity(dataWithSlug);
        entity = repository.save(entity);

        return mapper.toAdminRecord(entity);
    }
    @Transactional // Escritura
    public PageContentRecord updatePage(Long id, PageContentData data) {
        // 1. Buscar la entidad existente
        PageContent entity = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("No se puede editar. Página no encontrada ID: " + id));

        // 2. Sanitizar el Slug entrante (Igual que en create)
        // Si el usuario manda " Hola Mundo ", lo convertimos a "hola-mundo" antes de validar
        String incomingSlug = data.slug() != null ? data.slug() : entity.getTitle();
        String sanitizedSlug = slugGenerator.generateSlug(incomingSlug);

        // 3. Validar colisión
        if (repository.existsBySlugAndIdNot(sanitizedSlug, id)) {
            throw new IllegalArgumentException("El slug '" + sanitizedSlug + "' ya está en uso por otra página.");
        }

        // 4. Crear un nuevo DTO con el slug limpio para que el Mapper lo use
        // (Recordemos que los Records son inmutables)
        PageContentData cleanData = new PageContentData(
                sanitizedSlug, // Usamos el limpio
                data.title(),
                data.content(),
                data.published(),
                data.metaTitle(),
                data.metaDescription()
        );

        // 5. Actualizar campos
        mapper.updateEntity(cleanData, entity);

        // 6. Guardar
        entity = repository.save(entity);

        return mapper.toAdminRecord(entity);
    }

    @Transactional // Escritura
    public void deletePage(Long id) {
        if (!repository.existsById(id)) {
            throw new EntityNotFoundException("No se puede eliminar. Página no encontrada ID: " + id);
        }

        footerLinkRepository.deleteByPageId(id);
        repository.deleteById(id);
    }
}