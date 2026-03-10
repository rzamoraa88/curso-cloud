package es.um.atica.umufly.vuelos.adaptors.auth.api.rest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import es.um.atica.umufly.vuelos.adaptors.auth.JwtService;
import es.um.atica.umufly.vuelos.adaptors.auth.api.rest.dto.LoginRequestDTO;
import es.um.atica.umufly.vuelos.adaptors.auth.api.rest.dto.TokenResponseDTO;
import es.um.atica.umufly.vuelos.adaptors.persistence.jpa.entity.UsuarioEntity;
import es.um.atica.umufly.vuelos.adaptors.persistence.jpa.repository.JpaUsuarioRepository;

@RestController
@RequestMapping( "/auth" )
public class AuthController {

	private final JpaUsuarioRepository usuarioRepository;
	private final JwtService jwtService;
	private final PasswordEncoder passwordEncoder;

	public AuthController(JpaUsuarioRepository usuarioRepository, JwtService jwtService,
			PasswordEncoder passwordEncoder) {
		this.usuarioRepository = usuarioRepository;
		this.jwtService = jwtService;
		this.passwordEncoder = passwordEncoder;

	}

	@PostMapping( "/login" )
	public ResponseEntity<TokenResponseDTO> login( @RequestBody LoginRequestDTO loginRequest ) {
		UsuarioEntity usuario = usuarioRepository.findByEmail( loginRequest.email() ).orElseThrow( () -> new ResponseStatusException( HttpStatus.UNAUTHORIZED, "Credenciales incorrectas" ) );
		if ( !passwordEncoder.matches( loginRequest.password(), usuario.getPassword() ) ) {
			throw new ResponseStatusException( HttpStatus.UNAUTHORIZED, "Credenciales incorrectas" );
		}
		return ResponseEntity.ok( new TokenResponseDTO( jwtService.generarToken( usuario ) ) );
	}
}
