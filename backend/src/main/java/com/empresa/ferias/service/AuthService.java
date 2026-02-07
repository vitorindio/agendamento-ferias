package com.empresa.ferias.service;

import com.empresa.ferias.dto.auth.AuthResponse;
import com.empresa.ferias.dto.auth.LoginRequest;
import com.empresa.ferias.dto.auth.RegisterRequest;
import com.empresa.ferias.model.Role;
import com.empresa.ferias.model.SaldoFerias;
import com.empresa.ferias.model.Usuario;
import com.empresa.ferias.repository.SaldoFeriasRepository;
import com.empresa.ferias.repository.UsuarioRepository;
import com.empresa.ferias.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.Year;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final SaldoFeriasRepository saldoFeriasRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final EmailService emailService;

    @Value("${app.confirmation-token-expiration}")
    private long tokenExpiration;

    @Transactional
    public String register(RegisterRequest request) {
        if (usuarioRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email já cadastrado");
        }

        String token = UUID.randomUUID().toString();

        Usuario usuario = Usuario.builder()
                .nomeCompleto(request.getNomeCompleto())
                .email(request.getEmail())
                .senhaHash(passwordEncoder.encode(request.getSenha()))
                .cargo(request.getCargo())
                .dataAdmissao(request.getDataAdmissao())
                .isAtivo(false) // Aguardando confirmação de email
                .role(Role.USER)
                .tokenConfirmacao(token)
                .tokenConfirmacaoExpiracao(LocalDateTime.now().plus(Duration.ofMillis(tokenExpiration)))
                .build();

        usuarioRepository.save(usuario);

        // Criar saldo de férias para o ano atual
        SaldoFerias saldo = SaldoFerias.builder()
                .usuario(usuario)
                .anoReferencia(Year.now().getValue())
                .diasTotais(30)
                .diasUsados(0)
                .build();
        saldoFeriasRepository.save(saldo);

        // Enviar email de confirmação
        emailService.enviarEmailConfirmacao(usuario.getEmail(), usuario.getNomeCompleto(), token);

        return "Cadastro realizado! Verifique seu email para confirmar a conta.";
    }

    @Transactional
    public String confirmEmail(String token) {
        Usuario usuario = usuarioRepository.findByTokenConfirmacao(token)
                .orElseThrow(() -> new RuntimeException("Token inválido"));

        if (usuario.getTokenConfirmacaoExpiracao().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Token expirado");
        }

        usuario.setIsAtivo(true);
        usuario.setTokenConfirmacao(null);
        usuario.setTokenConfirmacaoExpiracao(null);
        usuarioRepository.save(usuario);

        return "Email confirmado com sucesso! Você já pode fazer login.";
    }

    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getSenha())
        );

        Usuario usuario = usuarioRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        if (!usuario.getIsAtivo()) {
            throw new RuntimeException("Conta não ativada. Verifique seu email.");
        }

        String jwtToken = jwtService.generateToken(usuario);

        return AuthResponse.builder()
                .token(jwtToken)
                .tipo("Bearer")
                .id(usuario.getId())
                .nomeCompleto(usuario.getNomeCompleto())
                .email(usuario.getEmail())
                .role(usuario.getRole())
                .build();
    }
}
