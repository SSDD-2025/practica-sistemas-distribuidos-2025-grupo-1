package es.codeurjc.helloword_vscode.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import es.codeurjc.helloword_vscode.model.Member;
import es.codeurjc.helloword_vscode.service.MemberService;
import es.codeurjc.helloword_vscode.dto.MemberDTO;

@RestController
@RequestMapping("/api/members")
public class MemberRestController {

    @Autowired
    private MemberService memberService;

    // Get all members
    @GetMapping("/")
    public List<MemberDTO> getAllMembers() {
        return memberService.findAll().stream()
                .map(member -> new MemberDTO(member.getId(), member.getName(), member.getSurname(), member.getRoles()))
                .toList();
    }

    // Get a member by ID
    @GetMapping("/{id}")
    public MemberDTO getMemberById(@PathVariable long id) {
        Optional<Member> member = memberService.findById(id);
        return member.map(m -> new MemberDTO(m.getId(), m.getName(), m.getSurname(), m.getRoles()))
                     .orElseThrow(() -> new RuntimeException("Member not found"));
    }

    // Create a new member
    @PostMapping("/")
    public MemberDTO createMember(@RequestBody MemberDTO dto) {
        memberService.createUser(dto.getName(), dto.getSurname(), "changeme");
        Member saved = memberService.findByName(dto.getName()).orElseThrow();
        return new MemberDTO(saved.getId(), saved.getName(), saved.getSurname(), saved.getRoles());
    }

    // Update an existing member
    @PutMapping("/{id}")
    public MemberDTO updateMember(@PathVariable long id, @RequestBody MemberDTO dto) {
        Member member = memberService.findById(id)
                .orElseThrow(() -> new RuntimeException("Member not found"));
        member.setName(dto.getName());
        member.setSurname(dto.getSurname());
        member.setRoles(dto.getRoles());
        memberService.save(member);
        return new MemberDTO(member.getId(), member.getName(), member.getSurname(), member.getRoles());
    }

    // Delete a member by ID
    @DeleteMapping("/{id}")
    public void deleteMember(@PathVariable long id) {
        try {
            memberService.deleteById(id);
        } catch (Exception e) {
            throw new RuntimeException("Error deleting member with id " + id, e);
        }
    }
}
