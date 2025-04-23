package es.codeurjc.helloword_vscode.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import es.codeurjc.helloword_vscode.model.Association;
import es.codeurjc.helloword_vscode.model.MemberType;
import es.codeurjc.helloword_vscode.model.Minute;
import es.codeurjc.helloword_vscode.model.UtilisateurEntity;
import es.codeurjc.helloword_vscode.repository.AssociationRepository;
import es.codeurjc.helloword_vscode.repository.MinuteRepository;
import es.codeurjc.helloword_vscode.repository.MemberTypeRepository;
import es.codeurjc.helloword_vscode.repository.UtilisateurEntityRepository;

import jakarta.annotation.PostConstruct;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import javax.sql.rowset.serial.SerialBlob;

import java.io.IOException;
import java.sql.Blob;
import java.sql.SQLException;
import org.springframework.util.StreamUtils;


import org.springframework.security.crypto.password.PasswordEncoder;

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

    private Blob getRandomImageBlob() {
    try {
        // List of file names
        String[] imageFiles = {"image1.jpg", "image2.jpg", "image3.jpg", "image4.jpg",
         "image5.jpg", "image6.jpg", "image7.jpg", "image8.jpg"};

        // Choose ramdom image
        String fileName = imageFiles[new Random().nextInt(imageFiles.length)];

        // Load file
        ClassPathResource imgFile = new ClassPathResource("static/images/asso/" + fileName);

        // Read in byte
            byte[] bytes = StreamUtils.copyToByteArray(imgFile.getInputStream());

            // Create blob
            return new SerialBlob(bytes);
        } catch (IOException | SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    @PostConstruct
    public void init() throws SQLException{
        // Add users
        UtilisateurEntity Utilisateurentity1 = new UtilisateurEntity("Jean", "Jan", passwordEncoder.encode("mdp"), "USER");
        UtilisateurEntity Utilisateurentity2 = new UtilisateurEntity("Pierre", "Pro", passwordEncoder.encode("pwd"), "USER", "ADMIN");
        UtilisateurEntity Utilisateurentity3 = new UtilisateurEntity("Luc", "lds", passwordEncoder.encode("aaa"), "USER", "ADMIN");
        UtilisateurEntityRepository.saveAll(Arrays.asList(Utilisateurentity1, Utilisateurentity2, Utilisateurentity3));

        // Add associations
        Association association1 = new Association("GreenPeace");
        associationRepository.save(association1); 
        Association association2 = new Association("GreatSchool");
        associationRepository.save(association2);
        Association association3 = new Association("Help");
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

        // Generate new data random
        List<UtilisateurEntity> utilisateurs = new ArrayList<>();
        for (int i = 1; i <= 20; i++) {
            String role = (i % 5 == 0) ? "USER,ADMIN" : "USER";
            UtilisateurEntity user = new UtilisateurEntity(
                "Name" + i, "surname" + i, passwordEncoder.encode("pass" + i), role.split(",")
            );
            utilisateurs.add(user);
        }
        UtilisateurEntityRepository.saveAll(utilisateurs);

        List<String> names = List.of("Love Earth", "Give Smile", "Construct Avenir", "Culture Club", "Nature Warrior", "Book Lovers");
        List<Association> associations = new ArrayList<>();
        for (String name : names) {
            Association asso = new Association(name);
            asso.setImageFile(getRandomImageBlob());
            associations.add(asso);
        }
        
        associationRepository.saveAll(associations);

        List<MemberType> roles = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            UtilisateurEntity user = utilisateurs.get(i);
            Association asso = associations.get(i % associations.size());
            String roleName = switch (i % 3) {
                case 0 -> "president";
                case 1 -> "secretary";
                default -> "member";
            };
            roles.add(new MemberType(roleName, user, asso));
        }
        roleRepository.saveAll(roles);

        List<Minute> minutes = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            Association asso = associations.get(i % associations.size());
            List<UtilisateurEntity> participants = Arrays.asList(
                utilisateurs.get(i % utilisateurs.size()),
                utilisateurs.get((i + 1) % utilisateurs.size())
            );
            minutes.add(new Minute(
                "2024-0" + ((i % 9) + 1) + "-0" + ((i % 27) + 1),
                participants,
                "Minute n°" + (i + 1),
                30.0 + i,
                asso
            ));
        }
        minuteRepository.saveAll(minutes);

        }

}
