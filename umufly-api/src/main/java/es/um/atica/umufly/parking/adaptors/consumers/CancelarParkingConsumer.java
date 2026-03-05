package es.um.atica.umufly.parking.adaptors.consumers;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import es.um.atica.umufly.parking.application.port.ReservasParkingWritePort;
import es.um.atica.umufly.parking.domain.event.CancelarParkingIntentEvent;

@Component
public class CancelarParkingConsumer {

	private final ReservasParkingWritePort reservasParkingWritePort;

	public CancelarParkingConsumer( ReservasParkingWritePort reservasParkingWritePort ) {
		this.reservasParkingWritePort = reservasParkingWritePort;
	}

	@EventListener
	public void accept( CancelarParkingIntentEvent event ) {
		// Logica de negocio, crea eventos de respuesta OK/KO
		reservasParkingWritePort.cancelarParking( event.getDocumentoIdentidadTitular(), event.getIdParking() );
	}
}
