package com.example.model;

import jakarta.persistence.*;
import lombok.Data;
import java.util.Date;

@Data
@Entity
@Table(name = "print_form")
public class PrintForm {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String fileName;

    private String name;

    private String uuid;

    private Date createDate;

    private Long size;

    private Long typeId;
}
