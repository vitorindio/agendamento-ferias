package com.empresa.ferias.dto;

import com.empresa.ferias.model.Equipe;
import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.stream.Collectors;

@Data
@Builder
public class EquipeDTO {

    private Long id;
    private String nome;
    private String descricao;
    private Long gestorId;
    private String gestorNome;
    private List<UsuarioDTO> membros;

    public static EquipeDTO fromEntity(Equipe equipe) {
        return EquipeDTO.builder()
                .id(equipe.getId())
                .nome(equipe.getNome())
                .descricao(equipe.getDescricao())
                .gestorId(equipe.getGestor() != null ? equipe.getGestor().getId() : null)
                .gestorNome(equipe.getGestor() != null ? equipe.getGestor().getNomeCompleto() : null)
                .membros(equipe.getMembros().stream()
                        .map(UsuarioDTO::fromEntity)
                        .collect(Collectors.toList()))
                .build();
    }

    public static EquipeDTO fromEntitySimple(Equipe equipe) {
        return EquipeDTO.builder()
                .id(equipe.getId())
                .nome(equipe.getNome())
                .descricao(equipe.getDescricao())
                .gestorId(equipe.getGestor() != null ? equipe.getGestor().getId() : null)
                .gestorNome(equipe.getGestor() != null ? equipe.getGestor().getNomeCompleto() : null)
                .build();
    }
}
