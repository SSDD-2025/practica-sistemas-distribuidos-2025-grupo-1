package es.codeurjc.helloword_vscode.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import es.codeurjc.helloword_vscode.ResourceNotFoundException;
import es.codeurjc.helloword_vscode.dto.MinuteDTO;
import es.codeurjc.helloword_vscode.mapper.MinuteMapper;
import es.codeurjc.helloword_vscode.model.Association;
import es.codeurjc.helloword_vscode.model.Minute;
import es.codeurjc.helloword_vscode.model.Member;
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

    @Autowired
    private MinuteRepository minuteRepository;

    @Autowired
    private AssociationService associationService;

    @Autowired
    private MinuteMapper minuteMapper;

    public List<MinuteDTO> findAll() {
        return minuteRepository.findAll().stream()
            .map(minuteMapper::toDTO)
            .toList();
    }

    public void save(MinuteDTO dto) throws IOException {
        Minute minute = minuteMapper.toEntity(dto);
        minuteRepository.save(minute);
    }

    public Optional<MinuteDTO> findById(long id) {
        return minuteRepository.findById(id).map(minuteMapper::toDTO);
    }

    public Optional<Minute> findEntityById(long id) {
        return minuteRepository.findById(id);
    }

    public void delete(Minute minute, Long assoId, List<Member> members) {
        Association association = associationService.findEntityById(assoId)
            .orElseThrow(() -> new ResourceNotFoundException("Association not found"));
        association.getMinutes().remove(minute);
        for (Member member : members) {
            member.getMinutes().remove(minute);
        }
        minuteRepository.delete(minute);
    }

    public void deleteMinuteById(Long minuteId, Long assoId) {
        Minute minute = minuteRepository.findById(minuteId)
            .orElseThrow(() -> new ResourceNotFoundException("Minute not found with id: " + minuteId));
        Association association = associationService.findEntityById(assoId)
            .orElseThrow(() -> new ResourceNotFoundException("Association not found with id: " + assoId));

        association.getMinutes().remove(minute);
        for (Member member : minute.getParticipants()) {
            member.getMinutes().remove(minute);
        }
        minuteRepository.delete(minute);
    }

    public List<Minute> findAllByParticipantsContains(Member participant) {
        return minuteRepository.findAllByParticipantsContains(participant);
    }
}
