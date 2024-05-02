package com.example.service;

import com.example.dao.PerformRepository;
import com.example.model.Perform;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class PerformService {

    @Autowired
    private PerformRepository performRepository;

    public void save(Perform perform) {
        performRepository.save(perform);
    }

    public List<Perform> getAll() {
        return StreamSupport
                .stream(Spliterators.spliteratorUnknownSize(performRepository.findAll().iterator(), Spliterator.NONNULL),
                        false)
                .collect(Collectors.toList());
    }

    public void deleteById(Long id) {
        performRepository.deleteById(id);
    }
}
