package ca.sfu.rating.service;

import ca.sfu.rating.model.StaffRating;
import ca.sfu.rating.repository.StaffRatingRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class StaffRatingService {
    private final StaffRatingRepository repository;

    public StaffRatingService(StaffRatingRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<StaffRating> findAll() { return repository.findAll(); }

    @Transactional(readOnly = true)
    public Optional<StaffRating> findById(Long id) { return repository.findById(id); }

    @Transactional(readOnly = true)
    public boolean emailTaken(String email, Long currentId) {
        if (email == null || email.isBlank()) return false;
        return currentId == null
                ? repository.existsByEmailIgnoreCase(email.trim())
                : repository.existsByEmailIgnoreCaseAndIdNot(email.trim(), currentId);
    }

    public StaffRating save(StaffRating rating) { return repository.save(rating); }

    public boolean deleteById(Long id) {
        if (!repository.existsById(id)) return false;
        repository.deleteById(id);
        return true;
    }
}
