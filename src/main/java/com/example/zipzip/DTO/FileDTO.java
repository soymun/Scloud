package com.example.zipzip.DTO;

import com.example.zipzip.Entity.Type;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FileDTO {

    private Long id;

    private String nameFile;

    private String urlToFile;

    private Type type;

    private Long userId;

    private Long size;

    private Long fileId;

    public FileDTO(String nameFile, String urlToFile, Type type, Long userId, Long size, Long fileId) {
        this.nameFile = nameFile;
        this.urlToFile = urlToFile;
        this.type = type;
        this.userId = userId;
        this.size = size;
        this.fileId = fileId;
    }
}
