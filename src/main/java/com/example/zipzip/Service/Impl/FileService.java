package com.example.zipzip.Service.Impl;

import com.example.zipzip.DTO.FileDTO;
import com.example.zipzip.Entity.Type;
import com.example.zipzip.Entity.User;
import com.example.zipzip.Mappers.FileMapper;
import com.example.zipzip.Repo.FileRepo;
import com.example.zipzip.Repo.UserRepo;
import com.example.zipzip.Service.FileServiceInt;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class FileService implements FileServiceInt {

    @Value("${toFile.url}")
    private String url;

    private final UserRepo userRepo;

    private final FileMapper fileMapper;

    private final FileRepo fileRepo;

    public FileService(UserRepo userRepo, FileMapper fileMapper, FileRepo fileRepo) {
        this.userRepo = userRepo;
        this.fileMapper = fileMapper;
        this.fileRepo = fileRepo;
    }

    @Override
    public void createDirById(Long userId,String urlToFile, String name) {
        File file = new File(url + urlToFile);
        if(!file.exists()){
            if(!file.mkdir()){
                log.debug("Папка не может быть сохранена {}", urlToFile);
                throw new RuntimeException("Не удалось создать папку");
            }
            log.info("Cохранение папки {}", urlToFile);
        }
        FileDTO fileDTO;
        if(urlToFile.contains("\\")) {
            String str = urlToFile.substring(0, urlToFile.lastIndexOf('\\'));
            fileDTO = new FileDTO(name, urlToFile, Type.FOLDER, userId, 0L, fileRepo.getFileByUrlToFileAndType(str, Type.FOLDER).getId());
        }
        else {
            fileDTO = new FileDTO(name, urlToFile, Type.FOLDER, userId, 0L, null);
        }
        saveFile(fileDTO);
    }

    @Override
    public void saveFile(FileDTO fileDTO) {
        log.info("Сохранение файла {}", fileDTO.getNameFile());
        fileRepo.save(fileMapper.fileDtoToFile(fileDTO));
    }

    @Override
    public FileDTO getFileById(Long id) {
        log.info("Выдача файла {}", id);
        return fileMapper.fileToFileDto(fileRepo.getFileById(id).orElseThrow(() -> {throw new RuntimeException("Файл не найден");}));
    }

    @Override
    @Transactional
    public void deleteFile(Long id) {
        log.info("Удаление файла {}", id);
        com.example.zipzip.Entity.File file = fileRepo.getFileById(id).orElseThrow(() -> {throw new RuntimeException("Файл не нвйден");});
        File file1 = new File(url + file.getUrlToFile());
        if(file1.delete()){
            User user = userRepo.findUserById(file.getUserId()).orElseThrow(() -> {throw new RuntimeException("Пользователь не найден");});
            user.setFreeSize(user.getFreeSize()+file.getSize());
            userRepo.save(user);
            fileRepo.deleteById(id);
        }
    }

    @Override
    @Transactional
    public void deleteDir(Long id) {
        log.info("Удаление папки {}", id);
        File file1 = new File(url + fileRepo.getFileById(id).orElseThrow(() -> {throw new RuntimeException("Папки не существует");}).getUrlToFile());
        if(!file1.delete()){
            fileRepo.getFileByFileId(id).forEach(n -> {
                if (n.getType() == Type.FILE) {
                    deleteFile(n.getId());
                } else {
                    deleteDir(n.getId());
                }
            });
            file1.delete();
        }
        fileRepo.deleteById(id);
    }

    @Override
    public FileDTO getFileByUrlToFile(String urlToFile, Type type) {
        log.info("Выдача файлов по url");
        return fileMapper.fileToFileDto(fileRepo.getFileByUrlToFileAndType(urlToFile, type));
    }

    @Override
    public List<FileDTO> getFileDtoByUserIdAndFileId(Long userId, Long fileId) {
        log.info("Выдача файлов в папке {}", fileRepo);
        return fileRepo.getFileByUserIdAndFileId(userId, fileId).stream().map(fileMapper::fileToFileDto).collect(Collectors.toList());
    }
}
