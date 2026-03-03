package es.um.atica.umufly.parking.domain.model;

public record Importe(double valor) {
	public Importe {
		if ( valor <= 0 ) {
			throw new IllegalArgumentException( "El valor del importe tiene que ser mayor que cero" );
		}
	}
}
