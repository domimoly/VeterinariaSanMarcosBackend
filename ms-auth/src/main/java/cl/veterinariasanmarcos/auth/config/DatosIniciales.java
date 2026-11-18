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

  private void crearSiNoExiste(UsuarioRepository repo, PasswordEncoder encoder,
      String nombre, String correo, String password, String rol) {
    if (repo.findByCorreoIgnoreCase(correo).isEmpty()) {
      Usuario u = new Usuario();
      u.setNombre(nombre);
      u.setCorreo(correo);
      u.setPassword(encoder.encode(password));
      u.setRol(rol);
      repo.save(u);
    }
  }

  /** Cuentas internas de ejemplo; los tutores se registran solos desde la web. */
  @Bean
  CommandLineRunner usuariosIniciales(UsuarioRepository repo, PasswordEncoder encoder,
      @Value("${app.recepcion.correo}") String correoRecepcion,
      @Value("${app.recepcion.password}") String passwordRecepcion,
      @Value("${app.admin.correo}") String correoAdmin,
      @Value("${app.admin.password}") String passwordAdmin) {
    return args -> {
      crearSiNoExiste(repo, encoder, "Recepción San Marcos", correoRecepcion,
        passwordRecepcion, AuthService.ROL_RECEPCIONISTA);
      crearSiNoExiste(repo, encoder, "Administrador San Marcos", correoAdmin,
        passwordAdmin, AuthService.ROL_ADMINISTRADOR);
    };
  }
}
