package com.empresa.ferias.repository;

import com.empresa.ferias.model.Equipe;
import com.empresa.ferias.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EquipeRepository extends JpaRepository<Equipe, Long> {

    List<Equipe> findByGestor(Usuario gestor);

    List<Equipe> findByGestorId(Long gestorId);

    // Query com JOIN FETCH para carregar membros em uma única query (evita N+1)
    @Query("SELECT DISTINCT e FROM Equipe e LEFT JOIN FETCH e.membros WHERE e.gestor.id = :gestorId")
    List<Equipe> findByGestorIdWithMembros(@Param("gestorId") Long gestorId);

    @Query("SELECT e FROM Equipe e JOIN e.membros m WHERE m.id = :usuarioId")
    List<Equipe> findByMembroId(Long usuarioId);

    boolean existsByNome(String nome);
}
