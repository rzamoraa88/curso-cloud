package es.um.atica.umufly.parking.adaptors.persistence.jpa;

import java.util.UUID;

import org.springframework.stereotype.Component;

import es.um.atica.umufly.parking.application.port.ReservasParkingWriteRepository;
import es.um.atica.umufly.parking.domain.model.ReservaParking;

@Component
public class ReservasParkingPersistenceWriteAdapter implements ReservasParkingWriteRepository {

	@Override
	public void persistirReserva( ReservaParking reservaParking ) {
		// TODO Auto-generated method stub

	}

	@Override
	public void cancelReserva( UUID idReserva ) {
		// TODO Auto-generated method stub

	}



}
