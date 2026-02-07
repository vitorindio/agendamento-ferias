package com.empresa.ferias.service;

import com.empresa.ferias.dto.SolicitacaoDTO;
import com.empresa.ferias.dto.SolicitacaoRequest;
import com.empresa.ferias.model.*;
import com.empresa.ferias.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SolicitacaoService {

    private final SolicitacaoRepository solicitacaoRepository;
    private final UsuarioRepository usuarioRepository;
    private final TipoAusenciaRepository tipoAusenciaRepository;
    private final SaldoFeriasRepository saldoFeriasRepository;
    private final EquipeRepository equipeRepository;
    private final EmailService emailService;

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @Transactional
    public SolicitacaoDTO criarSolicitacao(Long usuarioId, SolicitacaoRequest request) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        TipoAusencia tipoAusencia = tipoAusenciaRepository.findById(request.getTipoAusenciaId())
                .orElseThrow(() -> new RuntimeException("Tipo de ausência não encontrado"));

        // Validar datas
        if (request.getDataFim().isBefore(request.getDataInicio())) {
            throw new RuntimeException("Data fim deve ser maior ou igual à data início");
        }

        if (request.getDataInicio().isBefore(LocalDate.now())) {
            throw new RuntimeException("Não é possível solicitar férias para datas passadas");
        }

        // Verificar conflitos
        List<Solicitacao> conflitos = solicitacaoRepository.findConflitos(
                usuarioId, request.getDataInicio(), request.getDataFim());

        if (!conflitos.isEmpty()) {
            throw new RuntimeException("Já existe uma solicitação para este período");
        }

        // Verificar saldo se o tipo deduz saldo
        if (tipoAusencia.getDeduzSaldo()) {
            int ano = request.getDataInicio().getYear();
            SaldoFerias saldo = saldoFeriasRepository.findByUsuarioIdAndAnoReferencia(usuarioId, ano)
                    .orElseThrow(() -> new RuntimeException("Saldo de férias não encontrado para o ano " + ano));

            long diasSolicitados = java.time.temporal.ChronoUnit.DAYS.between(
                    request.getDataInicio(), request.getDataFim()) + 1;

            if (diasSolicitados > saldo.getDiasDisponiveis()) {
                throw new RuntimeException("Saldo insuficiente. Disponível: " + saldo.getDiasDisponiveis() + " dias");
            }
        }

        Solicitacao solicitacao = Solicitacao.builder()
                .usuario(usuario)
                .tipoAusencia(tipoAusencia)
                .dataInicio(request.getDataInicio())
                .dataFim(request.getDataFim())
                .observacao(request.getObservacao())
                .status(StatusSolicitacao.PENDENTE)
                .build();

        solicitacao = solicitacaoRepository.save(solicitacao);

        // Notificar gestores
        notificarGestores(usuario, request.getDataInicio(), request.getDataFim());

        return SolicitacaoDTO.fromEntity(solicitacao);
    }

    private void notificarGestores(Usuario funcionario, LocalDate inicio, LocalDate fim) {
        List<Equipe> equipes = equipeRepository.findByMembroId(funcionario.getId());
        String periodo = inicio.format(formatter) + " a " + fim.format(formatter);

        for (Equipe equipe : equipes) {
            if (equipe.getGestor() != null) {
                emailService.enviarNotificacaoSolicitacao(
                        equipe.getGestor().getEmail(),
                        equipe.getGestor().getNomeCompleto(),
                        funcionario.getNomeCompleto(),
                        periodo
                );
            }
        }
    }

    public List<SolicitacaoDTO> listarMinhasSolicitacoes(Long usuarioId) {
        return solicitacaoRepository.findByUsuarioId(usuarioId).stream()
                .map(SolicitacaoDTO::fromEntity)
                .collect(Collectors.toList());
    }

    public List<SolicitacaoDTO> listarMinhasSolicitacoesPorAno(Long usuarioId, int ano) {
        return solicitacaoRepository.findByUsuarioIdAndAno(usuarioId, ano).stream()
                .map(SolicitacaoDTO::fromEntity)
                .collect(Collectors.toList());
    }

    // Lista TODAS as solicitações pendentes (para gestores)
    public List<SolicitacaoDTO> listarTodasPendentes() {
        return solicitacaoRepository.findByStatus(StatusSolicitacao.PENDENTE).stream()
                .map(SolicitacaoDTO::fromEntity)
                .collect(Collectors.toList());
    }

    // Lista TODAS as solicitações (para gestores)
    public List<SolicitacaoDTO> listarTodasSolicitacoes() {
        return solicitacaoRepository.findAll().stream()
                .map(SolicitacaoDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional
    public SolicitacaoDTO aprovar(Long solicitacaoId, Long aprovadorId) {
        Solicitacao solicitacao = solicitacaoRepository.findById(solicitacaoId)
                .orElseThrow(() -> new RuntimeException("Solicitação não encontrada"));

        // Verificar se a solicitação está pendente
        if (solicitacao.getStatus() != StatusSolicitacao.PENDENTE) {
            throw new RuntimeException("Apenas solicitações pendentes podem ser aprovadas");
        }

        Usuario aprovador = usuarioRepository.findById(aprovadorId)
                .orElseThrow(() -> new RuntimeException("Aprovador não encontrado"));

        // Verificar se o aprovador é gestor de alguma equipe do funcionário
        verificarPermissaoAprovacao(aprovador, solicitacao.getUsuario());

        solicitacao.setStatus(StatusSolicitacao.APROVADO);
        solicitacao.setAprovador(aprovador);
        solicitacao.setDataAprovacao(LocalDateTime.now());

        // Atualizar saldo se aplicável
        if (solicitacao.getTipoAusencia().getDeduzSaldo()) {
            int ano = solicitacao.getDataInicio().getYear();
            SaldoFerias saldo = saldoFeriasRepository.findByUsuarioIdAndAnoReferencia(
                    solicitacao.getUsuario().getId(), ano)
                    .orElseThrow(() -> new RuntimeException("Saldo não encontrado"));

            saldo.setDiasUsados(saldo.getDiasUsados() + (int) solicitacao.getDiasTotal());
            saldoFeriasRepository.save(saldo);
        }

        solicitacao = solicitacaoRepository.save(solicitacao);

        // Notificar funcionário
        String periodo = solicitacao.getDataInicio().format(formatter) + " a " + solicitacao.getDataFim().format(formatter);
        emailService.enviarNotificacaoAprovacao(
                solicitacao.getUsuario().getEmail(),
                solicitacao.getUsuario().getNomeCompleto(),
                periodo,
                true,
                null
        );

        return SolicitacaoDTO.fromEntity(solicitacao);
    }

    @Transactional
    public SolicitacaoDTO rejeitar(Long solicitacaoId, Long aprovadorId, String motivo) {
        Solicitacao solicitacao = solicitacaoRepository.findById(solicitacaoId)
                .orElseThrow(() -> new RuntimeException("Solicitação não encontrada"));

        // Verificar se a solicitação está pendente
        if (solicitacao.getStatus() != StatusSolicitacao.PENDENTE) {
            throw new RuntimeException("Apenas solicitações pendentes podem ser rejeitadas");
        }

        Usuario aprovador = usuarioRepository.findById(aprovadorId)
                .orElseThrow(() -> new RuntimeException("Aprovador não encontrado"));

        verificarPermissaoAprovacao(aprovador, solicitacao.getUsuario());

        solicitacao.setStatus(StatusSolicitacao.REJEITADO);
        solicitacao.setAprovador(aprovador);
        solicitacao.setDataAprovacao(LocalDateTime.now());
        solicitacao.setMotivoRejeicao(motivo);

        solicitacao = solicitacaoRepository.save(solicitacao);

        // Notificar funcionário
        String periodo = solicitacao.getDataInicio().format(formatter) + " a " + solicitacao.getDataFim().format(formatter);
        emailService.enviarNotificacaoAprovacao(
                solicitacao.getUsuario().getEmail(),
                solicitacao.getUsuario().getNomeCompleto(),
                periodo,
                false,
                motivo
        );

        return SolicitacaoDTO.fromEntity(solicitacao);
    }

    @Transactional
    public SolicitacaoDTO cancelar(Long solicitacaoId, Long usuarioId) {
        Solicitacao solicitacao = solicitacaoRepository.findById(solicitacaoId)
                .orElseThrow(() -> new RuntimeException("Solicitação não encontrada"));

        if (!solicitacao.getUsuario().getId().equals(usuarioId)) {
            throw new RuntimeException("Você só pode cancelar suas próprias solicitações");
        }

        if (solicitacao.getStatus() == StatusSolicitacao.APROVADO) {
            // Devolver saldo se foi aprovado e deduziu saldo
            if (solicitacao.getTipoAusencia().getDeduzSaldo()) {
                int ano = solicitacao.getDataInicio().getYear();
                SaldoFerias saldo = saldoFeriasRepository.findByUsuarioIdAndAnoReferencia(usuarioId, ano)
                        .orElseThrow(() -> new RuntimeException("Saldo não encontrado"));

                saldo.setDiasUsados(Math.max(0, saldo.getDiasUsados() - (int) solicitacao.getDiasTotal()));
                saldoFeriasRepository.save(saldo);
            }
        }

        solicitacao.setStatus(StatusSolicitacao.CANCELADO);
        return SolicitacaoDTO.fromEntity(solicitacaoRepository.save(solicitacao));
    }

    private void verificarPermissaoAprovacao(Usuario aprovador, Usuario funcionario) {
        // Apenas gestores podem aprovar solicitações
        if (aprovador.getRole() != Role.GESTOR) {
            throw new RuntimeException("Apenas gestores podem aprovar solicitações");
        }
    }
}
