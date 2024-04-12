package com.example.dto.docs.example;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.util.HashMap;
import java.util.List;

@ApiModel(description = "Класс для нового заявления")
@Data
@ToString
public class ApplicationDto {

    @ApiModelProperty(notes = "Код услуги")
    private String serviceCode;
    @ApiModelProperty(notes = "Наименование услуги")
    private String serviceName;
    @ApiModelProperty(notes = "Номер цели услуги в конструкторе")
    private Long serviceTargetNumber;
    @ApiModelProperty(notes = "Тип ЛК заявителя")
    private String personalAccount;
    @ApiModelProperty(notes = "Код цели")
    private String serviceTargetCode;
    @ApiModelProperty(notes = "Наименование цели")
    private String serviceTargetName;
    @ApiModelProperty(notes = "Версия электронной услуги")
    private String serviceTargetVersion;
    @ApiModelProperty(notes = "Настройки электронной цели")
    private ServiceTargetSettingDto settings;
    @ApiModelProperty(notes = "Номер заявления")
    private String number;
    @ApiModelProperty(notes = "Дата/время создания заявления в формате ДД.ММ.ГГГГ ЧЧ:ММ:СС")
    private String createDateTime;
    @ApiModelProperty(notes = "Тип формы: 1 - запрос, 2 - ответ")
    private Integer formType = 1;
    @ApiModelProperty(notes = "Номер дела для сохранения ответа")
    private String dealNum;
    @ApiModelProperty(notes = "Список мест оказания услуги")
    private List<OrganizationDto> organizations;
    @ApiModelProperty(notes = "Выбранное место оказания услуги")
    private OrganizationDto organization;
    @ApiModelProperty(notes = "это возможность получения услуги без подписи файлов явкой в ведомство")
    private Boolean without_sign;
    @ApiModelProperty(notes = "это возможность получения услуги с подписью файлов без явки в ведомство")
    private Boolean with_sign;
    @Deprecated
    @ApiModelProperty(notes = "Данные заявления в виде пар \"код элемента\":\"значение\" (старый формат)")
    private HashMap<String, String> appData;
    @ApiModelProperty(notes = "Данные заявления в виде сформированных данных, с поддержкой мультиблоков")
    private List<ElementValueDto> values;
}
