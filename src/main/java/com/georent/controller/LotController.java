package com.georent.controller;


import com.georent.service.LotService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.ResponseEntity.status;

@Slf4j
@Controller
@RequestMapping("lot")
public class LotController {

    private final LotService lotService;

    @Autowired
    public LotController(final LotService lotService) {
        this.lotService = lotService;
    }

    @GetMapping
    public ResponseEntity<?> getLots(){
        return ResponseEntity.ok(lotService.getLotsDto());
    }

   @GetMapping ("/{id}")
    public ResponseEntity<?> getLotId(@PathVariable(value = "id") Long lotId) {
        return status(OK).body(lotService.getLotDto(lotId));
    }

    //Now it's working. Check the difference.
    @PostMapping("upload")
    public ResponseEntity<String> setLotUpload(@RequestParam(name = "file") MultipartFile file){
        String name = "test11";
        if (!file.isEmpty()) {
            try {
                byte[] bytes = file.getBytes();
                BufferedOutputStream stream =
                        new BufferedOutputStream(new FileOutputStream(new File(name + "-uploaded")));
                stream.write(bytes);
                stream.close();
                return ResponseEntity.ok("You successfully uploaded " + name + " into " + name + "-uploaded !");
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("You failed to upload " + name + " => " + e.getMessage());
            }
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("You failed to upload " + name + " because the file was empty.");
        }
    }

    //This is more concise variant.
    @PostMapping("upload-picture")
    public ResponseEntity<String> uploadPicture(@RequestParam(name = "file") MultipartFile multipartFile){
        String originalFilename = multipartFile.getOriginalFilename();
        try(InputStream inputStream = multipartFile.getInputStream()) {
            Path tempFile = Files.createTempFile("tmp_", originalFilename);
            Files.copy(inputStream, tempFile, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            log.error("Unable to save the file", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Unable to save the file.");
        }
        return ResponseEntity.status(HttpStatus.OK).body("File saved");
    }

}
