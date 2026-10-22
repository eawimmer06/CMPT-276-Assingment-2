package ca.sfu.rating.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "staff_ratings", uniqueConstraints = @UniqueConstraint(name = "uk_staff_rating_email", columnNames = "email"))
public class StaffRating {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Name is required.")
    @Size(min = 2, max = 80, message = "Name must be between 2 and 80 characters.")
    @Column(nullable = false, length = 80)
    private String name;

    @NotBlank(message = "Email is required.")
    @Email(message = "Enter a valid email address.")
    @Size(max = 254, message = "Email must be at most 254 characters.")
    @Column(nullable = false, unique = true, length = 254)
    private String email;

    @NotNull(message = "Please choose a role.")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private RoleType roleType;

    @NotNull(message = "Clarity score is required.")
    @Min(value = 1, message = "Score must be between 1 and 10.")
    @Max(value = 10, message = "Score must be between 1 and 10.")
    @Column(nullable = false)
    private Integer clarity;

    @NotNull(message = "Niceness score is required.")
    @Min(value = 1, message = "Score must be between 1 and 10.")
    @Max(value = 10, message = "Score must be between 1 and 10.")
    @Column(nullable = false)
    private Integer niceness;

    @NotNull(message = "Knowledgeable score is required.")
    @Min(value = 1, message = "Score must be between 1 and 10.")
    @Max(value = 10, message = "Score must be between 1 and 10.")
    @Column(nullable = false)
    private Integer knowledgeableScore;

    @Size(max = 1000, message = "Comment must be at most 1000 characters.")
    @Column(length = 1000)
    private String comment;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        createdAt = now;
        updatedAt = now;
    }

    @PreUpdate
    void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public int overallScore() {
        if (clarity == null || niceness == null || knowledgeableScore == null) return 0;
        return Math.round((clarity + niceness + knowledgeableScore) / 3.0f);
    }

    public StaffMemberProfile profile() {
        if (roleType == RoleType.TA) return new TAProfile();
        if (roleType == RoleType.PROF) return new ProfessorProfile();
        return new StaffMemberProfile() {
            @Override public String displayTitle() {
                return switch (roleType == null ? RoleType.STAFF : roleType) {
                    case INSTRUCTOR -> "Instructor";
                    case STAFF -> "Staff Member";
                    case TA -> "Teaching Assistant (TA)";
                    case PROF -> "Professor";
                };
            }
        };
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email == null ? null : email.trim().toLowerCase(); }
    public RoleType getRoleType() { return roleType; }
    public void setRoleType(RoleType roleType) { this.roleType = roleType; }
    public Integer getClarity() { return clarity; }
    public void setClarity(Integer clarity) { this.clarity = clarity; }
    public Integer getNiceness() { return niceness; }
    public void setNiceness(Integer niceness) { this.niceness = niceness; }
    public Integer getKnowledgeableScore() { return knowledgeableScore; }
    public void setKnowledgeableScore(Integer knowledgeableScore) { this.knowledgeableScore = knowledgeableScore; }
    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
}
