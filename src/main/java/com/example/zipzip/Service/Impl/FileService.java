package com.example.zipzip.Service.Impl;

import com.example.zipzip.Service.FileServiceInt;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;

@Service
@Slf4j
public class FileService implements FileServiceInt {

    @Value("${toFile.url}")
    private String url;


    @Override
    public void createDirById(String name) {
        File file = new File(url + name);
        if(!file.exists()){
            if(!file.mkdir()){
                log.debug("Папка не может быть сохранена {}", name);
                throw new RuntimeException("Не удалось создать папку");
            }
            log.info("Cохранение папки {}", name);
        }
    }
}
