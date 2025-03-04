package es.codeurjc.helloword_vscode.controller;

import org.hibernate.mapping.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import es.codeurjc.helloword_vscode.repository.AssociationRepository;
import es.codeurjc.helloword_vscode.repository.MinuteRepository;
import es.codeurjc.helloword_vscode.repository.RoleRepository;
import es.codeurjc.helloword_vscode.repository.UtilisateurEntityRepository;
import jakarta.servlet.http.HttpSession;

import es.codeurjc.helloword_vscode.entities.UtilisateurEntity;

@Controller
public class MemberController {

    @Autowired
    private UtilisateurEntityRepository utilisateurEntityRepository;

    @Autowired
    private AssociationRepository associationRepository;

    @GetMapping("/members")
    public String showMembers(Model model) {
        model.addAttribute("Utilisateursentity", utilisateurEntityRepository.findAll());
        return "members";
    }

    @GetMapping("/search")
    public String viewUser(@RequestParam(name = "searchId", required = false) Long id, 
                           @RequestParam(name = "searchType", required = false) String searchType, 
                           Model model) {
        if (id != null && "user".equals(searchType)) {
            utilisateurEntityRepository.findById(id).ifPresent(user -> model.addAttribute("userfind", user));
            return "members";
        }
        if (id != null && "association".equals(searchType)) {
            associationRepository.findById(id).ifPresent(association -> model.addAttribute("assofind", association));
            return "index";
        }
        return "index";
    } 
}

