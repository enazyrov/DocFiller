package com.example.service;

import com.example.dto.docs.GenerateDto;
import com.example.dto.docs.enums.DefTypes;
import com.example.dto.docs.enums.Evaluations;
import com.example.dto.docs.enums.Marks;
import com.example.dto.docs.enums.PrintFormCodes;
import com.example.dto.docs.example.*;
import com.example.model.Defense;
import com.example.model.Predefense;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.*;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

@Service
@Slf4j
public class GenerateService {

    private String PREDEFENSE_CFG_JSON_PATH = "C:\\Users\\enazy\\IdeaProjects\\DocFiller_\\src\\main\\resources\\static\\settings\\predefense.json";

    private String DEFENSE_CFG_JSON_PATH = "C:\\Users\\enazy\\IdeaProjects\\DocFiller_\\src\\main\\resources\\static\\settings\\defense.json";
    private final UnaryOperator<String> validationValue = s -> s != null ? s : "";
    private ObjectMapper objectMapper = new ObjectMapper();
    @Autowired
    private ReportService reportService;


    public JsonForPrintForm getJsonForExamplePrintForm(ApplicationDto applicationDto, OrganizationDto organization, String serviceName, String serviceTargetName) {
        List<ElementDto> elementsDto = new ArrayList<>();
        List<ElementValueDto> allValuesDto = new ArrayList<>();
        List<ElementDtoForJson> elementsDtoForJsons = new ArrayList<>();
        recursElementValueDto(applicationDto.getValues(), allValuesDto);
        recursElementDto(applicationDto.getSettings().getElectronicForm().getElements(), elementsDto);
        setValueElementValueDtoByElementDto(elementsDto, allValuesDto, elementsDtoForJsons);
        JsonForPrintForm jsonForPrintForm = new JsonForPrintForm();
        List<ElementDtoForJson> serviceBlock = createServiceBlock(organization, serviceName, serviceTargetName, applicationDto.getPersonalAccount());
        elementsDtoForJsons.addAll(serviceBlock);
        getJsonForPrintFormFromElementValueDto(elementsDtoForJsons, jsonForPrintForm);
        return jsonForPrintForm;
    }

    public JsonForPrintForm getJsonForDocPrintForm(GenerateDto generateDto) {
        List<ElementDto> elementsDto = new ArrayList<>();
        List<ElementValueDto> allValuesDto = new ArrayList<>();
        List<ElementDtoForJson> elementsDtoForJsons = new ArrayList<>();
        recursElementValueDto(generateDto.getValues(), allValuesDto);
        recursElementDto(generateDto.getSettings().getElectronicForm().getElements(), elementsDto);

        setValueElementValueDtoByElementDto(elementsDto, allValuesDto, elementsDtoForJsons);
        JsonForPrintForm jsonForPrintForm = new JsonForPrintForm();

        /*List<ElementDtoForJson> serviceBlock = createServiceBlock(organization, serviceName, serviceTargetName, applicationDto.getPersonalAccount());
        elementsDtoForJsons.addAll(serviceBlock);*/
        getJsonForPrintFormFromElementValueDto(elementsDtoForJsons, jsonForPrintForm);
        return jsonForPrintForm;
    }

    private List<ElementDtoForJson> createServiceBlock(OrganizationDto organization, String serviceName, String serviceTargetName, String personalAccount) {
        String organizationLocationName = organization != null && organization.getLocation() != null ? organization.getLocation().getName() : null;
        String organizationName = organization != null ? organization.getName() : null;
        ElementDtoForJson orgLocName = new ElementDtoForJson(PrintFormCodes.ORGANIZATION_LOCATION_NAME.getCode(), organizationLocationName, PrintFormCodes.SERVICE_BLOCK.getCode());
        ElementDtoForJson orgName = new ElementDtoForJson(PrintFormCodes.ORGANIZATION_NAME.getCode(), organizationName, PrintFormCodes.SERVICE_BLOCK.getCode());
        ElementDtoForJson servName = new ElementDtoForJson(PrintFormCodes.SERVICE_NAME.getCode(), serviceName, PrintFormCodes.SERVICE_BLOCK.getCode());
        ElementDtoForJson servTargetName = new ElementDtoForJson(PrintFormCodes.SERVICE_TARGET_NAME.getCode(), serviceTargetName, PrintFormCodes.SERVICE_BLOCK.getCode());
        ElementDtoForJson personalAccountType = new ElementDtoForJson(PrintFormCodes.PERSONAL_ACCOUNT.getCode(), personalAccount, PrintFormCodes.SERVICE_BLOCK.getCode());
        return Arrays.asList(orgLocName, orgName, servName, servTargetName, personalAccountType);
    }

    private void recursElementValueDto(List<ElementValueDto> elementValueDtoList, List<ElementValueDto> result) {
        for (ElementValueDto element : elementValueDtoList) {
            if (element.getElementValues() == null || element.getElementValues().isEmpty()) {
                result.add(element);
            } else {
                recursElementValueDto(element.getElementValues(), result);
            }
        }
    }

    private void recursElementDto(List<ElementDto> elementDtoList, List<ElementDto> result) {
        for (ElementDto elementDto : elementDtoList) {
            if (elementDto.getElements() == null || elementDto.getElements().isEmpty()) {
                result.add(elementDto);
            } else {
                recursElementDto(elementDto.getElements(), result);
            }
        }
    }

    private void setValueElementValueDtoByElementDto(
            List<ElementDto> elementDtoList,
            List<ElementValueDto> elements, List<ElementDtoForJson> elementsDtoForJson) {
        String splitValue = "___";
        elements = elements.stream()
                .filter(e -> (StringUtils.isNotEmpty(e.getValue()) || StringUtils.isNotEmpty(e.getText())))
                .collect(Collectors.toList());
        for (ElementDto elementDto : elementDtoList) {
            for (ElementValueDto elementValueDto : elements) {
                final String rCode = extractReportCode(elementValueDto);
                if (elementDto.getElementData() != null) {

                    if (elementDto.getElementData().getDictionaryCode() != null && elementValueDto.getCode().equals(elementDto.getCode())) {
                        String classifierValue = null;
                        /*try {
                            classifierValue = classifierService.getClassifierValue(elementDto.getElementData().getDictionaryCode(),
                                    elementValueDto.getValue()).getName();
                        } catch (Exception e) {
                            classifierValue = null;
                        }*/
                        elementsDtoForJson.add(new ElementDtoForJson(rCode,
                                classifierValue != null ? classifierValue : validationValue.apply(elementValueDto.getText()),
                                elementDto.getCode().split(splitValue)[0]));
                    } else if (elementDto.getElementData().getDictionaryCode() == null && elementValueDto.getCode().equals(elementDto.getCode())) {
                        if (elementValueDto.getFiles() != null && elementValueDto.getFiles().size() > 0) {
                            elementsDtoForJson.add(new ElementDtoForJson(rCode,
                                    validationValue.apply(elementDto.getName()), elementDto.getCode().split(splitValue)[0]));
                        } else {
                            elementsDtoForJson.add(new ElementDtoForJson(rCode,
                                    validationValue.apply(elementValueDto.getValue()), elementDto.getCode().split(splitValue)[0]));
                        }
                    }
                }
            }
        }
    }

    private String extractReportCode(ElementValueDto e) {
        if (!StringUtils.isBlank(e.getReportCode())) {
            return e.getReportCode();
        }
        if (StringUtils.isBlank(e.getCode())) {
            return e.getCode();
        }
        if (!e.getCode().contains("_")) {
            return e.getCode();
        }
        int idx = e.getCode().lastIndexOf('_');
        return e.getCode().substring(idx + 1);
    }

    private void getJsonForPrintFormFromElementValueDto(List<ElementDtoForJson> elements, JsonForPrintForm jsonForPrintForm) {
        Map<String, Map<String, String>> blocks = getBlocks(elements);
        jsonForPrintForm.setBlocks(blocks);
    }

    private Map<String, Map<String, String>> getBlocks(List<ElementDtoForJson> elements) {
        Map<String, Map<String, String>> blocks = new HashMap<>();
        for (ElementDtoForJson element : elements) {

            log.info("element block={} report={} value={}", element.getBlock(), element.getReportCode(), element.getValue());

            /*
             * Пропуск битых элементов, для исправления ошибки:
             * Null key for a Map not allowed in JSON (use a converting NullKeySerializer?)
             * (through reference chain: ru.dd.platform.provisionservice.dto.applications.forms.JsonForPrintForm[\"blocks\"]->java.util.HashMap[\"ServiceInfo\"])"
             */
            if (StringUtils.isBlank(element.getBlock()) || StringUtils.isBlank(element.getReportCode())) {
                log.warn("битый элемент block= {} ReportCode= {} v= {}", element.getBlock(), element.getReportCode(), element.getValue());
                continue;
            }
            final String blockKey = element.getBlock(),
                    codeKey = element.getReportCode(),
                    value = element.getValue();
            final Map<String, String> map = blocks.get(blockKey) == null ? new HashMap<>() : blocks.get(blockKey);
            map.put(codeKey, value);
            blocks.put(blockKey, map);
        }
        return blocks;
    }

    public ResponseEntity<Resource> getPreview(Long number, Long typeId, String jsonString) throws IOException {
        log.debug("getPredefensePreview(): Сохраняем заполненный документ...");
        reportService.upload(
                typeId,
                number + "-preview.pdf",
                number,
                jsonString
        );

        /*log.debug("getApplicationPreview(): Скачиваем и возвращаем пдф...");
        return download(
                number
        );*/

        return ResponseEntity.ok().build();
    }

    public GenerateDto fillPredefense(Predefense predefense) {
        GenerateDto result;
        try {

            File settingsFile = new File(PREDEFENSE_CFG_JSON_PATH);
            result = objectMapper.readValue(settingsFile, GenerateDto.class);

            ElementValueDto mainValue = result.getValues().get(0);

            for (ElementValueDto elementValue : mainValue.getElementValues()) {
                switch (elementValue.getReportCode()) {
                    case ("СтудентКорФиоРод") -> {
                        elementValue.setValue(predefense.getPerform().getShortFioGen());
                    }
                    case ("Тема") -> {
                        elementValue.setValue(predefense.getPerform().getTopic());
                    }
                    case ("ПредседательФио") -> {
                        elementValue.setValue(predefense.getChairmanFio());
                    }
                    case ("СтудентКорФио") -> {
                        elementValue.setValue(predefense.getPerform().getShortFio());
                    }
                    case ("Оценка") -> {
                        elementValue.setValue(Marks.findTextByCode(Integer.valueOf(predefense.getMark())));
                    }
                }
                result.setPerformId(String.valueOf(predefense.getPerform().getId()));
            }
        } catch (IOException e) {
            throw new IllegalArgumentException("Невозможно получить файл конфигурации печатной формы");
        }

        return result;
    }

    public GenerateDto fillDefense(Defense defense) {
        GenerateDto result;
        try {

            File settingsFile = new File(DEFENSE_CFG_JSON_PATH);
            result = objectMapper.readValue(settingsFile, GenerateDto.class);

            ElementValueDto mainValue = result.getValues().get(0);

            for (ElementValueDto elementValue : mainValue.getElementValues()) {
                switch (elementValue.getReportCode()) {
                    case ("Номер") -> {
                        elementValue.setValue(defense.getPerform().getId().toString());
                    }
                    case ("Группа") -> {
                        elementValue.setValue(defense.getPerform().getGroupNumber().toString());
                    }
                    case ("СтудентКорФио") -> {
                        elementValue.setValue(defense.getPerform().getShortFio());
                    }
                    case ("Тема") -> {
                        elementValue.setValue(defense.getPerform().getTopic());
                    }
                    case ("РуководительПротокол") -> {
                        elementValue.setValue(defense.getPerform().getSupervisorFioProtocol());
                    }
                    case ("КолвоСтраниц") -> {
                        elementValue.setValue(defense.getPages().toString());
                    }
                    case ("КолвоСлайдов") -> {
                        elementValue.setValue(defense.getSlides().toString());
                    }
                    case ("ОценкаРук") -> {
                        elementValue.setValue(Marks.findTextByCode(Integer.valueOf(defense.getSupervisorMark())));
                    }
                    case ("КолвоПроцентов") -> {
                        elementValue.setValue(defense.getOriginality().toString());
                    }
                    case ("КолвоМинут") -> {
                        elementValue.setValue(defense.getDuration().toString());
                    }
                    case ("Оценка") -> {
                        elementValue.setValue(Marks.findTextByCode(Integer.valueOf(defense.getMark())));
                    }
                    case ("Степень") -> {
                        elementValue.setValue(Evaluations.findAccusByText(defense.getEvaluation()));
                    }
                    case ("СтудентФио") -> {
                        elementValue.setValue(defense.getPerform().getFullFio());
                    }
                    case ("Квалификация") -> {
                        elementValue.setValue(DefTypes.findQualificationByType(defense.getType()));
                    }
                }
                result.setPerformId(String.valueOf(defense.getPerform().getId()));
            }
        } catch (IOException e) {
            throw new IllegalArgumentException("Невозможно получить файл конфигурации печатной формы");
        }

        return result;
    }
}
