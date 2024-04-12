package com.example.controller;

import com.example.model.PrintForm;
import com.example.service.PrintFormService;
import com.example.utils.FileManager;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

@Controller
@RequestMapping("/print-form")
@RequiredArgsConstructor
public class PrintFormController {

    @Autowired
    private PrintFormService printFormService;

    @PostMapping
    public ResponseEntity<PrintForm> upload(@RequestParam MultipartFile file) {
        try {
            return new ResponseEntity<>(printFormService.upload(file), HttpStatus.CREATED);
        } catch (IOException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping(path = "/{id}", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<Resource> download(@PathVariable(value = "id") Long id) {
        try {
            Optional<PrintForm> foundFile = printFormService.findById(id);
            if (foundFile.isPresent()) {
                PrintForm printForm = foundFile.get();
                Resource resource = FileManager.download(printForm.getUuid(), true);
                return ResponseEntity.ok()
                        .header("Content-Disposition", "attachment; filename=" + printForm.getFileName())
                        .body(resource);
            }
            else return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (IOException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
