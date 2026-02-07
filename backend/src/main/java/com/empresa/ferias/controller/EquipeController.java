package com.empresa.ferias.controller;

import com.empresa.ferias.dto.EquipeDTO;
import com.empresa.ferias.model.Usuario;
import com.empresa.ferias.service.EquipeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/equipes")
@RequiredArgsConstructor
public class EquipeController {

    private final EquipeService equipeService;

    @GetMapping
    @PreAuthorize("hasAnyRole('RH', 'ADMIN')")
    public ResponseEntity<List<EquipeDTO>> listarTodas() {
        return ResponseEntity.ok(equipeService.listarTodas());
    }

    @GetMapping("/{id}")
    public ResponseEntity<EquipeDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(equipeService.buscarPorId(id));
    }

    @GetMapping("/minhas")
    public ResponseEntity<List<EquipeDTO>> minhasEquipes(@AuthenticationPrincipal Usuario usuario) {
        return ResponseEntity.ok(equipeService.listarEquipesDoUsuario(usuario.getId()));
    }

    @GetMapping("/gerenciadas")
    @PreAuthorize("hasAnyRole('GESTOR', 'RH', 'ADMIN')")
    public ResponseEntity<List<EquipeDTO>> equipesGerenciadas(@AuthenticationPrincipal Usuario usuario) {
        return ResponseEntity.ok(equipeService.listarEquipesDoGestor(usuario.getId()));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('RH', 'ADMIN')")
    public ResponseEntity<EquipeDTO> criar(@RequestBody Map<String, Object> body) {
        String nome = (String) body.get("nome");
        String descricao = (String) body.get("descricao");
        Long gestorId = ((Number) body.get("gestorId")).longValue();
        return ResponseEntity.ok(equipeService.criar(nome, descricao, gestorId));
    }

    @PostMapping("/{id}/membros")
    @PreAuthorize("hasAnyRole('GESTOR', 'RH', 'ADMIN')")
    public ResponseEntity<EquipeDTO> adicionarMembro(
            @PathVariable Long id,
            @RequestBody Map<String, Long> body
    ) {
        return ResponseEntity.ok(equipeService.adicionarMembro(id, body.get("usuarioId")));
    }

    @DeleteMapping("/{id}/membros/{usuarioId}")
    @PreAuthorize("hasAnyRole('GESTOR', 'RH', 'ADMIN')")
    public ResponseEntity<EquipeDTO> removerMembro(
            @PathVariable Long id,
            @PathVariable Long usuarioId
    ) {
        return ResponseEntity.ok(equipeService.removerMembro(id, usuarioId));
    }
}
