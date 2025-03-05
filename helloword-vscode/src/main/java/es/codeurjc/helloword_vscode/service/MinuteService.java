package es.codeurjc.helloword_vscode.service;

// Importing necessary classes for Spring service and repository management
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import es.codeurjc.helloword_vscode.repository.MinuteRepository;

@Service // Marks this class as a Spring service, where business logic for minute-related operations will be implemented
public class MinuteService {
    
    @Autowired // Automatically injects the MinuteRepository to handle minute-related database operations
    private MinuteRepository MinutesRepository; // Repository that provides access to the Minute entity (meeting minutes)
}
