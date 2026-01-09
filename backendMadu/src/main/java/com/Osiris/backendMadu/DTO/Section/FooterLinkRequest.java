package com.Osiris.backendMadu.DTO.Section;

public record FooterLinkRequest(
        String label,
        String url,
        Long pageId,
        Long sectionId,
        Integer position,
        Boolean active
) {}
