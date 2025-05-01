package es.codeurjc.helloword_vscode.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
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

import java.io.IOException;
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
    

    /* Adds authentication attributes to all templates */ 
    @ModelAttribute
    public void addAttributes(Model model) {
        // Retrieve the current authentication information        
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        boolean isAuthenticated = auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getName());
        
        // Determine if the user is authenticated and not anonymous
        model.addAttribute("isAuthenticated", isAuthenticated);
        
        // If authenticated, add the username to the model
        if (isAuthenticated && auth.getPrincipal() instanceof UserDetails) {
            UserDetails userDetails = (UserDetails) auth.getPrincipal();
            model.addAttribute("username", userDetails.getUsername());
        }
    }
    

    /*  View all members */
    @GetMapping("/members")
    public String showMembers(Model model) {
        // Fetch all users and add them to the model
        model.addAttribute("Utilisateursentity", utilisateurEntityService.findAll());
        return "members";
    }


    /* Login page */ 
    @GetMapping("/login")
    public String login() {
        return "login";
    }


    /* Login error page */ 
    @GetMapping("/loginerror")
    public String loginerror() {
        return "loginerror";
    }


    /* User profile page */ 
    @GetMapping("/profile")
    public String profile(Model model, HttpServletRequest request) {
        // Retrieve the username of the authenticated user
        String name = request.getUserPrincipal().getName();
        Optional<UtilisateurEntity> utilisateurEntity = utilisateurEntityService.findByName(name);
        
        // Add the username and admin status to the model
        model.addAttribute("username", utilisateurEntity.get().getName());
        model.addAttribute("admin", request.isUserInRole("ADMIN"));
        return "profile";
    }


    /* Displays details of a specific user */ 
    @GetMapping("/user/{id}")
    public String userId(@PathVariable long id, Model model, Principal principal, HttpServletRequest request) {
        // Retrieve the user by ID
        Optional<UtilisateurEntity> utilisateurEntity = utilisateurEntityService.findById(id);
        if (utilisateurEntity.isPresent()) {
            UtilisateurEntity utilisateur = utilisateurEntity.get();
            model.addAttribute("utilisateur", utilisateur);
            
            // Map the user's roles in associations to a DTO and add to the model
            List<AssociationMemberTypeDTO> roles = utilisateur.getMemberTypes().stream()
                .map(mt -> new AssociationMemberTypeDTO(mt.getAssociation(), mt.getName()))
                .collect(Collectors.toList());
            model.addAttribute("associationRoles", roles);
            
            // Add the user's minutes to the model
            List<Minute> userMinutes = utilisateur.getMinutes();
            model.addAttribute("userMinutes", userMinutes);
            return "user_detail";
        } else {
            return "user_not_found";
        }   
    }


    /* Research of a specific association or user by ID */
    @GetMapping("/search")
    public String searchUserOrAssociation(@RequestParam(name = "searchId", required = false) Long id,
                                          @RequestParam(name = "searchType", required = false) String searchType,
                                          Model model) {
        if (id != null && "user".equals(searchType)) {
            // Search for a user by ID and add to the model
            utilisateurEntityService.findById(id).ifPresent(user ->
                model.addAttribute("Utilisateursentity", List.of(user))
            );
            return "members";
        }
    
        if (id != null && "association".equals(searchType)) {
            // Search for an association by ID and add to the model
            associationService.findById(id).ifPresent(association ->
                model.addAttribute("assofind", association)
            );
            return "index";
        }
        return "index";
    }
    

    /* Creation of an user */
    @PostMapping("/login/create")
    public String User(@RequestParam String name, 
                            @RequestParam String surname, 
                            @RequestParam String pwd,
                            Model model) {
        // Check if the username already exists                        
        Optional<UtilisateurEntity> existingUser = utilisateurEntityService.findByName(name);
        if (existingUser.isPresent()) {
            model.addAttribute("error", "This username already exists");
            return "new_member";
        }

        // Create a new user
        utilisateurEntityService.createUser( name,  surname, pwd);
        return "redirect:/";
    }


    /* Page with the form of user creation */
    @GetMapping("/profile/create")
    public String createPage() {
        return "new_member";
    }


    /* Page with the form of user edition */
    @GetMapping("/profile/edit")
    public String editProfile(Model model, Principal principal) {
        // Retrieve the user by name and add to the model
        UtilisateurEntity user = utilisateurEntityService.findByName(principal.getName()).orElseThrow();
        model.addAttribute("user", user);
        return "edit_profile";
    }


    /* Edition of an user */
    @PostMapping("/profile/update")
    public String updateProfile(Principal principal, @RequestParam String name, @RequestParam String surname, @RequestParam(required = false) String pwd, Model model) {
        String username = principal.getName();
        utilisateurEntityService.updateUser(username, name, surname, pwd);
        return "redirect:/logout";
    }

    /*  Page to confirm deletion of user */
    @GetMapping("/profile/delete")
    @PreAuthorize("isAuthenticated()")
    public String deleteConfirmation() {
        return "confirm_delete";
    }

    
    /* Deletion of our own account */ 
    @PostMapping("/profile/delete/confirm")
    @PreAuthorize("isAuthenticated()")
    public String deleteOwnAccount(Principal principal, HttpServletRequest request) throws IOException {
        String username = principal.getName();
        Optional<UtilisateurEntity> utilisateurEntity = utilisateurEntityService.findByName(username);

        if (utilisateurEntity.isPresent()) {
            // Delete the user by ID
            utilisateurEntityService.deleteById(utilisateurEntity.get().getId());

            // Logout after deletion
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


    /* Deletion of an user (only for admins) */
    @GetMapping("/profile/{id}/delete")
	public String deleteMember(@PathVariable long id) throws IOException {
        // Retrieve the user by ID
		Optional<UtilisateurEntity> utilisateurEntity = utilisateurEntityService.findById(id);
		if (utilisateurEntity.isPresent()) {
            // Delete the user by ID
			utilisateurEntityService.deleteById(id);
			return "redirect:/";
		} else {
			return "redirect:/";
		}
    }

}

