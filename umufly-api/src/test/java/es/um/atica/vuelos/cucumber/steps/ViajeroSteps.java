package es.um.atica.vuelos.cucumber.steps;

import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import es.um.atica.umufly.vuelos.adaptors.api.rest.v2.dto.ReservaVueloDTO;
import es.um.atica.umufly.vuelos.adaptors.api.rest.v2.dto.VueloDTO;
import es.um.atica.vuelos.cucumber.VuelosWrapper;
import io.cucumber.java.es.Cuando;
import io.cucumber.java.es.Dado;

public class ViajeroSteps {

	private String usuario;
	private List<VueloDTO> vuelos;
	private ReservaVueloDTO reserva;
	private WebTestClient webTestClient;
	private String port;

	private response;
	@Dado( "un viajero con NIF {string}" )
	public void cargo_datos_usuario( String nif ) {
		this.usuario = nif;
	}

	@Cuando( "lista de vuelos con página {int} y tamaño {int}" )
	public void listo_vuelos_disponibles( int page, int size ) {

		// Creo cliente
		this.webTestClient = WebTestClient.bindToServer().baseUrl( "http://localhost:" + port ).build();
		// Hago llamada
		response = webTestClient.get().uri( uriBuilder -> uriBuilder.path( "/private/v2.0/vuelos" ).queryParam( "page", page ).queryParam( "size", size ).build() ).header( "UMU-Usuario", "NIF:" + usuario ).accept( MediaType.APPLICATION_JSON ).exchange();
		// Guardo los vuelos en una variable para usarla en otros casos
		VuelosWrapper vuelosLista = response.expectBody( VuelosWrapper.class ).returnResult().getResponseBody();
		vuelos = vuelosLista.getVuelos();
	}

}
