package ca.sfu.rating.controller;

import ca.sfu.rating.model.StaffRating;
import ca.sfu.rating.model.RoleType;
import ca.sfu.rating.service.StaffRatingService;
import jakarta.validation.Valid;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class StaffRatingController {
    private final StaffRatingService service;

    public StaffRatingController(StaffRatingService service) { this.service = service; }

    @ModelAttribute("roles")
    public RoleType[] roles() { return RoleType.values(); }

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("ratings", service.findAll());
        return "index";
    }

    @GetMapping("/ratings/new")
    public String createForm(Model model) {
        model.addAttribute("rating", new StaffRating());
        return "create";
    }

    @PostMapping("/ratings")
    public String create(@Valid @ModelAttribute("rating") StaffRating rating,
                         BindingResult result, Model model, RedirectAttributes redirect) {
        checkUniqueEmail(rating, null, result);
        if (result.hasErrors()) return "create";
        try {
            StaffRating saved = service.save(rating);
            redirect.addFlashAttribute("successMessage", "Rating created successfully.");
            return "redirect:/ratings/" + saved.getId();
        } catch (DataIntegrityViolationException ex) {
            result.rejectValue("email", "duplicate", "That email is already used.");
            return "create";
        }
    }

    @GetMapping("/ratings/{id}")
    public String detail(@PathVariable Long id, Model model) {
        return service.findById(id).map(rating -> {
            model.addAttribute("rating", rating);
            model.addAttribute("profile", rating.profile());
            return "detail";
        }).orElse("error");
    }

    @GetMapping("/ratings/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        return service.findById(id).map(rating -> {
            model.addAttribute("rating", rating);
            return "edit";
        }).orElse("error");
    }

    @PostMapping("/ratings/{id}/edit")
    public String update(@PathVariable Long id, @Valid @ModelAttribute("rating") StaffRating form,
                         BindingResult result, Model model, RedirectAttributes redirect) {
        var existing = service.findById(id);
        if (existing.isEmpty()) return "error";
        form.setId(id);
        checkUniqueEmail(form, id, result);
        if (result.hasErrors()) return "edit";
        try {
            StaffRating rating = existing.get();
            rating.setName(form.getName());
            rating.setEmail(form.getEmail());
            rating.setRoleType(form.getRoleType());
            rating.setClarity(form.getClarity());
            rating.setNiceness(form.getNiceness());
            rating.setKnowledgeableScore(form.getKnowledgeableScore());
            rating.setComment(form.getComment());
            service.save(rating);
            redirect.addFlashAttribute("successMessage", "Rating updated successfully.");
            return "redirect:/ratings/" + id;
        } catch (DataIntegrityViolationException ex) {
            result.rejectValue("email", "duplicate", "That email is already used.");
            return "edit";
        }
    }

    @PostMapping("/ratings/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes redirect) {
        if (service.deleteById(id)) redirect.addFlashAttribute("successMessage", "Rating deleted.");
        else redirect.addFlashAttribute("errorMessage", "That rating no longer exists.");
        return "redirect:/";
    }

    private void checkUniqueEmail(StaffRating rating, Long id, BindingResult result) {
        if (rating.getEmail() != null && service.emailTaken(rating.getEmail(), id)) {
            result.rejectValue("email", "duplicate", "A rating with this email already exists.");
        }
    }
}
