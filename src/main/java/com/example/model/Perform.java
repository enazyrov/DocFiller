package com.example.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "perform")
public class Perform {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column
    private String type;

    @Column(name = "supervisor_fio")
    private String supervisorFio;
    @Column(name = "supervisor_fio_protocol")
    private String supervisorFioProtocol;

    @Column(name = "supervisor_fio_report")
    private String supervisorFioReport;

    @Column(name = "adv_fio_protocol")
    private String advisorFioProtocol;

    @Column(name = "full_fio")
    private String fullFio;

    @Column(name = "short_fio")
    private String shortFio;

    @Column(name = "short_fio_gen")
    private String shortFioGen;

    @Column(name = "short_fio_protocol")
    private String shortFioProtocol;

    @Column
    private String topic;

    @Column(name = "group_number")
    private Integer groupNumber;

    @OneToOne(mappedBy = "performId", cascade = CascadeType.REMOVE)
    private Defense defense;

    @OneToOne(mappedBy = "performId", cascade = CascadeType.REMOVE)
    private Predefense predefense;
}
