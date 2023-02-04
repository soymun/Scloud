package com.example.zipzip.Service;

import com.example.zipzip.DTO.FileDTO;
import com.example.zipzip.Entity.File;
import com.example.zipzip.Entity.Type;

import java.util.List;

public interface FileServiceInt {

    void createDirById(Long userId,String url, String name);

    void saveFile(FileDTO fileDTO);

    FileDTO getFileById(Long id);

    void deleteFile(Long id);

    void deleteDir(Long id);

    FileDTO getFileByUrlToFile(String urlToFile, Type type);

    List<FileDTO> getFileDtoByUserIdAndFileId(Long userId, Long fileId);
}
