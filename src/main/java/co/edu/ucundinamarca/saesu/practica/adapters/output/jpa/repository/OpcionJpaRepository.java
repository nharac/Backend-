package co.edu.ucundinamarca.saesu.practica.adapters.output.jpa.repository;

import co.edu.ucundinamarca.saesu.practica.adapters.output.jpa.entity.OpcionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio JPA para OpcionEntity.
 */
@Repository
public interface OpcionJpaRepository extends JpaRepository<OpcionEntity, Long> {

    /**
     * Lista todas las opciones de una pregunta, ordenadas por letra.
     */
    List<OpcionEntity> findByPreguntaIdOrderByLetra(Long preguntaId);

    /**
     * Busca la opción correcta de una pregunta.
     */
    Optional<OpcionEntity> findByPreguntaIdAndEsCorrectaTrue(Long preguntaId);
}