package com.example.voting.service;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ReflectionUtils;

import com.example.voting.model.Voting;
import com.example.voting.repository.VotingDao;

@Service
public class VotingService {

    private final VotingDao votingDao;

    public VotingService(VotingDao votingDao) {
        this.votingDao = votingDao;
    }

    // --- CREATE ---
    public Voting createVoting(Voting voting) {
        // Зберігаємо голосування
        Voting saved = votingDao.save(voting);
        // Зберігаємо кандидатів, якщо вони є
        if (voting.getCandidates() != null) {
            for (var candidate : voting.getCandidates()) {
                votingDao.addCandidate(saved.getId(), candidate.getName());
            }
        }
        return saved;
    }
    

    public void createVoting(String title, String owner, String candidateNames) {
        Voting v = new Voting();
        v.setTitle(title);
        v.setOwnerName(owner);
        Voting saved = votingDao.save(v);
        
        if (candidateNames != null && !candidateNames.isEmpty()) {
            for (String name : candidateNames.split(",")) {
                votingDao.addCandidate(saved.getId(), name.trim());
            }
        }
    }

    public List<Voting> getAllVotings(String titleFilter, int page, int size) {
        return votingDao.findAll(titleFilter);
    }
    
    public List<Voting> getAllVotings() {
        return votingDao.findAll(null);
    }

    public Optional<Voting> getVoting(String id) {
        return votingDao.findById(id);
    }


    public Optional<Voting> updateVoting(String id, Voting newVotingData) {
        Optional<Voting> existing = votingDao.findById(id);
        if (existing.isPresent()) {
            Voting voting = existing.get();
            voting.setTitle(newVotingData.getTitle());
            voting.setOwnerName(newVotingData.getOwnerName());
            voting.setActive(newVotingData.isActive());
            votingDao.update(voting); // Оновлюємо в БД
            return Optional.of(voting);
        }
        return Optional.empty();
    }


    public Optional<Voting> patchVoting(String id, Map<String, Object> fields) {
        Optional<Voting> existing = votingDao.findById(id);
        if (existing.isPresent()) {
            Voting voting = existing.get();
            fields.forEach((key, value) -> {
                Field field = ReflectionUtils.findField(Voting.class, key);
                if (field != null) {
                    field.setAccessible(true);
                    ReflectionUtils.setField(field, voting, value);
                }
            });
            votingDao.update(voting); // Зберігаємо зміни в БД
            return Optional.of(voting);
        }
        return Optional.empty();
    }

    // --- DELETE ---
    public boolean deleteVoting(String id) {
        Optional<Voting> v = votingDao.findById(id);
        if (v.isPresent()) {
            votingDao.deleteById(id);
            return true;
        }
        return false;
    }


    @Transactional
    public void vote(String votingId, String candidateName) {

    }
    

    public void toggleStatus(String votingId) {
        votingDao.findById(votingId).ifPresent(voting -> {
            voting.setActive(!voting.isActive());
            votingDao.update(voting); // Оновлюємо статус в БД
        });
    }


    @Transactional
    public Voting createVotingWithCandidates(Voting voting, boolean fail) {
        Voting saved = createVoting(voting);
        if (fail) {
            throw new RuntimeException("Test Rollback Exception");
        }
        return saved;
    }
}