# Testing strategy

This project uses a combination of test styles:

- `StaffRatingValidationTest` is a unit test of Jakarta Bean Validation constraints (email, required name, score range, and comment length).
- `StaffRatingRepositoryTest` is a `@DataJpaTest` persistence slice. It uses an in-memory H2 database in PostgreSQL compatibility mode to verify saving, retrieval, and deletion without requiring a running PostgreSQL server.
- `StaffRatingControllerTest` uses `@SpringBootTest` with `@AutoConfigureMockMvc`. It exercises the MVC routes, templates/model, create validation, update, and delete while using the test H2 database.

Run all tests with `./mvnw test` (Linux/macOS) or `mvn test` if Maven is installed. On Windows PowerShell, use `.\mvnw.cmd test` if the Maven wrapper has been generated.
