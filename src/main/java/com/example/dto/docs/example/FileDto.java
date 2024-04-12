package com.example.dto.docs.example;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

@ApiModel(description = "Данные сохраненного файла")
@Data
@ToString
public class FileDto {

    @ApiModelProperty(notes = "Тип файла")
    private String type;

    @ApiModelProperty(notes = "Имя файла")
    private String name;

    @ApiModelProperty(notes = "Размер файла")
    private String size;

    @ApiModelProperty(notes = "Uuid файла подписи")
    private String signUuid;

    @ApiModelProperty(notes = "Uuid файла")
    private String uuid;

    @ApiModelProperty(notes = "MD5-сумма")
    private String md5FileInfo;
}
