package com.Osiris.backendMadu.Mapper;


import com.Osiris.backendMadu.DTO.ImageResponse;
import com.Osiris.backendMadu.Entity.Image;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ImageMapper {
    ImageResponse toDto(Image image);
}