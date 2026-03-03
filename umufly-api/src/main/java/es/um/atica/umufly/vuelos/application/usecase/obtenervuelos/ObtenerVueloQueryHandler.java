package es.um.atica.umufly.vuelos.application.usecase.obtenervuelos;

import java.util.UUID;

import es.um.atica.fundewebjs.umubus.domain.cqrs.QueryHandler;
import es.um.atica.umufly.vuelos.application.dto.VueloAmpliadoDTO;
import es.um.atica.umufly.vuelos.application.mapper.ApplicationMapper;
import es.um.atica.umufly.vuelos.application.port.ReservasVueloReadRepository;
import es.um.atica.umufly.vuelos.application.port.VuelosReadRepository;
import es.um.atica.umufly.vuelos.domain.model.Vuelo;

public class ObtenerVueloQueryHandler implements QueryHandler<VueloAmpliadoDTO, ObtenerVueloQuery> {
	private final VuelosReadRepository vuelosReadRepository;
	private final ReservasVueloReadRepository reservasVueloReadRepository;

	public ObtenerVueloQueryHandler( VuelosReadRepository vuelosReadRepository, ReservasVueloReadRepository reservasVueloReadRepository ) {
		this.vuelosReadRepository = vuelosReadRepository;
		this.reservasVueloReadRepository = reservasVueloReadRepository;
	}

	@Override
	public VueloAmpliadoDTO handle( ObtenerVueloQuery query )  {
		Vuelo vuelo = vuelosReadRepository.findVuelo( query.getIdVuelo() );
		UUID vueloReserva = query.getDocumentoIdentidadPasajero() != null ? reservasVueloReadRepository.findReservaIdByVueloIdAndPasajero( query.getDocumentoIdentidadPasajero(), vuelo.getId() ) : null;

		return ApplicationMapper.vueloToDTO( vuelo, vueloReserva );
	}

}
