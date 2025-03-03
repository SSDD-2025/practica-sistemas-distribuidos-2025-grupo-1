package es.codeurjc.helloword_vscode.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import es.codeurjc.helloword_vscode.repository.AssociationRepository;
import es.codeurjc.helloword_vscode.repository.MinuteRepository;
import es.codeurjc.helloword_vscode.repository.RoleRepository;
import es.codeurjc.helloword_vscode.repository.UtilisateurEntityRepository;

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
        model.addAttribute("Utilisateursentity", UtilisateurEntityRepository.findAll());
        model.addAttribute("minutes", minuteRepository.findAll());
        model.addAttribute("roles", roleRepository.findAll());
        model.addAttribute("associations", associationRepository.findAll());
        return "index";
    }
}

