package com.empresa.ferias.repository;

import com.empresa.ferias.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByEmail(String email);

    Optional<Usuario> findByTokenConfirmacao(String token);

    boolean existsByEmail(String email);

    @Query("SELECT u FROM Usuario u JOIN u.equipes e WHERE e.id = :equipeId")
    List<Usuario> findByEquipeId(Long equipeId);

    List<Usuario> findByIsAtivoTrue();
}
