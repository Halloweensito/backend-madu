package com.Osiris.backendMadu.DTO.Section;

public record FooterLinkAdmin(
        Long id,
        String label,
        String url,
        Long pageId,
        String pageTitle,
        Long sectionId,
        Integer position,
        Boolean active
) {}
