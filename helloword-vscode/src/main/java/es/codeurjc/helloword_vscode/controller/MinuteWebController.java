package es.codeurjc.helloword_vscode.controller;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import es.codeurjc.helloword_vscode.model.Association;
import es.codeurjc.helloword_vscode.model.Minute;
import es.codeurjc.helloword_vscode.model.Member;
import es.codeurjc.helloword_vscode.service.AssociationService;
import es.codeurjc.helloword_vscode.service.MinuteService;
import es.codeurjc.helloword_vscode.service.MemberService;

@Controller
public class MinuteWebController {

    // Service for database interaction 

    @Autowired
	private MinuteService minuteService;

    @Autowired
	private AssociationService associationService;

    @Autowired
    private MemberService memberService;


    /* Create mintue */
    @PostMapping("/association/{id}/new_minute")
	public String createMinute(@PathVariable long id, String date, @RequestParam List<Long> participantsIds, String content, double duration, Model model) throws Exception {
        // Retrieve the association by ID
        Optional<Association> association = associationService.findById(id);
        if (association.isPresent()) {
            try {
                // Parse the submitted date
                LocalDate submittedDate = LocalDate.parse(date); 
                
                 // Check if the date is in the future
                if (submittedDate.isAfter(LocalDate.now())) {
                    model.addAttribute("error", "The date can not be in the futur");
                    model.addAttribute("association", association.get());
                    model.addAttribute("members", association.get().getMembers());
                    return "new_minute";
                }

                // Create a new minute object    
                Minute minute = new Minute();
                minute.setDate(date);

                // Retrieve participants by their IDs
                List<Member> participants = participantsIds.stream()
                .map(participantId -> memberService.findById(participantId).orElse(null))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

                // Set minute attributes
                minute.setParticipants(participants);
                minute.setContent(content);
                minute.setDuration(duration);
                minute.setAssociation(association.get());

                // Save the new minute
                minuteService.save(minute);
                return "redirect:/association/" + id;
		    } catch (DateTimeParseException e) {
                model.addAttribute("error", "Format de date invalide.");
                return "new_minute";
            } 
        } else {
			return "redirect:/";
		}
        
	}


    /* Page with forms to create minute (only if you're in association) */
    @PostMapping("/association/{id}/createMinute")
	public String createMinute(Model model, @PathVariable long id) {
        // Retrieve the association by ID
        Optional<Association> association = associationService.findById(id);
        
        // Add association and members to the model
        model.addAttribute("association", association.get());
        model.addAttribute("members", association.get().getMembers());
        model.addAttribute("today", LocalDate.now());
        return"new_minute";
	}


    /* Delete minute */
    @PostMapping("/minute/{minuteId}/asso/{assoId}/delete")
    public String deleteMinute(@PathVariable Long assoId, @PathVariable Long minuteId){
        minuteService.deleteMinuteById(minuteId, assoId);
        return "redirect:/association/" + assoId;
    }


    /* Edit minute page */
    @GetMapping("/minute/{minuteId}/asso/{assoId}/edit")
    @PreAuthorize("hasRole('ADMIN')")
	public String editMinute(Model model, @PathVariable Long assoId, @PathVariable Long minuteId) {
		// Retrieve the association and minute by their IDs
        Optional<Association> association = associationService.findById(assoId);
        Minute minute = minuteService.findById(minuteId).orElseThrow();
        
        // Add association, minute, and related data to the model
        model.addAttribute("association", association.get());
        model.addAttribute("minute", minute);
        model.addAttribute("today", LocalDate.now());
        model.addAttribute("members", association.get().getMembers());
        model.addAttribute("participants", minute.getParticipants());

        // Create a list of members who did not participate in the meeting
        Collection<Member> members = association.get().getMembers();;
        Collection<Member> participants = minute.getParticipants();
        Collection<Member> memberNoPart = new HashSet<Member>();
        memberNoPart.addAll(members);
        memberNoPart.removeAll(participants);
        model.addAttribute("noPart", memberNoPart);

		if (association.isPresent()) {
			return "editMinutePage";
		} else {
			return "redirect:/";
		}
	}


    /* Edit minute process */
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
        // Check if participants are selected                                
        if (participantsIds == null || participantsIds.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "You need to select at least one participant");
            return "redirect:/minute/" + minuteId + "/asso/" + assoId + "/edit";
        }
        
        // Retrieve the minute and association by their IDs
        Minute minute = minuteService.findById(minuteId).orElseThrow();
        Optional<Association> association = associationService.findById(assoId);
        
        // Update minute attributes
        minute.setDate(date);
        List<Member> participants = participantsIds.stream()
            .map(participantId -> memberService.findById(participantId).orElse(null))
            .filter(Objects::nonNull)
            .collect(Collectors.toList());
        minute.setParticipants(participants);
        minute.setContent(content);
        minute.setDuration(duration);
        minute.setAssociation(association.get());

        // Save the updated minute
        minuteService.save(minute);
        return "redirect:/association/" + assoId;
    }
}
