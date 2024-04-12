package com.example;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PrintFormCodes {
    SERVICE_NAME("Наименование_услуги_ПФ"),
    SERVICE_TARGET_NAME("Наименование_цели_ПФ"),
    ORGANIZATION_LOCATION_NAME("Организация_расположение_наименование_ПФ"),
    ORGANIZATION_NAME("Организация_наименование_ПФ"),
    SERVICE_BLOCK("Блок_информации_об_услуге_ПФ"),
    PERSONAL_ACCOUNT("ЛК");

    private String code;

}
