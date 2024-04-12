package com.example.dto.docs.example;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@ApiModel(description = "Элемент строки электронной формы заявления")
@Getter
@Setter
@ToString
public class ElementDto {

    @ApiModelProperty(notes = "Код типа элемента")
    String elementTypeCode;
    @ApiModelProperty(notes = "Порядковый номер элемента в строке")
    int orderNum;
    @ApiModelProperty(notes = "Код элемента")
    String code;
    @ApiModelProperty(notes = "Код ПФ элемента")
    String reportCode;

    @ApiModelProperty(notes = "Наименование элемента")
    String name;
    @ApiModelProperty(notes = "Данные элементов")
    ElementDataDto elementData;
    @ApiModelProperty(notes = "Данные блока")
    BlockDataDto blockData;

    @ApiModelProperty(notes = "Дочерние элементы")
    List<ElementDto> elements;

    @ApiModelProperty(notes = "Маска ввода")
    private String mask;
    @ApiModelProperty(notes = "Регулярное выражение")
    private String regexp;
    @ApiModelProperty(notes = "Функция валидации")
    private String validationFunction;
    @ApiModelProperty(notes = "Стандартный тип")
    private String standardType;
    @ApiModelProperty(notes = "Минимальное значение даты")
    private String minDateValue;
    @ApiModelProperty(notes = "Максимальное значение даты")
    private String maxDateValue;
    @ApiModelProperty(notes = "Меньше чем")
    private String lessDateValue;
    @ApiModelProperty(notes = "Больше чем")
    private String moreDateValue;
    @ApiModelProperty(notes = "id элемента для получения значения даты")
    private Long elementId;
}