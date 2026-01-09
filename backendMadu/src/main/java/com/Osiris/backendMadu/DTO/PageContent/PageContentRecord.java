package com.Osiris.backendMadu.DTO.PageContent;

import java.time.LocalDateTime;

public record PageContentRecord(
        Long id,
        String slug,
        String title,
        String content,
        boolean published,
        String metaTitle,
        String metaDescription,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}
