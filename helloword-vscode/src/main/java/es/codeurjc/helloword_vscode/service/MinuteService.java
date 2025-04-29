package es.codeurjc.helloword_vscode.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import es.codeurjc.helloword_vscode.ResourceNotFoundException;
import es.codeurjc.helloword_vscode.model.Association;
import es.codeurjc.helloword_vscode.model.Minute;
import es.codeurjc.helloword_vscode.model.UtilisateurEntity;
import es.codeurjc.helloword_vscode.repository.MinuteRepository;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

/**
 * This service class provides methods to perform various operations on Minute entities,
 * such as saving, retrieving, and deleting minutes. It interacts with the MinuteRepository
 * and AssociationService to perform database operations.
*/
@Service
public class MinuteService {
	// Autowired repositories and services for database interactions
    @Autowired
	private MinuteRepository minuteRepository;

	@Autowired
	private AssociationService associationService;


	/* Find all minutes */
    public List<Minute> findAll() {
		return minuteRepository.findAll();
	}


	/* Save minute */
	public void save (Minute minute) throws IOException{
		minuteRepository.save(minute);
	}


	/* Find minute by ID */
	public Optional<Minute> findById(long id){
		return minuteRepository.findById(id);
	}


	/* Delete minute and update association */
	public void delete(Minute minute, Long assoId, List <UtilisateurEntity> utilisateurs) {
		// Retrieve the association by ID
		Association association = associationService.findById(assoId).orElseThrow();

        // Remove the minute from the association's list of minutes		
		association.getMinutes().remove(minute);
		
		// Remove the minute from each user's list of minutes
		for (UtilisateurEntity utilisateur : utilisateurs ){
			utilisateur.getMinutes().remove(minute);
		}

		// Delete the minute from the repository
		this.minuteRepository.delete(minute);
	}


	/* Delete minute with association and minute ID */
	public void deleteMinuteById(Long minuteId, Long assoId) {
        Minute minute = minuteRepository.findById(minuteId)
            .orElseThrow(() -> new ResourceNotFoundException("Minute not found with id: " + minuteId));
        Association association = associationService.findById(assoId)
            .orElseThrow(() -> new ResourceNotFoundException("Association not found with id: " + assoId));

        association.getMinutes().remove(minute);
        for (UtilisateurEntity utilisateur : minute.getParticipants()) {
            utilisateur.getMinutes().remove(minute);
        }
        minuteRepository.delete(minute);
    }


	/* Find all Minute entities that contain the specified participant */
	List<Minute> findAllByParticipantsContains(UtilisateurEntity participant){
		return minuteRepository.findAllByParticipantsContains(participant);
	}
}