package com.example.dto.docs;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

@ApiModel(description = "Класс для нового протокола предзащиты")
@Data
@ToString
public class CommissionMemberDto {
    @ApiModelProperty("Номер сведений о защите")
    private Boolean state;
    @ApiModelProperty("ФИО")
    private String fio;
    @ApiModelProperty("Учебная степень")
    private String degree;
    @ApiModelProperty("Учебное звание")
    private String rank;
    @ApiModelProperty("Должность")
    private String position;
    @ApiModelProperty("Подразделение")
    private String subunit;
}
