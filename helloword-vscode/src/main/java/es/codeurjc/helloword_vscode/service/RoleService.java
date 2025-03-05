package es.codeurjc.helloword_vscode.service;

// Importing necessary classes for Spring service and repository management
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import es.codeurjc.helloword_vscode.repository.MemberTypeRepository;

@Service // Marks this class as a Spring service where business logic for role-related operations will be implemented
public class RoleService {
    
    @Autowired // Automatically injects the MemberTypeRepository to handle role-related database operations
    private MemberTypeRepository RolesRepository; // Repository that provides access to the MemberType entity (roles)
}
