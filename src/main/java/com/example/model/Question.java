package com.example.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "question")
public class Question {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column
    private String question;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "defense_id")
    private Defense defense;
}
