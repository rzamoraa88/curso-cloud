package es.um.atica.umufly.parking.domain.exception;

public class CancelacionNoPermitidaException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public CancelacionNoPermitidaException( String message ) {
		super( message );
	}

}
