package ca.sfu.rating.service;

import ca.sfu.rating.model.RoleType;
import ca.sfu.rating.model.StaffRating;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import java.util.Set;
import java.util.stream.Collectors;
import static org.junit.jupiter.api.Assertions.*;

class StaffRatingValidationTest {
    private static Validator validator;
    @BeforeAll static void setup() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }
    private StaffRating validRating() {
        StaffRating r = new StaffRating();
        r.setName("Alex Smith"); r.setEmail("alex@example.com"); r.setRoleType(RoleType.TA);
        r.setClarity(8); r.setNiceness(9); r.setKnowledgeableScore(7); r.setComment("Helpful");
        return r;
    }
    @Test void rejectsInvalidEmail() {
        StaffRating r = validRating(); r.setEmail("not-an-email");
        assertTrue(validator.validate(r).stream().anyMatch(v -> v.getPropertyPath().toString().equals("email")));
    }
    @Test void rejectsOutOfRangeScore() {
        StaffRating r = validRating(); r.setClarity(11);
        assertTrue(validator.validate(r).stream().anyMatch(v -> v.getPropertyPath().toString().equals("clarity")));
    }
    @Test void rejectsMissingName() {
        StaffRating r = validRating(); r.setName(" ");
        assertTrue(validator.validate(r).stream().anyMatch(v -> v.getPropertyPath().toString().equals("name")));
    }
    @Test void rejectsOverlongComment() {
        StaffRating r = validRating(); r.setComment("x".repeat(1001));
        Set<String> fields = validator.validate(r).stream().map(v -> v.getPropertyPath().toString()).collect(Collectors.toSet());
        assertTrue(fields.contains("comment"));
    }
}
