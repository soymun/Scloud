package com.example.zipzip.Controller;


import com.example.zipzip.DTO.FileDTO;
import com.example.zipzip.DTO.UserDto;
import com.example.zipzip.Entity.Type;
import com.example.zipzip.Mappers.UserMappers;
import com.example.zipzip.Response.ResponseDto;
import com.example.zipzip.Service.Impl.FileService;
import com.example.zipzip.Service.Impl.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Paths;

@RestController
public class FileController {

    private final UserService userService;

    private final FileService fileService;

    private final UserMappers userMappers;

    @Value("${toFile.url}")
    private String url;
    @Autowired
    public FileController(UserService userService, FileService fileService, UserMappers userMappers) {
        this.userService = userService;
        this.fileService = fileService;
        this.userMappers = userMappers;
    }

    @PostMapping("/save")
    public ResponseEntity<?> saveFile(Long id, String nameFile, String fileUrl, Long size, MultipartFile file) throws IOException {
        File file1 = new File(url + fileUrl);
        if(file1.createNewFile()){
            UserDto userDto = userService.findUserById(id);
            if(userDto.getFreeSize() - size < 0){
                throw new RuntimeException("Недостаточно памяти");
            }
            userDto.setFreeSize(userDto.getFreeSize()-size);
            userService.patchUser(userMappers.userDtoToUser(userDto));
            byte[] bytes = file.getBytes();
            BufferedOutputStream stream =  new BufferedOutputStream(new FileOutputStream(file1));
            stream.write(bytes);
            stream.close();
            String str = fileUrl.substring(0, fileUrl.lastIndexOf('\\'));
            FileDTO fileDTO = new FileDTO(nameFile, fileUrl, Type.FILE, id, size, fileService.getFileByUrlToFile(str, Type.FOLDER).getId());
            fileService.saveFile(fileDTO);
        }
        else {
            byte[] bytes = file.getBytes();
            BufferedOutputStream stream =  new BufferedOutputStream(new FileOutputStream(file1));
            stream.write(bytes);
            stream.close();
        }
        return ResponseEntity.ok(ResponseDto.builder().data("Успешно").build());
    }

    @PostMapping("/dir")
    public ResponseEntity<?> createDir(Long userId ,String url, String name){
        fileService.createDirById(userId, url, name);
        return ResponseEntity.ok(ResponseDto.builder().data("Успешно").build());
    }

    @DeleteMapping("/file/{id}")
    public ResponseEntity<?> deleteFile(@PathVariable Long id){
        fileService.deleteFile(id);
        return ResponseEntity.ok(ResponseDto.builder().data("Успешно").build());
    }

    @DeleteMapping("/dir")
    public ResponseEntity<?> deleteDir(@RequestParam Long id){
        fileService.deleteDir(id);
        return ResponseEntity.ok(ResponseDto.builder().data("Успешно").build());
    }

    @PostMapping("/download")
    public void downloadFile(HttpServletRequest request, HttpServletResponse response, @RequestBody FileDTO fileDTO) throws IOException {
        File file = new File(url + fileDTO.getNameFile());
        String mimeType = URLConnection.guessContentTypeFromName(file.getName());
        response.setContentType(mimeType);

        response.setHeader("Content-Disposition", String.format("inline; filename=\"" + file.getName() + "\""));

        response.setContentLength((int) file.length());

        InputStream inputStream = new BufferedInputStream(new FileInputStream(file));

        FileCopyUtils.copy(inputStream, response.getOutputStream());
    }
}
