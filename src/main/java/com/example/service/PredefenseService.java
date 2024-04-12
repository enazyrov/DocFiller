package com.example.service;

import com.example.dao.PredefenseRepository;
import com.example.model.Predefense;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class PredefenseService {

    @Autowired
    private PredefenseRepository predefenseRepository;

    public void save(Predefense predefense) {
        predefenseRepository.save(predefense);
    }

    public List<Predefense> getAll() {
        return StreamSupport
                .stream(Spliterators.spliteratorUnknownSize(predefenseRepository.findAll().iterator(), Spliterator.NONNULL),
                        false)
                .collect(Collectors.toList());
    }

    public void deleteById(Integer id) {
        predefenseRepository.deleteById(id);
    }

}
