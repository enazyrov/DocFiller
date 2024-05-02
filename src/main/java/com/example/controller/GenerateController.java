package com.example.controller;

import com.example.dao.DefenseRepository;
import com.example.dao.PredefenseRepository;
import com.example.dto.docs.GenerateDto;
import com.example.dto.docs.example.ApplicationDto;
import com.example.dto.docs.example.JsonForPrintForm;
import com.example.model.Defense;
import com.example.model.Predefense;
import com.example.service.GenerateService;
import com.example.service.ReportService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.MediaType;
import org.springframework.core.io.Resource;
import java.io.IOException;
import java.util.Optional;

@RestController
public class GenerateController {

    @Autowired
    private ReportService reportService;
    @Autowired
    private GenerateService generateService;
    @Autowired
    private PredefenseRepository predefenseRepository;
    @Autowired
    private DefenseRepository defenseRepository;

    @GetMapping("/controller")
    public ResponseEntity<String> foo() {
        String content = "Hello, World!";
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.setContentType(MediaType.TEXT_HTML);

        return new ResponseEntity<>(content, responseHeaders, HttpStatus.OK);
    }

    @PostMapping(path = "/generate-example", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Resource> generateExampleDoc(@RequestBody ApplicationDto dto) throws IOException {

        //InputStream inJson = ApplicationDto.class.getResourceAsStream("C:\\Users\\enazy\\IdeaProjects\\DocFiller_\\src\\main\\resources\\static\\docs\\request.json");
        //ApplicationDto dto = new ObjectMapper().readValue(inJson, ApplicationDto.class);
        final JsonForPrintForm jsonForPrintForm = generateService.getJsonForExamplePrintForm(dto,
                dto.getOrganization(),
                dto.getServiceName(),
                dto.getServiceTargetName());
        final ObjectMapper m = new ObjectMapper();
        final String json = m.writeValueAsString(jsonForPrintForm);

        return generateService.getPreview(Long.parseLong(dto.getNumber()), 3L, json);
    }

    @PostMapping(path = "/generate-predefense", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Resource> generatePredefenseDoc(@RequestBody GenerateDto dto) throws IOException {

        final JsonForPrintForm jsonForPrintForm = generateService.getJsonForDocPrintForm(dto);
        final ObjectMapper m = new ObjectMapper();
        final String json = m.writeValueAsString(jsonForPrintForm);

        return generateService.getPreview(Long.parseLong(dto.getPerformId()), 1L, json);
    }

    @PostMapping(path = "/generate-predefense/{id}")
    public ResponseEntity<Resource> generatePredefenseDoc(@PathVariable(value = "id") String id) throws IOException {
        GenerateDto dto = new GenerateDto();
        String json = null;

        Optional<Predefense> optionalPredefense = predefenseRepository.findById(Long.parseLong(id));
        if (optionalPredefense.isPresent()) {
            dto = generateService.fillPredefense(optionalPredefense.get());


            final JsonForPrintForm jsonForPrintForm = generateService.getJsonForDocPrintForm(dto);
            final ObjectMapper m = new ObjectMapper();
            json = m.writeValueAsString(jsonForPrintForm);
        }
        return generateService.getPreview(Long.parseLong(dto.getPerformId()), 1L, json);
    }

    @PostMapping(path = "/generate-defense/{id}")
    public ResponseEntity<Resource> generateDefenseDoc(@PathVariable(value = "id") String id) throws IOException {
        GenerateDto dto = new GenerateDto();
        String json = null;

        Optional<Defense> optionalDefense = defenseRepository.findById(Long.parseLong(id));
        if (optionalDefense.isPresent()) {
            dto = generateService.fillDefense(optionalDefense.get());

            final JsonForPrintForm jsonForPrintForm = generateService.getJsonForDocPrintForm(dto);
            final ObjectMapper m = new ObjectMapper();
            json = m.writeValueAsString(jsonForPrintForm);
        }
        return generateService.getPreview(Long.parseLong(dto.getPerformId()), 2L, json);
    }
}
