package com.example.service;

import com.example.model.Review;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.dao.ReviewRepository;

import java.util.List;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class ReviewService {

    @Autowired
    private ReviewRepository repository;

    public void save(Review review) {
        repository.save(review);
    }

    public List<Review> getAll() {
        return StreamSupport
                .stream(Spliterators.spliteratorUnknownSize(repository.findAll().iterator(), Spliterator.NONNULL),
                        false)
                .collect(Collectors.toList());
    }

    public void deleteById(Integer id) {
        repository.deleteById(id);
    }
}
