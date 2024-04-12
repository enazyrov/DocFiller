package com.example.dto.docs.example;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ElementDtoForJson {
    private String reportCode;
    private String value;
    private String block;
}
