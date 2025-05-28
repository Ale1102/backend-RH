package com.udb.rrhh.security;

import com.udb.rrhh.models.Usuario;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

// Clase que implementa UserDetails para representar un usuario autenticado en Spring Security
public class UserPrincipal implements UserDetails {

    // ID único del usuario
    private Long id;

    // Nombre de usuario
    private String username;

    // Email del usuario
    private String email;

    // Contraseña encriptada
    private String password;

    // Lista de autoridades/roles del usuario
    private Collection<? extends GrantedAuthority> authorities;

    // Constructor que inicializa todos los campos
    public UserPrincipal(Long id, String username, String email, String password,
                         Collection<? extends GrantedAuthority> authorities) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.password = password;
        this.authorities = authorities;
    }

    // Método estático factory para crear UserPrincipal desde entidad Usuario
    public static UserPrincipal create(Usuario usuario) {
        // Convierte los roles del usuario a GrantedAuthority
        List<GrantedAuthority> authorities = usuario.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.getNombre())) // Prefijo ROLE_ requerido
                .collect(Collectors.toList()); // Colecta como lista

        // Retorna nueva instancia de UserPrincipal
        return new UserPrincipal(
                usuario.getIdUsuario(),
                usuario.getUsername(),
                usuario.getEmail(),
                usuario.getPassword(),
                authorities
        );
    }

    // Getter para el ID del usuario
    public Long getId() {
        return id;
    }

    // Getter para el email del usuario
    public String getEmail() {
        return email;
    }

    // Implementación de UserDetails - retorna la contraseña
    @Override
    public String getPassword() {
        return password;
    }

    // Implementación de UserDetails - retorna el username
    @Override
    public String getUsername() {
        return username;
    }

    // Implementación de UserDetails - retorna las autoridades/roles
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    // Implementación de UserDetails - indica si la cuenta no ha expirado
    @Override
    public boolean isAccountNonExpired() {
        return true; // Por simplicidad, las cuentas no expiran
    }

    // Implementación de UserDetails - indica si la cuenta no está bloqueada
    @Override
    public boolean isAccountNonLocked() {
        return true; // Por simplicidad, las cuentas no se bloquean
    }

    // Implementación de UserDetails - indica si las credenciales no han expirado
    @Override
    public boolean isCredentialsNonExpired() {
        return true; // Por simplicidad, las credenciales no expiran
    }

    // Implementación de UserDetails - indica si el usuario está habilitado
    @Override
    public boolean isEnabled() {
        return true; // Por simplicidad, todos los usuarios están habilitados
    }

    // Método equals para comparar objetos UserPrincipal
    @Override
    public boolean equals(Object o) {
        if (this == o) return true; // Misma referencia
        if (o == null || getClass() != o.getClass()) return false; // Null o clase diferente
        UserPrincipal that = (UserPrincipal) o; // Cast seguro
        return Objects.equals(id, that.id); // Compara por ID
    }

    // Método hashCode basado en el ID
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
