package es.codeurjc.helloword_vscode.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import es.codeurjc.helloword_vscode.entities.Minute;
import es.codeurjc.helloword_vscode.repository.AssociationRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

import es.codeurjc.helloword_vscode.entities.Association;

import es.codeurjc.helloword_vscode.ResourceNotFoundException;

@Service
public class AssociationService {
    @Autowired
	private AssociationRepository associationRepository;

    @Transactional
    public List<Minute> getMinutes(Long associationId) {
        Association association = associationRepository.findById(associationId)
            .orElseThrow(() -> new ResourceNotFoundException("Association non trouvée avec l'id : " + associationId));
        return association.getMinutes();
    }

}
