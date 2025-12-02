package com.example.voting.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;

import org.springframework.stereotype.Repository;

import com.example.voting.model.Voting;

// @Repository - компонент рівня доступу до даних
@Repository
public class VotingRepository {
    // Емуляція бази даних в пам'яті (Stub)
    private final List<Voting> votings = new CopyOnWriteArrayList<>();

    public List<Voting> findAll() {
        return new ArrayList<>(votings);
    }

    public void save(Voting voting) {
        votings.add(voting);
    }

    public Optional<Voting> findById(String id) {
        return votings.stream().filter(v -> v.getId().equals(id)).findFirst();
    }
}