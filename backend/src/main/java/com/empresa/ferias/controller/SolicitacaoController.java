package com.empresa.ferias.controller;

import com.empresa.ferias.dto.SolicitacaoDTO;
import com.empresa.ferias.dto.SolicitacaoRequest;
import com.empresa.ferias.model.Usuario;
import com.empresa.ferias.service.SolicitacaoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.Year;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/solicitacoes")
@RequiredArgsConstructor
public class SolicitacaoController {

    private final SolicitacaoService solicitacaoService;

    @PostMapping
    public ResponseEntity<SolicitacaoDTO> criar(
            @AuthenticationPrincipal Usuario usuario,
            @Valid @RequestBody SolicitacaoRequest request
    ) {
        return ResponseEntity.ok(solicitacaoService.criarSolicitacao(usuario.getId(), request));
    }

    @GetMapping("/minhas")
    public ResponseEntity<List<SolicitacaoDTO>> listarMinhas(@AuthenticationPrincipal Usuario usuario) {
        return ResponseEntity.ok(solicitacaoService.listarMinhasSolicitacoes(usuario.getId()));
    }

    @GetMapping("/minhas/ano/{ano}")
    public ResponseEntity<List<SolicitacaoDTO>> listarMinhasPorAno(
            @AuthenticationPrincipal Usuario usuario,
            @PathVariable int ano
    ) {
        return ResponseEntity.ok(solicitacaoService.listarMinhasSolicitacoesPorAno(usuario.getId(), ano));
    }

    @GetMapping("/minhas/ano-atual")
    public ResponseEntity<List<SolicitacaoDTO>> listarMinhasAnoAtual(@AuthenticationPrincipal Usuario usuario) {
        return ResponseEntity.ok(
                solicitacaoService.listarMinhasSolicitacoesPorAno(usuario.getId(), Year.now().getValue())
        );
    }

    @GetMapping("/equipe/pendentes")
    @PreAuthorize("hasRole('GESTOR')")
    public ResponseEntity<List<SolicitacaoDTO>> listarPendentes(@AuthenticationPrincipal Usuario usuario) {
        return ResponseEntity.ok(solicitacaoService.listarTodasPendentes());
    }

    @GetMapping("/equipe")
    @PreAuthorize("hasRole('GESTOR')")
    public ResponseEntity<List<SolicitacaoDTO>> listarTodas(@AuthenticationPrincipal Usuario usuario) {
        return ResponseEntity.ok(solicitacaoService.listarTodasSolicitacoes());
    }

    @PostMapping("/{id}/aprovar")
    @PreAuthorize("hasRole('GESTOR')")
    public ResponseEntity<SolicitacaoDTO> aprovar(
            @PathVariable Long id,
            @AuthenticationPrincipal Usuario usuario
    ) {
        return ResponseEntity.ok(solicitacaoService.aprovar(id, usuario.getId()));
    }

    @PostMapping("/{id}/rejeitar")
    @PreAuthorize("hasRole('GESTOR')")
    public ResponseEntity<SolicitacaoDTO> rejeitar(
            @PathVariable Long id,
            @AuthenticationPrincipal Usuario usuario,
            @RequestBody Map<String, String> body
    ) {
        String motivo = body.get("motivo");
        return ResponseEntity.ok(solicitacaoService.rejeitar(id, usuario.getId(), motivo));
    }

    @PostMapping("/{id}/cancelar")
    public ResponseEntity<SolicitacaoDTO> cancelar(
            @PathVariable Long id,
            @AuthenticationPrincipal Usuario usuario
    ) {
        return ResponseEntity.ok(solicitacaoService.cancelar(id, usuario.getId()));
    }
}
