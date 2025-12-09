package com.example.voting.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.voting.model.Voting;
import com.example.voting.service.VotingService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;

@RestController
@RequestMapping("/api/votings")
public class VotingRestController {

    private final VotingService service;

    public VotingRestController(VotingService service) {
        this.service = service;
    }

    @GetMapping
    public List<Voting> getAll(@RequestParam(required = false) String titleFilter) {
        return service.getAllVotings(titleFilter);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Voting> get(@PathVariable String id) {
        return service.getVoting(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Створити голосування (з тестом транзакції)")
    @PostMapping
    public ResponseEntity<Voting> create(
            @RequestBody Voting voting,
            @Parameter(description = "Симулювати помилку для перевірки Rollback")
            @RequestParam(defaultValue = "false") boolean testRollback) {

        try {
            return new ResponseEntity<>(
                    service.createVotingWithCandidates(voting, testRollback),
                    HttpStatus.CREATED
            );
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Voting> update(@PathVariable String id, @RequestBody Voting voting) {
        return service.updateVoting(id, voting)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Voting> patch(@PathVariable String id, @RequestBody Map<String, Object> fields) {
        return service.patchVoting(id, fields)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        if (service.deleteVoting(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}