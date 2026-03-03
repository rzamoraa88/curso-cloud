package es.um.atica.umufly.parking.application.port;

import java.util.UUID;

import es.um.atica.umufly.parking.domain.model.ReservaParking;

public interface ReservasParkingWriteRepository {

	/**
	 * M�todo que persiste una reserva de vuelo.
	 *
	 * @param reservaParking
	 */
	void persistirReserva( ReservaParking reservaParking );

	/**
	 * Metodo que cancela una reserva a traves de su id.
	 *
	 * @param idReserva
	 * @return
	 */
	void cancelReserva( UUID idReserva );
}
