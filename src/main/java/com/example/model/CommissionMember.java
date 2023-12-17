package com.example.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.HashSet;
import java.util.Set;

@Data
@Entity
@Table(name = "commission_member")
public class CommissionMember {

    @Id
    @SequenceGenerator(name = "commission_member_seq_gen", sequenceName = "commission_member_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "commission_member_seq_gen")
    private Integer id;

    @Column
    private Boolean state;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "commission_member_predefense",
            joinColumns = {@JoinColumn(name = "commission_member_id")},
            inverseJoinColumns = {@JoinColumn(name = "predefense_id")}
    )
    Set<Predefense> predefenses = new HashSet<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "commission_member_defense",
            joinColumns = {@JoinColumn(name = "commission_member_id")},
            inverseJoinColumns = {@JoinColumn(name = "defense_id")}
    )
    Set<Defense> defenses = new HashSet<>();
    @Column
    private String fio;

    @Column
    private String shortFio;

    @Column
    private String degree;

    @Column
    private String rank;
}
