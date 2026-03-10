package es.um.atica.umufly.vuelos.adaptors.persistence.jpa.entity;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

@Entity
@Table( name = "USUARIO" )
public class UsuarioEntity implements UserDetails {

	private static final long serialVersionUID = 5343102352687268040L;

	private String email;
	private String password;
	private String nombre;

	// Creamos temporalmente un rol administrador (no lo cargamos de base de datos)
	// El rol debe empezar siempre por "ROLE_"
	@Override
	// Transient para que hibernate no lo considere una columna
	@Transient
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return List.of( new SimpleGrantedAuthority( "ROLE_ADMIN" ) );
	}

	// El identificador de usuario en el sistema sera el email
	@Override
	//Transient para que hibernate no lo considere una columna
	@Transient
	public String getUsername() {
		return getEmail();
	}

	@Id
	@Column( name = "EMAIL" )
	public String getEmail() {
		return email;
	}

	public void setEmail( String email ) {
		this.email = email;
	}

	@Override
	public String getPassword() {
		return password;
	}

	public void setPassword( String password ) {
		this.password = password;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre( String nombre ) {
		this.nombre = nombre;
	}

	@Override
	public int hashCode() {
		return Objects.hash( email );
	}

	@Override
	public boolean equals( Object obj ) {
		if ( this == obj ) {
			return true;
		}
		if ( obj == null || getClass() != obj.getClass() ) {
			return false;
		}
		UsuarioEntity other = ( UsuarioEntity ) obj;
		return Objects.equals( email, other.email );
	}

}
