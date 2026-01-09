package com.Osiris.backendMadu.Service;


import com.Osiris.backendMadu.DTO.Section.FooterSectionPublic;
import com.Osiris.backendMadu.Entity.FooterLink;
import com.Osiris.backendMadu.Entity.FooterSection;
import com.Osiris.backendMadu.Mapper.FooterMapper;
import com.Osiris.backendMadu.Repository.FooterLinkRepository;
import com.Osiris.backendMadu.Repository.FooterSectionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FooterPublicService {

    private final FooterSectionRepository sectionRepository;
    private final FooterLinkRepository linkRepository;
    private final FooterMapper footerMapper;

    public List<FooterSectionPublic> getFooter() {

        List<FooterSection> sections =
                sectionRepository.findByActiveTrueOrderByPositionAsc();

        List<FooterLink> links =
                linkRepository.findPublicLinks();

        Map<Long, List<FooterLink>> linksBySection =
                links.stream()
                        .collect(Collectors.groupingBy(
                                l -> l.getSection().getId(),
                                LinkedHashMap::new,
                                Collectors.toList()
                        ));

        return sections.stream()
                .map(section -> new FooterSectionPublic(
                        section.getTitle(),
                        footerMapper.toPublicLinks(
                                linksBySection.getOrDefault(section.getId(), List.of())
                        )
                ))
                .toList();
    }
}
