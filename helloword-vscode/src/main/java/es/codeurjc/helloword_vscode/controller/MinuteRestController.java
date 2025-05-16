package es.codeurjc.helloword_vscode.controller;

import es.codeurjc.helloword_vscode.dto.MinuteDTO;
import es.codeurjc.helloword_vscode.model.Minute;
import es.codeurjc.helloword_vscode.model.Member;
import es.codeurjc.helloword_vscode.model.Association;
import es.codeurjc.helloword_vscode.service.MinuteService;
import es.codeurjc.helloword_vscode.service.MemberService;
import es.codeurjc.helloword_vscode.service.AssociationService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/minutes")
public class MinuteRestController {

    @Autowired
    private MinuteService minuteService;

    @Autowired
    private MemberService memberService;

    @Autowired
    private AssociationService associationService;

    // GET all minutes
    @GetMapping
    public List<MinuteDTO> getAllMinutes() {
        return minuteService.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // GET minute by ID
    @GetMapping("/{id}")
    public ResponseEntity<MinuteDTO> getMinuteById(@PathVariable long id) {
        Optional<Minute> minuteOpt = minuteService.findById(id);
        return minuteOpt.map(minute -> ResponseEntity.ok(convertToDTO(minute)))
                        .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // DELETE a minute
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteMinute(@PathVariable long id) {
        Optional<Minute> minuteOpt = minuteService.findById(id);
        if (minuteOpt.isPresent()) {
            Minute minute = minuteOpt.get();
            Association asso = minute.getAssociation();
            List<Member> participants = minute.getParticipants();
            minuteService.delete(minute, asso.getId(), participants);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // Utility method to convert entity to DTO
    private MinuteDTO convertToDTO(Minute minute) {
        List<Long> participantIds = minute.getParticipants().stream()
                .map(Member::getId)
                .collect(Collectors.toList());

        return new MinuteDTO(
                minute.getId(),
                minute.getDate(),
                participantIds,
                minute.getContent(),
                minute.getDuration(),
                minute.getAssociation().getId()
        );
    }
}
