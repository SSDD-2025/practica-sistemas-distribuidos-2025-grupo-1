package es.codeurjc.helloword_vscode.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import es.codeurjc.helloword_vscode.repository.MinuteRepository;

import java.io.IOException;
import java.lang.reflect.Member;
import java.util.List;
import java.util.Optional;

import es.codeurjc.helloword_vscode.entities.Association;
import es.codeurjc.helloword_vscode.entities.Minute;
import es.codeurjc.helloword_vscode.entities.UtilisateurEntity;

import es.codeurjc.helloword_vscode.service.AssociationService;

@Service
public class MinuteService {
    @Autowired
	private MinuteRepository minuteRepository;

	@Autowired
	private AssociationService associationService;

    public List<Minute> findAll() {
		return minuteRepository.findAll();
	}

	public void save (Minute minute) throws IOException{
		minuteRepository.save(minute);
	}

	public Optional<Minute> findById(long id){
		return minuteRepository.findById(id);
	}

	public void delete(Minute minute, Long assoId, List <UtilisateurEntity> utilisateurs) {
		Association association = associationService.findById(assoId).orElseThrow();
		association.getMinutes().remove(minute);
		for (UtilisateurEntity utilisateur : utilisateurs ){
			utilisateur.getMinutes().remove(minute);
		}
		this.minuteRepository.delete(minute);
	}
}