package com.example.controller;

import com.example.dto.docs.example.ApplicationDto;
import com.example.dto.docs.example.JsonForPrintForm;
import com.example.service.GenerateService;
import com.example.service.ReportService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.MediaType;
import org.springframework.core.io.Resource;
import java.io.IOException;

@RestController
public class GenerateController {

    @Autowired
    private ReportService reportService;
    @Autowired
    private GenerateService generateService;

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
        final JsonForPrintForm jsonForPrintForm = generateService.getJsonForPrintForm(dto,
                dto.getOrganization(),
                dto.getServiceName(),
                dto.getServiceTargetName());
        final ObjectMapper m = new ObjectMapper();
        final String json = m.writeValueAsString(jsonForPrintForm);

        return generateService.getExamplePreview(Long.parseLong(dto.getNumber()), json);
    }

    /*@PostMapping(path = "/generate-predefense", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Resource> generatePredefenseDoc(@RequestBody ApplicationDto dto) throws IOException {

        final JsonForPrintForm jsonForPrintForm = generateService.getJsonForPrintForm(dto,
                dto.getOrganization(),
                dto.getServiceName(),
                dto.getServiceTargetName());
        final ObjectMapper m = new ObjectMapper();
        final String json = m.writeValueAsString(jsonForPrintForm);

        return generateService.getPredefensePreview(Long.parseLong(dto.getNumber()), json);
    }*/
}
