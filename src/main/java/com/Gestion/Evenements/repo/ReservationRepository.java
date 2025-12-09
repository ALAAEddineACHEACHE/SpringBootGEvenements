package com.Gestion.Evenements.repo;
import com.Gestion.Evenements.models.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    List<Reservation> findByUserIdAndEventId(Long userId, Long eventId);
    List<Reservation> findByUserId(Long userId);
    //int countByUserIdAndEventId(Long userId, Long eventId);
    Optional<Reservation> findById(Long userId);

}
