package es.um.atica.umufly.vuelos.application.usecase.crearreservas;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.stereotype.Component;

import es.um.atica.fundewebjs.umubus.domain.cqrs.SyncCommandHandler;
import es.um.atica.umufly.vuelos.application.port.ReservasVueloReadRepository;
import es.um.atica.umufly.vuelos.application.port.ReservasVueloWritePort;
import es.um.atica.umufly.vuelos.application.port.ReservasVueloWriteRepository;
import es.um.atica.umufly.vuelos.application.port.VuelosReadRepository;
import es.um.atica.umufly.vuelos.domain.model.ReservaVuelo;
import es.um.atica.umufly.vuelos.domain.model.Vuelo;

@Component
public class CrearReservaCommandHandler implements SyncCommandHandler<ReservaVuelo, CrearReservaCommand> {

	private VuelosReadRepository vuelosReadRepository;
	private ReservasVueloReadRepository reservasVueloReadRepository;
	private ReservasVueloWriteRepository reservasVueloWriteRepository;
	private final ReservasVueloWritePort reservasVueloWritePort;
	private final Clock clock;

	public CrearReservaCommandHandler( VuelosReadRepository vuelosReadRepository, ReservasVueloReadRepository reservasVueloReadRepository, ReservasVueloWriteRepository reservasVueloWriteRepository, ReservasVueloWritePort reservasVueloWritePort, Clock clock ) {
		this.vuelosReadRepository = vuelosReadRepository;
		this.reservasVueloReadRepository = reservasVueloReadRepository;
		this.reservasVueloWriteRepository = reservasVueloWriteRepository;
		this.reservasVueloWritePort = reservasVueloWritePort;
		this.clock = clock;
	}

	@Override
	public ReservaVuelo handle( CrearReservaCommand command ) throws Exception {

		// 1. Recuperar el vuelo
		Vuelo vuelo = vuelosReadRepository.findVuelo( command.getIdVuelo() );

		// 2. Recuperar el número de reservas del pasajero en el vuelo
		int numeroReservasPasajeroEnVuelo = reservasVueloReadRepository.countReservasByIdVueloAndPasajero( command.getIdVuelo(), command.getPasajero() );

		// 3. Recuperar el número de plazas disponibles en el avión
		int numeroPlazasDisponiblesAvion = vuelosReadRepository.plazasDisponiblesEnVuelo( vuelo );

		// 4. Creamos y persistimos la reserva de vuelo
		ReservaVuelo reservaVuelo = ReservaVuelo.solicitarReserva( command.getDocumentoIdentidadTitular(), command.getPasajero(), vuelo, command.getClaseAsiento(), LocalDateTime.now( clock ), numeroReservasPasajeroEnVuelo, numeroPlazasDisponiblesAvion );
		reservasVueloWriteRepository.persistirReserva( reservaVuelo );

		// 5. Formalizamos la reserva llamando al backoffice para que se haga eco de la nueva reserva que acabamos de crear
		UUID idReservaFormalizada = reservasVueloWritePort.formalizarReservaVuelo( reservaVuelo );
		reservaVuelo.formalizarReserva();
		reservasVueloWriteRepository.persistirFormalizacionReserva( reservaVuelo.getId(), idReservaFormalizada );

		return reservaVuelo;

		// 1. Recuperar el vuelo
		// vuelosReadRepository.findVuelo( command.getIdVuelo() )
		/*
		reservasVueloReadRepository.findReservaById( command.getDocumentoIdentidadTitular(), command.getIdReserva() ).ifPresentOrElse( ( reservaVuelo ) -> { // CASO: Existe la reserva
			// 2. Cancelamos la reserva en el dominio (FrontOffice)
			reservaVuelo.cancelarReserva( LocalDateTime.now( clock ) );
			reservasVueloWriteRepository.cancelReserva( reservaVuelo.getId() );

			// 3. Obtenemos el ID de formalización y notificamos al BackOffice
			// Nota: He usado la reserva que ya tenemos en memoria para evitar otra consulta
			UUID idFormalizada = reservasVueloReadRepository.findIdFormalizadaByReservaById( command.getIdReserva() );

			reservasVueloWritePort.cancelarReservaVuelo( command.getDocumentoIdentidadTitular(), idFormalizada );
		}, () -> { // CASO: No existe la reserva
			throw new EntityNotFoundException( "No se encontró la reserva con ID: " + command.getIdReserva() );
		} );*/
	}

}
