package com.example.voting.repository;

import com.example.voting.model.Candidate;
import com.example.voting.model.Voting;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository("jdbcTemplateDao")
public class VotingDaoJdbcTemplateImpl implements VotingDao {

    private final JdbcTemplate jdbcTemplate;

    public VotingDaoJdbcTemplateImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // RowMapper для перетворення рядка БД в об'єкт
    private final RowMapper<Voting> votingRowMapper = (rs, rowNum) -> {
        Voting v = new Voting();
        v.setId(rs.getString("id"));
        v.setTitle(rs.getString("title"));
        v.setOwnerName(rs.getString("owner_name"));
        v.setActive(rs.getBoolean("active"));
        return v;
    };

    @Override
    public Voting save(Voting voting) {
        if (voting.getId() == null) {
            voting.setId(UUID.randomUUID().toString());
        }
        String sql = "INSERT INTO votings (id, title, owner_name, active) VALUES (?, ?, ?, ?)";
        jdbcTemplate.update(sql, voting.getId(), voting.getTitle(), voting.getOwnerName(), voting.isActive());
        return voting;
    }

    @Override
    public Optional<Voting> findById(String id) {
        String sql = "SELECT * FROM votings WHERE id = ?";
        List<Voting> result = jdbcTemplate.query(sql, votingRowMapper, id);
        if (result.isEmpty()) return Optional.empty();

        Voting voting = result.get(0);
        // Завантажуємо кандидатів окремим запитом
        String candSql = "SELECT * FROM candidates WHERE voting_id = ?";
        List<Candidate> candidates = jdbcTemplate.query(candSql, (rs, rowNum) -> 
            new Candidate(rs.getString("name"), rs.getInt("votes")), id);
        voting.setCandidates(candidates);
        
        return Optional.of(voting);
    }

    @Override
    public List<Voting> findAll(String titleFilter) {
        String sql = "SELECT * FROM votings";
        if (titleFilter != null) {
            sql += " WHERE LOWER(title) LIKE LOWER(?)";
            return jdbcTemplate.query(sql, votingRowMapper, "%" + titleFilter + "%");
        }
        return jdbcTemplate.query(sql, votingRowMapper);
    }

    @Override
    public void update(Voting voting) {
        String sql = "UPDATE votings SET title = ?, owner_name = ?, active = ? WHERE id = ?";
        jdbcTemplate.update(sql, voting.getTitle(), voting.getOwnerName(), voting.isActive(), voting.getId());
    }

    @Override
    public void deleteById(String id) {
        jdbcTemplate.update("DELETE FROM votings WHERE id = ?", id);
    }

    @Override
    public void addCandidate(String votingId, String candidateName) {
        jdbcTemplate.update("INSERT INTO candidates (name, votes, voting_id) VALUES (?, 0, ?)", candidateName, votingId);
    }
}