package es.um.atica.umufly.parking.domain.event;

import es.um.atica.fundewebjs.umubus.domain.events.Event;

public class CancelarParkingIntentEvent extends Event {

	private final String id;
	private final String idReservaFormalizada;
	private final String tipoIdentificadorCliente;
	private final String numeroIdentificadorCliente;

	private CancelarParkingIntentEvent( String id, String idReservaFormalizada, String tipoIdentificadorCliente, String numeroIdentificadorCliente ) {
		this.id = id;
		this.idReservaFormalizada = idReservaFormalizada;
		this.tipoIdentificadorCliente = tipoIdentificadorCliente;
		this.numeroIdentificadorCliente = numeroIdentificadorCliente;
	}

	public static CancelarParkingIntentEvent of( String id, String idReservaFormalizada, String tipoIdentificadorCliente, String numeroIdentificadorCliente ) {
		return new CancelarParkingIntentEvent( id, idReservaFormalizada, tipoIdentificadorCliente, numeroIdentificadorCliente );
	}

	public String getId() {
		return id;
	}

	public String getIdReservaFormalizada() {
		return idReservaFormalizada;
	}

	public String getTipoIdentificadorCliente() {
		return tipoIdentificadorCliente;
	}

	public String getNumeroIdentificadorCliente() {
		return numeroIdentificadorCliente;
	}

}
