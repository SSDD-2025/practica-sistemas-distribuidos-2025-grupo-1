package es.codeurjc.helloword_vscode.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import es.codeurjc.helloword_vscode.ResourceNotFoundException;
import es.codeurjc.helloword_vscode.model.Association;
import es.codeurjc.helloword_vscode.model.Minute;
import es.codeurjc.helloword_vscode.model.Member;
import es.codeurjc.helloword_vscode.repository.MinuteRepository;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

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

	@Autowired
	private MemberService memberService;


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
	public void delete(Minute minute, Long assoId, List <Member> members) {
		// Retrieve the association by ID
		Association association = associationService.findById(assoId).orElseThrow();

        // Remove the minute from the association's list of minutes		
		association.getMinutes().remove(minute);
		
		// Remove the minute from each user's list of minutes
		for (Member member : members ){
			member.getMinutes().remove(minute);
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
        for (Member member : minute.getParticipants()) {
            member.getMinutes().remove(minute);
        }
        minuteRepository.delete(minute);
    }


	/* Find all Minute entities that contain the specified participant */
	List<Minute> findAllByParticipantsContains(Member participant){
		return minuteRepository.findAllByParticipantsContains(participant);
	}

	/* Create new minute */
	public Map<String, Object> processCreateMinute(Association association, String dateStr, List<Long> participantIds, String content, double duration) {
		Map<String, Object> model = new HashMap<>();
		model.put("association", association);
		model.put("members", association.getMembers());

		try {
			LocalDate date = LocalDate.parse(dateStr);
			if (date.isAfter(LocalDate.now())) {
				model.put("error", "The date can not be in the futur");
				return model;
			}

			Minute minute = new Minute();
			minute.setDate(dateStr);
			List<Member> participants = participantIds.stream()
				.map(id -> memberService.findById(id).orElse(null))
				.filter(Objects::nonNull)
				.collect(Collectors.toList());
			minute.setParticipants(participants);
			minute.setContent(content);
			minute.setDuration(duration);
			minute.setAssociation(association);

			minuteRepository.save(minute);
			return model;
		} catch (DateTimeParseException e) {
			model.put("error", "Invalid date format.");
			return model;
		}
	}

	public List<Member> findMembers(Association association){
		return association.getMembers();
	}

	/* Find participants of a meeting */
	public List<Member> findParticipants(Minute minute){
		return minute.getParticipants();
	}

	/* Find association members who didn't attend the meeting */
	public Collection<Member> findNoParticipants(Association association, Minute minute){
		// Create a list of members who did not participate in the meeting
        Collection<Member> members = association.getMembers();;
        Collection<Member> participants = minute.getParticipants();
        Collection<Member> memberNoPart = new HashSet<Member>();
        memberNoPart.addAll(members);
        memberNoPart.removeAll(participants);
		return memberNoPart;
	}

	public void update(Minute minute, String date, List<Long> participantsIds, String content, double duration, Association association){
        // Update minute attributes
        minute.setDate(date);
        List<Member> participants = participantsIds.stream()
            .map(participantId -> memberService.findById(participantId).orElse(null))
            .filter(Objects::nonNull)
            .collect(Collectors.toList());
        minute.setParticipants(participants);
        minute.setContent(content);
        minute.setDuration(duration);
        minute.setAssociation(association);
		minuteRepository.save(minute);
	}
}