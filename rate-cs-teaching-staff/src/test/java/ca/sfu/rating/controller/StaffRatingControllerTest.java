package ca.sfu.rating.controller;

import ca.sfu.rating.model.RoleType;
import ca.sfu.rating.model.StaffRating;
import ca.sfu.rating.repository.StaffRatingRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class StaffRatingControllerTest {
    @Autowired MockMvc mvc;
    @Autowired StaffRatingRepository repository;

    @BeforeEach void clean() { repository.deleteAll(); }

    private StaffRating sample() {
        StaffRating r = new StaffRating();
        r.setName("Taylor Kim"); r.setEmail("taylor@example.com"); r.setRoleType(RoleType.TA);
        r.setClarity(8); r.setNiceness(9); r.setKnowledgeableScore(7); r.setComment("Explains concepts well.");
        return r;
    }

    @Test void indexReturns200AndRatingsModel() throws Exception {
        repository.saveAndFlush(sample());
        mvc.perform(get("/"))
           .andExpect(status().isOk())
           .andExpect(view().name("index"))
           .andExpect(model().attributeExists("ratings"))
           .andExpect(content().string(containsString("Taylor Kim")));
    }

    @Test void createSuccessRedirectsToDetail() throws Exception {
        mvc.perform(post("/ratings").contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("name", "Jordan Park").param("email", "jordan@example.com")
                .param("roleType", "PROF").param("clarity", "8").param("niceness", "8")
                .param("knowledgeableScore", "9").param("comment", "Clear lectures"))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrlPattern("/ratings/*"));
        org.assertj.core.api.Assertions.assertThat(repository.findAll()).hasSize(1);
    }

    @Test void invalidCreateReturnsFormWithErrors() throws Exception {
        mvc.perform(post("/ratings").contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("name", "").param("email", "not-an-email").param("roleType", "TA")
                .param("clarity", "11").param("niceness", "5").param("knowledgeableScore", "7"))
            .andExpect(status().isOk())
            .andExpect(view().name("create"))
            .andExpect(model().attributeHasFieldErrors("rating", "name", "email", "clarity"));
    }

    @Test void detailShowsExistingRating() throws Exception {
        StaffRating saved = repository.saveAndFlush(sample());
        mvc.perform(get("/ratings/" + saved.getId()))
            .andExpect(status().isOk()).andExpect(view().name("detail"))
            .andExpect(model().attributeExists("rating", "profile"))
            .andExpect(content().string(containsString("Taylor Kim")));
    }

    @Test void updateChangesRating() throws Exception {
        StaffRating saved = repository.saveAndFlush(sample());
        mvc.perform(post("/ratings/" + saved.getId() + "/edit")
                .param("name", "Taylor Updated").param("email", "taylor@example.com")
                .param("roleType", "TA").param("clarity", "6").param("niceness", "7")
                .param("knowledgeableScore", "8").param("comment", "Updated"))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/ratings/" + saved.getId()));
        org.assertj.core.api.Assertions.assertThat(repository.findById(saved.getId()).orElseThrow().getName())
            .isEqualTo("Taylor Updated");
    }

    @Test void deleteRemovesRating() throws Exception {
        StaffRating saved = repository.saveAndFlush(sample());
        mvc.perform(post("/ratings/" + saved.getId() + "/delete"))
            .andExpect(status().is3xxRedirection()).andExpect(redirectedUrl("/"));
        org.assertj.core.api.Assertions.assertThat(repository.findById(saved.getId())).isEmpty();
    }
}
