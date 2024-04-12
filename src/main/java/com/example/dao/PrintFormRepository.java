package com.example.dao;

import com.example.model.PrintForm;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PrintFormRepository extends JpaRepository<PrintForm, Long> {
    Optional<PrintForm> findByTypeId(Long typeId);
}
