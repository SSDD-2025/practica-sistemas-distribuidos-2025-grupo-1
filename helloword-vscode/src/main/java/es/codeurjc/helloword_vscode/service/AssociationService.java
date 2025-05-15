package es.codeurjc.helloword_vscode.service;

import org.hibernate.engine.jdbc.BlobProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import es.codeurjc.helloword_vscode.repository.AssociationRepository;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

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

			// Verify if the user isn't already in the association
            if (!association.getMembers().contains(user)) {

				// If the user is the first one to join an association he will become president else he will be member
				if(association.getMembers().isEmpty()){
					MemberType memberType = new MemberType("president", user, association);
                	memberTypeService.save(memberType);
				} else {
					MemberType memberType = new MemberType("member", user, association);
                	memberTypeService.save(memberType);
				}
            }
        }
    }
	
	/* Delete user from an association */
	@Transactional
	public void deleteUserFromAssociation(Long associationId, Long userId) {
		Optional<Association> associationOpt = associationRepository.findById(associationId);
		Optional<Member> memberOpt = memberService.findById(userId);

		if (associationOpt.isPresent() && memberOpt.isPresent()) {
			Member member = memberOpt.get();
	
			// Find MemberType matching
			List<MemberType> memberTypes = member.getMemberTypes().stream()
				.filter(mt -> mt.getAssociation().getId() == associationId)
				.collect(Collectors.toList());

			for (MemberType memberType : memberTypes) {
				if ("president".equalsIgnoreCase(memberType.getName())) {
					throw new IllegalStateException("You must choose a new president before leaving the association");
				}
				memberTypeService.delete(memberType);
			}
		}
	}

	public Map<String, Object> getAssociationViewModel(Long associationId, String currentUsername, boolean isAdmin) {
		Optional<Association> optAsso = findById(associationId);
		if (optAsso.isEmpty()) {
			throw new ResourceNotFoundException("Association not found");
		}

		Association asso = optAsso.get();
		Map<String, Object> modelMap = new HashMap<>();

		modelMap.put("association", asso);
		modelMap.put("minutes", asso.getMinutes());
		modelMap.put("hasImage", asso.getImageFile() != null);
		modelMap.put("isAdmin", isAdmin);

		List<Map<String, Object>> memberTypeData = asso.getMemberTypes().stream().map(mt -> {
			Map<String, Object> data = new HashMap<>();
			data.put("id", mt.getId());
			data.put("name", mt.getName());
			data.put("member", mt.getMember());
			data.put("presidentSelected", "president".equalsIgnoreCase(mt.getName()));
			data.put("vicePresidentSelected", "vice-president".equalsIgnoreCase(mt.getName()));
			data.put("secretarySelected", "secretary".equalsIgnoreCase(mt.getName()));
			data.put("treasurerSelected", "treasurer".equalsIgnoreCase(mt.getName()));
			data.put("memberSelected", "member".equalsIgnoreCase(mt.getName()));
			return data;
		}).collect(Collectors.toList());

		modelMap.put("memberTypes", memberTypeData);

		Optional<Member> user = currentUsername != null ? memberService.findByName(currentUsername) : Optional.empty();

		boolean isMember = user.isPresent() && asso.getMembers().contains(user.get());
		boolean isPresident = user.isPresent() &&
			memberTypeService.getPresident(asso).map(p -> p.equals(user.get())).orElse(false);

		modelMap.put("isMember", isMember);
		modelMap.put("isPresident", isPresident);

		return modelMap;
	}

	/* Edit the name of an association */
	public void updateAsso(Association association, String name){
		association.setName(name);
		associationRepository.save(association);
	}

	/* Edit an association with image */
	public void updateAssoImage(Association association, String name, MultipartFile multipartFile) throws IOException {
		association.setName(name);
		if(!multipartFile.isEmpty()) {
			// Set the image file as a Blob in the association
			association.setImageFile(BlobProxy.generateProxy(multipartFile.getInputStream(), multipartFile.getSize()));
		}
		associationRepository.save(association);
	}

	/* Delete image from association */
	public void deleteImage(Association association){
		association.setImageFile(null);
		associationRepository.save(association);
	}
}
