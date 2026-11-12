package cl.veterinariasanmarcos.auth.controller;

import cl.veterinariasanmarcos.auth.dto.*;
import cl.veterinariasanmarcos.auth.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Autenticación", description = "Login, registro de tutores y renovación de token")
public class AuthController {
  private final AuthService auth;

  public AuthController(AuthService auth) {
    this.auth = auth;
  }

  @Operation(summary = "Iniciar sesión (tutor o recepcionista)")
  @PostMapping("/login")
  public LoginResponse login(@Valid @RequestBody LoginRequest request) {
    return auth.login(request);
  }

  @Operation(summary = "Registrar un tutor nuevo")
  @PostMapping("/registro")
  @ResponseStatus(HttpStatus.CREATED)
  public LoginResponse registro(@Valid @RequestBody RegistroRequest request) {
    return auth.registrarTutor(request);
  }

  @Operation(summary = "Renovar el token (enviar Authorization: Bearer <token>)")
  @PostMapping("/renovar")
  public LoginResponse renovar(@RequestHeader(value = "Authorization", required = false) String authorization) {
    return auth.renovar(authorization);
  }

  @Operation(summary = "Datos del usuario del token (enviar Authorization: Bearer <token>)")
  @GetMapping("/perfil")
  public PerfilResponse perfil(@RequestHeader(value = "Authorization", required = false) String authorization) {
    return auth.perfil(authorization);
  }
}
