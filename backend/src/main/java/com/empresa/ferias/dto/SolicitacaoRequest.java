package com.empresa.ferias.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class SolicitacaoRequest {

    @NotNull(message = "Tipo de ausência é obrigatório")
    private Long tipoAusenciaId;

    @NotNull(message = "Data de início é obrigatória")
    private LocalDate dataInicio;

    @NotNull(message = "Data de fim é obrigatória")
    private LocalDate dataFim;

    private String observacao;
}
