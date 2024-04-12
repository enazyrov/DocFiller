package com.example.dto.docs.example;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@ApiModel(description = "Электронная форма заявления")
@Data
public class FormDto {

    @ApiModelProperty(notes = "Наименование электронной формы заявления")
    private String name;

    @ApiModelProperty(notes = "Код электронной формы заявления")
    private String code;
    @ApiModelProperty(notes = "Код ПФ элемента")
    private String reportCode;

    @ApiModelProperty(notes = "Дочерние элементы")
    private List<ElementDto> elements;

    @ApiModelProperty(notes = "Условия отображения для формы")
    private List<FormConditionDto> formConditions;

    @ApiModelProperty(notes = "Условия отказа предоставления услуги/ВС")
    private List<FormAccessDeniedConditionDto> formAccessDeniedConditions;

    @ApiModelProperty(notes = "Атрибуты формы")
    private FormDataDto electronicFormData;
}
