package com.Gestion.Evenements.repo;
import com.Gestion.Evenements.models.Event;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventRepository extends JpaRepository<Event, Long> {
}
