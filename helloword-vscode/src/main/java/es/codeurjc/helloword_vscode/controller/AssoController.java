package es.codeurjc.helloword_vscode.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import es.codeurjc.helloword_vscode.repository.AssociationRepository;
import es.codeurjc.helloword_vscode.repository.MinuteRepository;
import es.codeurjc.helloword_vscode.repository.RoleRepository;
import es.codeurjc.helloword_vscode.repository.UtilisateurEntityRepository;
import jakarta.servlet.http.HttpSession;

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

    @GetMapping("/")
    public String getPosts(Model model) {
        model.addAttribute("minutes", minuteRepository.findAll());
        model.addAttribute("roles", roleRepository.findAll());
        model.addAttribute("associations", associationRepository.findAll());
        return "index";
    }

    @GetMapping("/search/")
    public String search(@RequestParam String searchId, @RequestParam String searchType, Model model) {
        if ("user".equals(searchType)) {
            return "redirect:/users/" + searchId;
        } else if ("association".equals(searchType)) {
            return "redirect:/associations/" + searchId;
        }
        return "redirect:/";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }

    @GetMapping("/profile")
    public String profile() {
        return "profile";
    }

    // @GetMapping("/members")
    // public String users(Model model) {
    //     model.addAttribute("Utilisateursentity", UtilisateurEntityRepository.findAll());
    //     return "members";
    // }

    @GetMapping("/associations")
    public String associations() {
        return "index";
    }
}

