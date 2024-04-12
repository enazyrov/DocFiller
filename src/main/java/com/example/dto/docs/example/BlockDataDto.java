package com.example.dto.docs.example;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BlockDataDto {


    @ApiModelProperty(notes = "Подсказка")
    String note;
    @ApiModelProperty(notes = "Множественность")
    boolean multiple;

    @ApiModelProperty(notes = "Горизонтальное расположение блока")
    private boolean horizontal;

    @ApiModelProperty(notes = "Обязательно отображать")
    boolean showRequired;

    @ApiModelProperty(notes = "Не изменяемое")
    private boolean nonEditable;

    @ApiModelProperty(notes = "Минимальное количество повторений")
    private Integer repeatMinValue;

    @ApiModelProperty(notes = "Максимальное количество повторений")
    private Integer repeatMaxValue;

    @ApiModelProperty(notes = "Тип ЛК - ФЛ")
    private boolean physicalPerson;

    @ApiModelProperty(notes = "Тип ЛК - ЮЛ")
    private boolean employee;

    @ApiModelProperty(notes = "Тип ЛК - ИП")
    private boolean businessman;
}