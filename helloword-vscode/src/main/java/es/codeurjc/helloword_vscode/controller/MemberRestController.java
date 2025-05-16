package es.codeurjc.helloword_vscode.controller;

import es.codeurjc.helloword_vscode.dto.MemberDTO;
import es.codeurjc.helloword_vscode.service.MemberService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/members")
public class MemberRestController {

    @Autowired
    private MemberService memberService;

    @GetMapping
    public List<MemberDTO> getAllMembers() {
        return memberService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<MemberDTO> getMemberById(@PathVariable long id) {
        Optional<MemberDTO> member = memberService.findById(id);
        return member.map(ResponseEntity::ok)
                     .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Void> createMember(@RequestBody MemberDTO memberDTO) {
        memberService.save(memberDTO);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMember(@PathVariable long id) {
        try {
            memberService.deleteById(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}
