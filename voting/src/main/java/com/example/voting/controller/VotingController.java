package com.example.voting.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.voting.service.VotingService;

@Controller
public class VotingController {

    private final VotingService service;

    public VotingController(VotingService service) {
        this.service = service;
    }

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("votings", service.getAllVotings());
        return "index";
    }

    @PostMapping("/create")
    public String create(@RequestParam String title, 
                         @RequestParam String owner, 
                         @RequestParam String candidates) {
        service.createVoting(title, owner, candidates);
        return "redirect:/";
    }

    @GetMapping("/voting/{id}")
    public String viewVoting(@PathVariable String id, Model model) {
        model.addAttribute("voting", service.getVoting(id));
        return "voting";
    }

    @PostMapping("/voting/{id}/vote")
    public String vote(@PathVariable String id, @RequestParam String candidate) {
        service.vote(id, candidate);
        return "redirect:/voting/" + id;
    }
    
    @PostMapping("/voting/{id}/toggle")
    public String toggleStatus(@PathVariable String id) {
        service.toggleStatus(id);
        return "redirect:/voting/" + id;
    }
}