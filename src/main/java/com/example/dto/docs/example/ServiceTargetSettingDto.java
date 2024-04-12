package com.example.dto.docs.example;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.OffsetDateTime;

@ApiModel(description = "Настройки цели электронной услуги")
@Data
public class ServiceTargetSettingDto {

    @ApiModelProperty(notes = "Электронная форма заявления")
    private FormDto electronicForm;
    @ApiModelProperty(notes = "Код печатной формы заявления")
    private PrintFormShortDto printForm;
    @ApiModelProperty(notes = "Разрешено создание черновиков заявлений")
    private boolean draftAllowed;
    @ApiModelProperty(notes = "Тип учётной записи ЕСИА")
    private String esiaTypeId;
    @ApiModelProperty(notes = "дата и время, с которой доступна цель")
    private OffsetDateTime enableDateTimeFrom;
    @ApiModelProperty(notes = "дата и время, по которую доступна цель")
    private OffsetDateTime enableDateTimeTo;
    @ApiModelProperty(notes = "текст, который показывать пока цель недоступна")
    private String disableInfo;
}
