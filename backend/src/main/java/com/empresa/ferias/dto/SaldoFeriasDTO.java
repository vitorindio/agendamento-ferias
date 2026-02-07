package com.empresa.ferias.dto;

import com.empresa.ferias.model.SaldoFerias;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SaldoFeriasDTO {

    private Long id;
    private Long usuarioId;
    private String usuarioNome;
    private Integer anoReferencia;
    private Integer diasTotais;
    private Integer diasUsados;
    private Integer diasDisponiveis;

    public static SaldoFeriasDTO fromEntity(SaldoFerias saldo) {
        return SaldoFeriasDTO.builder()
                .id(saldo.getId())
                .usuarioId(saldo.getUsuario().getId())
                .usuarioNome(saldo.getUsuario().getNomeCompleto())
                .anoReferencia(saldo.getAnoReferencia())
                .diasTotais(saldo.getDiasTotais())
                .diasUsados(saldo.getDiasUsados())
                .diasDisponiveis(saldo.getDiasDisponiveis())
                .build();
    }
}
