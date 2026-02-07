package com.empresa.ferias.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${app.frontend-url}")
    private String frontendUrl;

    @Async
    public void enviarEmailConfirmacao(String email, String nome, String token) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(email);
            message.setSubject("Confirme seu cadastro - Sistema de Férias");
            message.setText(
                    "Olá " + nome + ",\n\n" +
                    "Obrigado por se cadastrar no Sistema de Gestão de Férias!\n\n" +
                    "Clique no link abaixo para confirmar seu email:\n" +
                    frontendUrl + "/confirmar-email?token=" + token + "\n\n" +
                    "Este link expira em 24 horas.\n\n" +
                    "Se você não solicitou este cadastro, ignore este email.\n\n" +
                    "Atenciosamente,\n" +
                    "Equipe Sistema de Férias"
            );

            mailSender.send(message);
            log.info("Email de confirmação enviado para: {}", email);
        } catch (Exception e) {
            log.error("Erro ao enviar email de confirmação para {}: {}", email, e.getMessage());
            // Em desenvolvimento, não falhar por causa de email
        }
    }

    @Async
    public void enviarNotificacaoSolicitacao(String emailGestor, String nomeGestor, String nomeFuncionario, String periodo) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(emailGestor);
            message.setSubject("Nova solicitação de férias pendente");
            message.setText(
                    "Olá " + nomeGestor + ",\n\n" +
                    "Você tem uma nova solicitação de férias para aprovar:\n\n" +
                    "Funcionário: " + nomeFuncionario + "\n" +
                    "Período: " + periodo + "\n\n" +
                    "Acesse o sistema para aprovar ou rejeitar a solicitação.\n\n" +
                    "Atenciosamente,\n" +
                    "Sistema de Férias"
            );

            mailSender.send(message);
            log.info("Notificação de solicitação enviada para: {}", emailGestor);
        } catch (Exception e) {
            log.error("Erro ao enviar notificação para {}: {}", emailGestor, e.getMessage());
        }
    }

    @Async
    public void enviarNotificacaoAprovacao(String email, String nome, String periodo, boolean aprovado, String motivo) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(email);
            message.setSubject("Sua solicitação de férias foi " + (aprovado ? "aprovada" : "rejeitada"));

            String texto = "Olá " + nome + ",\n\n" +
                    "Sua solicitação de férias para o período " + periodo + " foi " +
                    (aprovado ? "APROVADA" : "REJEITADA") + ".\n\n";

            if (!aprovado && motivo != null && !motivo.isEmpty()) {
                texto += "Motivo: " + motivo + "\n\n";
            }

            texto += "Atenciosamente,\n" +
                    "Sistema de Férias";

            message.setText(texto);

            mailSender.send(message);
            log.info("Notificação de {} enviada para: {}", aprovado ? "aprovação" : "rejeição", email);
        } catch (Exception e) {
            log.error("Erro ao enviar notificação para {}: {}", email, e.getMessage());
        }
    }
}
