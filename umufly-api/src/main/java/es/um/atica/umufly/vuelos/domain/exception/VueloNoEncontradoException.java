package es.um.atica.umufly.vuelos.domain.exception;

public class VueloNoEncontradoException extends RuntimeException {
	private static final long serialVersionUID = 4050411643980076071L;

	private final String idVuelo;

	public VueloNoEncontradoException(String idVuelo) {
		super("Vuelo no encontrado");
		this.idVuelo = idVuelo;
	}

	public String getIdVuelo() {
		return idVuelo;
	}
}
