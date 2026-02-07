package com.empresa.ferias.dto.auth;

import com.empresa.ferias.model.Role;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AuthResponse {

    private String token;
    private String tipo;
    private Long id;
    private String nomeCompleto;
    private String email;
    private Role role;
}
