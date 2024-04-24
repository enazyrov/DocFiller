package com.example.dto.docs.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;


@Getter
@AllArgsConstructor
public enum Evaluations {
    SUFFICIENT( "Достаточная", "достаточную"),
    WELL("Хорошая", "хорошую"),
    QUALITATIVE("Высокая", "высокую");

    private String evaluation;
    private String evaluationAccus;

    public static String findAccusByText(String text) {
        return Arrays.stream(values()).filter(evaluation -> evaluation.getEvaluation().equals(text)).findFirst().get().getEvaluationAccus();
    }
}
