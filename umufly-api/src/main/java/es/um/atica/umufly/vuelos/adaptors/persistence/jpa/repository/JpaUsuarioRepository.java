package es.um.atica.umufly.vuelos.adaptors.persistence.jpa.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import es.um.atica.umufly.vuelos.adaptors.persistence.jpa.entity.UsuarioEntity;

@Repository
public interface JpaUsuarioRepository extends JpaRepository<UsuarioEntity, String> {

	Optional<UsuarioEntity> findByEmail( String email );
}
