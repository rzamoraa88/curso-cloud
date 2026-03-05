package es.um.atica.umufly.parking.domain.event;

import java.util.UUID;

import es.um.atica.fundewebjs.umubus.domain.events.Event;
import es.um.atica.umufly.parking.domain.model.DocumentoIdentidad;

public class CancelarParkingIntentEvent extends Event {

	private final DocumentoIdentidad documentoIdentidadTitular;
	private final UUID idParking;

	private CancelarParkingIntentEvent( DocumentoIdentidad documentoIdentidadTitular, UUID idParking ) {
		this.documentoIdentidadTitular = documentoIdentidadTitular;
		this.idParking = idParking;
	}

	public static CancelarParkingIntentEvent of( DocumentoIdentidad documentoIdentidadTitular, UUID idParking ) {
		return new CancelarParkingIntentEvent( documentoIdentidadTitular, idParking );
	}

	public DocumentoIdentidad getDocumentoIdentidadTitular() {
		return documentoIdentidadTitular;
	}

	public UUID getIdParking() {
		return idParking;
	}

}
