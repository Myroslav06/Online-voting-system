package com.example.voting.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.voting.model.Voting;

@Repository
public interface VotingRepository extends JpaRepository<Voting, String> {

    List<Voting> findByTitleContaining(String title);

    @Query("SELECT v FROM Voting v WHERE v.ownerName = :owner")
    List<Voting> findByOwnerNameCustom(@Param("owner") String owner);

}