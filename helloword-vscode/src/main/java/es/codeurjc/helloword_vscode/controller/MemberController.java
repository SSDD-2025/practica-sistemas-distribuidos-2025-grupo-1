package es.codeurjc.helloword_vscode.controller;

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
import org.springframework.web.bind.annotation.RequestParam;

import es.codeurjc.helloword_vscode.service.UtilisateurEntityService;
import es.codeurjc.helloword_vscode.service.AssociationService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import es.codeurjc.helloword_vscode.model.AssociationMemberTypeDTO;
import es.codeurjc.helloword_vscode.model.Minute;
import es.codeurjc.helloword_vscode.model.UtilisateurEntity;
import es.codeurjc.helloword_vscode.repository.UtilisateurEntityRepository;

import java.security.Principal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Controller
public class MemberController {

    // Service for database interaction 

    @Autowired
    private AssociationService associationService;

    @Autowired
    private UtilisateurEntityService utilisateurEntityService;
    
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
        model.addAttribute("Utilisateursentity", utilisateurEntityService.findAll());
        return "members";
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

    // User profile page
    @GetMapping("/profile")
    public String profile(Model model, HttpServletRequest request) {
        String name = request.getUserPrincipal().getName();
        Optional<UtilisateurEntity> utilisateurEntity = utilisateurEntityService.findByName(name);
        model.addAttribute("username", utilisateurEntity.get().getName());
        model.addAttribute("admin", request.isUserInRole("ADMIN"));
        return "profile";
    }

    // Displays details of a specific user
    @GetMapping("/user/{id}")
    public String userId(@PathVariable long id, Model model, Principal principal, HttpServletRequest request) {
        Optional<UtilisateurEntity> utilisateurEntity = utilisateurEntityService.findById(id);
        if (utilisateurEntity.isPresent()) {
            UtilisateurEntity utilisateur = utilisateurEntity.get();
            model.addAttribute("utilisateur", utilisateur);
            //model.addAttribute("associations", utilisateur.getAssociations());
            List<AssociationMemberTypeDTO> roles = utilisateur.getMemberTypes().stream()
            .map(mt -> new AssociationMemberTypeDTO(mt.getAssociation(), mt.getName()))
            .collect(Collectors.toList());
            model.addAttribute("associationRoles", roles);
            List<Minute> userMinutes = utilisateur.getMinutes();
            model.addAttribute("userMinutes", userMinutes);
            return "user_detail";
        } else {
            return "user_not_found";
        }   
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

    @PostMapping("/login/create")
    public String createUser(@RequestParam String name, 
                            @RequestParam String surname, 
                            @RequestParam String pwd,
                            Model model) {
        Optional<UtilisateurEntity> existingUser = utilisateurEntityService.findByName(name);
        if (existingUser.isPresent()) {
            model.addAttribute("error", "This username already exists");
            return "new_member";
        }
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
                                @RequestParam(required=false) String pwd,
                                Model model) {
        String username = principal.getName();
        Optional<UtilisateurEntity> userOpt = utilisateurEntityService.findByName(username);
        if (userOpt.isPresent()) {
            UtilisateurEntity user = userOpt.get();

            // Verify if an other user have already this name
            Optional<UtilisateurEntity> existing = utilisateurEntityService.findByName(name);
            if (existing.isPresent() && existing.get().getId() != user.getId()) {
                model.addAttribute("error", "This username already exists");
                model.addAttribute("user", user);
                return "edit_profile";
            }
            user.setName(name);
            user.setSurname(surname);
    
            // Change password only if there is a new valors
            if (pwd != null && !pwd.isBlank()) {
                user.setPwd(passwordEncoder.encode(pwd));
            }
    
            utilisateurEntityService.save(user);
    
            return "redirect:/logout"; 
        }
        return "redirect:/login";
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
        Optional<UtilisateurEntity> utilisateurEntity = utilisateurEntityService.findByName(username);

        if (utilisateurEntity.isPresent()) {
            utilisateurEntityService.deleteById(utilisateurEntity.get().getId());

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
		Optional<UtilisateurEntity> utilisateurEntity = utilisateurEntityService.findById(id);
		if (utilisateurEntity.isPresent()) {
			utilisateurEntityService.deleteById(id);
			return "redirect:/";
		} else {
			return "redirect:/";
		}
    }

}

