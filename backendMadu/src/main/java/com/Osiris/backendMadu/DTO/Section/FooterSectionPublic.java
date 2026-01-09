package com.Osiris.backendMadu.DTO.Section;

import java.util.List;

public record FooterSectionPublic(
        String title,
        List<FooterLinkPublic> links
) {}
