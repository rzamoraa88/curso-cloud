package es.um.atica.umufly.vuelos.application.usecase.obtenerreservas;

import org.springframework.stereotype.Component;

import es.um.atica.fundewebjs.umubus.domain.cqrs.QueryHandler;
import es.um.atica.umufly.vuelos.application.port.ReservasVueloReadRepository;
import es.um.atica.umufly.vuelos.domain.model.ReservaVuelo;

@Component
public class ObtenerReservasQueryHandler implements QueryHandler<ReservaVuelo, ObtenerReservasQuery> {

	private final ReservasVueloReadRepository reservasVueloReadRepository;

	public ObtenerReservasQueryHandler( ReservasVueloReadRepository reservasVueloReadRepository ) {
		this.reservasVueloReadRepository = reservasVueloReadRepository;
	}

	@Override
	public ReservaVuelo handle( ObtenerReservasQuery query ) {
		return reservasVueloReadRepository.findReservaById( query.getDocumentoIdentidad(), query.getIdReserva() );
	}
}
