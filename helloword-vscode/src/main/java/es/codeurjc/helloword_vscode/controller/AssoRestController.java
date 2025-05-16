package es.codeurjc.helloword_vscode.controller;

import es.codeurjc.helloword_vscode.dto.AssociationDTO;
import es.codeurjc.helloword_vscode.service.AssociationService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.List;

@RestController
@RequestMapping("/api/associations")
public class AssoRestController {

    @Autowired
    private AssociationService associationService;

    // GET all associations
    @GetMapping
    public List<AssociationDTO> getAllAssociations() {
        return associationService.findAll();
    }

    // GET one association
    @GetMapping("/{id}")
    public ResponseEntity<AssociationDTO> getAssociationById(@PathVariable long id) {
        return associationService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // DELETE an association
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteAssociation(@PathVariable long id) {
        if (associationService.findById(id).isPresent()) {
            associationService.deleteById(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
