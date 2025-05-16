package es.codeurjc.helloword_vscode.controller;

import es.codeurjc.helloword_vscode.dto.AssociationDTO;
import es.codeurjc.helloword_vscode.model.Association;
import es.codeurjc.helloword_vscode.model.Member;
import es.codeurjc.helloword_vscode.model.Minute;
import es.codeurjc.helloword_vscode.service.AssociationService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.stream.Collectors;
import java.util.List;

@RestController
@RequestMapping("/api/associations")
public class AssoRestController {

    @Autowired
    private AssociationService associationService;

    // GET all associations
    @GetMapping
    public List<AssociationDTO> getAllAssociations() {
        return associationService.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // GET one association
    @GetMapping("/{id}")
    public ResponseEntity<AssociationDTO> getAssociationById(@PathVariable long id) {
        Optional<Association> assoOpt = associationService.findById(id);
        if (assoOpt.isPresent()) {
            return ResponseEntity.ok(convertToDTO(assoOpt.get()));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // DELETE an association
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteAssociation(@PathVariable long id) {
        Optional<Association> association = associationService.findById(id);
        if (association.isPresent()) {
            associationService.deleteById(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // Utility method to convert Entity to DTO
    private AssociationDTO convertToDTO(Association association) {
        List<Long> memberIds = association.getMembers().stream()
                .map(Member::getId)
                .collect(Collectors.toList());

        List<Long> minuteIds = association.getMinutes().stream()
                .map(Minute::getId)
                .collect(Collectors.toList());

        return new AssociationDTO(
                association.getId(),
                association.getName(),
                association.getImageFile() != null,
                memberIds,
                minuteIds
        );
    }
}
