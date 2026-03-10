package es.um.atica.vuelos.cucumber;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.List;

import org.junit.platform.suite.api.ConfigurationParameter;
import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.Suite;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.test.web.reactive.server.WebTestClient.ResponseSpec;

import es.um.atica.umufly.vuelos.adaptors.api.rest.v2.dto.VueloDTO;
import io.cucumber.java.es.Cuando;
import io.cucumber.java.es.Dado;
import io.cucumber.java.es.Entonces;
import io.cucumber.java.es.Y;
import io.cucumber.junit.platform.engine.Constants;

@Suite
@IncludeEngines( "cucumber" )
@ConfigurationParameter( key = Constants.PLUGIN_PROPERTY_NAME, value = "pretty, html:target/cucumber-reports/report.html" )
@ConfigurationParameter( key = Constants.GLUE_PROPERTY_NAME, value = "es.um.atica.vuelos.cucumber,es.um.atica.vuelos.cucumber.steps" )
@ConfigurationParameter( key = Constants.FEATURES_PROPERTY_NAME, value = "classpath:features" )
public class CucumberTestSuite {

	private String usuario;
	private WebTestClient webTestClient;
	private ResponseSpec response;
	private List<VueloDTO> vuelos;

	@Dado( "un viajero con NIF {string}" )
	public void cargo_datos_usuario( String nif ) {
		this.usuario = nif;
	}

	@Cuando( "lista de vuelos con página {int} y tamaño {int}" )
	public void listo_vuelos_disponibles( int page, int size ) {
		String port = "8080";
		// Creo cliente
		this.webTestClient = WebTestClient.bindToServer().baseUrl( "http://localhost:" + port ).build();
		// Hago llamada
		response = webTestClient.get().uri( uriBuilder -> uriBuilder.path( "/private/v2.0/vuelos" ).queryParam( "page", page ).queryParam( "size", size ).build() ).header( "UMU-Usuario", "NIF:" + usuario ).accept( MediaType.APPLICATION_JSON ).exchange();
		// Guardo los vuelos en una variable para usarla en otros casos
		VuelosWrapper vuelosLista = response.expectBody( VuelosWrapper.class ).returnResult().getResponseBody();
		vuelos = vuelosLista.getVuelos();
	}

	@Entonces( "la respuesta debe tener status {int}" )
	public void verificar_status( int estadoEsperado ) {
		response.expectStatus().isEqualTo( estadoEsperado );
	}

	@Y( "devolver una lista de vuelos rellena o vacia" )
	public void verifica_lista_vuelos() {
		assertNotNull( vuelos );
	}
}
