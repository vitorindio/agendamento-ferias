package com.empresa.ferias.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "equipes")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Equipe {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @Column(nullable = false)
    private String nome;

    private String descricao;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "gestor_id")
    private Usuario gestor;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "membros_equipe",
        joinColumns = @JoinColumn(name = "equipe_id"),
        inverseJoinColumns = @JoinColumn(name = "usuario_id")
    )
    @Builder.Default
    private Set<Usuario> membros = new HashSet<>();

    @Column(name = "created_at")
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    public void adicionarMembro(Usuario usuario) {
        this.membros.add(usuario);
        usuario.getEquipes().add(this);
    }

    public void removerMembro(Usuario usuario) {
        this.membros.remove(usuario);
        usuario.getEquipes().remove(this);
    }
}
