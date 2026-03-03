package es.um.atica.umufly.parking.domain.model;


public enum TipoReserva {
	CORTA_DURACION(0.02),
	LARGA_DURACION(7.00);

	private final double precio;

	TipoReserva(double precio) {
		this.precio = precio;
	}

	public double getPrecio() {
		return precio;
	}
}
