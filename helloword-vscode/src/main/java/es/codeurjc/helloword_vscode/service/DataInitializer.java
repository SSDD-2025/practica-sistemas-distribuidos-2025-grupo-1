package es.codeurjc.helloword_vscode.service;

// Importing necessary classes for Spring Data, Spring Security, and data initialization
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
import jakarta.annotation.PostConstruct; // Used to mark the method to be executed after bean initialization
import java.util.Arrays;
import org.springframework.security.crypto.password.PasswordEncoder; // Used for password encoding
import org.springframework.stereotype.Component;

@Service // Marks this class as a Spring service component
public class DataInitializer {

    @Autowired // Automatically injects the dependencies for repository beans
    private UtilisateurEntityRepository UtilisateurEntityRepository;

    @Autowired
    private MinuteRepository minuteRepository;

    @Autowired
    private MemberTypeRepository roleRepository;

    @Autowired
    private AssociationRepository associationRepository;

    @Autowired
    private PasswordEncoder passwordEncoder; // Password encoder to securely store passwords

    @PostConstruct // This method is executed after the bean has been initialized
    public void init() {
        // Add users with encoded passwords
        UtilisateurEntity Utilisateurentity1 = new UtilisateurEntity("Jean", "Jan", passwordEncoder.encode("mdp"), "USER");
        UtilisateurEntity Utilisateurentity2 = new UtilisateurEntity("Pierre", "Pro", passwordEncoder.encode("pwd"), "USER", "ADMIN");
        // Save users to the database
        UtilisateurEntityRepository.saveAll(Arrays.asList(Utilisateurentity1, Utilisateurentity2));

        // Add associations
        Association association1 = new Association("GreenPeace");
        Association association2 = new Association("GreatSchool");
        Association association3 = new Association("Help");
        // Save associations to the database
        associationRepository.saveAll(Arrays.asList(association1, association2, association3));

        // Add roles (member types)
        MemberType role1 = new MemberType("secretary", Utilisateurentity1, association1);
        MemberType role2 = new MemberType("president", Utilisateurentity2, association1);
        MemberType role3 = new MemberType("president", Utilisateurentity2, association2);
        // Save roles to the database
        roleRepository.saveAll(Arrays.asList(role1, role2, role3));

        // Add minutes (meeting records)
        Minute minute1 = new Minute("2023-10-01", Arrays.asList(Utilisateurentity1, Utilisateurentity2), "New actions about climate", 60.0, association1);
        Minute minute2 = new Minute("2024-11-03", Arrays.asList(Utilisateurentity1, Utilisateurentity2), "Discussion on government measures", 30.0, association1);
        // Save minutes to the database
        minuteRepository.saveAll(Arrays.asList(minute1, minute2));
    }
}
