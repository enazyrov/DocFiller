package com.example.dao;

import com.example.model.Report;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ReportRepository extends JpaRepository<Report, Long> {
    Optional<Report> findByDealId(Long dealId);

    Optional<Report> findByDealIdAndTypeId(Long dealId, Long typeId);
}
