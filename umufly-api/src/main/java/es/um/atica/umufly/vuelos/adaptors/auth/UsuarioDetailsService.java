package es.um.atica.umufly.vuelos.adaptors.auth;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import es.um.atica.umufly.vuelos.adaptors.persistence.jpa.repository.JpaUsuarioRepository;

@Service
public class UsuarioDetailsService implements UserDetailsService {

	private final JpaUsuarioRepository usuarioRepository;

	public UsuarioDetailsService( JpaUsuarioRepository usuarioRepository ) {
		this.usuarioRepository = usuarioRepository;
	}

	@Override
	public UserDetails loadUserByUsername( String username ) throws UsernameNotFoundException {
		// username tiene formato email@dominio.com
		// En nuestra bbdd: prueba@um.es
		return usuarioRepository.findByEmail( username ).orElseThrow( () -> new UsernameNotFoundException( "Usuario no encontrado: " + username ) );
	}
}
