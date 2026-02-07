package com.empresa.ferias.service;

import com.empresa.ferias.dto.EquipeDTO;
import com.empresa.ferias.model.Equipe;
import com.empresa.ferias.model.Usuario;
import com.empresa.ferias.repository.EquipeRepository;
import com.empresa.ferias.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EquipeService {

    private final EquipeRepository equipeRepository;
    private final UsuarioRepository usuarioRepository;

    public List<EquipeDTO> listarTodas() {
        return equipeRepository.findAll().stream()
                .map(EquipeDTO::fromEntity)
                .collect(Collectors.toList());
    }

    public EquipeDTO buscarPorId(Long id) {
        Equipe equipe = equipeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Equipe não encontrada"));
        return EquipeDTO.fromEntity(equipe);
    }

    public List<EquipeDTO> listarEquipesDoGestor(Long gestorId) {
        return equipeRepository.findByGestorId(gestorId).stream()
                .map(EquipeDTO::fromEntity)
                .collect(Collectors.toList());
    }

    public List<EquipeDTO> listarEquipesDoUsuario(Long usuarioId) {
        return equipeRepository.findByMembroId(usuarioId).stream()
                .map(EquipeDTO::fromEntitySimple)
                .collect(Collectors.toList());
    }

    @Transactional
    public EquipeDTO criar(String nome, String descricao, Long gestorId) {
        Usuario gestor = usuarioRepository.findById(gestorId)
                .orElseThrow(() -> new RuntimeException("Gestor não encontrado"));

        Equipe equipe = Equipe.builder()
                .nome(nome)
                .descricao(descricao)
                .gestor(gestor)
                .build();

        return EquipeDTO.fromEntity(equipeRepository.save(equipe));
    }

    @Transactional
    public EquipeDTO adicionarMembro(Long equipeId, Long usuarioId) {
        Equipe equipe = equipeRepository.findById(equipeId)
                .orElseThrow(() -> new RuntimeException("Equipe não encontrada"));

        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        equipe.adicionarMembro(usuario);
        return EquipeDTO.fromEntity(equipeRepository.save(equipe));
    }

    @Transactional
    public EquipeDTO removerMembro(Long equipeId, Long usuarioId) {
        Equipe equipe = equipeRepository.findById(equipeId)
                .orElseThrow(() -> new RuntimeException("Equipe não encontrada"));

        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        equipe.removerMembro(usuario);
        return EquipeDTO.fromEntity(equipeRepository.save(equipe));
    }
}
