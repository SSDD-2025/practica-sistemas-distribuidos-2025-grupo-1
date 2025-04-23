package es.codeurjc.helloword_vscode.service;

import org.hibernate.engine.jdbc.BlobProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import es.codeurjc.helloword_vscode.repository.AssociationRepository;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import es.codeurjc.helloword_vscode.ResourceNotFoundException;
import es.codeurjc.helloword_vscode.model.Association;
import es.codeurjc.helloword_vscode.model.Minute;

@Service
public class AssociationService {
    @Autowired
	private AssociationRepository associationRepository;

    public void save(Association association) {
		associationRepository.save(association);
	}

	public void save(Association association, MultipartFile imageFile) throws IOException{
		if(!imageFile.isEmpty()) {
			association.setImageFile(BlobProxy.generateProxy(imageFile.getInputStream(), imageFile.getSize()));
		}
		this.save(association);
	}

	public Optional<Association> findById(long id) {
		return associationRepository.findById(id);
	}
	
    @Transactional
    public List<Minute> getMinutes(Long associationId) {
        Association association = associationRepository.findById(associationId)
            .orElseThrow(() -> new ResourceNotFoundException("Association non trouvée avec l'id : " + associationId));
        return association.getMinutes();
    }

	public List<Association> findAll() {
		return associationRepository.findAll();
	}

	public void deleteById(long id) {
		try {
			associationRepository.deleteById(id);
	    } catch (Exception e) {
			System.err.println("Erreur lors de la suppression de l'association : " + e.getMessage());
		};		
	}

}
