package com.empresa.ferias.config;

import com.empresa.ferias.model.*;
import com.empresa.ferias.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.Year;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;
    private final TipoAusenciaRepository tipoAusenciaRepository;
    private final EquipeRepository equipeRepository;
    private final SaldoFeriasRepository saldoFeriasRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        // Criar tipos de ausência padrão
        if (tipoAusenciaRepository.count() == 0) {
            log.info("Criando tipos de ausência padrão...");

            tipoAusenciaRepository.save(TipoAusencia.builder()
                    .nome("Férias")
                    .corHex("#34D399")
                    .deduzSaldo(true)
                    .descricao("Férias anuais remuneradas")
                    .build());

            tipoAusenciaRepository.save(TipoAusencia.builder()
                    .nome("Licença Médica")
                    .corHex("#F87171")
                    .deduzSaldo(false)
                    .descricao("Afastamento por motivo de saúde")
                    .build());

            tipoAusenciaRepository.save(TipoAusencia.builder()
                    .nome("Licença Maternidade")
                    .corHex("#A78BFA")
                    .deduzSaldo(false)
                    .descricao("Licença maternidade de 120 dias")
                    .build());

            tipoAusenciaRepository.save(TipoAusencia.builder()
                    .nome("Licença Paternidade")
                    .corHex("#60A5FA")
                    .deduzSaldo(false)
                    .descricao("Licença paternidade de 5 dias")
                    .build());

            tipoAusenciaRepository.save(TipoAusencia.builder()
                    .nome("Day Off")
                    .corHex("#FBBF24")
                    .deduzSaldo(false)
                    .descricao("Folga compensatória")
                    .build());

            tipoAusenciaRepository.save(TipoAusencia.builder()
                    .nome("Home Office")
                    .corHex("#818CF8")
                    .deduzSaldo(false)
                    .descricao("Trabalho remoto")
                    .build());

            log.info("Tipos de ausência criados!");
        }

        // Criar usuário gestor padrão
        if (!usuarioRepository.existsByEmail("gestor@empresa.com")) {
            log.info("Criando usuário gestor padrão...");

            Usuario gestor = Usuario.builder()
                    .nomeCompleto("Gestor Principal")
                    .email("gestor@empresa.com")
                    .senhaHash(passwordEncoder.encode("123456"))
                    .cargo("Gestor")
                    .dataAdmissao(LocalDate.of(2020, 1, 1))
                    .role(Role.GESTOR)
                    .isAtivo(true)
                    .build();

            gestor = usuarioRepository.save(gestor);

            // Criar saldo de férias para o gestor
            saldoFeriasRepository.save(SaldoFerias.builder()
                    .usuario(gestor)
                    .anoReferencia(Year.now().getValue())
                    .diasTotais(30)
                    .diasUsados(0)
                    .build());

            log.info("Usuário gestor criado! Email: gestor@empresa.com, Senha: 123456");
        }

        // Criar alguns usuários de exemplo para testes
        if (usuarioRepository.count() < 4) {
            log.info("Criando usuários de exemplo...");

            // Usuários comuns
            criarUsuarioSeNaoExistir(
                    "João Santos",
                    "joao@empresa.com",
                    "123456",
                    "Desenvolvedor",
                    Role.USER
            );

            criarUsuarioSeNaoExistir(
                    "Ana Oliveira",
                    "ana@empresa.com",
                    "123456",
                    "Designer",
                    Role.USER
            );

            criarUsuarioSeNaoExistir(
                    "Carlos Silva",
                    "carlos@empresa.com",
                    "123456",
                    "Analista",
                    Role.USER
            );

            log.info("Usuários de exemplo criados!");
        }
    }

    private Usuario criarUsuarioSeNaoExistir(String nome, String email, String senha, String cargo, Role role) {
        if (usuarioRepository.existsByEmail(email)) {
            return usuarioRepository.findByEmail(email).orElse(null);
        }

        Usuario usuario = Usuario.builder()
                .nomeCompleto(nome)
                .email(email)
                .senhaHash(passwordEncoder.encode(senha))
                .cargo(cargo)
                .dataAdmissao(LocalDate.of(2023, 1, 1))
                .role(role)
                .isAtivo(true)
                .build();

        usuario = usuarioRepository.save(usuario);

        saldoFeriasRepository.save(SaldoFerias.builder()
                .usuario(usuario)
                .anoReferencia(Year.now().getValue())
                .diasTotais(30)
                .diasUsados(0)
                .build());

        return usuario;
    }
}
