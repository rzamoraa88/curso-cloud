package es.um.atica.umufly.vuelos.application.usecase.cancelarreservas;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.stereotype.Component;

import es.um.atica.fundewebjs.umubus.domain.cqrs.SyncCommandHandler;
import es.um.atica.umufly.vuelos.application.port.ReservasVueloReadRepository;
import es.um.atica.umufly.vuelos.application.port.ReservasVueloWritePort;
import es.um.atica.umufly.vuelos.application.port.ReservasVueloWriteRepository;
import es.um.atica.umufly.vuelos.domain.model.ReservaVuelo;

@Component
public class CancelarReservaCommandHandler implements SyncCommandHandler<ReservaVuelo, CancelarReservaCommand> {

	private final ReservasVueloReadRepository reservasVueloReadRepository;
	private final ReservasVueloWriteRepository reservasVueloWriteRepository;
	private final ReservasVueloWritePort reservasVueloWritePort;
	private final Clock clock;

	public CancelarReservaCommandHandler( ReservasVueloReadRepository reservasVueloReadRepository, ReservasVueloWriteRepository reservasVueloWriteRepository, ReservasVueloWritePort reservasVueloWritePort, Clock clock ) {
		this.reservasVueloReadRepository = reservasVueloReadRepository;
		this.reservasVueloWriteRepository = reservasVueloWriteRepository;
		this.reservasVueloWritePort = reservasVueloWritePort;
		this.clock = clock;
	}

	@Override
	public ReservaVuelo handle( CancelarReservaCommand command ) throws Exception {
		// 1. Recuperamos la reserva
		ReservaVuelo reservaVuelo = reservasVueloReadRepository.findReservaById( command.getDocumentoIdentidadTitular(), command.getIdReserva() );

		// 2. Cancelamos la reserva en el fronOffice
		reservaVuelo.cancelarReserva( LocalDateTime.now( clock ) );
		reservasVueloWriteRepository.cancelReserva( reservaVuelo.getId() );

		// 3. Cancelamos la reserva llamando al backoffice para que se haga eco de la cancelacion
		UUID idReservaFormalizada = reservasVueloReadRepository.findIdFormalizadaByReservaById( command.getIdReserva() );
		reservasVueloWritePort.cancelarReservaVuelo( command.getDocumentoIdentidadTitular(), idReservaFormalizada );

		return reservaVuelo;
		/*
		 * // Idempotencia // 1. Recuperamos la reserva reservasVueloReadRepository.findReservaById(
		 * command.getDocumentoIdentidadTitular(), command.getIdReserva() ).ifPresentOrElse( ( reservaVuelo ) -> { // CASO:
		 * Existe la reserva // 2. Cancelamos la reserva en el dominio (FrontOffice) reservaVuelo.cancelarReserva(
		 * LocalDateTime.now( clock ) ); reservasVueloWriteRepository.cancelReserva( reservaVuelo.getId() ); // 3. Obtenemos el
		 * ID de formalización y notificamos al BackOffice // Nota: He usado la reserva que ya tenemos en memoria para evitar
		 * otra consulta UUID idFormalizada = reservasVueloReadRepository.findIdFormalizadaByReservaById( command.getIdReserva()
		 * ); reservasVueloWritePort.cancelarReservaVuelo( command.getDocumentoIdentidadTitular(), idFormalizada ); }, () -> {
		 * // CASO: No existe la reserva throw new EntityNotFoundException( "No se encontró la reserva con ID: " +
		 * command.getIdReserva() ); } );
		 */
	}

}
