package es.codeurjc.helloword_vscode.service;

import es.codeurjc.helloword_vscode.ResourceNotFoundException;
import es.codeurjc.helloword_vscode.dto.MinuteDTO;
import es.codeurjc.helloword_vscode.mapper.MinuteMapper;
import es.codeurjc.helloword_vscode.model.Minute;
import es.codeurjc.helloword_vscode.repository.MinuteRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MinuteService {

    @Autowired
    private MinuteRepository minuteRepository;

    @Autowired
    private MinuteMapper minuteMapper;

    public void save(MinuteDTO dto) {
        Minute minute = minuteMapper.toDomain(dto);
        minuteRepository.save(minute);
    }

    public Optional<MinuteDTO> findById(long id) {
        return minuteRepository.findById(id).map(minuteMapper::toDTO);
    }

    public List<MinuteDTO> findAll() {
        return minuteMapper.toDTOs(minuteRepository.findAll());
    }

    public void deleteById(long id) {
        if (!minuteRepository.existsById(id)) {
            throw new ResourceNotFoundException("Minute not found with id: " + id);
        }
        minuteRepository.deleteById(id);
    }

    // Pour les opérations internes
    public Optional<Minute> findEntityById(long id) {
        return minuteRepository.findById(id);
    }
}
