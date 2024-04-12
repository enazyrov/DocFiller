package com.example.controller;

import com.example.model.PrintForm;
import com.example.model.Report;
import com.example.service.ReportService;
import com.example.utils.FileManager;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;
import java.util.Optional;

@Controller
@RequestMapping("/report")
@RequiredArgsConstructor
public class ReportController {

    @Autowired
    private ReportService reportService;

    @GetMapping(path = "/{id}", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<Resource> download(@PathVariable(value = "id") Long id) {
        try {
            Optional<Report> foundFile = reportService.findById(id);
            if (foundFile.isPresent()) {
                Report report = foundFile.get();
                System.out.println("NAME: " + report.getFileName());
                Resource resource = FileManager.download(report.getUuid(), false);
                return ResponseEntity.ok()
                        .header("Content-Disposition", "attachment; filename=" + report.getFileName())
                        .body(resource);
            }
            else return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (IOException e) {
            return new ResponseEntity<>(HttpStatus.BAD_GATEWAY);
        }
    }
}
