package com.example.dto.docs.example;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@ApiModel(description = "Значения формы")
@Data
@ToString
public class ElementValueDto {

    @ApiModelProperty(notes = "Код элемента")
    private String code;

    @ApiModelProperty(notes = "Код элемента")
    private String reportCode;

    @ApiModelProperty(notes = "Значение элемента")
    private String value;

    @ApiModelProperty(notes = "Текстовое значения справочника")
    private String text;

    @ApiModelProperty(notes = "Тип файла")
    private String fileType;

    @ApiModelProperty(notes = "Имя файла")
    private String fileName;

    @ApiModelProperty(notes = "Размер файла")
    private String fileSize;

    @ApiModelProperty(notes = "Uuid файла подписи")
    private String signUuid;

    @ApiModelProperty(notes = "MD5-сумма")
    private String md5FileInfo;

    @ApiModelProperty(notes = "Данные файлов")
    private List<FileDto> files;

    @ApiModelProperty(notes = "Для повторяющихся элементов, номер при повторении")
    private Integer repeat;

    @ApiModelProperty(notes = "Значения дочерних элементов (для блоков)")
    private List<ElementValueDto> elementValues;

    @ApiModelProperty(notes = "Значения справочников для мультивыбора")
    private List<DictElementValueDto> dictValues;

    public void addFileDto(FileDto fileDto) {
        if (files == null) {
            files = new ArrayList<>();
        }
        files.add(fileDto);
    }
}

