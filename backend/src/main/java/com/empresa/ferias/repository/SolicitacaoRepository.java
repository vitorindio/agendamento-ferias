package com.empresa.ferias.repository;

import com.empresa.ferias.model.Solicitacao;
import com.empresa.ferias.model.StatusSolicitacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface SolicitacaoRepository extends JpaRepository<Solicitacao, Long> {

    List<Solicitacao> findByUsuarioId(Long usuarioId);

    List<Solicitacao> findByUsuarioIdAndStatus(Long usuarioId, StatusSolicitacao status);

    @Query("SELECT s FROM Solicitacao s WHERE s.usuario.id = :usuarioId " +
           "AND s.status NOT IN (com.empresa.ferias.model.StatusSolicitacao.CANCELADO, " +
           "com.empresa.ferias.model.StatusSolicitacao.REJEITADO) " +
           "AND ((s.dataInicio BETWEEN :inicio AND :fim) OR (s.dataFim BETWEEN :inicio AND :fim) " +
           "OR (s.dataInicio <= :inicio AND s.dataFim >= :fim))")
    List<Solicitacao> findConflitos(@Param("usuarioId") Long usuarioId,
                                    @Param("inicio") LocalDate inicio,
                                    @Param("fim") LocalDate fim);

    @Query("SELECT s FROM Solicitacao s WHERE s.usuario.id IN :usuarioIds")
    List<Solicitacao> findByUsuarioIds(@Param("usuarioIds") List<Long> usuarioIds);

    @Query("SELECT s FROM Solicitacao s WHERE s.usuario.id IN :usuarioIds AND s.status = :status")
    List<Solicitacao> findByUsuarioIdsAndStatus(@Param("usuarioIds") List<Long> usuarioIds,
                                                 @Param("status") StatusSolicitacao status);

    @Query("SELECT s FROM Solicitacao s WHERE YEAR(s.dataInicio) = :ano OR YEAR(s.dataFim) = :ano")
    List<Solicitacao> findByAno(@Param("ano") int ano);

    @Query("SELECT s FROM Solicitacao s WHERE s.usuario.id = :usuarioId AND (YEAR(s.dataInicio) = :ano OR YEAR(s.dataFim) = :ano)")
    List<Solicitacao> findByUsuarioIdAndAno(@Param("usuarioId") Long usuarioId, @Param("ano") int ano);

    List<Solicitacao> findByStatus(StatusSolicitacao status);
}
