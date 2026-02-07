package com.empresa.ferias.service;

import com.empresa.ferias.dto.SaldoFeriasDTO;
import com.empresa.ferias.dto.UpdateUsuarioRequest;
import com.empresa.ferias.dto.UsuarioDTO;
import com.empresa.ferias.model.Role;
import com.empresa.ferias.model.SaldoFerias;
import com.empresa.ferias.model.Usuario;
import com.empresa.ferias.repository.SaldoFeriasRepository;
import com.empresa.ferias.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Year;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final SaldoFeriasRepository saldoFeriasRepository;

    public UsuarioDTO buscarPorId(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
        return UsuarioDTO.fromEntity(usuario);
    }

    public UsuarioDTO buscarPorEmail(String email) {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
        return UsuarioDTO.fromEntity(usuario);
    }

    public List<UsuarioDTO> listarTodos() {
        return usuarioRepository.findAll().stream()
                .map(UsuarioDTO::fromEntity)
                .collect(Collectors.toList());
    }

    public List<UsuarioDTO> listarAtivos() {
        return usuarioRepository.findByIsAtivoTrue().stream()
                .map(UsuarioDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional
    public UsuarioDTO atualizar(Long id, UpdateUsuarioRequest request) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        if (request.getNomeCompleto() != null && !request.getNomeCompleto().isBlank()) {
            usuario.setNomeCompleto(request.getNomeCompleto());
        }
        if (request.getEmail() != null && !request.getEmail().isBlank()) {
            // Verificar se o email já está em uso por outro usuário
            usuarioRepository.findByEmail(request.getEmail()).ifPresent(existing -> {
                if (!existing.getId().equals(id)) {
                    throw new RuntimeException("Email já está em uso por outro usuário");
                }
            });
            usuario.setEmail(request.getEmail());
        }
        if (request.getCargo() != null) {
            usuario.setCargo(request.getCargo());
        }
        if (request.getDataAdmissao() != null) {
            usuario.setDataAdmissao(request.getDataAdmissao());
        }

        usuarioRepository.save(usuario);
        return UsuarioDTO.fromEntity(usuario);
    }

    @Transactional
    public UsuarioDTO alterarRole(Long id, Role novaRole) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
        usuario.setRole(novaRole);
        usuarioRepository.save(usuario);
        return UsuarioDTO.fromEntity(usuario);
    }

    @Transactional
    public UsuarioDTO toggleAtivo(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
        usuario.setIsAtivo(!usuario.getIsAtivo());
        usuarioRepository.save(usuario);
        return UsuarioDTO.fromEntity(usuario);
    }

    // ---- Saldos ----

    public SaldoFeriasDTO buscarSaldoAtual(Long usuarioId) {
        int anoAtual = Year.now().getValue();
        SaldoFerias saldo = saldoFeriasRepository.findByUsuarioIdAndAnoReferencia(usuarioId, anoAtual)
                .orElseGet(() -> criarSaldoParaAno(usuarioId, anoAtual));
        return SaldoFeriasDTO.fromEntity(saldo);
    }

    public SaldoFeriasDTO buscarSaldoPorAno(Long usuarioId, int ano) {
        SaldoFerias saldo = saldoFeriasRepository.findByUsuarioIdAndAnoReferencia(usuarioId, ano)
                .orElseGet(() -> criarSaldoParaAno(usuarioId, ano));
        return SaldoFeriasDTO.fromEntity(saldo);
    }

    public List<SaldoFeriasDTO> listarTodosSaldos(Long usuarioId) {
        return saldoFeriasRepository.findByUsuarioId(usuarioId).stream()
                .map(SaldoFeriasDTO::fromEntity)
                .collect(Collectors.toList());
    }

    public List<SaldoFeriasDTO> listarSaldosPorAno(int ano) {
        return saldoFeriasRepository.findByAnoReferencia(ano).stream()
                .map(SaldoFeriasDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional
    public SaldoFeriasDTO ajustarSaldo(Long usuarioId, int ano, int diasTotais) {
        SaldoFerias saldo = saldoFeriasRepository.findByUsuarioIdAndAnoReferencia(usuarioId, ano)
                .orElseGet(() -> criarSaldoParaAno(usuarioId, ano));

        if (diasTotais < saldo.getDiasUsados()) {
            throw new RuntimeException("Dias totais não pode ser menor que dias já usados (" + saldo.getDiasUsados() + ")");
        }

        saldo.setDiasTotais(diasTotais);
        saldoFeriasRepository.save(saldo);
        return SaldoFeriasDTO.fromEntity(saldo);
    }

    private SaldoFerias criarSaldoParaAno(Long usuarioId, int ano) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        SaldoFerias novoSaldo = SaldoFerias.builder()
                .usuario(usuario)
                .anoReferencia(ano)
                .diasTotais(30)
                .diasUsados(0)
                .build();

        return saldoFeriasRepository.save(novoSaldo);
    }
}
