package es.codeurjc.helloword_vscode.controller;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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
import es.codeurjc.helloword_vscode.service.AssociationService;
import es.codeurjc.helloword_vscode.service.MinuteService;

@Controller
public class MinuteWebController {

    // Service for database interaction 

    @Autowired
	private MinuteService minuteService;

    @Autowired
	private AssociationService associationService;



    /* Create mintue */
    @PostMapping("/association/{id}/new_minute")
    public String createMinute(@PathVariable long id, String date, @RequestParam List<Long> participantsIds, String content, double duration, Model model) throws Exception {
        Optional<Association> optAsso = associationService.findById(id);
        if(optAsso.isPresent()){
            Map<String, Object> result = minuteService.processCreateMinute(optAsso.get(), date, participantsIds, content, duration);
            if (result.containsKey("error")) {
                model.addAllAttributes(result);
                return "new_minute";
            }
        }
        return "redirect:/association/" + id;
    }


    /* Page with forms to create minute (only if you're in association) */
    @PostMapping("/association/{id}/createMinute")
	public String createMinute(Model model, @PathVariable long id) {
        // Retrieve the association by ID
        Optional<Association> association = associationService.findById(id);
        if(association.isPresent()){
            // Add association and members to the model
            model.addAttribute("association", association.get());
            model.addAttribute("members", minuteService.findMembers(association.get()));
            model.addAttribute("today", LocalDate.now());
            return"new_minute"; 
        }
        return "redirect:/association/" + id;
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
        model.addAttribute("members", minuteService.findMembers(association.get()));
        model.addAttribute("participants", minuteService.findParticipants(minute));
        model.addAttribute("noPart", minuteService.findNoParticipants(association.get(), minute));

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
        if(association.isPresent()){
            // Save the updated minute
            minuteService.update(minute, date, participantsIds, content, duration, association.get());
        }
        return "redirect:/association/" + assoId;
    }
}
