package com.example.dto.docs.file;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class FileUploadData {

    private String fileuuid;

    private Long filesize;

    private String originalfilename;

    private String mimecode;

    private boolean success;
}