package com.example.service;

import com.example.dao.CommissionMemberRepository;
import com.example.model.CommissionMember;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class CommissionMemberService {

    @Autowired
    private CommissionMemberRepository commissionMemberRepository;

    public Optional<CommissionMember> find(Integer id) {
        return Optional.of(commissionMemberRepository.getById(id));
    }
    public void save(CommissionMember commissionMember) {
        commissionMemberRepository.save(commissionMember);
    }

    public List<CommissionMember> getAll() {
        return StreamSupport
                .stream(Spliterators.spliteratorUnknownSize(commissionMemberRepository.findAll().iterator(), Spliterator.NONNULL),
                        false)
                .collect(Collectors.toList());
    }

    public void deleteById(Integer id) {
        commissionMemberRepository.deleteById(id);
    }
}
