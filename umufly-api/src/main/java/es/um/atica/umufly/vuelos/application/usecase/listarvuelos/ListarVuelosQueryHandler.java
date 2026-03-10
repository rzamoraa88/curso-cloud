package es.um.atica.umufly.vuelos.application.usecase.listarvuelos;

import java.util.Collections;
import java.util.Map;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import es.um.atica.fundewebjs.umubus.domain.cqrs.QueryHandler;
import es.um.atica.umufly.vuelos.application.dto.VueloAmpliadoDTO;
import es.um.atica.umufly.vuelos.application.mapper.ApplicationMapper;
import es.um.atica.umufly.vuelos.application.port.ReservasVueloReadRepository;
import es.um.atica.umufly.vuelos.application.port.VuelosReadRepository;
import es.um.atica.umufly.vuelos.domain.model.Vuelo;

@Component
public class ListarVuelosQueryHandler implements QueryHandler<Page<VueloAmpliadoDTO>,ListarVuelosQuery> {
	private final VuelosReadRepository vuelosReadRepository;
	private final ReservasVueloReadRepository reservasVueloReadRepository;

	public ListarVuelosQueryHandler( VuelosReadRepository vuelosReadRepository, ReservasVueloReadRepository reservasVueloReadRepository ) {
		this.vuelosReadRepository = vuelosReadRepository;
		this.reservasVueloReadRepository = reservasVueloReadRepository;
	}

	@Override
	public Page<VueloAmpliadoDTO> handle( ListarVuelosQuery query ) {
		Page<Vuelo> vuelos = vuelosReadRepository.findVuelos( query.getPagina(), query.getTamanioPagina() );
		Map<UUID, UUID> vuelosReserva = query.getDocumentoIdentidadPasajero() != null ? reservasVueloReadRepository.findReservasIdByVueloIdAndPasajero( query.getDocumentoIdentidadPasajero(), vuelos.map( Vuelo::getId ).getContent() ) : Collections.emptyMap();

		return vuelos.map( v -> ApplicationMapper.vueloToDTO( v, vuelosReserva.get( v.getId() ) ) );
	}

}
