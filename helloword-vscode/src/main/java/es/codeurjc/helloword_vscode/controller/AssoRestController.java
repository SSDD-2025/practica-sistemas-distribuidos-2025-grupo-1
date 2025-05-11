package es.codeurjc.helloword_vscode.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import es.codeurjc.helloword_vscode.model.Association;
import es.codeurjc.helloword_vscode.service.AssociationService;

@RestController
@RequestMapping("/api/associations")
public class AssoRestController {

    @Autowired
    private AssociationService associationService;

    // GET all associations
    @GetMapping
    public List<Association> getAllAssociations() {
        return associationService.findAll();
    }

    // GET one association by id
    @GetMapping("/{id}")
    public Association getAssociation(@PathVariable Long id) {
        Optional<Association> asso = associationService.findById(id);
        return asso.orElseThrow(() -> new RuntimeException("Association not found"));
    }

    // POST create a new association
    @PostMapping
    public Association createAssociation(@RequestBody Association association) {
        associationService.save(association);
        return association;
    }

    // PUT update an existing association
    @PutMapping("/{id}")
    public Association updateAssociation(@PathVariable Long id, @RequestBody Association updatedAssociation) {
        Optional<Association> optional = associationService.findById(id);
        if (optional.isPresent()) {
            Association asso = optional.get();
            asso.setName(updatedAssociation.getName());
            // Ajoute d'autres champs si besoin
            associationService.save(asso);
            return asso;
        } else {
            throw new RuntimeException("Association not found");
        }
    }

    // DELETE an association
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAssociation(@PathVariable long id) {
        if (associationService.findById(id).isPresent()) {
            associationService.deleteById(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
