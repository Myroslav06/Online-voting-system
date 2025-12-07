package com.example.voting.repository;

import com.example.voting.model.Voting;
import java.util.List;
import java.util.Optional;

public interface VotingDao {
    Voting save(Voting voting); // Create
    Optional<Voting> findById(String id); // Read
    List<Voting> findAll(String titleFilter); // Read with Filter
    void update(Voting voting); // Update
    void deleteById(String id); // Delete
    void addCandidate(String votingId, String candidateName); // Helper
}