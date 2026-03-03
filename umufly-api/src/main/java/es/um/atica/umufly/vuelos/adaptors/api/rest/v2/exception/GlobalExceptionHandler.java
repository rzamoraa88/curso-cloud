package es.um.atica.umufly.vuelos.adaptors.api.rest.v2.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import es.um.atica.umufly.vuelos.adaptors.api.rest.v2.dto.ApiError;
import es.um.atica.umufly.vuelos.domain.exception.ReservaNoEncontradaException;
import es.um.atica.umufly.vuelos.domain.exception.VueloNoEncontradoException;
import jakarta.servlet.http.HttpServletRequest;

@ControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(ReservaNoEncontradaException.class)
	public ResponseEntity<ApiError> handleReservaNoEncontrada(
			ReservaNoEncontradaException ex,
			HttpServletRequest request) {

		ApiError error = new ApiError(
				404,
				"Reserva no encontrada",
				"No existe la reserva con id: " + ex.getIdReserva(),
				request.getRequestURI()
				);

		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
	}

	@ExceptionHandler(VueloNoEncontradoException.class)
	public ResponseEntity<ApiError> handleVueloNoEncontrado(
			VueloNoEncontradoException ex,
			HttpServletRequest request) {

		ApiError error = new ApiError(
				404,
				"Vuelo no encontrado",
				"No existe el vuelo con id: " + ex.getIdVuelo(),
				request.getRequestURI()
				);

		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ApiError> handleGeneric(
			Exception ex,
			HttpServletRequest request) {

		ApiError error = new ApiError(
				500,
				"Error interno",
				"Ha ocurrido un error inesperado",
				request.getRequestURI()
				);

		return ResponseEntity.internalServerError().body(error);
	}
}
