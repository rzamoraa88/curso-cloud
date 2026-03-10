package es.um.atica.umufly.vuelos.adaptors.auth;

import java.io.IOException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

	private final JwtService jwtService;
	private final UsuarioDetailsService usuarioDetailsService;

	public JwtAuthFilter( JwtService jwtService, UsuarioDetailsService usuarioDetailsService ) {
		this.jwtService = jwtService;
		this.usuarioDetailsService = usuarioDetailsService;
	}

	@Override
	protected void doFilterInternal( HttpServletRequest request, HttpServletResponse response, FilterChain filterChain ) throws ServletException, IOException {
		// Obtengo la cabecera de autorizacion
		String authHeader = request.getHeader( "Authorization" );
		// Al ser JWT debe empezar por "Bearer "
		// Si no existe o no empieza con "Bearer " ojo va con un espacio en blanco
		// Continuo el filtrado (no autorizo la peticion)
		if ( authHeader == null || !authHeader.startsWith( "Bearer " ) ) {
			filterChain.doFilter( request, response );
		}
		String token = authHeader.substring( 7 );
		try {
			// Extraigo el nombre de usuario
			String username = jwtService.extraerUsername( token );
			if ( username != null && SecurityContextHolder.getContext().getAuthentication() == null ) {
				// Cargo el usuario en el sistema
				UserDetails userDetails = usuarioDetailsService.loadUserByUsername( username );
				// Creo un "principal" de autenticacion basdo en el flujo usuario-pasword
				// Cargo el usuario y sus roles, no incluyo credenciales ya que no las necesito (el campo null)
				UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken( userDetails, null, userDetails.getAuthorities() );
				// Construyo un objeto WebAuthenticationDetails a partir de la peticion (guarda IP e identificador de sesion si hay)
				// Lo aniado a la credencial de autenticacion
				auth.setDetails( new WebAuthenticationDetailsSource().buildDetails( request ) );
				// Aniado al contexto de seguridad el "pricipal" de autenticacion relleno para que autorice la peticion
				SecurityContextHolder.getContext().setAuthentication( auth );
			}
		} catch ( Exception e ) {
			response.sendError( HttpServletResponse.SC_UNAUTHORIZED, "Token inválido o expirado" );
			return;
		}
		filterChain.doFilter( request, response );
	}
}
