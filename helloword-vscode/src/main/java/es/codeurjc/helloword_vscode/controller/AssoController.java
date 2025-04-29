package es.codeurjc.helloword_vscode.controller;

import java.io.IOException;
import java.security.Principal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
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

import es.codeurjc.helloword_vscode.model.Association;
import es.codeurjc.helloword_vscode.model.AssociationMemberTypeDTO;
import es.codeurjc.helloword_vscode.model.MemberType;
import es.codeurjc.helloword_vscode.model.Minute;
import es.codeurjc.helloword_vscode.model.UtilisateurEntity;
import es.codeurjc.helloword_vscode.service.*;
import jakarta.servlet.http.HttpServletRequest;



@Controller
public class AssoController {

    // Service for database interaction 

    @Autowired
    private UtilisateurEntityService utilisateurEntityService;

    @Autowired
	private AssociationService associationService;

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

    // Displays details of a specific association
    @GetMapping("/association/{id}")
    public String associationId(@PathVariable long id, Model model, Principal principal, HttpServletRequest request) {
        Optional<Association> asso = associationService.findById(id);
        if (asso.isPresent()) {
            model.addAttribute("association", asso.get());
            model.addAttribute("members", asso.get().getMembers());
            model.addAttribute("minutes", asso.get().getMinutes());
            model.addAttribute("isAdmin", request.isUserInRole("ADMIN"));
            model.addAttribute("hasImage", asso.get().getImageFile() != null);

            // Check if the user is a member of the association
            if (principal != null) {
                String username = principal.getName();
                Optional<UtilisateurEntity> user = utilisateurEntityService.findByName(username);
                if (user.isPresent()) {
                    boolean isMember = asso.get().getMembers().contains(user.get());
                    model.addAttribute("isMember", isMember);
                } else {
                    model.addAttribute("isMember", false);
                }
            }            
            return "association_detail";
        } else {
            return "asso_not_found";
        }
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
    @PreAuthorize("hasRole('ADMIN')")
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

    @GetMapping("/createasso")
    public String createAsso(){
        return "new_asso";
    }
}
