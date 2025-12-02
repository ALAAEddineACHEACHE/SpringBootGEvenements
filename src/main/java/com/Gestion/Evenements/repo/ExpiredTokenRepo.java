package com.Gestion.Evenements.repo;

import com.Gestion.Evenements.models.ExpiredToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;

@Repository
public interface ExpiredTokenRepo extends JpaRepository<ExpiredToken, Long> {
    boolean existsByToken(String token);
    void deleteByAddedAtBefore(Date date);
}