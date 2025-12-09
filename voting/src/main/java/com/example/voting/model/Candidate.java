package com.example.voting.model;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.Entity; 
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity 
@Table(name = "candidates")
public class Candidate {

    @Id 
    @GeneratedValue(strategy = GenerationType.UUID) 
    private String id;

    private String name;
    private int votes;


    @ManyToOne
    @JoinColumn(name = "voting_id") 
    @JsonBackReference 
    private Voting voting;

    public Candidate() {
    }

    public Candidate(String name) {
        this.name = name;
        this.votes = 0;
    }



    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getVotes() {
        return votes;
    }

    public void setVotes(int votes) {
        this.votes = votes;
    }

    public Voting getVoting() {
        return voting;
    }

    public void setVoting(Voting voting) {
        this.voting = voting;
    }
}