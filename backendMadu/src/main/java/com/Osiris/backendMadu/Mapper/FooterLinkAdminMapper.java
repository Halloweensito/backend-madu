package com.Osiris.backendMadu.Mapper;


import com.Osiris.backendMadu.DTO.Section.FooterLinkAdmin;
import com.Osiris.backendMadu.DTO.Section.FooterLinkRequest;
import com.Osiris.backendMadu.Entity.FooterLink;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface FooterLinkAdminMapper {

    /* ===== READ ===== */
    @Mapping(target = "pageId", source = "page.id")
    @Mapping(target = "pageTitle", source = "page.title")
    @Mapping(target = "sectionId", source = "section.id")
    FooterLinkAdmin toAdminDTO(FooterLink link);

    /* ===== CREATE ===== */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "page", ignore = true)
    @Mapping(target = "section", ignore = true)
    FooterLink toEntity(FooterLinkRequest dto);

    /* ===== UPDATE ===== */
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "page", ignore = true)
    @Mapping(target = "section", ignore = true)
    void updateEntity(
            FooterLinkRequest dto,
            @MappingTarget FooterLink entity
    );
}

