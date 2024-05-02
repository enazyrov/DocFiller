package com.example.dao;

import com.example.model.Predefense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PredefenseRepository extends JpaRepository<Predefense, Long> {
}
