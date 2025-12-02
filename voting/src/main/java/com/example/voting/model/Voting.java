package com.example.voting.model;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import lombok.Data;

@Data
public class Voting {
    private String id;
    private String title;
    private String ownerName;
    private boolean isActive;
    private List<Candidate> candidates = new ArrayList<>();

    public Voting() {
        this.id = UUID.randomUUID().toString();
        this.isActive = true;
    }
}