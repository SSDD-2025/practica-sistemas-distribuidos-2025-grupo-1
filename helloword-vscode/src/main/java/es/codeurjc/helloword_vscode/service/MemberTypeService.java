package es.codeurjc.helloword_vscode.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import es.codeurjc.helloword_vscode.repository.MemberTypeRepository;

import es.codeurjc.helloword_vscode.model.MemberType;

/* 
 * This service class provides methods to perform various operations on
 * the MemberType entity, such as saving member types. It interacts with 
 * the MemberTypeRepository to perform database operations.
*/
@Service
public class MemberTypeService {
  // Autowired repository for database interactions
  @Autowired
  private MemberTypeRepository memberTypeRepository;

  /* Save member type */
  public void save(MemberType memberType) {
    memberTypeRepository.save(memberType);
  }
}

