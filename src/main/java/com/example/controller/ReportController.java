package com.example.controller;

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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import java.io.IOException;
import java.util.Optional;

@Controller
@RequestMapping("/report")
@RequiredArgsConstructor
public class ReportController {

    @Autowired
    private ReportService reportService;

    @GetMapping
    public ResponseEntity<Resource> download(@RequestParam Integer dealId, @RequestParam String typeId) {
        try {
            Optional<Report> foundFile = reportService.findByDealIdAndTypeId(Long.valueOf(dealId), Long.valueOf(typeId));
            if (foundFile.isPresent()) {
                Report report = foundFile.get();
                Resource resource = FileManager.download(report.getUuid(), false);
                return ResponseEntity.ok()
                        .header("Content-Disposition", "attachment; filename=" + report.getFileName())
                        .body(resource);
            }
        } catch (IOException e) {
            throw new IllegalArgumentException("Не удалось скачать файл документа!");
        }
        return ResponseEntity.ok().build();
    }
}
