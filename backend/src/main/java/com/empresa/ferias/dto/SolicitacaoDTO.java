package com.empresa.ferias.dto;

import com.empresa.ferias.model.Solicitacao;
import com.empresa.ferias.model.StatusSolicitacao;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
public class SolicitacaoDTO {

    private Long id;
    private Long usuarioId;
    private String usuarioNome;
    private Long tipoAusenciaId;
    private String tipoAusenciaNome;
    private String tipoAusenciaCor;
    private LocalDate dataInicio;
    private LocalDate dataFim;
    private long diasTotal;
    private StatusSolicitacao status;
    private String observacao;
    private String motivoRejeicao;
    private String aprovadorNome;
    private LocalDateTime dataAprovacao;
    private LocalDateTime createdAt;

    public static SolicitacaoDTO fromEntity(Solicitacao solicitacao) {
        return SolicitacaoDTO.builder()
                .id(solicitacao.getId())
                .usuarioId(solicitacao.getUsuario().getId())
                .usuarioNome(solicitacao.getUsuario().getNomeCompleto())
                .tipoAusenciaId(solicitacao.getTipoAusencia().getId())
                .tipoAusenciaNome(solicitacao.getTipoAusencia().getNome())
                .tipoAusenciaCor(solicitacao.getTipoAusencia().getCorHex())
                .dataInicio(solicitacao.getDataInicio())
                .dataFim(solicitacao.getDataFim())
                .diasTotal(solicitacao.getDiasTotal())
                .status(solicitacao.getStatus())
                .observacao(solicitacao.getObservacao())
                .motivoRejeicao(solicitacao.getMotivoRejeicao())
                .aprovadorNome(solicitacao.getAprovador() != null ? solicitacao.getAprovador().getNomeCompleto() : null)
                .dataAprovacao(solicitacao.getDataAprovacao())
                .createdAt(solicitacao.getCreatedAt())
                .build();
    }
}
