package com.example.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "remark")
public class Remark {

    @Id
    @SequenceGenerator(name = "remark_seq_gen", sequenceName = "remark_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "remark_seq_gen")
    private Integer id;

    @Column
    private String note;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "predefense_id")
    private Predefense predefense;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "defense_id")
    private Defense defense;
}
