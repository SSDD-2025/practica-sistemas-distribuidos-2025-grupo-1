package es.codeurjc.helloword_vscode.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import es.codeurjc.helloword_vscode.repository.MemberTypeRepository;
import es.codeurjc.helloword_vscode.repository.UtilisateurEntityRepository;
import es.codeurjc.helloword_vscode.repository.AssociationRepository;

import es.codeurjc.helloword_vscode.entities.*;

import org.springframework.transaction.annotation.Transactional;

@Service
public class MemberTypeService {

    @Autowired
    private MemberTypeRepository memberTypeRepository;

    public void save(MemberType memberType) {
		memberTypeRepository.save(memberType);
	}

}

