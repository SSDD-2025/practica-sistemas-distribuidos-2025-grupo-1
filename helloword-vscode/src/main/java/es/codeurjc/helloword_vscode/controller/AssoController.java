package es.codeurjc.helloword_vscode.controller;

import java.io.IOException;
import java.security.Principal;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.sql.rowset.serial.SerialBlob;

import java.sql.Blob;
import org.springframework.http.HttpHeaders;

import org.springframework.core.io.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import es.codeurjc.helloword_vscode.entities.*;
import es.codeurjc.helloword_vscode.repository.*;
import es.codeurjc.helloword_vscode.service.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.rowset.serial.SerialException;

@Controller
public class AssoController {

    // Service for database interaction 

    @Autowired
    private UtilisateurEntityService utilisateurEntityService;

    @Autowired
	private AssociationService associationService;

    @Autowired
	private MinuteService minuteService;

    @Autowired
	private MemberTypeService memberTypeService;

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

    // Home page: Displays associations
    @GetMapping("/")
    public String getPosts(Model model, HttpServletRequest request) {
        model.addAttribute("associations", associationService.findAll());
        model.addAttribute("isAdmin", request.isUserInRole("ADMIN"));
        return "index";
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

    // Private page (for authenticated users)
    @GetMapping("/private")
    public String privatePage() {
        return "private";
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

    // Displays details of a specific association
    @GetMapping("/association/{id}")
    public String associationId(@PathVariable long id, Model model, Principal principal, HttpServletRequest request) {
        Optional<Association> asso = associationService.findById(id);
        model.addAttribute("association", asso.get());
        model.addAttribute("members", asso.get().getMembers());
        model.addAttribute("minutes", asso.get().getMinutes());
        model.addAttribute("isAdmin", request.isUserInRole("ADMIN"));
        model.addAttribute("hasImage", asso.get().getImageFile() != null);


        // Check if the user is a member of the association
        if (principal != null) {
            String username = principal.getName();
            Optional<UtilisateurEntity> user = utilisateurEntityService.findByName(username);
            boolean isMember = asso.get().getMembers().contains(user.get());
            model.addAttribute("isMember", isMember);
        }
        return "association_detail";
    }

    // Allows a user to join an association
    @PostMapping("/association/{id}/join")
    public String joinAssociation(@PathVariable Long id, Principal principal) {
        if (principal != null) {
            Optional<UtilisateurEntity> user = utilisateurEntityService.findById(id);
            Optional<Association> association = associationService.findById(id);
            
            if (!association.get().getMembers().contains(user.get())) {
                MemberType memberType = new MemberType("member", user.get(), association.get());
                memberTypeService.save(memberType);
            }
        }
        return "redirect:/association/" + id;
    }

    @PostMapping("/association/{id}/delete")
    @PreAuthorize("hasRole('ADMIN')")
	public String deleteAssociation(@PathVariable long id, Authentication auth) {
        System.out.println("Utilisateur connecté : " + auth.getName());
        System.out.println("Rôles : " + auth.getAuthorities());

		Optional<Association> association = associationService.findById(id);
		if (association.isPresent()) {
			associationService.deleteById(id);
			return "redirect:/";
		} else {
			return "redirect:/";
		}
	}

    // Creates a new association (only for admins)
    @PostMapping("/association/create")
    @PreAuthorize("hasRole('ADMIN')")
    public String createAssociation(Model model, Association association, MultipartFile image) throws Exception {
        associationService.save(association, image);
        return "redirect:/";
    }

    // Download Image
    @GetMapping("/association/{id}/image")
    public ResponseEntity<Object> downloadImage(@PathVariable long id) throws SQLException {
        Optional<Association> op = associationService.findById(id);
        if (op.isPresent() && op.get().getImageFile() != null) {
            Blob image = op.get().getImageFile();
            Resource file = new InputStreamResource(image.getBinaryStream());
            return ResponseEntity.ok().header(HttpHeaders.CONTENT_TYPE, "image/png")
            .contentLength(image.length()).body(file);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    
	@GetMapping("/editasso/{id}")
	public String editAsso(Model model, @PathVariable long id) {

		Optional<Association> association = associationService.findById(id);
		if (association.isPresent()) {
			model.addAttribute("association", association.get());
			return "editAssoPage";
		} else {
			return "redirect:/";
		}
	}
    
    @PostMapping("/editasso")
    public String editAssoProcess(@RequestParam Long id,
                                  @RequestParam String name,
                                  @RequestParam(required = false) MultipartFile image,
                                  Model model) {
        Optional<Association> optAsso = associationService.findById(id);
        if (optAsso.isPresent()) {
            Association asso = optAsso.get();
            asso.setName(name);
    
            if (image != null && !image.isEmpty()) {
                try {
                    byte[] bytes = image.getBytes();
                    Blob blob = new SerialBlob(bytes);
                    asso.setImageFile(blob);
                } catch (SQLException | IOException e) {
                    e.printStackTrace();
                }
            }
    
            associationService.save(asso);
            return "redirect:/association/" + id;
        } else {
            return "redirect:/";
        }
    }

    @PostMapping("/association/{id}/deleteImage")
    public String deleteAssociationImage(@PathVariable long id) {
        Optional<Association> optAsso = associationService.findById(id);
        if (optAsso.isPresent()) {
            Association asso = optAsso.get();
            asso.setImageFile(null);
            associationService.save(asso);
        }
        return "redirect:/editasso/"+id;
    }

    
}
