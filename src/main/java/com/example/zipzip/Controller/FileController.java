package com.example.zipzip.Controller;


import com.example.zipzip.DTO.FileDTO;
import com.example.zipzip.Entity.User;
import com.example.zipzip.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
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

    private final String url = "D:\\IdeaProject\\zipzip\\src\\main\\file\\";
    @Autowired
    public FileController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/save")
    public String saveFile(Long id, MultipartFile file) throws IOException {
        File file1 = new File(url + file.getOriginalFilename());
        if(file1.createNewFile()){
            file.transferTo(file1);
            User user = userService.findUserById(id);
            user.saveFile(file1.getPath());
            userService.save(user);
        }
        else {
            BufferedWriter writer = Files.newBufferedWriter(Paths.get(url + file.getOriginalFilename()));
            writer.write("");
            writer.flush();
            file.transferTo(file1);
        }
        return "Suggest";
    }

    @GetMapping("/download")
    public void getFiles(HttpServletRequest request, HttpServletResponse response, @RequestBody FileDTO fileDTO) throws IOException {
        if(!userService.findFilm(url + fileDTO.getNameFile(), fileDTO.getUser())){
            return;
        }
        File file = new File(url + fileDTO.getNameFile());
        String mimeType = URLConnection.guessContentTypeFromName(file.getName());
        response.setContentType(mimeType);

        response.setHeader("Content-Disposition", String.format("inline; filename=\"" + file.getName() + "\""));

        response.setContentLength((int) file.length());

        InputStream inputStream = new BufferedInputStream(new FileInputStream(file));

        FileCopyUtils.copy(inputStream, response.getOutputStream());
    }
}
