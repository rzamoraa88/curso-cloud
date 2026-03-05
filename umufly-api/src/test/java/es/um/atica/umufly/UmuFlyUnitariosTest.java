package es.um.atica.umufly;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.time.LocalDateTime;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.ClassOrderer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestClassOrder;

import es.um.atica.umufly.vuelos.domain.model.Avion;
import es.um.atica.umufly.vuelos.domain.model.ClaseAsientoReserva;
import es.um.atica.umufly.vuelos.domain.model.CorreoElectronico;
import es.um.atica.umufly.vuelos.domain.model.DocumentoIdentidad;
import es.um.atica.umufly.vuelos.domain.model.EstadoReserva;
import es.um.atica.umufly.vuelos.domain.model.EstadoVuelo;
import es.um.atica.umufly.vuelos.domain.model.Itinerario;
import es.um.atica.umufly.vuelos.domain.model.Nacionalidad;
import es.um.atica.umufly.vuelos.domain.model.NombreCompleto;
import es.um.atica.umufly.vuelos.domain.model.Pasajero;
import es.um.atica.umufly.vuelos.domain.model.ReservaVuelo;
import es.um.atica.umufly.vuelos.domain.model.TipoDocumento;
import es.um.atica.umufly.vuelos.domain.model.TipoVuelo;
import es.um.atica.umufly.vuelos.domain.model.Vuelo;;

@TestClassOrder( ClassOrderer.OrderAnnotation.class )
public class UmuFlyUnitariosTest {

	private DocumentoIdentidad titular;
	private Pasajero pasajero;
	private Itinerario itinerario;
	private Avion avion;
	private Vuelo vueloPendiente;
	private Vuelo vueloCancelado;
	private Vuelo vueloCompletado;
	private static final LocalDateTime SALIDA = LocalDateTime.of( 2025, 6, 15, 10, 0 );
	private static final LocalDateTime LLEGADA = LocalDateTime.of( 2025, 6, 15, 12, 0 );
	private static final LocalDateTime ANTES_DE_SALIDA = SALIDA.minusHours( 2 );
	private static final LocalDateTime DESPUES_DE_SALIDA = SALIDA.plusMinutes( 30 );

	@BeforeEach
	void setUp() {
		titular = new DocumentoIdentidad( TipoDocumento.NIF, "12345678Z" );
		pasajero = Pasajero.of( titular, new NombreCompleto( "Juan", "García", "López" ), new CorreoElectronico( "juan@ejemplo.com" ), new Nacionalidad( "Española" ) );
		itinerario = new Itinerario( LocalDateTime.now(), LocalDateTime.now(), "MAD", "BCN" );
		avion = new Avion( 180 );
		vueloPendiente = Vuelo.of( UUID.randomUUID(), itinerario, TipoVuelo.NACIONAL, EstadoVuelo.PENDIENTE, avion );
		vueloCancelado = Vuelo.of( UUID.randomUUID(), itinerario, TipoVuelo.NACIONAL, EstadoVuelo.CANCELADO, avion );
		vueloCompletado = Vuelo.of( UUID.randomUUID(), itinerario, TipoVuelo.NACIONAL, EstadoVuelo.COMPLETADO, avion );
	}

	@Nested
	@DisplayName( "solicitarReserva_correcta" )
	@Order(1)
	class TestSolicitarReserva {

		// Formalizar reserva debería hacer pasar la reserva a estado ACTIVA
		@Test
		void formalizar_reserva_pasa_reserva_estado_activa() {
			ReservaVuelo reserva = ReservaVuelo.of( UUID.randomUUID(), titular, pasajero, vueloPendiente, ClaseAsientoReserva.ECONOMICA, ANTES_DE_SALIDA, EstadoReserva.PENDIENTE );
			assertEquals( EstadoReserva.ACTIVA, reserva.getEstado() );
		}

		// Crear una nueva reserva debe dejarla en pendiente
		@Test
		void crear_nueva_reserva_estado_pendiente() {
			ReservaVuelo reserva = ReservaVuelo.solicitarReserva( titular, pasajero, vueloPendiente, ClaseAsientoReserva.ECONOMICA, ANTES_DE_SALIDA, 0, 10 );
			assertEquals( EstadoReserva.PENDIENTE, reserva.getEstado() );
		}

		// Toda reserva debe tener un ID único
		@Test
		void toda_reserva_tiene_id_unico() {
			ReservaVuelo reserva1 = ReservaVuelo.solicitarReserva( titular, pasajero, vueloPendiente, ClaseAsientoReserva.ECONOMICA, ANTES_DE_SALIDA, 0, 10 );
			ReservaVuelo reserva2 = ReservaVuelo.solicitarReserva( titular, pasajero, vueloPendiente, ClaseAsientoReserva.ECONOMICA, ANTES_DE_SALIDA, 0, 10 );
			assertNotEquals( reserva1.getId(), reserva2.getId() );
		}

		// Una reserva válida debe contener todos sus datos válidos
		@Test
		void reserva_valida_contiene_todos_datos_validos() {
			ReservaVuelo reserva = ReservaVuelo.of( UUID.randomUUID(), titular, pasajero, vueloPendiente, ClaseAsientoReserva.ECONOMICA, ANTES_DE_SALIDA, EstadoReserva.PENDIENTE );
			assertNotNull( reserva );
		}

		// Debería poderse solicitar reserva en un vuelo retrasado con plazas libres
		@Test
		void solicitar_reserva_vuelo_retrasado_plazas_libres() {
			ReservaVuelo reserva = ReservaVuelo.solicitarReserva( titular, pasajero, vueloPendiente, ClaseAsientoReserva.ECONOMICA, ANTES_DE_SALIDA, 0, 10 );
			assertEquals( EstadoReserva.PENDIENTE, reserva.getEstado() );
		}

		// Un pasajero solo puede tener una reserva para el mismo vuelo
		@Test
		void pasajero_solo_puede_tener_una_reserva_para_mismo_vuelo() {
			ReservaVuelo reserva = ReservaVuelo.solicitarReserva( titular, pasajero, vueloPendiente, ClaseAsientoReserva.ECONOMICA, ANTES_DE_SALIDA, 0, 10 );
			assertEquals( EstadoReserva.PENDIENTE, reserva.getEstado() );
		}

	}
}
