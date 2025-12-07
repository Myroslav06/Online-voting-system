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
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/votings")
@Tag(name = "Voting API", description = "Управління голосуваннями")
public class VotingRestController {

    private final VotingService service;

    public VotingRestController(VotingService service) {
        this.service = service;
    }

    @Operation(summary = "Отримати список голосувань")
    @GetMapping
    public ResponseEntity<List<Voting>> getAll(
            @Parameter(description = "Фільтр за назвою") @RequestParam(required = false) String title,
            @Parameter(description = "Сторінка (0..N)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Розмір сторінки") @RequestParam(defaultValue = "10") int size) {
        return new ResponseEntity<>(service.getAllVotings(title, page, size), HttpStatus.OK);
    }

    @Operation(summary = "Отримати голосування за ID")
    @ApiResponse(responseCode = "200", description = "Знайдено")
    @ApiResponse(responseCode = "404", description = "Не знайдено")
    @GetMapping("/{id}")
    public ResponseEntity<Voting> getOne(@PathVariable String id) {
        return service.getVoting(id)
                .map(voting -> new ResponseEntity<>(voting, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @Operation(summary = "Створити голосування")
    @PostMapping
    public ResponseEntity<Voting> create(
            @RequestBody Voting voting,
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

    @Operation(summary = "Повне оновлення (PUT)")
    @PutMapping("/{id}")
    public ResponseEntity<Voting> update(@PathVariable String id, @RequestBody Voting voting) {
        return service.updateVoting(id, voting)
                .map(updated -> new ResponseEntity<>(updated, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @Operation(summary = "Часткове оновлення (PATCH)")
    @PatchMapping("/{id}")
    public ResponseEntity<Voting> patch(@PathVariable String id, @RequestBody Map<String, Object> fields) {
        return service.patchVoting(id, fields)
                .map(patched -> new ResponseEntity<>(patched, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @Operation(summary = "Видалити голосування")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        if (service.deleteVoting(id)) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}
