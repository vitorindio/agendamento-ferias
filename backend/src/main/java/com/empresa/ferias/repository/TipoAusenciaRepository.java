package com.empresa.ferias.repository;

import com.empresa.ferias.model.TipoAusencia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TipoAusenciaRepository extends JpaRepository<TipoAusencia, Long> {

    Optional<TipoAusencia> findByNome(String nome);

    List<TipoAusencia> findByIsAtivoTrue();

    boolean existsByNome(String nome);
}
