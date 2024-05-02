package com.example.dao;

import com.example.model.Perform;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PerformRepository extends JpaRepository<Perform, Long> {
}
