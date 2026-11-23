package cl.veterinariasanmarcos.auth.config;

import cl.veterinariasanmarcos.auth.security.JwtAuthenticationFilter;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
public class SecurityConfig {

  @Bean
  PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  SecurityFilterChain filterChain(HttpSecurity http, JwtAuthenticationFilter jwtFilter) throws Exception {
    return http.csrf(csrf -> csrf.disable())
      .cors(Customizer.withDefaults())
      .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
      .authorizeHttpRequests(auth -> auth
        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
        // Sin token solo se puede iniciar sesión y registrarse (más la documentación Swagger)
        .requestMatchers(HttpMethod.POST, "/api/auth/login", "/api/auth/registro").permitAll()
        .requestMatchers("/swagger-ui/**", "/swagger-ui.html", "/v3/api-docs/**").permitAll()
        // Renovar el token y ver o editar el perfil exigen un token válido
        .requestMatchers("/api/auth/**").authenticated()
        // La gestión de cuentas es solo del administrador
        .requestMatchers("/api/usuarios/**").hasRole("ADMINISTRADOR")
        .anyRequest().denyAll())
      .exceptionHandling(e -> e
        .authenticationEntryPoint((req, res, ex) -> responderJson(res, 401, "Debes iniciar sesión para continuar"))
        .accessDeniedHandler((req, res, ex) -> responderJson(res, 403, "No tienes permiso para realizar esta acción")))
      .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
      .build();
  }

  @Bean
  CorsConfigurationSource corsConfigurationSource(
      @Value("${app.cors.allowed-origin:http://localhost:5173}") String[] origenesPermitidos) {
    CorsConfiguration c = new CorsConfiguration();
    c.setAllowedOrigins(List.of(origenesPermitidos));
    c.setAllowedMethods(List.of("GET", "POST", "PUT", "OPTIONS"));
    c.setAllowedHeaders(List.of("Content-Type", "Authorization"));
    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/api/**", c);
    return source;
  }

  /** Los errores de seguridad también se responden en JSON, igual que el resto de la API. */
  private static void responderJson(HttpServletResponse res, int estado, String mensaje) throws IOException {
    res.setStatus(estado);
    res.setContentType("application/json;charset=UTF-8");
    res.getWriter().write("{\"mensaje\":\"" + mensaje + "\"}");
  }
}
