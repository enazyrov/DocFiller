package com.example.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "question")
public class Question {

    @Id
    @SequenceGenerator(name = "question_seq_gen", sequenceName = "question_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "question_seq_gen")
    private Integer id;

    @Column
    private String question;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "defense_id")
    private Defense defense;
}
