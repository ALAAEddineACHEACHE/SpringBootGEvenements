package com.Gestion.Evenements.repo;
import com.Gestion.Evenements.models.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    List<Reservation> findByUserIdAndEventId(Long userId, Long eventId);
    int countByUserIdAndEventId(Long userId, Long eventId);
}
