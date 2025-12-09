package com.example.voting.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.voting.model.Candidate;
import com.example.voting.model.Voting;
import com.example.voting.service.VotingService;

@Controller
public class VotingController {

    private final VotingService votingService;

    public VotingController(VotingService votingService) {
        this.votingService = votingService;
    }

    @GetMapping("/")
    public String index(Model model, @RequestParam(required = false) String titleFilter) {
        model.addAttribute("votings", votingService.getAllVotings(titleFilter));
        return "index";
    }

    @GetMapping("/voting")
    public String voting(@RequestParam String id, Model model) {
        votingService.getVoting(id).ifPresent(v -> model.addAttribute("voting", v));
        return "voting";
    }

    @PostMapping("/create")
    public String create(@RequestParam String title, 
                         @RequestParam String ownerName, 
                         @RequestParam String candidates) {
        Voting voting = new Voting();
        voting.setTitle(title);
        voting.setOwnerName(ownerName);
        voting.setActive(true);

        if (candidates != null && !candidates.isEmpty()) {
            for (String name : candidates.split(",")) {
                voting.addCandidate(new Candidate(name.trim()));
            }
        }

        votingService.createVoting(voting);
        return "redirect:/";
    }

    @PostMapping("/vote")
    public String vote(@RequestParam String votingId) {
        return "redirect:/voting?id=" + votingId;
    }
    
    @PostMapping("/toggle")
    public String toggle(@RequestParam String id) {
        votingService.toggleStatus(id);
        return "redirect:/";
    }
}