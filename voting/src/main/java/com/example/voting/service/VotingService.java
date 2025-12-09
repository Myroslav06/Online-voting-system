package com.example.voting.service;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ReflectionUtils;

import com.example.voting.model.Voting;
import com.example.voting.repository.VotingRepository;

@Service
public class VotingService {

    private final VotingRepository votingRepository;

    public VotingService(VotingRepository votingRepository) {
        this.votingRepository = votingRepository;
    }


    @Transactional 
    public Voting createVoting(Voting voting) {
        if (voting.getCandidates() != null) {
            voting.getCandidates().forEach(candidate -> candidate.setVoting(voting));
        }

        return votingRepository.save(voting);
    }


    public List<Voting> getAllVotings(String titleFilter) {
        if (titleFilter != null && !titleFilter.isEmpty()) {
            return votingRepository.findByTitleContaining(titleFilter);
        }
        return votingRepository.findAll();
    }

    public Optional<Voting> getVoting(String id) {
        return votingRepository.findById(id);
    }

    public Optional<Voting> updateVoting(String id, Voting newVotingData) {
        return votingRepository.findById(id).map(existing -> {
            existing.setTitle(newVotingData.getTitle());
            existing.setOwnerName(newVotingData.getOwnerName());
            existing.setActive(newVotingData.isActive());
            return votingRepository.save(existing);
        });
    }

    public void toggleStatus(String id) {
        votingRepository.findById(id).ifPresent(voting -> {
            voting.setActive(!voting.isActive());
            votingRepository.save(voting);
        });
    }


    public Optional<Voting> patchVoting(String id, Map<String, Object> fields) {
        Optional<Voting> existing = votingRepository.findById(id);
        if (existing.isPresent()) {
            Voting voting = existing.get();
            fields.forEach((key, value) -> {
                Field field = ReflectionUtils.findField(Voting.class, key);
                if (field != null) {
                    field.setAccessible(true);
                    ReflectionUtils.setField(field, voting, value);
                }
            });
            votingRepository.save(voting);
            return Optional.of(voting);
        }
        return Optional.empty();
    }

    public boolean deleteVoting(String id) {
        if (votingRepository.existsById(id)) {
            votingRepository.deleteById(id);
            return true;
        }
        return false;
    }

    @Transactional
    public Voting createVotingWithCandidates(Voting voting, boolean fail) {
        Voting saved = createVoting(voting);
        
        if (fail) {
            throw new RuntimeException("Test Rollback Exception (Simulated)");
        }
        return saved;
    }
}