package com.empresa.ferias.controller;

import com.empresa.ferias.dto.SaldoFeriasDTO;
import com.empresa.ferias.dto.UpdateUsuarioRequest;
import com.empresa.ferias.dto.UsuarioDTO;
import com.empresa.ferias.model.Role;
import com.empresa.ferias.model.Usuario;
import com.empresa.ferias.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioService usuarioService;

    // ---- Endpoints do próprio usuário ----

    @GetMapping("/me")
    public ResponseEntity<UsuarioDTO> perfilAtual(@AuthenticationPrincipal Usuario usuario) {
        return ResponseEntity.ok(UsuarioDTO.fromEntity(usuario));
    }

    @GetMapping("/me/saldo")
    public ResponseEntity<SaldoFeriasDTO> saldoAtual(@AuthenticationPrincipal Usuario usuario) {
        return ResponseEntity.ok(usuarioService.buscarSaldoAtual(usuario.getId()));
    }

    @GetMapping("/me/saldo/{ano}")
    public ResponseEntity<SaldoFeriasDTO> saldoPorAno(
            @AuthenticationPrincipal Usuario usuario,
            @PathVariable int ano
    ) {
        return ResponseEntity.ok(usuarioService.buscarSaldoPorAno(usuario.getId(), ano));
    }

    @GetMapping("/me/saldos")
    public ResponseEntity<List<SaldoFeriasDTO>> todosSaldos(@AuthenticationPrincipal Usuario usuario) {
        return ResponseEntity.ok(usuarioService.listarTodosSaldos(usuario.getId()));
    }

    // ---- Endpoints de gestão (GESTOR) ----

    @GetMapping
    @PreAuthorize("hasRole('GESTOR')")
    public ResponseEntity<List<UsuarioDTO>> listarTodos() {
        return ResponseEntity.ok(usuarioService.listarTodos());
    }

    @GetMapping("/ativos")
    @PreAuthorize("hasRole('GESTOR')")
    public ResponseEntity<List<UsuarioDTO>> listarAtivos() {
        return ResponseEntity.ok(usuarioService.listarAtivos());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('GESTOR')")
    public ResponseEntity<UsuarioDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(usuarioService.buscarPorId(id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('GESTOR')")
    public ResponseEntity<UsuarioDTO> atualizar(@PathVariable Long id, @RequestBody UpdateUsuarioRequest request) {
        return ResponseEntity.ok(usuarioService.atualizar(id, request));
    }

    @PatchMapping("/{id}/role")
    @PreAuthorize("hasRole('GESTOR')")
    public ResponseEntity<UsuarioDTO> alterarRole(@PathVariable Long id, @RequestBody Map<String, String> body) {
        String roleStr = body.get("role");
        if (roleStr == null || roleStr.isBlank()) {
            throw new RuntimeException("Role é obrigatório");
        }
        Role novaRole;
        try {
            novaRole = Role.valueOf(roleStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Role inválida. Valores aceitos: USER, GESTOR");
        }
        return ResponseEntity.ok(usuarioService.alterarRole(id, novaRole));
    }

    @PatchMapping("/{id}/toggle-ativo")
    @PreAuthorize("hasRole('GESTOR')")
    public ResponseEntity<UsuarioDTO> toggleAtivo(@PathVariable Long id) {
        return ResponseEntity.ok(usuarioService.toggleAtivo(id));
    }

    // ---- Saldos de usuários (GESTOR) ----

    @GetMapping("/{id}/saldo")
    @PreAuthorize("hasRole('GESTOR')")
    public ResponseEntity<SaldoFeriasDTO> saldoUsuario(@PathVariable Long id) {
        return ResponseEntity.ok(usuarioService.buscarSaldoAtual(id));
    }

    @GetMapping("/{id}/saldo/{ano}")
    @PreAuthorize("hasRole('GESTOR')")
    public ResponseEntity<SaldoFeriasDTO> saldoUsuarioPorAno(@PathVariable Long id, @PathVariable int ano) {
        return ResponseEntity.ok(usuarioService.buscarSaldoPorAno(id, ano));
    }

    @GetMapping("/{id}/saldos")
    @PreAuthorize("hasRole('GESTOR')")
    public ResponseEntity<List<SaldoFeriasDTO>> saldosUsuario(@PathVariable Long id) {
        return ResponseEntity.ok(usuarioService.listarTodosSaldos(id));
    }

    @PutMapping("/{id}/saldo/{ano}")
    @PreAuthorize("hasRole('GESTOR')")
    public ResponseEntity<SaldoFeriasDTO> ajustarSaldo(
            @PathVariable Long id,
            @PathVariable int ano,
            @RequestBody Map<String, Integer> body
    ) {
        Integer diasTotais = body.get("diasTotais");
        if (diasTotais == null || diasTotais < 0) {
            throw new RuntimeException("diasTotais é obrigatório e deve ser >= 0");
        }
        return ResponseEntity.ok(usuarioService.ajustarSaldo(id, ano, diasTotais));
    }

    @GetMapping("/saldos/{ano}")
    @PreAuthorize("hasRole('GESTOR')")
    public ResponseEntity<List<SaldoFeriasDTO>> listarSaldosPorAno(@PathVariable int ano) {
        return ResponseEntity.ok(usuarioService.listarSaldosPorAno(ano));
    }
}
