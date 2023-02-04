package com.example.zipzip.Controller;


import com.example.zipzip.Facade.FileFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RestController
@RequestMapping("/file")
public class FileController {

    private final FileFacade fileFacade;

    @Autowired
    public FileController(FileFacade fileFacade) {
        this.fileFacade = fileFacade;
    }

    @PostMapping("/save")
    public ResponseEntity<?> saveFile(Long id, String nameFile, String fileUrl, Long size, MultipartFile file) throws IOException {
        return fileFacade.saveFile(id, nameFile, fileUrl, size, file);
    }
    @PutMapping("/update")
    public ResponseEntity<?> updateFile(Long fileId, Long userId, Long size,MultipartFile file) throws IOException {
        return fileFacade.updateFile(fileId, userId, size, file);
    }

    @GetMapping()
    public ResponseEntity<?> getRoot(Long userId,@RequestParam(required = false) Long fileId){
        return fileFacade.getRoot(userId, fileId);
    }

    @PutMapping("/update/name")
    public ResponseEntity<?> updateNameFile(@RequestParam Long fileId,@RequestParam(required = false) String name, @RequestParam(required = false) String urlToFile) throws IOException {
        return fileFacade.updateNameFile(fileId, name, urlToFile);
    }

    @PostMapping("/dir")
    public ResponseEntity<?> createDir(Long userId ,String url, String name){
        return fileFacade.createDir(userId, url, name);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteFile(@PathVariable Long id){
        return fileFacade.deleteFile(id);
    }

    @DeleteMapping("/dir")
    public ResponseEntity<?> deleteDir(@RequestParam Long id){
        return fileFacade.deleteDir(id);
    }

    @PostMapping("/download/{id}")
    public void downloadFile(HttpServletRequest request, HttpServletResponse response, @PathVariable Long id) throws Exception {
        fileFacade.downloadFile(request, response, id);
    }
}
