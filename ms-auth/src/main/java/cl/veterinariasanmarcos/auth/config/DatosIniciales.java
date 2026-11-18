package cl.veterinariasanmarcos.auth.config;

import cl.veterinariasanmarcos.auth.model.Usuario;
import cl.veterinariasanmarcos.auth.repository.UsuarioRepository;
import cl.veterinariasanmarcos.auth.service.AuthService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DatosIniciales {

  @Bean
  CommandLineRunner usuariosIniciales(UsuarioRepository repo, PasswordEncoder encoder,
      @Value("${app.recepcion.correo}") String correo,
      @Value("${app.recepcion.password}") String password) {
    return args -> {
      if (repo.findByCorreoIgnoreCase(correo).isEmpty()) {
        Usuario u = new Usuario();
        u.setNombre("Recepción San Marcos");
        u.setCorreo(correo);
        u.setPassword(encoder.encode(password));
        u.setRol(AuthService.ROL_RECEPCIONISTA);
        repo.save(u);
      }
    };
  }
}
