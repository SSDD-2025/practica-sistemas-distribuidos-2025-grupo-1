package es.codeurjc.helloword_vscode.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;

import es.codeurjc.helloword_vscode.repository.AssociationRepository;
import es.codeurjc.helloword_vscode.repository.MinuteRepository;
import es.codeurjc.helloword_vscode.repository.RoleRepository;
import es.codeurjc.helloword_vscode.repository.UtilisateurEntityRepository;
import jakarta.servlet.http.HttpSession;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.Model;

@Controller
public class AssoController {

    @Autowired
    private UtilisateurEntityRepository UtilisateurEntityRepository;

    @Autowired
    private MinuteRepository minuteRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private AssociationRepository associationRepository;

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

    @GetMapping("/")
    public String getPosts(Model model) {
        model.addAttribute("minutes", minuteRepository.findAll());
        model.addAttribute("roles", roleRepository.findAll());
        model.addAttribute("associations", associationRepository.findAll());
        return "index";
    }

    @GetMapping("/login")
    public String login(/*HttpSession session*/) {
        //session.invalidate();
        return "login";
    }

    @GetMapping("/loginerror")
    public String loginerror(/*HttpSession session*/) {
        //session.invalidate();
        return "loginerror";
    }

    @GetMapping("/private")
    public String privatrePageString(/*HttpSession session*/) {
        //session.invalidate();
        return "private";
    }

    @GetMapping("/profile")
    public String profile() {
        return "profile";
    }
}

