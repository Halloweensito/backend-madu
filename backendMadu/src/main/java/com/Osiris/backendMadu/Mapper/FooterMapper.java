package com.Osiris.backendMadu.Mapper;

import com.Osiris.backendMadu.DTO.Section.FooterLinkPublic;
import com.Osiris.backendMadu.Entity.FooterLink;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface FooterMapper {

    /* ===== Links ===== */

    @Mapping(
            target = "url",
            expression = "java(resolveUrl(link))"
    )
    FooterLinkPublic toPublicLink(FooterLink link);

    List<FooterLinkPublic> toPublicLinks(List<FooterLink> links);

    /* ===== Helpers ===== */

    default String resolveUrl(FooterLink link) {
        if (link.getPage() != null) {
            return "/" + link.getPage().getSlug();
        }
        return link.getUrl();
    }
}

