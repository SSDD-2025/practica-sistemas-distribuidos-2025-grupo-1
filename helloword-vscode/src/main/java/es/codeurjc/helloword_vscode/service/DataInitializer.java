package es.codeurjc.helloword_vscode.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import es.codeurjc.helloword_vscode.entities.Association;
import es.codeurjc.helloword_vscode.entities.Minute;
import es.codeurjc.helloword_vscode.entities.Role;
import es.codeurjc.helloword_vscode.entities.UtilisateurEntity;
import es.codeurjc.helloword_vscode.repository.AssociationRepository;
import es.codeurjc.helloword_vscode.repository.MinuteRepository;
import es.codeurjc.helloword_vscode.repository.RoleRepository;
import es.codeurjc.helloword_vscode.repository.UtilisateurEntityRepository;

import jakarta.annotation.PostConstruct;
import java.util.Arrays;

@Service
public class DataInitializer {

    @Autowired
    private UtilisateurEntityRepository UtilisateurEntityRepository;

    @Autowired
    private MinuteRepository minuteRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private AssociationRepository associationRepository;

    @PostConstruct
    public void init() {
        // Ajoutez des utilisateurs
        UtilisateurEntity Utilisateurentity1 = new UtilisateurEntity("UtilisateurEntity 1", "Juan", "mdp", false, false);
        UtilisateurEntity Utilisateurentity2 = new UtilisateurEntity("UtilisateurEntity 2", "Pedro", "pwd", false, false);
        UtilisateurEntityRepository.saveAll(Arrays.asList(Utilisateurentity1, Utilisateurentity2));

        // Ajoutez des associations
        Association association = new Association("Association 1");
        associationRepository.save(association);

        // Ajoutez des rôles
        Role role = new Role("Role 1", Utilisateurentity1, association);
        roleRepository.save(role);

        // Ajoutez des minutes
        Minute minute = new Minute("2023-10-01", Arrays.asList(Utilisateurentity1, Utilisateurentity2), "Content", 60.0, association);
        minuteRepository.save(minute);
    }
}
