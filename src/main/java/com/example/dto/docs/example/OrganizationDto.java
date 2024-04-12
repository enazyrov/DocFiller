package com.example.dto.docs.example;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(description = "Организация")
public class OrganizationDto {

    @ApiModelProperty(notes = "Идентификатор")
    private Long id;

    @ApiModelProperty(notes = "Имя")
    private String name;

    @ApiModelProperty(notes = "Район")
    private LocationDto location;
}
