package com.example.model;

import jakarta.persistence.*;
import lombok.Data;

import java.sql.Date;
import java.util.List;

@Data
@Entity
@Table(name = "predefense")
public class Predefense {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column
    private String type;

    @Column
    private Date date;

    @Column
    private String mark;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "perform_id", nullable = false)
    private Perform performId;

    @Column(name = "chairman_fio")
    private String chairmanFio;

    @ManyToMany(mappedBy = "predefenses", fetch = FetchType.LAZY)
    private List<CommissionMember> members;

    @OneToMany(mappedBy = "predefense", fetch = FetchType.LAZY)
    private List<Remark> remarks;
}
