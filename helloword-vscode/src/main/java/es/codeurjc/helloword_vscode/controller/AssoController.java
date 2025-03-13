package es.codeurjc.helloword_vscode.controller;

import java.security.Principal;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.sql.Blob;
import org.springframework.http.HttpHeaders;

import org.springframework.core.io.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import es.codeurjc.helloword_vscode.entities.*;
import es.codeurjc.helloword_vscode.repository.*;
import es.codeurjc.helloword_vscode.service.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.transaction.annotation.Transactional;

@Controller
public class AssoController {

    // Repositories for database interaction
    @Autowired
    private UtilisateurEntityRepository utilisateurEntityRepository;

    @Autowired
    private MinuteRepository minuteRepository;

    @Autowired
    private MemberTypeRepository memberTypeRepository;

    @Autowired
    private AssociationRepository associationRepository;

    @Autowired
	private AssociationService associationService;

    // Adds authentication attributes to all templates
    @ModelAttribute
    public void addAttributes(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        boolean isAuthenticated = auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getName());
        
        model.addAttribute("isAuthenticated", isAuthenticated);
        
        if (isAuthenticated && auth.getPrincipal() instanceof UserDetails) {
            UserDetails userDetails = (UserDetails) auth.getPrincipal();
            model.addAttribute("username", userDetails.getUsername());
        }
    }

    // Home page: Displays associations, roles, and minutes
    @GetMapping("/")
    public String getPosts(Model model, HttpServletRequest request) {
        model.addAttribute("minutes", minuteRepository.findAll());
        model.addAttribute("roles", memberTypeRepository.findAll());
        model.addAttribute("associations", associationRepository.findAll());
        model.addAttribute("isAdmin", request.isUserInRole("ADMIN"));
        return "index";
    }

    // Login page
    @GetMapping("/login")
    public String login() {
        return "login";
    }

    // Login error page
    @GetMapping("/loginerror")
    public String loginerror() {
        return "loginerror";
    }

    // Private page (for authenticated users)
    @GetMapping("/private")
    public String privatePage() {
        return "private";
    }

    // User profile page
    @GetMapping("/profile")
    public String profile(Model model, HttpServletRequest request) {
        String name = request.getUserPrincipal().getName();
        UtilisateurEntity utilisateurEntity = utilisateurEntityRepository.findByName(name).orElseThrow();
        model.addAttribute("username", utilisateurEntity.getName());
        model.addAttribute("admin", request.isUserInRole("ADMIN"));
        return "profile";
    }

    // Admin dashboard page
    @GetMapping("/admin")
    public String admin() {
        return "admin";
    }

    // Displays details of a specific association
    @GetMapping("/association/{id}")
    public String associationId(@PathVariable long id, Model model, Principal principal, HttpServletRequest request) {
        Association association = associationRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Invalid association Id:" + id));
            Optional<Association> asso = associationService.findById(id);
        model.addAttribute("association", association);
        model.addAttribute("members", association.getMembers());
        model.addAttribute("minutes", association.getMinutes());
        model.addAttribute("isAdmin", request.isUserInRole("ADMIN"));
        model.addAttribute("hasImage", asso.get().getImageFile() != null);


        // Check if the user is a member of the association
        if (principal != null) {
            String username = principal.getName();
            UtilisateurEntity user = utilisateurEntityRepository.findByName(username).orElse(null);
            boolean isMember = association.getMembers().contains(user);
            model.addAttribute("isMember", isMember);
        }
        
        return "association_detail";
    }

    // Allows a user to join an association
    @PostMapping("/association/{id}/join")
    public String joinAssociation(@PathVariable Long id, Principal principal) {
        if (principal != null) {
            String username = principal.getName();
            UtilisateurEntity user = utilisateurEntityRepository.findByName(username).orElseThrow();
            Association association = associationRepository.findById(id).orElseThrow();
            
            if (!association.getMembers().contains(user)) {
                MemberType memberType = new MemberType("member", user, association);
                memberTypeRepository.save(memberType);
            }
        }
        return "redirect:/associations/" + id;
    }

    // Deletes an association (only for admins)
    @PostMapping("/association/{id}/delete")
    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public String deleteAssociation(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        Optional<Association> optionalAssociation = associationRepository.findById(id);
    
        if (optionalAssociation.isPresent()) {
            Association association = optionalAssociation.get();
    
            // Step 1: Delete associated minutes
            for (Minute minute : association.getMinutes()) {
                minute.getParticipants().clear(); // Remove participant references
                minuteRepository.delete(minute); // Delete the minute
            }
    
            // Step 2: Delete member roles in the association
            for (MemberType memberType : association.getMemberTypes()) {
                memberType.setUtilisateurEntity(null); // Remove user reference
                memberTypeRepository.delete(memberType);
            }
    
            // Step 3: Delete the association itself
            associationRepository.delete(association);
    
            redirectAttributes.addFlashAttribute("success", "Association successfully deleted!");
        } else {
            redirectAttributes.addFlashAttribute("error", "Association not found!");
        }
    
        return "redirect:/";
    }

    // Creates a new association (only for admins)
    @PostMapping("/association/create")
    @PreAuthorize("hasRole('ADMIN')")
    public String createAssociation(Model model, Association association, MultipartFile image) throws Exception {
        associationService.save(association, image);
        return "redirect:/";
    }

    // Download Image
    @GetMapping("/association/{id}/image")
    public ResponseEntity<Object> downloadImage(@PathVariable long id) throws SQLException {
        Optional<Association> op = associationService.findById(id);
        if (op.isPresent() && op.get().getImageFile() != null) {
            Blob image = op.get().getImageFile();
            Resource file = new InputStreamResource(image.getBinaryStream());
            return ResponseEntity.ok().header(HttpHeaders.CONTENT_TYPE, "image/png")
            .contentLength(image.length()).body(file);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
