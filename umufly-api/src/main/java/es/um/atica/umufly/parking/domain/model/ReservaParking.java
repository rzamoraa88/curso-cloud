package es.um.atica.umufly.parking.domain.model;

import java.time.LocalDateTime;
import java.util.UUID;

import es.um.atica.umufly.parking.domain.exception.CancelacionNoPermitidaException;
import es.um.atica.umufly.parking.domain.exception.ReservaYaCanceladaException;

public class ReservaParking {
	private UUID id;
	private DocumentoIdentidad identificadorPasajero;
	private TipoReserva tipo;
	private Periodo periodo;
	private Importe importe;
	private LocalDateTime fechaReserva;
	private EstadoReserva estado;

	private ReservaParking( UUID id, DocumentoIdentidad identificadorPasajero, TipoReserva tipo, Periodo periodo, Importe importe, LocalDateTime fechaReserva, EstadoReserva estado ) {
		this.id = id;
		this.identificadorPasajero = identificadorPasajero;
		this.tipo = tipo;
		this.periodo = periodo;
		this.importe = importe;
		this.fechaReserva = fechaReserva;
		this.estado = estado;
	}

	public static ReservaParking of(UUID id, DocumentoIdentidad identificadorPasajero, TipoReserva tipo, Periodo periodo, Importe importe, LocalDateTime fechaReserva, EstadoReserva estado) {
		if ( id == null ) {
			throw new IllegalArgumentException( "El id de la reserva no puede ser nulo" );
		}
		if ( identificadorPasajero == null ) {
			throw new IllegalArgumentException( "El titular de la reserva no puede ser nulo" );
		}
		if ( tipo == null ) {
			throw new IllegalArgumentException( "El tipo de la reserva no puede ser nulo" );
		}
		if ( periodo == null ) {
			throw new IllegalArgumentException( "El periodo de la reserva no puede ser nulo" );
		}
		if ( importe == null ) {
			throw new IllegalArgumentException( "El importe de la reserva no puede ser nulo" );
		}
		if ( fechaReserva == null ) {
			throw new IllegalArgumentException( "La fecha de la reserva no puede ser nula" );
		}
		if ( estado == null ) {
			throw new IllegalArgumentException( "El estado de la reserva no puede ser nulo" );
		}

		return new ReservaParking( id, identificadorPasajero, tipo, periodo, importe, fechaReserva, estado );
	}

	public UUID getId() {
		return id;
	}

	public DocumentoIdentidad getIdentificadorPasajero() {
		return identificadorPasajero;
	}

	public TipoReserva getTipo() {
		return tipo;
	}

	public Periodo getPeriodo() {
		return periodo;
	}

	public Importe getImporte() {
		return importe;
	}

	public LocalDateTime getFechaReserva() {
		return fechaReserva;
	}

	public EstadoReserva getEstado() {
		return estado;
	}

	// Acciones sobre la reserva
	/**
	 * Método para crear una reserva de parking
	 * @param identificadorPasajero
	 * @param tipo
	 * @param periodo
	 * @param importe
	 * @param fechaReserva
	 * @param tieneReservaVuelo
	 * @param vueloIniciado
	 * @return
	 */
	public static ReservaParking solicitarReserva( DocumentoIdentidad identificadorPasajero, TipoReserva tipo, Periodo periodo, Importe importe, LocalDateTime fechaReserva, boolean tieneReservaVuelo, boolean vueloIniciado ) {
		if ( tieneReservaVuelo && !vueloIniciado ) {
			importe = new Importe( importe.valor() * 0.75  );
		}
		return of(UUID.randomUUID(), identificadorPasajero, tipo, periodo, importe, fechaReserva, EstadoReserva.ACTIVA);
	}

	/**
	 * Método para cancelar una reserva de parking. Las restricciones que se aplicaran para cancelar una reserva de vuelo son
	 * las siguientes:
	 * <ol>
	 * <li>Solo se puede cancelar una reserva que se encuentre activa.</li>
	 * <li>No se puede cancelar una reserva despues de que inicie el periodo de estacionamiento.</li>
	 * </ol>
	 * @param now
	 */
	public void cancelarReserva( LocalDateTime now ) {
		if (this.estado == EstadoReserva.CANCELADA) {
			throw new ReservaYaCanceladaException("La reserva con id: " + this.getId().toString() + " ya está cancelada");
		}
		if ( now.isAfter( periodo.inicio() ) ) {
			throw new CancelacionNoPermitidaException( "Puede cancelar antes del inicio del periodo de estacionamiento" );
		}
		this.estado = EstadoReserva.CANCELADA;
	}
}
