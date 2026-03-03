package es.um.atica.umufly.vuelos.adaptors.persistence.jpa;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import es.um.atica.umufly.vuelos.adaptors.persistence.jpa.entity.EstadoReservaVueloEnum;
import es.um.atica.umufly.vuelos.adaptors.persistence.jpa.entity.ReservaVueloViewEntity;
import es.um.atica.umufly.vuelos.adaptors.persistence.jpa.mapper.JpaPersistenceMapper;
import es.um.atica.umufly.vuelos.adaptors.persistence.jpa.repository.JpaReservaVueloRepository;
import es.um.atica.umufly.vuelos.adaptors.persistence.jpa.repository.JpaReservaVueloViewRepository;
import es.um.atica.umufly.vuelos.adaptors.persistence.jpa.repository.JpaVueloRepository;
import es.um.atica.umufly.vuelos.application.port.ReservasVueloReadRepository;
import es.um.atica.umufly.vuelos.domain.exception.ReservaNoEncontradaException;
import es.um.atica.umufly.vuelos.domain.exception.VueloNoEncontradoException;
import es.um.atica.umufly.vuelos.domain.model.DocumentoIdentidad;
import es.um.atica.umufly.vuelos.domain.model.Pasajero;
import es.um.atica.umufly.vuelos.domain.model.ReservaVuelo;

@Component
public class ReservasVueloPersistenceReadAdapter implements ReservasVueloReadRepository {

	private final JpaReservaVueloRepository jpaReservaVueloRepository;
	private final JpaReservaVueloViewRepository jpaReservaVueloViewRepository;
	private final JpaVueloRepository jpaVueloRepository;

	public ReservasVueloPersistenceReadAdapter( JpaReservaVueloRepository jpaReservaVueloRepository, JpaReservaVueloViewRepository jpaReservaVueloViewRepository, JpaVueloRepository jpaVueloRepository ) {
		this.jpaReservaVueloRepository = jpaReservaVueloRepository;
		this.jpaReservaVueloViewRepository = jpaReservaVueloViewRepository;
		this.jpaVueloRepository = jpaVueloRepository;
	}

	@Override
	public Map<UUID, UUID> findReservasIdByVueloIdAndPasajero( DocumentoIdentidad documentoIdentidadPasajero, List<UUID> vueloIds ) {
		if ( vueloIds.isEmpty() ) {
			return Collections.emptyMap();
		}

		List<ReservaVueloViewEntity> reservasVuelo = jpaReservaVueloViewRepository.findByPasajerosTipoDocumentoAndPasajerosNumeroDocumentoAndIdVueloInAndEstadoReservaIn( JpaPersistenceMapper.tipoDocumentoToEntity( documentoIdentidadPasajero.tipo() ),
				documentoIdentidadPasajero.identificador(), vueloIds.stream().map( UUID::toString ).toList(), Arrays.asList( EstadoReservaVueloEnum.P, EstadoReservaVueloEnum.A ) );
		return reservasVuelo.stream().collect( Collectors.toMap( r -> UUID.fromString( r.getIdVuelo() ), r -> UUID.fromString( r.getId() ) ) );
	}

	@Override
	public UUID findReservaIdByVueloIdAndPasajero( DocumentoIdentidad documentoIdentidadPasajero, UUID vueloId ) {
		ReservaVueloViewEntity reservaPasajero = jpaReservaVueloViewRepository.findByPasajerosTipoDocumentoAndPasajerosNumeroDocumentoAndIdVueloAndEstadoReservaIn( JpaPersistenceMapper.tipoDocumentoToEntity( documentoIdentidadPasajero.tipo() ),
				documentoIdentidadPasajero.identificador(),
				vueloId.toString(), Arrays.asList( EstadoReservaVueloEnum.P, EstadoReservaVueloEnum.A ) );
		return reservaPasajero != null ? UUID.fromString( reservaPasajero.getIdVuelo() ) : null;
	}

	@Override
	public int countReservasByIdVueloAndPasajero( UUID idVuelo, Pasajero pasajero ) {
		return jpaReservaVueloViewRepository.countReservasByIdVueloAndPasajero( idVuelo.toString(), JpaPersistenceMapper.tipoDocumentoToEntity( pasajero.getIdentificador().tipo() ).toString(), pasajero.getIdentificador().identificador() );
	}

	@Override
	public ReservaVuelo findReservaById( DocumentoIdentidad documentoIdentidad, UUID idReserva ) {
		return jpaReservaVueloViewRepository
				.findByIdAndPasajerosTipoDocumentoAndPasajerosNumeroDocumentoOrTipoDocumentoTitularAndNumeroDocumentoTitular(
						idReserva.toString(),
						JpaPersistenceMapper.tipoDocumentoToEntity(documentoIdentidad.tipo()),
						documentoIdentidad.identificador()
						)
				.map(reservaEntity -> {

					var vueloEntity = jpaVueloRepository
							.findById(reservaEntity.getIdVuelo())
							.orElseThrow(() -> new VueloNoEncontradoException( reservaEntity.getIdVuelo() ));

					return JpaPersistenceMapper.reservaVueloToModel(
							reservaEntity,
							vueloEntity
							);
				})
				.orElseThrow(() -> new ReservaNoEncontradaException(idReserva.toString()));
	}

	@Override
	public Page<ReservaVuelo> findReservas( DocumentoIdentidad documentoIdentidad, int pagina, int tamanioPagina ) {
		return jpaReservaVueloViewRepository
				.findByPasajerosTipoDocumentoAndPasajerosNumeroDocumentoOrTipoDocumentoTitularAndNumeroDocumentoTitular(
						JpaPersistenceMapper.tipoDocumentoToEntity(documentoIdentidad.tipo()),
						documentoIdentidad.identificador(),
						PageRequest.of(pagina, tamanioPagina)
						)
				.map(r -> {

					var vuelo = jpaVueloRepository
							.findById(r.getIdVuelo())
							.orElseThrow(() ->
							new VueloNoEncontradoException( r.getIdVuelo() ));

					return JpaPersistenceMapper
							.reservaVueloToModel(r, vuelo);
				});
	}

	@Override
	public UUID findIdFormalizadaByReservaById( UUID idReserva ) {
		return UUID.fromString( jpaReservaVueloRepository.findById( idReserva.toString() ).orElseThrow( () -> new ReservaNoEncontradaException( idReserva.toString() ) ).getIdReservaFormalizada() );
	}

}
