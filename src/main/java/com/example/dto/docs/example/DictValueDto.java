package com.example.dto.docs.example;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel(description = "Значения справочника")
@Data
public class DictValueDto {

    @ApiModelProperty(notes = "Порядковый номер")
    int orderNum;
    @ApiModelProperty(notes = "Код родительского значения")
    String parentCode;
    @ApiModelProperty(notes = "Код значения")
    String code;
    @ApiModelProperty(notes = "Наименование")
    String name;
    @ApiModelProperty(notes = "Признак отказного значения")
    boolean isNegative;
    @ApiModelProperty(notes = "Комментарий к отказу")
    String negativeComment;
}
