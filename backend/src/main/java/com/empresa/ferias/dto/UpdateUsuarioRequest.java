package com.empresa.ferias.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class UpdateUsuarioRequest {
    private String nomeCompleto;
    private String email;
    private String cargo;
    private LocalDate dataAdmissao;
}
