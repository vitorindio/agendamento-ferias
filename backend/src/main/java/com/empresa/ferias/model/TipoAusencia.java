package com.empresa.ferias.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tipos_ausencia")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TipoAusencia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    @Column(name = "cor_hex")
    @Builder.Default
    private String corHex = "#34D399";

    @Column(name = "deduz_saldo")
    @Builder.Default
    private Boolean deduzSaldo = true;

    private String descricao;

    @Column(name = "is_ativo")
    @Builder.Default
    private Boolean isAtivo = true;
}
