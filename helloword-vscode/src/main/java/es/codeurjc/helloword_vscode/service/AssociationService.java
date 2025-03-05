package es.codeurjc.helloword_vscode.service;

// Importing necessary classes
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import es.codeurjc.helloword_vscode.entities.Minute;
import es.codeurjc.helloword_vscode.repository.AssociationRepository;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;
import es.codeurjc.helloword_vscode.entities.Association;
import es.codeurjc.helloword_vscode.ResourceNotFoundException;

@Service // Marks the class as a Spring service component, eligible for dependency injection
public class AssociationService {

    @Autowired // Automatically injects the AssociationRepository dependency
    private AssociationRepository associationRepository;

    @Transactional // Marks the method as transactional, ensuring the operations are done within a transaction
    public List<Minute> getMinutes(Long associationId) {
        // Find the association by its ID, or throw an exception if not found
        Association association = associationRepository.findById(associationId)
            .orElseThrow(() -> new ResourceNotFoundException("Association not found with id: " + associationId));
        
        // Return the list of minutes associated with the found association
        return association.getMinutes();
    }

}
