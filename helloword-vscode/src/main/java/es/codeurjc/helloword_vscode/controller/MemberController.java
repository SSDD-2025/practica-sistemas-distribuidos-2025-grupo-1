package es.codeurjc.helloword_vscode.controller;

import org.hibernate.mapping.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import es.codeurjc.helloword_vscode.service.UtilisateurEntityService;
import es.codeurjc.helloword_vscode.service.AssociationService;
import es.codeurjc.helloword_vscode.service.MemberTypeService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import es.codeurjc.helloword_vscode.entities.MemberType;
import es.codeurjc.helloword_vscode.entities.UtilisateurEntity;
import es.codeurjc.helloword_vscode.repository.UtilisateurEntityRepository;

import java.security.Principal;
import java.util.Optional;


@Controller
public class MemberController {

    @Autowired
    private AssociationService associationService;

    @Autowired
    private MemberTypeService memberTypeService;

    @Autowired
    private UtilisateurEntityService utilisateursEntityService;
    
    @Autowired
    private UtilisateurEntityRepository utilisateursEntityRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

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
        model.addAttribute("Utilisateursentity", utilisateursEntityService.findAll());
        return "members";
    }

    @GetMapping("/search")
    public String viewUser(@RequestParam(name = "searchId", required = false) Long id, 
                           @RequestParam(name = "searchType", required = false) String searchType, 
                           Model model) {
        if (id != null && "user".equals(searchType)) {
            utilisateursEntityService.findById(id).ifPresent(user -> model.addAttribute("userfind", user));
            return "members";
        }
        if (id != null && "association".equals(searchType)) {
            associationService.findById(id).ifPresent(association -> model.addAttribute("assofind", association));
            return "index";
        }
        return "index";
    } 

    @PostMapping("/login/create")
    public String createUser(@RequestParam String name, @RequestParam String surname, @RequestParam String pwd) {
        UtilisateurEntity user = new UtilisateurEntity(name, surname, passwordEncoder.encode(pwd), "USER");
        utilisateursEntityRepository.save(user);
        return "redirect:/";
    }

    @GetMapping("/profile/create")
    public String createPage() {
        return "new_member";
    }

    @GetMapping("/profile/edit")
    public String editProfile(Model model, Principal principal) {
        UtilisateurEntity user = utilisateursEntityRepository.findByName(principal.getName()).orElseThrow();
        model.addAttribute("user", user);
        return "edit_profile";
    }

    @PostMapping("/profile/update")
    public String updateProfile(Principal principal,
                                @RequestParam String name,
                                @RequestParam String surname,
                                @RequestParam String pwd) {
        UtilisateurEntity user = utilisateursEntityRepository.findByName(principal.getName()).orElseThrow();
        user.setName(name);
        user.setSurname(surname);
        user.setPwd(passwordEncoder.encode(pwd));
        utilisateursEntityRepository.save(user);
        return "redirect:/profile";
    }


    @GetMapping("/profile/delete")
    @PreAuthorize("isAuthenticated()")
    public String deleteConfirmation() {
        return "confirm_delete";
    }

    @PostMapping("/profile/delete/confirm")
    @PreAuthorize("isAuthenticated()")
    public String deleteOwnAccount(Principal principal, HttpServletRequest request) {
        String username = principal.getName();
        Optional<UtilisateurEntity> utilisateurEntity = utilisateursEntityService.findByName(username);

        if (utilisateurEntity.isPresent()) {
            utilisateursEntityService.deleteById(utilisateurEntity.get().getId());

            // Logout after delete
            try {
                request.logout();
            } catch (ServletException e) {
                e.printStackTrace();
            }

            return "redirect:/";
        } else {
            return "redirect:/";
        }
    }


    @PostMapping("/profile/{id}/delete")
	public String deleteMember(@PathVariable long id) {
		Optional<UtilisateurEntity> utilisateurEntity = utilisateursEntityService.findById(id);
		if (utilisateurEntity.isPresent()) {
			utilisateursEntityService.deleteById(id);
			return "redirect:/";
		} else {
			return "redirect:/";
		}
    }

}

