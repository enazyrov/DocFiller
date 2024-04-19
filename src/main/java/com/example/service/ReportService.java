package com.example.service;

import com.example.dao.ReportRepository;
import com.example.model.PrintForm;
import com.example.model.Report;
import com.example.utils.FileManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.util.IOUtils;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.time.OffsetDateTime;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class ReportService extends ResponseService {
    private final ReportRepository reportRepository;
    private final PrintFormService printFormService;

    public Report findByDealIdAndTypeId(Long dealId, Long typeId){
        return reportRepository.findByDealIdAndTypeId(dealId, typeId).orElseThrow();
    }

    public Optional<Report> findById(Long repId) {
        return reportRepository.findById(repId);
    }

    public Optional<Report> findByDealId(Long dealId) {
        return reportRepository.findByDealId(dealId);
    }

    public void upload(Long typeId,  String fileName,  Long dealId,  String jsonString) throws IOException {

        final Report existingReport = reportRepository.findByDealIdAndTypeId(dealId, typeId).orElse(null);

        if (existingReport != null) {
            reportRepository.deleteById(existingReport.getId());
        }

        Resource resource = printFormService.createGeneratedPrintFormFileResponse(printFormService.findOne(typeId), fileName, jsonString).getBody();
        try (InputStream inputStream = resource.getInputStream()) {
            byte[] bytes = IOUtils.toByteArray(inputStream);
            //DataStorageUploadResponse dataStorageUploadResponse = dataStorageClient.saveBytesToDataStoragePost(bytes, fileName, "");
            Report report = new Report();
            report.setDealId(dealId);
            report.setFileName(fileName);

            String uuid = FileManager.generateUuid(fileName);
            report.setUuid(uuid);

            report.setCreateDate(OffsetDateTime.now());
            report.setTypeId(typeId);
            //report.setSize((long) dataStorageUploadResponse.getFileSize());
            reportRepository.save(report);
            FileManager.upload(bytes, uuid, false);
            log.info("Цель {}. ПФ для дела {} успешно сохранена", typeId == 1L ? "Протокол предзащиты" : "Протокол защиты", dealId);
        } catch (Exception e) {
            throw new RuntimeException("Ошибка сохранения в локальное хранилище: " + e.getMessage());
        }
    }

    public ResponseEntity<Resource> createPrintFormFileResponse(Report report) throws IOException {
        return createResponseEntity(getReportFile(report), report.getFileName());
    }

    public File getReportFile(Report report) {
        return new File("заглушка" + report.getFileName());
    }
}
