package com.empresa.ferias.repository;

import com.empresa.ferias.model.SaldoFerias;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SaldoFeriasRepository extends JpaRepository<SaldoFerias, Long> {

    Optional<SaldoFerias> findByUsuarioIdAndAnoReferencia(Long usuarioId, Integer anoReferencia);

    List<SaldoFerias> findByUsuarioId(Long usuarioId);

    List<SaldoFerias> findByAnoReferencia(Integer anoReferencia);

    boolean existsByUsuarioIdAndAnoReferencia(Long usuarioId, Integer anoReferencia);
}
