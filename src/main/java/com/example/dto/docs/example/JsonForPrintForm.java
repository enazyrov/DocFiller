package com.example.dto.docs.example;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.util.HashMap;
import java.util.Map;

@ApiModel(description = "Json для печатной форма")
@Data
@ToString
public class JsonForPrintForm {

    @ApiModelProperty(notes = "Блоки для печатной формы")
    private Map<String, Map<String, String>> blocks = new HashMap<>();
}
