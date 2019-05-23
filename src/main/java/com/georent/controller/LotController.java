package com.georent.controller;


import com.georent.service.LotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;

import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.ResponseEntity.status;

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

    @PostMapping("/upload")
    public String setLotUpload(@RequestParam("file") MultipartFile file){
        String name = "test11";
        if (!file.isEmpty()) {
            try {
                byte[] bytes = file.getBytes();
                BufferedOutputStream stream =
                        new BufferedOutputStream(new FileOutputStream(new File(name + "-uploaded")));
                stream.write(bytes);
                stream.close();
                return "You successfully uploaded " + name + " into " + name + "-uploaded !";
            } catch (Exception e) {
                return "You failed to upload " + name + " => " + e.getMessage();
            }
        } else {
            return "You failed to upload " + name + " because the file was empty.";
        }
    }
}
