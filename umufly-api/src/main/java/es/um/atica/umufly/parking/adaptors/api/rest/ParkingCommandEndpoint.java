package es.um.atica.umufly.parking.adaptors.api.rest;


import java.util.UUID;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import es.um.atica.fundewebjs.umubus.domain.cqrs.CommandBus;
import es.um.atica.fundewebjs.umubus.domain.cqrs.SyncCommandBus;
import es.um.atica.umufly.parking.adaptors.api.rest.dto.ReservaParkingDTO;
import es.um.atica.umufly.parking.application.usecase.cancelarparking.CancelarParkingCommand;
import es.um.atica.umufly.parking.application.usecase.crearparking.CrearParkingCommand;
import es.um.atica.umufly.parking.domain.model.Periodo;
import jakarta.validation.Valid;

@RestController
public class ParkingCommandEndpoint {
	private final SyncCommandBus syncCommandBus;
	private final CommandBus commandBus;
	private final ParkingModelAssembler reservasModelAssembler;
	private final AuthServiceParking authService;

	public ParkingCommandEndpoint( SyncCommandBus syncCommandBus, CommandBus commandBus, ParkingModelAssembler reservasModelAssembler,
			AuthServiceParking authService ) {
		this.syncCommandBus = syncCommandBus;
		this.commandBus = commandBus;
		this.reservasModelAssembler = reservasModelAssembler;
		this.authService = authService;
	}

	@PostMapping( Constants.PRIVATE_PREFIX + Constants.API_VERSION_1 + Constants.RESOURCE_PARKINGS )
	public ReservaParkingDTO creaParking( @RequestHeader( name = "UMU-Usuario", required = true ) String usuario, @RequestBody @Valid ReservaParkingDTO nuevaReserva ) throws Exception {
		return reservasModelAssembler.toModel( syncCommandBus.handle( CrearParkingCommand.of( authService.parseUserHeader( usuario ), new Periodo( nuevaReserva.getFechaInicio(), nuevaReserva.getFechaFin() ) ) ) );
	}

	@DeleteMapping( Constants.PRIVATE_PREFIX + Constants.API_VERSION_1 + Constants.RESOURCE_PARKINGS + Constants.ID_RESERVA )
	public void cancelarParking( @RequestHeader( name = "UMU-Usuario", required = true ) String usuario, @PathVariable( "idParking" ) UUID idParking ) throws Exception {
		commandBus.handle( CancelarParkingCommand.of( authService.parseUserHeader( usuario ), idParking ) );
	}
}
