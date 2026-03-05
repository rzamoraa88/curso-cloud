package es.um.atica.vuelos.cucumber;

import java.util.Collections;
import java.util.List;

import es.um.atica.umufly.vuelos.adaptors.api.rest.v2.dto.VueloDTO;
import io.cucumber.messages.ndjson.internal.com.fasterxml.jackson.annotation.JsonProperty;

public class VuelosWrapper {

	@JsonProperty( "_embedded" )
	private Embedded embedded;

	public List<VueloDTO> getVuelos() {
		return embedded != null ? embedded.vuelos : Collections.emptyList();
	}

	public class Embedded {

		@JsonProperty( "vuelos" )
		private List<VueloDTO> vuelos;
	}
}
