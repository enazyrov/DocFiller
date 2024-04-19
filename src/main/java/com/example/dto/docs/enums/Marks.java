package com.example.dto.docs.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Optional;


@Getter
@AllArgsConstructor
public enum Marks {
    ADEQUATELY(3, "Удовлетворительно"),
    WELL(4, "Хорошо"),
    PERFECTLY(5, "Отлично");

    private Integer code;
    private String text;

    public static String findTextByCode(Integer code) {
        return Arrays.stream(values()).filter(mark -> mark.getCode().equals(code)).findFirst().get().getText();
    }
}
