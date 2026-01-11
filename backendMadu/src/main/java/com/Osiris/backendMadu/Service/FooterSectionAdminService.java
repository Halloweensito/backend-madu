package com.Osiris.backendMadu.Service;

import com.Osiris.backendMadu.DTO.Section.FooterSectionAdmin;
import com.Osiris.backendMadu.DTO.Section.FooterSectionRequest;
import com.Osiris.backendMadu.Entity.FooterSection;
import com.Osiris.backendMadu.Mapper.FooterSectionMapper;
import com.Osiris.backendMadu.Repository.FooterLinkRepository;
import com.Osiris.backendMadu.Repository.FooterSectionRepository;
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
public class FooterSectionAdminService {

    private final FooterSectionRepository sectionRepository;
    private final FooterSectionMapper sectionMapper;
    private final FooterLinkRepository footerLinkRepository;

    @Transactional(readOnly = true)
    public List<FooterSectionAdmin> findAll() {
        return sectionRepository.findAllByOrderByPositionAsc()
                .stream()
                .map(sectionMapper::toAdminDTO)
                .toList();
    }

    public FooterSectionAdmin create(FooterSectionRequest dto) {
        // 1. Validación de Título Único
        if (sectionRepository.existsByTitleIgnoreCase(dto.title())) {
            throw new IllegalArgumentException("Ya existe una sección con el título: " + dto.title());
        }

        FooterSection section = sectionMapper.toEntity(dto);

        // 2. Lógica de Posición Robusta (Reemplaza al count())
        if (section.getPosition() == null) {
            Integer maxPos = sectionRepository.findMaxPosition();
            // Si devuelve null (tabla vacía), empezamos en 0. Si no, max + 1
            section.setPosition(maxPos == null ? 0 : maxPos + 1);
        }

        return sectionMapper.toAdminDTO(
                sectionRepository.save(section)
        );
    }

    public FooterSectionAdmin update(Long id, FooterSectionRequest dto) {
        FooterSection section = sectionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Sección no encontrada"));

        // 3. Validación al Actualizar (Evitar duplicados)
        // Verificamos si el título ha cambiado Y si ese título ya existe en OTRO ID
        if (!section.getTitle().equalsIgnoreCase(dto.title()) &&
                sectionRepository.existsByTitleIgnoreCaseAndIdNot(dto.title(), id)) {
            throw new IllegalArgumentException("Ya existe otra sección con el título: " + dto.title());
        }

        sectionMapper.updateEntity(dto, section);

        return sectionMapper.toAdminDTO(sectionRepository.save(section));
    }

    public void delete(Long id) {
        if (!sectionRepository.existsById(id)) {
            throw new EntityNotFoundException("Sección no encontrada");
        }

        footerLinkRepository.deleteBySectionId(id);

        sectionRepository.deleteById(id);
    }

    public void reorder(List<Long> orderedIds) {
        // 1. Traemos todas las secciones (son pocas, no hay problema de performance)
        List<FooterSection> sections = sectionRepository.findAll();

        // 2. Mapeamos ID -> Entidad para acceso rápido O(1)
        Map<Long, FooterSection> sectionMap = sections.stream()
                .collect(Collectors.toMap(FooterSection::getId, Function.identity()));

        // 3. Asignamos la nueva posición basada en el índice de la lista recibida
        for (int i = 0; i < orderedIds.size(); i++) {
            FooterSection section = sectionMap.get(orderedIds.get(i));
            if (section != null) {
                section.setPosition(i);
            }
        }

        // Al ser @Transactional, Hibernate detecta los cambios y hace los UPDATE automáticamente.
        // No hace falta llamar a saveAll explícitamente.
    }
}