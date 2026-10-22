package ca.sfu.rating.repository;

import ca.sfu.rating.model.RoleType;
import ca.sfu.rating.model.StaffRating;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class StaffRatingRepositoryTest {
    @Autowired StaffRatingRepository repository;

    private StaffRating sample() {
        StaffRating r = new StaffRating();
        r.setName("Jamie Lee"); r.setEmail("jamie@example.com"); r.setRoleType(RoleType.PROF);
        r.setClarity(8); r.setNiceness(7); r.setKnowledgeableScore(9);
        return r;
    }
    @Test void savesAndRetrievesRating() {
        StaffRating saved = repository.saveAndFlush(sample());
        assertThat(repository.findById(saved.getId())).isPresent();
        assertThat(repository.findById(saved.getId()).orElseThrow().getName()).isEqualTo("Jamie Lee");
    }
    @Test void deletesRating() {
        StaffRating saved = repository.saveAndFlush(sample());
        repository.deleteById(saved.getId());
        repository.flush();
        assertThat(repository.findById(saved.getId())).isEmpty();
    }
}
