package es.codeurjc.helloword_vscode.controller;

import es.codeurjc.helloword_vscode.dto.MinuteDTO;
import es.codeurjc.helloword_vscode.service.MinuteService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/minutes")
public class MinuteRestController {

    @Autowired
    private MinuteService minuteService;

    @GetMapping
    public List<MinuteDTO> getAllMinutes() {
        return minuteService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<MinuteDTO> getMinuteById(@PathVariable long id) {
        Optional<MinuteDTO> minute = minuteService.findById(id);
        return minute.map(ResponseEntity::ok)
                     .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Void> createMinute(@RequestBody MinuteDTO minuteDTO) {
        minuteService.save(minuteDTO);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMinute(@PathVariable long id) {
        try {
            minuteService.deleteById(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}
