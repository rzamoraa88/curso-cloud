package es.um.atica.umufly.vuelos.adaptors.auth;

import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import es.um.atica.umufly.vuelos.adaptors.persistence.jpa.entity.UsuarioEntity;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {

	@Value( "${jwt.secret}" )
	private String secret;
	@Value( "${jwt.expiration-ms}" )
	private long expirationMs;

	private SecretKey getKey() {
		return Keys.hmacShaKeyFor( Decoders.BASE64.decode( secret ) );
	}

	// Generamos el token a partir del usuario
	public String generarToken( UsuarioEntity usuario ) {
		return Jwts.builder().subject( usuario.getUsername() ) // Establecemos sub
				.claim( "rol", usuario.getAuthorities() ) // Claim extra: rol
				.claim( "nombre", usuario.getNombre() ) // Claim extra:nombre
				.issuedAt( new Date() ).expiration( new Date( System.currentTimeMillis() + expirationMs ) ).signWith( getKey() ).compact();
	}

	public Claims validarYExtraer( String token ) {
		return Jwts.parser().verifyWith( getKey() ).build().parseSignedClaims( token ).getPayload();
	}

	public String extraerUsername( String token ) {
		return validarYExtraer( token ).getSubject();
	}
}
