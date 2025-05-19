package es.codeurjc.helloword_vscode.controller;

import java.io.IOException;
import java.security.Principal;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.List;

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
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import es.codeurjc.helloword_vscode.ResourceNotFoundException;
import es.codeurjc.helloword_vscode.dto.AssociationDTO;
import es.codeurjc.helloword_vscode.dto.MemberTypeDTO;
import es.codeurjc.helloword_vscode.dto.MinuteDTO;
import es.codeurjc.helloword_vscode.dto.NewAssoRequestDTO;
import es.codeurjc.helloword_vscode.model.Member;
import es.codeurjc.helloword_vscode.model.MemberType;
import es.codeurjc.helloword_vscode.model.Minute;
import es.codeurjc.helloword_vscode.service.AssociationService;
import es.codeurjc.helloword_vscode.service.MemberService;
import jakarta.servlet.http.HttpServletRequest;



@Controller
public class AssoWebController {

    // Service for database interaction 

    @Autowired
    private MemberService memberService;

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

            // Check if the user has the ADMIN role and add this information to the model
			model.addAttribute("isAdmin", request.isUserInRole("ADMIN"));
			
		} else {
			model.addAttribute("logged", false);
		}
        
    }


    /* Home page: Displays associations */
    @GetMapping("/")
    public String getPosts(Model model, HttpServletRequest request) {
        // Fetch all associations and add them to the model
        model.addAttribute("associations", associationService.findAllDTOs());
        return "index";
    }


    /* Displays details of a specific association */ 
    @GetMapping("/association/{id}")
    public String associationId(@PathVariable long id, Model model, Principal principal, HttpServletRequest request) {
        String username = (principal != null) ? principal.getName() : null;
        boolean isAdmin = request.isUserInRole("ADMIN");

        try {
            Map<String, Object> attributes = associationService.getAssociationViewModel(id, username, isAdmin);
            model.addAllAttributes(attributes);
            return "association_detail";
        } catch (ResourceNotFoundException e) {
            return "asso_not_found";
        }
    }



    /* Allows an user to join an association */ 
    @PostMapping("/association/{id}/join")
    public String joinAssociation(@PathVariable Long id, Principal principal) {
        if (principal != null) {
            String username = principal.getName();
            associationService.addUserToAssociation(id, username);
        }
        return "redirect:/association/" + id;
    }


    /* Allow an user to leave an association */ 
    @PostMapping("/association/{id}/leave")
    public String leaveAssociation(@PathVariable Long id, Principal principal, Model model, RedirectAttributes redirectAttributes) {
        if (principal != null) {
            String username = principal.getName();
            Optional<Member> user = memberService.findByName(username);
            if (user.isPresent()) {
                try {
                associationService.deleteUserFromAssociation(id, user.get().getId());
            } catch (IllegalStateException e) {
                // Redirect with a message
                redirectAttributes.addFlashAttribute("leaveError", e.getMessage());
                return "redirect:/association/" + id;
            }
            }
        }
        return "redirect:/association/" + id;
    } 

    // @PostMapping("/association/{id}/leave")
    // public String leaveAssociation(@PathVariable Long id, Principal principal, RedirectAttributes redirectAttributes) {
    //     if (principal != null) {
    //         String username = principal.getName();
    //         try {
    //             associationService.deleteUserFromAssociation(id, username);
    //         } catch (IllegalStateException e) {
    //             redirectAttributes.addFlashAttribute("leaveError", e.getMessage());
    //             return "redirect:/association/" + id;
    //         }
    //     }
    //     return "redirect:/association/" + id;
    // }


    /*  Delete association (only for admins) */
    @PostMapping("/association/{id}/delete")
    @PreAuthorize("hasRole('ADMIN')")
	public String deleteAssociation(@PathVariable long id, Authentication auth) {
        associationService.findByIdDTO(id); // throws if not found
        associationService.deleteAssociation(id); // Delete the association by ID
        return "redirect:/";
	}


    /* Page with form to create association (only for admins) */ 
    @GetMapping("/createasso")
    public String createAsso(){
        return "new_asso";
    }


    /* Create a new association (only for admins) */ 
    @PostMapping("/association/create")
    @PreAuthorize("hasRole('ADMIN')")
    public String createAssociation(Model model, NewAssoRequestDTO newAssoRequestDTO) throws IOException, SQLException {

        AssociationDTO createdAsso = createOrReplaceAssociation(null, newAssoRequestDTO, null);
        return "redirect:/association/" + createdAsso.id();
    }

    /* Download Image on association */ 
    @GetMapping("/association/{id}/image")
    public ResponseEntity<Object> downloadImage(@PathVariable long id) throws SQLException, IOException {
    Resource image = associationService.getImage(id);

    return ResponseEntity.ok()
        .header(HttpHeaders.CONTENT_TYPE, "image/png")
        .body(image);
    }

    /* Page to edit association (only for admins) */ 
	@GetMapping("/editasso/{id}")
    @PreAuthorize("hasRole('ADMIN')")
	public String editAsso(Model model, @PathVariable long id) {
        AssociationDTO association = associationService.findByIdDTO(id); // Retrieve the association by ID
        model.addAttribute("association", association);  // Add the association to the model
		return "editAssoPage";
	}
    

    /* Edit association (only for admins) */ 
    @PostMapping("/editasso")
    public String editAssoProcess(Model model, Long id,
                                    NewAssoRequestDTO newAssoRequestDTO,
                                    Boolean removeImage) throws IOException, SQLException {

        AssociationDTO updatedAsso = createOrReplaceAssociation(id, newAssoRequestDTO, removeImage);
        return "redirect:/association/" + updatedAsso.id();
    }


    private AssociationDTO createOrReplaceAssociation(Long id,
                                                    NewAssoRequestDTO request,
                                                    Boolean removeImage) throws IOException, SQLException {
        boolean image = false;
        if (id != null) {
            AssociationDTO old = associationService.findByIdDTO(id);
            image = (removeImage != null && removeImage) ? false : old.image();
        }

        List<MinuteDTO> minutes = Collections.emptyList();
        List<MemberTypeDTO> memberTypes = Collections.emptyList();

        AssociationDTO dto = new AssociationDTO(id, request.name(), image, null, memberTypes, minutes);
        AssociationDTO saved = associationService.createOrReplaceAssociation(id, dto);

        MultipartFile imageField = request.imageField();
        if (!imageField.isEmpty()) {
            associationService.createAssociationImage(dto.id(), imageField.getInputStream(), imageField.getSize());
        }

        return saved;
    }


    /* Delete image from association */ 
    @PostMapping("/association/{id}/deleteImage")
    public String deleteAssociationImage(@PathVariable long id) {
        associationService.deleteImage(id);
        return "redirect:/editasso/" + id;
    }
}
