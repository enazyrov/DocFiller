package com.example.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.OffsetDateTime;

@Data
@Entity
@Table(name = "report")
public class Report {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long dealId;

    private Long typeId;

    private String fileName;

    private String uuid;

    private OffsetDateTime createDate;

    private Long size;

}