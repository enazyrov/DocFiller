package com.example.model;

import jakarta.persistence.*;
import lombok.Data;

import java.sql.Date;
import java.sql.Time;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@Entity
@Table(name = "defense")
public class Defense {

    @Id
    @SequenceGenerator(name = "defense_seq_gen", sequenceName = "defense_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "defense_seq_gen")
    private Integer id;

    @Column(name = "protocol_number")
    private Integer protocolNumber;

    @Column
    private String type;

    @Column
    private Date date;

    @Column(name = "begin_time")
    private Time beginTime;

    @Column(name = "end_time")
    private Time endTime;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "perform_id", nullable = false)
    private Perform performId;

    @Column(name = "chairman_fio")
    private String chairmanFio;

    @Column(name = "short_chairman_fio")
    private String shortChairmanFio;

    @ManyToMany(mappedBy = "defenses", fetch = FetchType.LAZY)
    private List<CommissionMember> members;

    @Column(name = "secretary_fio")
    private String secretaryFio;

    @Column(name = "short_secretary_fio")
    private String shortSecretaryFio;

    @Column
    private Integer pages;

    @Column
    private Integer slides;

    @Column(name = "supervisor_mark")
    private String supervisorMark;

    @Column(name = "reviewer_fio")
    private String reviewerFio;

    @Column(name = "reviewer_mark")
    private String reviewerMark;

    @Column
    private Integer originality;

    @Column(name = "other_documents")
    private String otherDocuments;

    @Column
    private Integer duration;

    @OneToMany(mappedBy = "defense", fetch = FetchType.LAZY)
    private List<Question> questions;

    @Column
    private String mark;

    @Column
    private String evaluation;

    @Column(name = "direction_number")
    private String directionNumber;

    @OneToMany(mappedBy = "defense", fetch = FetchType.LAZY)
    private List<Remark> remarks;
}
