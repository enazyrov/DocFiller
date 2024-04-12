package com.example.dto.docs.example;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ApiModel(description = "Значения справочников для мультивыбора")
public class DictElementValueDto {

    @ApiModelProperty(notes = "Значение справочника")
    private String value;

    @ApiModelProperty(notes = "Текстовое значения справочника")
    private String text;

}
