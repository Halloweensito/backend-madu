package com.Osiris.backendMadu.Service;

import com.Osiris.backendMadu.DTO.Section.FooterLinkAdmin;
import com.Osiris.backendMadu.DTO.Section.FooterLinkRequest;
import com.Osiris.backendMadu.Entity.FooterLink;
import com.Osiris.backendMadu.Entity.FooterSection;
import com.Osiris.backendMadu.Entity.PageContent;
import com.Osiris.backendMadu.Mapper.FooterLinkAdminMapper;
import com.Osiris.backendMadu.Repository.FooterLinkRepository;
import com.Osiris.backendMadu.Repository.FooterSectionRepository;
import com.Osiris.backendMadu.Repository.PageContentRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class FooterLinkAdminService {

    private final FooterLinkRepository linkRepository;
    private final FooterSectionRepository sectionRepository;
    private final PageContentRepository pageRepository;
    private final FooterLinkAdminMapper linkMapper;

    @Transactional(readOnly = true) // Optimización de lectura
    public List<FooterLinkAdmin> findAll() {
        return linkRepository.findAllForAdmin()
                .stream()
                .map(linkMapper::toAdminDTO)
                .toList();
    }

    public FooterLinkAdmin create(FooterLinkRequest dto) {
        validateLinkTarget(dto);

        FooterSection section = sectionRepository.findById(dto.sectionId())
                .orElseThrow(() -> new EntityNotFoundException("Sección no encontrada"));

        FooterLink link = linkMapper.toEntity(dto);
        link.setSection(section);

        resolveLinkDestination(link, dto);

        // CAMBIO: Usar MAX() en lugar de size() para evitar colisiones
        if (link.getPosition() == null) {
            Integer maxPos = linkRepository.findMaxPositionBySectionId(section.getId());
            link.setPosition(maxPos == null ? 0 : maxPos + 1);
        }

        return linkMapper.toAdminDTO(linkRepository.save(link));
    }

    public FooterLinkAdmin update(Long id, FooterLinkRequest dto) {
        FooterLink link = linkRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Link no encontrado"));

        validateLinkTarget(dto);

        // 1. Detectar si cambió de sección ANTES de mapear
        boolean sectionChanged = dto.sectionId() != null
                && !dto.sectionId().equals(link.getSection().getId());

        linkMapper.updateEntity(dto, link); // MapStruct actualiza campos simples

        // 2. Resolver destino (Page vs URL)
        resolveLinkDestination(link, dto);

        // 3. Manejar cambio de Sección
        if (sectionChanged) {
            FooterSection newSection = sectionRepository.findById(dto.sectionId())
                    .orElseThrow(() -> new EntityNotFoundException("Nueva sección no encontrada"));

            link.setSection(newSection);

            // CAMBIO CRÍTICO: Si cambia de sección, lo mandamos al final de la NUEVA lista
            Integer maxPos = linkRepository.findMaxPositionBySectionId(newSection.getId());
            link.setPosition(maxPos == null ? 0 : maxPos + 1);
        }

        return linkMapper.toAdminDTO(linkRepository.save(link));
    }

    public void delete(Long id) {
        if (!linkRepository.existsById(id)) {
            throw new EntityNotFoundException("Link no encontrado");
        }
        linkRepository.deleteById(id);
    }

    public void reorder(Long sectionId, List<Long> orderedIds) {
        // Tu lógica aquí era buena, solo agregué validación de seguridad
        List<FooterLink> links = linkRepository.findBySectionIdOrderByPositionAsc(sectionId);

        Map<Long, FooterLink> map = links.stream()
                .collect(Collectors.toMap(FooterLink::getId, Function.identity()));

        for (int i = 0; i < orderedIds.size(); i++) {
            FooterLink link = map.get(orderedIds.get(i));
            // Seguridad: Solo reordenamos si el link pertenece a esta sección
            if (link != null) {
                link.setPosition(i);
            }
        }
        // No es estrictamente necesario llamar a saveAll con @Transactional,
        // pero a veces es bueno para claridad. JPA hará el update automático al cerrar transacción.
    }

    /* ===== HELPERS ===== */

    private void resolveLinkDestination(FooterLink link, FooterLinkRequest dto) {
        if (dto.pageId() != null) {
            PageContent page = pageRepository.findById(dto.pageId())
                    .orElseThrow(() -> new EntityNotFoundException("Página no encontrada"));
            link.setPage(page);
            link.setUrl(null); // Limpiamos URL si hay página
        } else {
            link.setPage(null); // Limpiamos página si es URL externa
            // La URL ya se actualizó en el mapper.updateEntity
        }
    }

    private void validateLinkTarget(FooterLinkRequest dto) {
        boolean hasPage = dto.pageId() != null;
        // Ojo: Validamos que url no sea nula Y no esté vacía/blanca
        boolean hasUrl = dto.url() != null && !dto.url().isBlank();

        if (hasPage == hasUrl) { // XOR: Si ambos son true o ambos false -> Error
            throw new IllegalArgumentException(
                    "Debe especificarse una página interna o una URL externa, pero no ambas."
            );
        }
    }
}