package com.empresa.ferias.controller;

import com.empresa.ferias.model.TipoAusencia;
import com.empresa.ferias.service.TipoAusenciaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/tipos-ausencia")
@RequiredArgsConstructor
public class TipoAusenciaController {

    private final TipoAusenciaService tipoAusenciaService;

    @GetMapping
    public ResponseEntity<List<TipoAusencia>> listarAtivos() {
        return ResponseEntity.ok(tipoAusenciaService.listarAtivos());
    }

    @GetMapping("/todos")
    @PreAuthorize("hasRole('GESTOR')")
    public ResponseEntity<List<TipoAusencia>> listarTodos() {
        return ResponseEntity.ok(tipoAusenciaService.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TipoAusencia> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(tipoAusenciaService.buscarPorId(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('GESTOR')")
    public ResponseEntity<TipoAusencia> criar(@RequestBody Map<String, Object> body) {
        String nome = (String) body.get("nome");
        String corHex = (String) body.getOrDefault("corHex", "#34D399");
        Boolean deduzSaldo = (Boolean) body.getOrDefault("deduzSaldo", true);
        String descricao = (String) body.get("descricao");

        return ResponseEntity.ok(tipoAusenciaService.criar(nome, corHex, deduzSaldo, descricao));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('GESTOR')")
    public ResponseEntity<TipoAusencia> atualizar(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        String nome = (String) body.get("nome");
        String corHex = (String) body.get("corHex");
        Boolean deduzSaldo = body.containsKey("deduzSaldo") ? (Boolean) body.get("deduzSaldo") : null;
        String descricao = (String) body.get("descricao");

        return ResponseEntity.ok(tipoAusenciaService.atualizar(id, nome, corHex, deduzSaldo, descricao));
    }

    @PatchMapping("/{id}/toggle-ativo")
    @PreAuthorize("hasRole('GESTOR')")
    public ResponseEntity<TipoAusencia> toggleAtivo(@PathVariable Long id) {
        return ResponseEntity.ok(tipoAusenciaService.toggleAtivo(id));
    }
}
