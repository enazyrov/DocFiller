package com.example.dto.docs.example;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel(description = "Печатная форма заявления")
@Data
public class PrintFormShortDto {

    @ApiModelProperty(notes = "Код печатной формы заявления")
    String code;
}