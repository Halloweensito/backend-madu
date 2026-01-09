package com.Osiris.backendMadu.DTO.PageContent;

public record PageContentData(
        String slug,
        String title,
        String content,
        boolean published,
        String metaTitle,
        String metaDescription
) {}
