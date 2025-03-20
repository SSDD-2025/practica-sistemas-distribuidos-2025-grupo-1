package es.codeurjc.helloword_vscode.controller;

import org.hibernate.mapping.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import es.codeurjc.helloword_vscode.service.UtilisateurEntityService;
import es.codeurjc.helloword_vscode.service.AssociationService;
import jakarta.servlet.http.HttpSession;

import es.codeurjc.helloword_vscode.entities.UtilisateurEntity;

@Controller
public class MemberController {

    @Autowired
    private AssociationService associationService;

    @Autowired
    private UtilisateurEntityService utilisateurEntityService;

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
    
    @GetMapping("/members")
    public String showMembers(Model model) {
        model.addAttribute("Utilisateursentity", utilisateurEntityService.findAll());
        return "members";
    }

    @GetMapping("/search")
    public String viewUser(@RequestParam(name = "searchId", required = false) Long id, 
                           @RequestParam(name = "searchType", required = false) String searchType, 
                           Model model) {
        if (id != null && "user".equals(searchType)) {
            utilisateurEntityService.findById(id).ifPresent(user -> model.addAttribute("userfind", user));
            return "members";
        }
        if (id != null && "association".equals(searchType)) {
            associationService.findById(id).ifPresent(association -> model.addAttribute("assofind", association));
            return "index";
        }
        return "index";
    } 
}

