package com.example.dto.docs.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

@Getter
@AllArgsConstructor
public enum DefTypes {
    BAKALVR("Бакалаврская", "бакалавра"),
    MAGISTR("Магистрская", "магистра");
    private String type;
    private String qualification;

    public static String findQualificationByType(String program) {
        return Arrays.stream(values()).filter(type -> type.getType().equals(program)).findFirst().get().getQualification();
    }

}
