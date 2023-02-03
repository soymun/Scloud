package com.example.zipzip.Mappers;


import com.example.zipzip.DTO.FileDTO;
import com.example.zipzip.Entity.File;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface FileMapper {

    File fileDtoToFile(FileDTO fileDTO);

    FileDTO fileToFileDto(File file);
}
