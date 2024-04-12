package com.example.dto.docs.example;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(description = "Район оказания услуги")
public class LocationDto {

    @ApiModelProperty(notes = "Идентификатор")
    private Long id;

    @ApiModelProperty(notes = "Имя")
    private String name;

    @ApiModelProperty(notes = "Административный центр")
    private String admCenter;
}
