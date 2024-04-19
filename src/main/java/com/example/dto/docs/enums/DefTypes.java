package com.example.dto.docs.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum DefTypes {
    BAKALVR("Бакалаврская"),
    MAGISTR("Магистрская");
    private String code;
}
