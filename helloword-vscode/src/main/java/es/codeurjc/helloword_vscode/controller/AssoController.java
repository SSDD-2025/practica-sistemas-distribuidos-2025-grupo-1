package es.codeurjc.helloword_vscode.controller;

import java.io.IOException;
import java.security.Principal;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.Optional;

import javax.sql.rowset.serial.SerialBlob;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
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
import org.springframework.web.multipart.MultipartFile;

import es.codeurjc.helloword_vscode.model.Association;
import es.codeurjc.helloword_vscode.model.UtilisateurEntity;
import es.codeurjc.helloword_vscode.service.AssociationService;
import es.codeurjc.helloword_vscode.service.UtilisateurEntityService;
import jakarta.servlet.http.HttpServletRequest;



@Controller
public class AssoController {

    // Service for database interaction 

    @Autowired
    private UtilisateurEntityService utilisateurEntityService;

    @Autowired
	private AssociationService associationService;


    /* Adds authentication attributes to all templates */ 
    @ModelAttribute
    public void addAttributes(Model model, HttpServletRequest request) {

        // Retrieve the current authentication information
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        
        // Determine if the user is authenticated and not anonymous
        boolean isAuthenticated = auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getName());
        model.addAttribute("isAuthenticated", isAuthenticated);
        
        // If authenticated, add the username to the model
        if (isAuthenticated && auth.getPrincipal() instanceof UserDetails) {
            UserDetails userDetails = (UserDetails) auth.getPrincipal();
            model.addAttribute("username", userDetails.getUsername());
        }

        // Attributes are created based on the user
        Principal principal = request.getUserPrincipal();
        if(principal != null) {
		
			model.addAttribute("logged", true);		
			model.addAttribute("userName", principal.getName());
			model.addAttribute("admin", request.isUserInRole("ADMIN"));
			
		} else {
			model.addAttribute("logged", false);
		}
        
    }


    /* Home page: Displays associations */
    @GetMapping("/")
    public String getPosts(Model model, HttpServletRequest request) {
        // Fetch all associations and add them to the model
        model.addAttribute("associations", associationService.findAll());
        
        // Check if the user has the ADMIN role and add this information to the model
        model.addAttribute("isAdmin", request.isUserInRole("ADMIN"));
        return "index";
    }


    /* Displays details of a specific association */ 
    @GetMapping("/association/{id}")
    public String associationId(@PathVariable long id, Model model, Principal principal, HttpServletRequest request) {
        // Retrieve the association by ID
        Optional<Association> asso = associationService.findById(id);
        
        if (asso.isPresent()) {
            // Add association details to the model
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


    /* Allows a user to join an association */ 
    @PostMapping("/association/{id}/join")
    public String joinAssociation(@PathVariable Long id, Principal principal) {
        if (principal != null) {
            String username = principal.getName();
            Optional<UtilisateurEntity> user = utilisateurEntityService.findByName(username);
            if (user.isPresent()) {
                associationService.addUserToAssociation(id, user.get().getId());
            }
        }
        return "redirect:/association/" + id;
    }    


    /*  Delete association (only for admins) */
    @PostMapping("/association/{id}/delete")
    @PreAuthorize("hasRole('ADMIN')")
	public String deleteAssociation(@PathVariable long id, Authentication auth) {
	    // Retrieve the association by ID	
        Optional<Association> association = associationService.findById(id);
		if (association.isPresent()) {
            // Delete the association by its ID         
			associationService.deleteById(id);
			return "redirect:/";
		} else {
			return "redirect:/";
		}
	}


    /* Page with form to create association (only for admins) */ 
    @GetMapping("/createasso")
    public String createAsso(){
        return "new_asso";
    }


    /* Create a new association (only for admins) */ 
    @PostMapping("/association/create")
    @PreAuthorize("hasRole('ADMIN')")
    public String createAssociation(Model model, Association association, MultipartFile image) throws Exception {
        associationService.save(association, image);
        return "redirect:/";
    }


    /* Download Image on association */ 
    @GetMapping("/association/{id}/image")
    public ResponseEntity<Object> downloadImage(@PathVariable long id) throws SQLException {
        // Retrieve the association by ID
        Optional<Association> op = associationService.findById(id);
        if (op.isPresent() && op.get().getImageFile() != null) {
            // Get the image file as a Blob
            Blob image = op.get().getImageFile();
            Resource file = new InputStreamResource(image.getBinaryStream());
            
            // Return the image as a response entity
            return ResponseEntity.ok().header(HttpHeaders.CONTENT_TYPE, "image/png")
            .contentLength(image.length()).body(file);
        } else {
            // Return a not found response if the image does not exist
            return ResponseEntity.notFound().build();
        }
    }

    /* Page to edit association (only for admins) */ 
	@GetMapping("/editasso/{id}")
    @PreAuthorize("hasRole('ADMIN')")
	public String editAsso(Model model, @PathVariable long id) {
		// Retrieve the association by ID
        Optional<Association> association = associationService.findById(id);
		if (association.isPresent()) {
            // Add the association to the model
			model.addAttribute("association", association.get());
			return "editAssoPage";
		} else {
			return "redirect:/";
		}
	}
    

    /* Edit association (only for admins) */ 
    @PostMapping("/editasso")
    public String editAssoProcess(@RequestParam Long id,
                                  @RequestParam String name,
                                  @RequestParam(required = false) MultipartFile image,
                                  Model model) {
        // Retrieve the association by ID                            
        Optional<Association> optAsso = associationService.findById(id);
        
        if (optAsso.isPresent()) {
            Association asso = optAsso.get();
            // Update the association's name
            asso.setName(name);
    
            // Update the association's image if a new one is provided
            if (image != null && !image.isEmpty()) {
                try {
                    byte[] bytes = image.getBytes();
                    Blob blob = new SerialBlob(bytes);
                    asso.setImageFile(blob);
                } catch (SQLException | IOException e) {
                    e.printStackTrace();
                }
            }    
            // Save the updated association
            associationService.save(asso);
            return "redirect:/association/" + id;
        } else {
            return "redirect:/";
        }
    }


    /* Delete image from association */ 
    @PostMapping("/association/{id}/deleteImage")
    public String deleteAssociationImage(@PathVariable long id) {
        // Retrieve the association by ID
        Optional<Association> optAsso = associationService.findById(id);
        if (optAsso.isPresent()) {
            Association asso = optAsso.get();

            // Remove the image from the association
            asso.setImageFile(null);

            // Save the updated association
            associationService.save(asso);
        }
        return "redirect:/editasso/"+id;
    }
}
