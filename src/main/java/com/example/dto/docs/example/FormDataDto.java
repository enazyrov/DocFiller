package com.example.dto.docs.example;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel(description = "Атрибуты электронной формы заявления")
@Data
public class FormDataDto {

    @ApiModelProperty(notes = "Электронная форма")
    private Long electronicFormId;

    @ApiModelProperty(notes = "Подсказка")
    private String note;

    @ApiModelProperty(notes = "Повторяющийся")
    private boolean multiple;

    @ApiModelProperty(notes = "Горизонтальное расположение блока")
    private boolean horizontal;

    @ApiModelProperty(notes = "Щбязательно отображать")
    private boolean showRequired;

    @ApiModelProperty(notes = "Не изменяемое")
    private boolean nonEditable;

    @ApiModelProperty(notes = "Минимальное количество повторений")
    private Integer repeatMinValue;

    @ApiModelProperty(notes = "Максимальное количество повторений")
    private Integer repeatMaxValue;
}

