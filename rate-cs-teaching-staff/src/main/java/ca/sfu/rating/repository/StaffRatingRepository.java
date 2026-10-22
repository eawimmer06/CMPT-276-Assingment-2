package ca.sfu.rating.repository;

import ca.sfu.rating.model.StaffRating;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StaffRatingRepository extends JpaRepository<StaffRating, Long> {
    boolean existsByEmailIgnoreCase(String email);
    boolean existsByEmailIgnoreCaseAndIdNot(String email, Long id);
}
