package com.example.zipzip.Repo;

import com.example.zipzip.Entity.File;
import com.example.zipzip.Entity.Type;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface FileRepo extends JpaRepository<File, Long> {

    Optional<File> getFileById(Long id);

    @Transactional
    void deleteByUrlToFile(String urlToFile);

    File getFileByUrlToFileAndType(String urlToFile, Type type);

    List<File> getFileByFileId(Long fileId);
}
