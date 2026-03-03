package es.um.atica.umufly.vuelos.domain.exception;

public class ReservaNoEncontradaException extends RuntimeException {
	private static final long serialVersionUID = -8164952868648606261L;

	private final String idReserva;

	public ReservaNoEncontradaException(String idReserva) {
		super("Reserva no encontrada");
		this.idReserva = idReserva;
	}

	public String getIdReserva() {
		return idReserva;
	}
}
