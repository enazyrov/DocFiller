package com.example.dto.docs.example;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@ApiModel(description = "Условия для отклонения заявления")
@Getter
@Setter
public class FormAccessDeniedConditionDto {

    @ApiModelProperty(notes = "Код элемента")
    private String elementCode;

    @ApiModelProperty(notes = "Код справочника условия")
    private String classifierCode;

    @ApiModelProperty(notes = "Значение справочника")
    private String classifierValueCode;

    @ApiModelProperty(notes = "Причина отклонения")
    private String comment;

    @ApiModelProperty(notes = "Порядковый номер условия для элемента")
    private int orderNum;
}
