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
import es.codeurjc.helloword_vscode.model.MemberType;
import es.codeurjc.helloword_vscode.model.Minute;
import es.codeurjc.helloword_vscode.model.Member;

/* 
 * This service class provides methods to perform various operations on
 * the Association entity, such as saving, retrieving, and deleting 
 * associations. It interacts with the AssociationRepository to perform 
 * database operations.
*/
@Service
public class AssociationService {

	// Autowired repository for database interactions
    @Autowired
	private AssociationRepository associationRepository;

    @Autowired
    private MemberService memberService;

    @Autowired
    private MemberTypeService memberTypeService;

	/* Save association without image */
    public void save(Association association) {
		associationRepository.save(association);
	}


	/* Save association with image */
	public void save(Association association, MultipartFile imageFile) throws IOException{
		if(!imageFile.isEmpty()) {
			// Set the image file as a Blob in the association
			association.setImageFile(BlobProxy.generateProxy(imageFile.getInputStream(), imageFile.getSize()));
		}
		// Save association
		this.save(association);
	}


	/* Find association by ID */
	public Optional<Association> findById(long id) {
		return associationRepository.findById(id);
	}
	
	
	/* Get all minutes associated with a specific association */
    @Transactional
    public List<Minute> getMinutes(Long associationId) {
        Association association = associationRepository.findById(associationId)
            .orElseThrow(() -> new ResourceNotFoundException("Association non trouvée avec l'id : " + associationId));
        return association.getMinutes();
    }


	/* Find all associations */
	public List<Association> findAll() {
		return associationRepository.findAll();
	}


	/* Delete association by ID */
	public void deleteById(long id) {
		try {
			associationRepository.deleteById(id);
	    } catch (Exception e) {
			// Log the error message if deletion fails
			System.err.println("Erreur lors de la suppression de l'association : " + e.getMessage());
		};		
	}


	/* Add user to an association */
	public void addUserToAssociation(Long associationId, Long userId) {
        Optional<Association> associationOpt = associationRepository.findById(associationId);
        Optional<Member> userOpt = memberService.findById(userId);

        if (associationOpt.isPresent() && userOpt.isPresent()) {
            Association association = associationOpt.get();
            Member user = userOpt.get();

            if (!association.getMembers().contains(user)) {
                MemberType memberType = new MemberType("member", user, association);
                memberTypeService.save(memberType);
            }
        }
    }
}
