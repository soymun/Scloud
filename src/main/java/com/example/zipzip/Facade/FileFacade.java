package com.example.zipzip.Facade;

import com.example.zipzip.DTO.FileDTO;
import com.example.zipzip.DTO.UserDto;
import com.example.zipzip.Entity.Type;
import com.example.zipzip.Mappers.UserMappers;
import com.example.zipzip.Response.ResponseDto;
import com.example.zipzip.Service.Impl.FileService;
import com.example.zipzip.Service.Impl.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLConnection;

@Service
@Slf4j
public class FileFacade {

    private final UserService userService;

    private final FileService fileService;

    private final UserMappers userMappers;

    @Value("${toFile.url}")
    private String url;
    @Autowired
    public FileFacade(UserService userService, FileService fileService, UserMappers userMappers) {
        this.userService = userService;
        this.fileService = fileService;
        this.userMappers = userMappers;
    }

    public ResponseEntity<?> saveFile(Long id, String nameFile, String fileUrl, Long size, MultipartFile file) throws IOException {
        File file1 = new File(url + fileUrl);
        if(file1.createNewFile()) {
            UserDto userDto = userService.findUserById(id);
            if (userDto.getFreeSize() - size < 0) {
                throw new RuntimeException("Недостаточно памяти");
            }
            userDto.setFreeSize(userDto.getFreeSize() - size);
            userService.patchUser(userMappers.userDtoToUser(userDto));
            byte[] bytes = file.getBytes();
            BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(file1));
            stream.write(bytes);
            stream.close();
            String str = fileUrl.substring(0, fileUrl.lastIndexOf('\\'));
            FileDTO fileDTO = new FileDTO(nameFile, fileUrl, Type.FILE, id, size, fileService.getFileByUrlToFile(str, Type.FOLDER).getId());
            fileService.saveFile(fileDTO);
        }
        return ResponseEntity.ok(ResponseDto.builder().data("Успешно").build());
    }


    public ResponseEntity<?> updateFile(Long fileId, Long userId, Long size,MultipartFile file) throws IOException {
        FileDTO fileDTO = fileService.getFileById(fileId);
        Long differenceSize = (fileDTO.getSize() - size);
        fileDTO.setSize(fileDTO.getSize() - differenceSize);
        fileService.saveFile(fileDTO);
        UserDto userDto = userService.findUserById(userId);
        userDto.setFreeSize(userDto.getFreeSize() + differenceSize);
        userService.patchUser(userMappers.userDtoToUser(userDto));
        File file1 = new File(url + fileDTO.getUrlToFile());
        byte[] bytes = file.getBytes();
        BufferedOutputStream stream =  new BufferedOutputStream(new FileOutputStream(file1));
        stream.write(bytes);
        stream.close();
        return ResponseEntity.ok(ResponseDto.builder().data("Успешно").build());
    }


    public ResponseEntity<?> getRoot(Long userId, Long fileId){
        return ResponseEntity.ok(ResponseDto.builder().data(fileService.getFileDtoByUserIdAndFileId(userId, fileId)).build());
    }


    public ResponseEntity<?> updateNameFile(Long fileId, String name, String urlToFile) throws IOException {
        FileDTO fileDTO = fileService.getFileById(fileId);
        if(name != null){
            fileDTO.setNameFile(name);
            File fromFile = new File(url + fileDTO.getUrlToFile());
            String type = fromFile.getName().split("\\.")[1];
            fileDTO.setUrlToFile(fileDTO.getUrlToFile().substring(0, fileDTO.getUrlToFile().lastIndexOf("\\")) + "\\" + name + "." + type);
            File file = new File(url + fileDTO.getUrlToFile());
            if (createFile(fromFile, file)){
                fileService.saveFile(fileDTO);
                return ResponseEntity.ok(ResponseDto.builder().data("Успешно").build());}
        }
        if(urlToFile != null){
            File fromFile = new File(url + fileDTO.getUrlToFile());
            File file = new File(url + urlToFile);
            if (createFile(fromFile, file)) {
                fileDTO.setUrlToFile(urlToFile);
                fileService.saveFile(fileDTO);
                return ResponseEntity.ok(ResponseDto.builder().data("Успешно").build());}
        }
        return ResponseEntity.ok(ResponseDto.builder().error("Не удалось изменить файл"));
    }

    private boolean createFile(File fromFile, File file) throws IOException {
        if(file.createNewFile()){
            BufferedInputStream bufferedInputStream = new BufferedInputStream(new FileInputStream(fromFile));
            byte[] bytes = bufferedInputStream.readAllBytes();
            bufferedInputStream.close();
            BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(file));
            stream.write(bytes);
            stream.close();
            return fromFile.delete();
        }
        return false;
    }


    public ResponseEntity<?> createDir(Long userId ,String url, String name){
        fileService.createDirById(userId, url, name);
        return ResponseEntity.ok(ResponseDto.builder().data("Успешно").build());
    }


    public ResponseEntity<?> deleteFile(Long id){
        fileService.deleteFile(id);
        return ResponseEntity.ok(ResponseDto.builder().data("Успешно").build());
    }


    public ResponseEntity<?> deleteDir(Long id){
        fileService.deleteDir(id);
        return ResponseEntity.ok(ResponseDto.builder().data("Успешно").build());
    }


    public void downloadFile(HttpServletRequest request, HttpServletResponse response, Long id) throws Exception {
        FileDTO fileDTO = fileService.getFileById(id);
        if(fileDTO.getType().equals(Type.FILE)) {
            File file = new File(url + fileDTO.getUrlToFile());
            String mimeType = URLConnection.guessContentTypeFromName(file.getName());
            response.setContentType(mimeType);

            response.setHeader("Content-Disposition", String.format("inline; filename=\"" + file.getName() + "\""));

            response.setContentLength((int) file.length());

            InputStream inputStream = new BufferedInputStream(new FileInputStream(file));

            FileCopyUtils.copy(inputStream, response.getOutputStream());
            log.info("Файл {} скачен", id);
        }
    }
}