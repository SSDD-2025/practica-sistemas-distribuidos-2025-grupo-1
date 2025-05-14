package es.codeurjc.helloword_vscode.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.codeurjc.helloword_vscode.repository.MemberTypeRepository;

import es.codeurjc.helloword_vscode.model.MemberType;
import es.codeurjc.helloword_vscode.model.Association;
import es.codeurjc.helloword_vscode.model.Member;

import java.util.List;
import java.util.Optional;


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

  @Autowired
  @Lazy
  private MemberService memberService;

  @Autowired
  @Lazy
  private AssociationService associationService;

  /* Save member type */
  public void save(MemberType memberType) {
    memberTypeRepository.save(memberType);
  }


  /* Delete member type */
  public void delete(MemberType memberType) {
    memberTypeRepository.delete(memberType);
  }


  /* Find all member type from user */
  public List<MemberType> findByMember(Member member) {
    return memberTypeRepository.findByMember(member);
  }

  /* Find the president of an association */
  public Optional<Member> getPresident(Association association) {
    return association.getMemberTypes().stream()
        .filter(mt -> "president".equalsIgnoreCase(mt.getName()))
        .map(MemberType::getMember)
        .findFirst();
  }

  /* Find MemberType by id*/
  public Optional<MemberType> findById(long id) {
		return memberTypeRepository.findById(id);
	}

  @Transactional
  public void changeMemberRole(Long associationId, Long requesterId, Long memberTypeId, String newRole) {
      Optional<Member> requesterOpt = memberService.findById(requesterId);
      Optional<Association> associationOpt = associationService.findById(associationId);
      Optional<MemberType> targetMemberTypeOpt = findById(memberTypeId);

      if (requesterOpt.isEmpty() || associationOpt.isEmpty() || targetMemberTypeOpt.isEmpty()) {
          throw new IllegalArgumentException("Invalid data");
      }

      Member requester = requesterOpt.get();
      Association association = associationOpt.get();
      MemberType targetMemberType = targetMemberTypeOpt.get();

      // Verify if the person who ask is president
      boolean isPresident = requester.getMemberTypes().stream()
          .anyMatch(mt -> mt.getAssociation().equals(association) && "president".equalsIgnoreCase(mt.getName()));

      if (!isPresident) {
          throw new SecurityException("Only the president can change roles");
      }

      // Add the new role
      targetMemberType.setName(newRole);
      save(targetMemberType);
  }


}

