package com.example.voting.service;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ReflectionUtils;

import com.example.voting.model.Candidate;
import com.example.voting.model.Voting;
import com.example.voting.repository.VotingRepository;

@Service
public class VotingService {

    private final VotingRepository repository;

    @Autowired
    public VotingService(VotingRepository repository) {
        this.repository = repository;
    }

    // --- CRUD: CREATE (для API) ---
    public Voting createVoting(Voting voting) {
        repository.save(voting);
        return voting;
    }

    // --- CREATE (для HTML-форми, старий метод) ---
    public void createVoting(String title, String owner, String candidateNames) {
        Voting voting = new Voting();
        voting.setTitle(title);
        voting.setOwnerName(owner);
        
        // Логіка додавання кандидатів зі стрічки
        if (candidateNames != null && !candidateNames.isEmpty()) {
            for (String name : candidateNames.split(",")) {
                voting.getCandidates().add(new Candidate(name.trim(), 0));
            }
        }
        
        repository.save(voting);
    }

    // --- CRUD: READ (Single) ---
    public Optional<Voting> getVoting(String id) {
        return repository.findById(id);
    }

    // --- CRUD: READ (List with Pagination & Filtering) ---
    public List<Voting> getAllVotings(String titleFilter, int page, int size) {
        return repository.findAll().stream()
                .filter(v -> titleFilter == null || v.getTitle().toLowerCase().contains(titleFilter.toLowerCase()))
                .skip((long) page * size)
                .limit(size)
                .collect(Collectors.toList());
    }

    // --- READ ALL (для HTML, без пагінації) ---
    public List<Voting> getAllVotings() {
        return repository.findAll();
    }

    // --- CRUD: UPDATE (Full) ---
    public Optional<Voting> updateVoting(String id, Voting newVotingData) {
        return repository.findById(id).map(existingVoting -> {
            existingVoting.setTitle(newVotingData.getTitle());
            existingVoting.setOwnerName(newVotingData.getOwnerName());
            existingVoting.setActive(newVotingData.isActive());
            // Кандидатів можна перезаписати, якщо потрібно
            if (newVotingData.getCandidates() != null && !newVotingData.getCandidates().isEmpty()) {
                existingVoting.setCandidates(newVotingData.getCandidates());
            }
            return existingVoting;
        });
    }

    // --- CRUD: PATCH (Partial Update) ---
    public Optional<Voting> patchVoting(String id, Map<String, Object> fields) {
        Optional<Voting> votingOptional = repository.findById(id);

        if (votingOptional.isPresent()) {
            Voting voting = votingOptional.get();
            fields.forEach((key, value) -> {
                Field field = ReflectionUtils.findField(Voting.class, key);
                if (field != null) {
                    field.setAccessible(true);
                    ReflectionUtils.setField(field, voting, value);
                }
            });
            return Optional.of(voting);
        }
        return Optional.empty();
    }

    // --- CRUD: DELETE ---
    public boolean deleteVoting(String id) {
        Optional<Voting> voting = repository.findById(id);
        if (voting.isPresent()) {
            repository.findAll().remove(voting.get());
            return true;
        }
        return false;
    }

    // --- БІЗНЕС-ЛОГІКА: Голосування ---
    public void vote(String votingId, String candidateName) {
        getVoting(votingId).ifPresent(voting -> {
            if (!voting.isActive()) return;
            voting.getCandidates().stream()
                    .filter(c -> c.getName().equals(candidateName))
                    .findFirst()
                    .ifPresent(c -> c.setVotes(c.getVotes() + 1));
        });
    }
    
    // --- БІЗНЕС-ЛОГІКА: Зміна статусу (для кнопок) ---
    public void toggleStatus(String votingId) {
        getVoting(votingId).ifPresent(voting -> {
            voting.setActive(!voting.isActive());
        });
    }
}