package com.example.dto.docs.example;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@ApiModel(description = "Настройки Аттрибутов элемента формы")
public class ElementDataDto {

    @ApiModelProperty(notes = "Ширина")
    private int width;

    @ApiModelProperty(notes = "Положение по оси X")
    private int xPos;

    @ApiModelProperty(notes = "Подсказка")
    private String note;

    @ApiModelProperty(notes = "Код справочника")
    private String dictionaryCode;

    @ApiModelProperty(notes = "Код родительского элемента (для зависимых справочников)")
    private String parentElementCode;

    @ApiModelProperty(notes = "Значения справочника")
    private List<DictValueDto> dictionaryValues;

    @ApiModelProperty(notes = "Обязательность")
    private boolean required;

    @ApiModelProperty(notes = "Видимость")
    private boolean visible;

    @ApiModelProperty(notes = "Только чтение")
    private boolean readOnly;

    @ApiModelProperty(notes = "Минимальное значение")
    private String minValue;

    @ApiModelProperty(notes = "Максимальное значение")
    private String maxValue;

    @ApiModelProperty(notes = "Минимальная длина")
    private String minLength;

    @ApiModelProperty(notes = "Максимальная длина")
    private String maxLength;

    @ApiModelProperty(notes = "Множественность")
    private boolean multiple;

    @ApiModelProperty(notes = "Маска")
    private String mask;

    @ApiModelProperty(notes = "Адрес ссылки")
    private String url;

    @ApiModelProperty(notes = "Расширения")
    private String extensions;

    @ApiModelProperty(notes = "Является условием отображения")
    private boolean condition;

    @ApiModelProperty(notes = "Условие для обязательности")
    private String requiredCondition;

    @ApiModelProperty(notes = "Условие для режима \"только чтение\"")
    private String readOnlyCondition;

    @ApiModelProperty(notes = "Значение по умолчанию")
    private String defaultValue;

    @ApiModelProperty(notes = "Обязательно отображать")
    private boolean showRequired;

    @Deprecated
    @ApiModelProperty(notes = "Обязательно подписывать")
    private boolean necessarilySigning;

    @ApiModelProperty(notes = "Не изменяемое")
    private boolean nonEditable;

    @ApiModelProperty(notes = "Минимальное число повторений")
    private Integer repeatMinValue;

    @ApiModelProperty(notes = "Максимальное число повторений")
    private Integer repeatMaxValue;

    @ApiModelProperty(notes = "Тип ЛК - ФЛ")
    private boolean physicalPerson;

    @ApiModelProperty(notes = "Тип ЛК - ЮЛ")
    private boolean employee;

    @ApiModelProperty(notes = "Тип ЛК - ИП")
    private boolean businessman;
}
