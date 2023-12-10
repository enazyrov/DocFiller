package com.example.service;

import com.example.dao.DefenseRepository;
import com.example.model.Defense;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class DefenseService {

    @Autowired
    private DefenseRepository defenseRepository;

    public Optional<Defense> find(Integer id) {
        return Optional.of(defenseRepository.getById(id));
    }
    public void save(Defense defense) {
        defenseRepository.save(defense);
    }

    public List<Defense> getAll() {
        return StreamSupport
                .stream(Spliterators.spliteratorUnknownSize(defenseRepository.findAll().iterator(), Spliterator.NONNULL),
                        false)
                .collect(Collectors.toList());
    }

    public void deleteById(Integer id) {
        defenseRepository.deleteById(id);
    }
}
