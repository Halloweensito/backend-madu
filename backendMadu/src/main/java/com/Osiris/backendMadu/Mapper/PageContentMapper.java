package com.Osiris.backendMadu.Mapper;

import com.Osiris.backendMadu.DTO.PageContent.PageContentData;
import com.Osiris.backendMadu.DTO.PageContent.PageContentRecord;
import com.Osiris.backendMadu.DTO.PageContent.PublicPageContent;
import com.Osiris.backendMadu.Entity.PageContent;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface PageContentMapper {

    /* ======================
       CREATE
       ====================== */

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "layout", ignore = true)
    PageContent toEntity(PageContentData data);

    /* ======================
       UPDATE (MUY IMPORTANTE)
       ====================== */

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "layout", ignore = true)
    void updateEntity(
            PageContentData data,
            @MappingTarget PageContent entity
    );

    /* ======================
       PUBLIC VIEW
       ====================== */

    PublicPageContent toPublicRecord(PageContent entity);


    PageContentRecord toAdminRecord(PageContent entity);
}