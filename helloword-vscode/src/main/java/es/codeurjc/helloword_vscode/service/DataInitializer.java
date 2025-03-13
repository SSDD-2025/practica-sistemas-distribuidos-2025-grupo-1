package es.codeurjc.helloword_vscode.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import es.codeurjc.helloword_vscode.entities.Association;
import es.codeurjc.helloword_vscode.entities.Minute;
import es.codeurjc.helloword_vscode.entities.MemberType;
import es.codeurjc.helloword_vscode.entities.UtilisateurEntity;
import es.codeurjc.helloword_vscode.repository.AssociationRepository;
import es.codeurjc.helloword_vscode.repository.MinuteRepository;
import es.codeurjc.helloword_vscode.repository.MemberTypeRepository;
import es.codeurjc.helloword_vscode.repository.UtilisateurEntityRepository;

import jakarta.annotation.PostConstruct;
import java.util.Arrays;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Service
public class DataInitializer {

    @Autowired
    private UtilisateurEntityRepository UtilisateurEntityRepository;

    @Autowired
    private MinuteRepository minuteRepository;

    @Autowired
    private MemberTypeRepository roleRepository;

    @Autowired
    private AssociationRepository associationRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostConstruct
    public void init() {
        // Add users
        UtilisateurEntity Utilisateurentity1 = new UtilisateurEntity("Jean", "Jan", passwordEncoder.encode("mdp"), "USER");
        UtilisateurEntity Utilisateurentity2 = new UtilisateurEntity("Pierre", "Pro", passwordEncoder.encode("pwd"), "USER", "ADMIN");
        UtilisateurEntityRepository.saveAll(Arrays.asList(Utilisateurentity1, Utilisateurentity2));

        // Add associations
        Association association1 = new Association("GreenPeace");
        associationRepository.save(association1); //helloword-vscode\src\main\java\es\codeurjc\helloword_vscode\service\DataInitializer.java
        Association association2 = new Association("GreatSchool", "greenPeaceLogo.png");
        associationRepository.save(association2);
        Association association3 = new Association("Help", "greenPeaceLogo.png");
        associationRepository.save(association3);

        // Add roles
        MemberType role1 = new MemberType("secretary", Utilisateurentity1, association1);
        roleRepository.save(role1);
        MemberType role2 = new MemberType("president", Utilisateurentity2, association1);
        roleRepository.save(role2);
        MemberType role3 = new MemberType("president", Utilisateurentity2, association2);
        roleRepository.save(role3);

        // Add minutes
        Minute minute1 = new Minute("2023-10-01", Arrays.asList(Utilisateurentity1, Utilisateurentity2), "New actions about climat", 60.0, association1);
        minuteRepository.save(minute1);
        Minute minute2 = new Minute("2024-11-03", Arrays.asList(Utilisateurentity1, Utilisateurentity2), "Discussion on government measures", 30.0, association1);
        minuteRepository.save(minute2);
    }
}
