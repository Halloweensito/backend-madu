package com.Osiris.backendMadu.Mapper;

import com.Osiris.backendMadu.DTO.Section.FooterSectionAdmin;
import com.Osiris.backendMadu.DTO.Section.FooterSectionRequest;
import com.Osiris.backendMadu.Entity.FooterSection;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface FooterSectionMapper {

    FooterSectionAdmin toAdminDTO(FooterSection section);

    @Mapping(target = "id", ignore = true)
    FooterSection toEntity(FooterSectionRequest dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    void updateEntity(
            FooterSectionRequest dto,
            @MappingTarget FooterSection section
    );
}

