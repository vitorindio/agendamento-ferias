package com.empresa.ferias.service;

import com.empresa.ferias.model.TipoAusencia;
import com.empresa.ferias.repository.TipoAusenciaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TipoAusenciaService {

    private final TipoAusenciaRepository tipoAusenciaRepository;

    public List<TipoAusencia> listarTodos() {
        return tipoAusenciaRepository.findAll();
    }

    public List<TipoAusencia> listarAtivos() {
        return tipoAusenciaRepository.findByIsAtivoTrue();
    }

    public TipoAusencia buscarPorId(Long id) {
        return tipoAusenciaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tipo de ausência não encontrado"));
    }

    @Transactional
    public TipoAusencia criar(String nome, String corHex, Boolean deduzSaldo, String descricao) {
        if (tipoAusenciaRepository.existsByNome(nome)) {
            throw new RuntimeException("Já existe um tipo de ausência com este nome");
        }

        TipoAusencia tipo = TipoAusencia.builder()
                .nome(nome)
                .corHex(corHex)
                .deduzSaldo(deduzSaldo)
                .descricao(descricao)
                .isAtivo(true)
                .build();

        return tipoAusenciaRepository.save(tipo);
    }

    @Transactional
    public TipoAusencia atualizar(Long id, String nome, String corHex, Boolean deduzSaldo, String descricao) {
        TipoAusencia tipo = tipoAusenciaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tipo de ausência não encontrado"));

        if (nome != null && !nome.isBlank()) {
            // Verificar se o novo nome já existe para outro tipo
            tipoAusenciaRepository.findByNome(nome).ifPresent(existing -> {
                if (!existing.getId().equals(id)) {
                    throw new RuntimeException("Já existe um tipo de ausência com este nome");
                }
            });
            tipo.setNome(nome);
        }
        if (corHex != null) {
            tipo.setCorHex(corHex);
        }
        if (deduzSaldo != null) {
            tipo.setDeduzSaldo(deduzSaldo);
        }
        if (descricao != null) {
            tipo.setDescricao(descricao);
        }

        return tipoAusenciaRepository.save(tipo);
    }

    @Transactional
    public TipoAusencia toggleAtivo(Long id) {
        TipoAusencia tipo = tipoAusenciaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tipo de ausência não encontrado"));
        tipo.setIsAtivo(!tipo.getIsAtivo());
        return tipoAusenciaRepository.save(tipo);
    }
}
