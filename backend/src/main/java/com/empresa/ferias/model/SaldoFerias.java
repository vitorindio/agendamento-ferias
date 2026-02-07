package com.empresa.ferias.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "saldo_ferias", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"usuario_id", "ano_referencia"})
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SaldoFerias {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @Column(name = "ano_referencia", nullable = false)
    private Integer anoReferencia;

    @Column(name = "dias_totais")
    @Builder.Default
    private Integer diasTotais = 30;

    @Column(name = "dias_usados")
    @Builder.Default
    private Integer diasUsados = 0;

    public Integer getDiasDisponiveis() {
        return diasTotais - diasUsados;
    }
}
