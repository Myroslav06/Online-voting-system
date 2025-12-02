package com.example.voting.service;

import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.voting.component.ActionLogger;
import com.example.voting.model.Candidate;
import com.example.voting.model.Voting;
import com.example.voting.repository.VotingRepository;

@Service
public class VotingService {

    private final VotingRepository repository;
    private String appName;
    
    // Ін'єкція Prototype біна через ObjectFactory (щоб отримувати нові інстанси)
    @Autowired
    private ObjectFactory<ActionLogger> loggerFactory;

    // 1. Ін'єкція через конструктор (Рекомендований спосіб) 
    @Autowired 
    public VotingService(VotingRepository repository) {
        this.repository = repository;
    }

    // 2. Ін'єкція через сетер 
    @Autowired
    public void setAppName(String applicationName) { // береться з AppConfig
        this.appName = applicationName;
    }

    // 3. Ін'єкція напряму у поле (Не рекомендовано, але вимагається в завданні) 
    // Використано вище для loggerFactory

    public void createVoting(String title, String owner, String candidateNames) {
        Voting voting = new Voting();
        voting.setTitle(title);
        voting.setOwnerName(owner);
        
        for (String name : candidateNames.split(",")) {
            voting.getCandidates().add(new Candidate(name.trim(), 0));
        }
        
        repository.save(voting);
        loggerFactory.getObject().log("Created voting: " + title);
    }

    public void vote(String votingId, String candidateName) {
        repository.findById(votingId).ifPresent(voting -> {
            if (!voting.isActive()) return;
            
            voting.getCandidates().stream()
                    .filter(c -> c.getName().equals(candidateName))
                    .findFirst()
                    .ifPresent(c -> c.setVotes(c.getVotes() + 1));
            
            loggerFactory.getObject().log("Voted for " + candidateName + " in " + voting.getTitle());
        });
    }
    
    public void toggleStatus(String votingId) {
        repository.findById(votingId).ifPresent(voting -> {
            voting.setActive(!voting.isActive());
            loggerFactory.getObject().log("Changed status for " + voting.getTitle());
        });
    }

    public java.util.List<Voting> getAllVotings() {
        return repository.findAll();
    }
    
    public Voting getVoting(String id) {
        return repository.findById(id).orElse(null);
    }
}