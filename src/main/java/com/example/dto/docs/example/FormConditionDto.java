package com.example.dto.docs.example;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel(description = "Условия электронной формы заявления")
@Data
public class FormConditionDto {

    @ApiModelProperty(notes = "Код элемента")
    private String elementCode;
    @ApiModelProperty(notes = "Порядковый номер условия для элемента")
    private int orderNum;
    @ApiModelProperty(notes = "Код элемента, значение которого является условием")
    private String conditionElementCode;
    @ApiModelProperty(notes = "Оператор сравнения для значения")
    private String comparisonOperator;
    @ApiModelProperty(notes = "Значение, которое является условием")
    private String conditionElementValue;
    @ApiModelProperty(notes = "Логический оператор для следующего условия")
    private String logicalOperator;
}
