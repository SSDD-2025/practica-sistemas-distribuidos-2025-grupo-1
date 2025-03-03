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
        // Add users
        UtilisateurEntity Utilisateurentity1 = new UtilisateurEntity("Jean", "Juan", "mdp", false, false);
        UtilisateurEntity Utilisateurentity2 = new UtilisateurEntity("Pierre", "Pedro", "pwd", false, false);
        UtilisateurEntityRepository.saveAll(Arrays.asList(Utilisateurentity1, Utilisateurentity2));

        // Add associations
        Association association = new Association("GreenPeace");
        associationRepository.save(association);

        // Add roles
        Role role = new Role("secretary", Utilisateurentity1, association);
        roleRepository.save(role);

        // Add minutes
        Minute minute = new Minute("2023-10-01", Arrays.asList(Utilisateurentity1, Utilisateurentity2), "New actions about climat", 60.0, association);
        minuteRepository.save(minute);
    }
}
