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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import es.codeurjc.helloword_vscode.model.Association;
import es.codeurjc.helloword_vscode.model.AssociationMemberTypeDTO;
import es.codeurjc.helloword_vscode.model.MemberType;
import es.codeurjc.helloword_vscode.model.Minute;
import es.codeurjc.helloword_vscode.model.UtilisateurEntity;
import es.codeurjc.helloword_vscode.service.*;
import jakarta.servlet.http.HttpServletRequest;
import java.time.format.DateTimeParseException;
import java.util.Collection;



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

    

    @PostMapping("/association/{id}/new_minute")
	public String createMinute(@PathVariable long id, String date, @RequestParam List<Long> participantsIds, String content, double duration, Model model) throws Exception {
        Optional<Association> association = associationService.findById(id);

        if (association.isPresent()) {
            try {
                LocalDate submittedDate = LocalDate.parse(date); 
                if (submittedDate.isAfter(LocalDate.now())) {
                    model.addAttribute("error", "The date can not be in the futur");
                    model.addAttribute("association", association.get());
                    model.addAttribute("members", association.get().getMembers());
                    return "new_minute";
                }
			Minute minute = new Minute();
            minute.setDate(date);
            List<UtilisateurEntity> participants = participantsIds.stream()
            .map(participantId -> utilisateurEntityService.findById(participantId).orElse(null))
            .filter(Objects::nonNull)
            .collect(Collectors.toList());
            minute.setParticipants(participants);
            minute.setContent(content);
            minute.setDuration(duration);
            minute.setAssociation(association.get());
            minuteService.save(minute);
			return "redirect:/association/" + id;
		         } catch (DateTimeParseException e) {
            model.addAttribute("error", "Format de date invalide.");
            return "new_minute";
        } }
        else {
			return "redirect:/";
		}
        
	}

    @PostMapping("/association/{id}/createMinute")
	public String createMinute(Model model, @PathVariable long id) {
        Optional<Association> association = associationService.findById(id);
        model.addAttribute("association", association.get());
        model.addAttribute("members", association.get().getMembers());
        model.addAttribute("today", LocalDate.now());
        return"new_minute";
	}

    @GetMapping("/createasso")
    public String createAsso(){
        return "new_asso";
    }

    @PostMapping("/minute/{minuteId}/asso/{assoId}/delete")
    public String deleteMinute(@PathVariable Long assoId, @PathVariable Long minuteId){
        Minute minute = minuteService.findById(minuteId).orElseThrow();
        List<UtilisateurEntity> utilisateurs = minute.getParticipants();
        minuteService.delete(minute, assoId, utilisateurs);
        return "redirect:/association/" + assoId;
    }

    @GetMapping("/minute/{minuteId}/asso/{assoId}/edit")
    @PreAuthorize("hasRole('ADMIN')")
	public String editMinute(Model model, @PathVariable Long assoId, @PathVariable Long minuteId) {
		Optional<Association> association = associationService.findById(assoId);
        Minute minute = minuteService.findById(minuteId).orElseThrow();
        model.addAttribute("association", association.get());
        model.addAttribute("minute", minute);
        model.addAttribute("today", LocalDate.now());
        model.addAttribute("members", association.get().getMembers());
        model.addAttribute("participants", minute.getParticipants());

        //Create a list of all members association who doesn't attend to the meeting
        Collection<UtilisateurEntity> members = association.get().getMembers();;
        Collection<UtilisateurEntity> participants = minute.getParticipants();
        Collection<UtilisateurEntity> memberNoPart = new HashSet<UtilisateurEntity>();
        memberNoPart.addAll(members);
        memberNoPart.removeAll(participants);
        model.addAttribute("noPart", memberNoPart);

		if (association.isPresent()) {
			return "editMinutePage";
		} else {
			return "redirect:/";
		}
	}

    @PostMapping("/editminute")
    public String editMinuteProcess(@RequestParam long minuteId, 
                                    @RequestParam long assoId,
                                    @RequestParam String date,
                                    @RequestParam(required = false) List<Long> participantsIds, 
                                    @RequestParam String content, 
                                    @RequestParam double duration, 
                                    Model model,
                                    RedirectAttributes redirectAttributes
                                    ) throws IOException {
        if (participantsIds == null || participantsIds.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "You need to select at least one participant");
            return "redirect:/minute/" + minuteId + "/asso/" + assoId + "/edit";
        }
        Minute minute = minuteService.findById(minuteId).orElseThrow();
        Optional<Association> association = associationService.findById(assoId);
        minute.setDate(date);
        List<UtilisateurEntity> participants = participantsIds.stream()
        .map(participantId -> utilisateurEntityService.findById(participantId).orElse(null))
        .filter(Objects::nonNull)
        .collect(Collectors.toList());
        minute.setParticipants(participants);
        minute.setContent(content);
        minute.setDuration(duration);
        minute.setAssociation(association.get());
        minuteService.save(minute);
        return "redirect:/association/" + assoId;
    }

}
