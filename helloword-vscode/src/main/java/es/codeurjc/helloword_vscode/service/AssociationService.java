package es.codeurjc.helloword_vscode.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import es.codeurjc.helloword_vscode.repository.AssociationRepository;

@Service
public class AssociationService {
    @Autowired
	private AssociationRepository associationsRepository;
}
