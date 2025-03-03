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
        Association association1 = new Association("GreenPeace");
        associationRepository.save(association1);
        Association association2 = new Association("GreatSchool");
        associationRepository.save(association2);
        Association association3 = new Association("Help");
        associationRepository.save(association3);

        // Add roles
        Role role1 = new Role("secretary", Utilisateurentity1, association1);
        roleRepository.save(role1);
        Role role2 = new Role("president", Utilisateurentity2, association1);
        roleRepository.save(role2);

        // Add minutes
        Minute minute1 = new Minute("2023-10-01", Arrays.asList(Utilisateurentity1, Utilisateurentity2), "New actions about climat", 60.0, association1);
        minuteRepository.save(minute1);
        Minute minute2 = new Minute("2024-11-03", Arrays.asList(Utilisateurentity1, Utilisateurentity2), "Discussion on government measures", 30.0, association1);
        minuteRepository.save(minute2);
    }
}
