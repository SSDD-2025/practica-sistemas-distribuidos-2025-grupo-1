package es.codeurjc.helloword_vscode.controller;

import org.hibernate.mapping.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import es.codeurjc.helloword_vscode.repository.AssociationRepository;
import es.codeurjc.helloword_vscode.repository.UtilisateurEntityRepository;
import es.codeurjc.helloword_vscode.entities.UtilisateurEntity;

@Controller
public class MemberController {

    // Repository for accessing user data
    @Autowired
    private UtilisateurEntityRepository utilisateurEntityRepository;

    // Repository for accessing association data
    @Autowired
    private AssociationRepository associationRepository;

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
    
    // Displays a list of all registered users
    @GetMapping("/members")
    public String showMembers(Model model) {
        model.addAttribute("Utilisateursentity", utilisateurEntityRepository.findAll());
        return "members";
    }

    // Searches for users or associations based on ID and type
    @GetMapping("/search")
    public String viewUser(@RequestParam(name = "searchId", required = false) Long id, 
                           @RequestParam(name = "searchType", required = false) String searchType, 
                           Model model) {
        // Search for a user by ID
        if (id != null && "user".equals(searchType)) {
            utilisateurEntityRepository.findById(id).ifPresent(user -> model.addAttribute("userfind", user));
            return "members";
        }
        // Search for an association by ID
        if (id != null && "association".equals(searchType)) {
            associationRepository.findById(id).ifPresent(association -> model.addAttribute("assofind", association));
            return "index";
        }
        // Default redirection if no valid search criteria are provided
        return "index";
    } 
}
