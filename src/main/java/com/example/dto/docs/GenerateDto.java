package com.example.dto.docs;

import com.example.dto.docs.example.ElementValueDto;
import com.example.dto.docs.example.ServiceTargetSettingDto;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.util.List;

@ApiModel(description = "Класс для автоматического заполнения протокола предзащиты")
@Data
@ToString
public class GenerateDto {
    @ApiModelProperty(notes = "Номер сведений о защите")
    private String performId;
    @ApiModelProperty(notes = "Настройки печатной формы")
    private ServiceTargetSettingDto settings;
    @ApiModelProperty(notes = "Данные заявления в виде сформированных данных, с поддержкой мультиблоков")
    private List<ElementValueDto> values;
}
