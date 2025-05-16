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
import es.codeurjc.helloword_vscode.dto.AssociationDTO;
import es.codeurjc.helloword_vscode.dto.MinuteDTO;
import es.codeurjc.helloword_vscode.mapper.AssociationMapper;
import es.codeurjc.helloword_vscode.mapper.MinuteMapper;
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

    @Autowired
    private AssociationRepository associationRepository;

    @Autowired
    private MemberService memberService;

    @Autowired
    private MemberTypeService memberTypeService;

    @Autowired
    private AssociationMapper associationMapper;

    @Autowired
    private MinuteMapper minuteMapper;

    public void save(AssociationDTO dto) {
        Association association = associationMapper.toEntity(dto);
        associationRepository.save(association);
    }

    public void save(AssociationDTO dto, MultipartFile imageFile) throws IOException {
        Association association = associationMapper.toEntity(dto);
        if (!imageFile.isEmpty()) {
            association.setImageFile(BlobProxy.generateProxy(imageFile.getInputStream(), imageFile.getSize()));
        }
        associationRepository.save(association);
    }

    public Optional<AssociationDTO> findById(long id) {
        return associationRepository.findById(id)
            .map(associationMapper::toDTO);
    }

    @Transactional
    public List<MinuteDTO> getMinutes(Long associationId) {
        Association association = associationRepository.findById(associationId)
            .orElseThrow(() -> new ResourceNotFoundException("Association non trouvée avec l'id : " + associationId));
        return association.getMinutes().stream()
            .map(minuteMapper::toDTO)
            .toList();
    }

    public List<AssociationDTO> findAll() {
        return associationRepository.findAll().stream()
            .map(associationMapper::toDTO)
            .toList();
    }

    public void deleteById(long id) {
        try {
            associationRepository.deleteById(id);
        } catch (Exception e) {
            System.err.println("Erreur lors de la suppression de l'association : " + e.getMessage());
        }
    }

    public void addUserToAssociation(Long associationId, Long userId) {
        Optional<Association> associationOpt = associationRepository.findById(associationId);
        Optional<Member> userOpt = memberService.findEntityById(userId);

        if (associationOpt.isPresent() && userOpt.isPresent()) {
            Association association = associationOpt.get();
            Member user = userOpt.get();

            if (!association.getMembers().contains(user)) {
                MemberType memberType = new MemberType("member", user, association);
                memberTypeService.save(memberType);
            }
        }
    }

    // Pour les besoins internes (deleteMinuteById, etc.)
    public Optional<Association> findEntityById(long id) {
        return associationRepository.findById(id);
    }
}
