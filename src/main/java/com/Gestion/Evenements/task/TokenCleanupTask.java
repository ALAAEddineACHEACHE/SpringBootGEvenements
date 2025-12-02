package com.Gestion.Evenements.task;

import com.Gestion.Evenements.repo.ExpiredTokenRepo;
import lombok.RequiredArgsConstructor;
//import org.example.springsecuritydemo.repo.ExpiredTokenRepo;
//import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Component
@RequiredArgsConstructor
public class TokenCleanupTask {
    private final ExpiredTokenRepo expiredTokenRepo;

//    private final ExpiredTokenRepo expiredTokenRepo;

    // Run every day at 2 AM to clean up tokens older than 30 days
    @Scheduled(cron = "0 0 2 * * ?")
    @Transactional
    public void cleanupExpiredTokens() {
        Date thirtyDaysAgo = new Date(System.currentTimeMillis() - (30L * 24 * 60 * 60 * 1000));
      expiredTokenRepo.deleteByAddedAtBefore(thirtyDaysAgo);
   }
}