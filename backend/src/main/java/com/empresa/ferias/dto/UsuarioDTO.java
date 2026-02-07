package com.empresa.ferias.dto;

import com.empresa.ferias.model.Role;
import com.empresa.ferias.model.Usuario;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class UsuarioDTO {

    private Long id;
    private String nomeCompleto;
    private String email;
    private String cargo;
    private LocalDate dataAdmissao;
    private Role role;
    private Boolean isAtivo;

    public static UsuarioDTO fromEntity(Usuario usuario) {
        return UsuarioDTO.builder()
                .id(usuario.getId())
                .nomeCompleto(usuario.getNomeCompleto())
                .email(usuario.getEmail())
                .cargo(usuario.getCargo())
                .dataAdmissao(usuario.getDataAdmissao())
                .role(usuario.getRole())
                .isAtivo(usuario.getIsAtivo())
                .build();
    }
}
