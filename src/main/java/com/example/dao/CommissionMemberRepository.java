package com.example.dao;

import com.example.model.CommissionMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommissionMemberRepository extends JpaRepository<CommissionMember, Integer> {
}
