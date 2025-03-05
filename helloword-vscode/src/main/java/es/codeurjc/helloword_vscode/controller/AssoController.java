package es.codeurjc.helloword_vscode.controller;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import es.codeurjc.helloword_vscode.entities.Association;
import es.codeurjc.helloword_vscode.entities.MemberType;
import es.codeurjc.helloword_vscode.entities.Minute;
import es.codeurjc.helloword_vscode.entities.UtilisateurEntity;
import es.codeurjc.helloword_vscode.repository.AssociationRepository;
import es.codeurjc.helloword_vscode.repository.MinuteRepository;
import es.codeurjc.helloword_vscode.repository.MemberTypeRepository;
import es.codeurjc.helloword_vscode.repository.UtilisateurEntityRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.Model;

import java.util.List;
import java.util.Optional;
import org.springframework.transaction.annotation.Transactional;


@Controller
public class AssoController {

    @Autowired
    private UtilisateurEntityRepository UtilisateurEntityRepository;

    @Autowired
    private MinuteRepository minuteRepository;

    @Autowired
    private MemberTypeRepository memberTypeRepository;

    @Autowired
    private AssociationRepository associationRepository;

    @Autowired
    private UtilisateurEntityRepository utilisateurEntityRepository;

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
    public String getPosts(Model model, HttpServletRequest request) {
        model.addAttribute("minutes", minuteRepository.findAll());
        model.addAttribute("roles", memberTypeRepository.findAll());
        model.addAttribute("associations", associationRepository.findAll());
        model.addAttribute("isAdmin", request.isUserInRole("ADMIN"));
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
    public String profile(Model model, HttpServletRequest request) {
        String name = request.getUserPrincipal().getName();
		UtilisateurEntity utilisateurEntity = utilisateurEntityRepository.findByName(name).orElseThrow();
        model.addAttribute("username", utilisateurEntity.getName());
        model.addAttribute("admin", request.isUserInRole("ADMIN"));
        return "profile";
    }

    @GetMapping("/admin")
    public String admin() {
        return "admin";
    }

    @GetMapping("/association/{id}")
    public String associationId(@PathVariable long id, Model model, Principal principal, HttpServletRequest request){
        Association association = associationRepository.findById(id)
        .orElseThrow(() -> new IllegalArgumentException("Invalid association Id:" + id));
    
        List<Minute> minutes = association.getMinutes();
        minutes.size();

        model.addAttribute("association", association);
        model.addAttribute("members", association.getMembers());
        model.addAttribute("minutes", association.getMinutes());
        model.addAttribute("isAdmin", request.isUserInRole("ADMIN"));

        if (principal != null) {
            String username = principal.getName();
            UtilisateurEntity user = utilisateurEntityRepository.findByName(username).orElse(null);
            boolean isMember = association.getMembers().contains(user);
            model.addAttribute("isMember", isMember);
        }
        
        return "association_detail";
    }

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

    @PostMapping("/association/{id}/delete")
    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public String deleteAssociation(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        Optional<Association> optionalAssociation = associationRepository.findById(id);
    
        if (optionalAssociation.isPresent()) {
            Association association = optionalAssociation.get();
    
            // Étape 1 : Supprimer les minutes associées à l'association
            for (Minute minute : association.getMinutes()) {
                minute.getParticipants().clear(); // Supprime les références des participants
                minuteRepository.delete(minute); // Supprime la minute
            }
    
            // Étape 2 : Supprimer les rôles associés à l'association
            for (MemberType memberType : association.getMemberTypes()) {
                memberType.setUtilisateurEntity(null); // Supprime la relation avec l'utilisateur
                memberTypeRepository.delete(memberType);
            }
    
            // Étape 3 : Supprimer l'association
            associationRepository.delete(association);
    
            redirectAttributes.addFlashAttribute("success", "Association supprimée avec succès !");
        } else {
            redirectAttributes.addFlashAttribute("error", "Association non trouvée !");
        }
    
        return "redirect:/";
    }
    


    @PostMapping("/association/create")
    @PreAuthorize("hasRole('ADMIN')")
    public String createAssociation(@RequestParam String name) {
        Association association = new Association(name);
        associationRepository.save(association);
        return "redirect:/";
    }
}

