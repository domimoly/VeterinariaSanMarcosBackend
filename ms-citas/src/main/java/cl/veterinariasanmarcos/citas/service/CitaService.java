package cl.veterinariasanmarcos.citas.service;

import cl.veterinariasanmarcos.citas.dto.CitaRequest;
import cl.veterinariasanmarcos.citas.dto.ReagendarRequest;
import cl.veterinariasanmarcos.citas.client.MascotaClient;
import cl.veterinariasanmarcos.citas.client.MascotaRemota;
import cl.veterinariasanmarcos.citas.client.ServicioClient;
import cl.veterinariasanmarcos.citas.client.ServicioRemoto;
import cl.veterinariasanmarcos.citas.exception.AccesoDenegadoException;
import cl.veterinariasanmarcos.citas.exception.ConflictoException;
import cl.veterinariasanmarcos.citas.exception.RecursoNoEncontradoException;
import cl.veterinariasanmarcos.citas.exception.ReglaNegocioException;
import cl.veterinariasanmarcos.citas.model.Cita;
import cl.veterinariasanmarcos.citas.repository.CitaRepository;
import cl.veterinariasanmarcos.citas.security.UsuarioActual;
import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class CitaService {
  private static final ZoneId ZONA = ZoneId.of("America/Santiago");
  private static final int BLOQUE_MIN = 30;

  private final CitaRepository repository;
  private final MascotaClient mascotas;
  private final ServicioClient servicios;
  private final UsuarioActual actual;

  @Value("${app.agenda.cupos-por-bloque}") private int cuposPorBloque;
  @Value("${app.agenda.anticipacion-minima-horas}") private int anticipacionMinimaHoras;
  @Value("${app.agenda.anticipacion-maxima-semanas}") private int anticipacionMaximaSemanas;

  public CitaService(CitaRepository repository, MascotaClient mascotas,
      ServicioClient servicios, UsuarioActual actual) {
    this.repository = repository;
    this.mascotas = mascotas;
    this.servicios = servicios;
    this.actual = actual;
  }

  // ---------- consultas

  /** El tutor ve sus citas; la recepción ve toda la agenda (opcionalmente de un día). */
  public List<Cita> listar(LocalDate fecha) {
    if (!actual.esRecepcionista()) {
      return repository.findByTutorCorreoIgnoreCaseOrderByFechaAscHoraAsc(actual.correo());
    }
    return fecha == null ? repository.findAllByOrderByFechaAscHoraAsc()
                         : repository.findByFechaOrderByHoraAsc(fecha);
  }

  public Cita buscar(Long id) {
    Cita c = repository.findById(id).orElseThrow(() ->
      new RecursoNoEncontradoException("Cita no encontrada: " + id));
    if (!actual.esRecepcionista() && !c.getTutorCorreo().equalsIgnoreCase(actual.correo())) {
      throw new AccesoDenegadoException("Esta cita pertenece a otro tutor");
    }
    return c;
  }

  /** Horas de inicio con cupo libre para un servicio en una fecha. */
  public List<String> disponibilidad(LocalDate fecha, String servicioCodigo) {
    ServicioRemoto s = servicios.buscarPorCodigo(servicioCodigo);
    List<String> libres = new ArrayList<>();
    for (LocalTime[] tramo : tramos(fecha.getDayOfWeek())) {
      for (LocalTime h = tramo[0]; h.isBefore(tramo[1]); h = h.plusMinutes(BLOQUE_MIN)) {
        try {
          validarHorario(fecha, h, s.duracionMin(), null, null);
          libres.add(h.toString());
        } catch (ReglaNegocioException | ConflictoException e) {
          // hora no disponible: se omite
        }
      }
    }
    return libres;
  }

  // ---------- acciones

  public Cita crear(CitaRequest r) {
    MascotaRemota mascota = mascotas.buscar(r.mascotaId());
    ServicioRemoto servicio = servicios.buscarPorCodigo(r.servicioCodigo());

    if (!servicio.agendableOnline()) {
      throw new ReglaNegocioException("Este servicio no se agenda en línea, llama a la clínica");
    }
    if (mascota.esterilizada() && servicio.codigo().startsWith("esterilizacion")) {
      throw new ReglaNegocioException("La mascota ya está esterilizada");
    }
    validarHorario(r.fecha(), r.hora(), servicio.duracionMin(), mascota.id(), null);

    Cita c = new Cita();
    c.setTutorCorreo(actual.correo());
    c.setMascotaId(mascota.id());
    c.setMascotaNombre(mascota.nombre());
    c.setServicioCodigo(servicio.codigo());
    c.setServicioNombre(servicio.nombre());
    c.setDuracionMin(servicio.duracionMin());
    c.setPrecio(servicio.precio());
    c.setFecha(r.fecha());
    c.setHora(r.hora());
    c.setTelefono(r.telefono());
    c.setObservaciones(r.observaciones());
    c.setEstado(Cita.PENDIENTE);
    c.setCreadaEn(LocalDateTime.now(ZONA));
    c = repository.save(c);
    c.setCodigo(String.format("SM-%06d", c.getId()));
    return repository.save(c);
  }

  public Cita confirmar(Long id) {
    Cita c = buscar(id);
    if (Cita.CANCELADA.equals(c.getEstado())) {
      throw new ReglaNegocioException("No se puede confirmar una cita cancelada");
    }
    c.setEstado(Cita.CONFIRMADA);
    return repository.save(c);
  }

  public Cita reagendar(Long id, ReagendarRequest r) {
    Cita c = buscar(id);
    if (Cita.CANCELADA.equals(c.getEstado())) {
      throw new ReglaNegocioException("No se puede reagendar una cita cancelada");
    }
    validarHorario(r.fecha(), r.hora(), c.getDuracionMin(), c.getMascotaId(), c.getId());
    c.setFecha(r.fecha());
    c.setHora(r.hora());
    c.setNota(r.nota());
    c.setEstado(Cita.REAGENDADA);
    return repository.save(c);
  }

  public Cita cancelar(Long id) {
    Cita c = buscar(id);
    if (Cita.CANCELADA.equals(c.getEstado())) {
      throw new ReglaNegocioException("La cita ya está cancelada");
    }
    if (!actual.esRecepcionista()
        && !LocalDateTime.of(c.getFecha(), c.getHora()).isAfter(LocalDateTime.now(ZONA))) {
      throw new ReglaNegocioException("No se puede cancelar una cita que ya comenzó");
    }
    c.setEstado(Cita.CANCELADA);
    return repository.save(c);
  }

  // ---------- reglas de agenda

  /** Lunes a viernes 09-13 y 14-18, sábado 10-13, domingo cerrado. */
  private List<LocalTime[]> tramos(DayOfWeek dia) {
    if (dia == DayOfWeek.SUNDAY) {
      return List.of();
    }
    if (dia == DayOfWeek.SATURDAY) {
      return List.<LocalTime[]>of(new LocalTime[] {LocalTime.of(10, 0), LocalTime.of(13, 0)});
    }
    return List.of(
      new LocalTime[] {LocalTime.of(9, 0), LocalTime.of(13, 0)},
      new LocalTime[] {LocalTime.of(14, 0), LocalTime.of(18, 0)});
  }

  private void validarHorario(LocalDate fecha, LocalTime hora, int duracionMin,
      Long mascotaId, Long citaExcluidaId) {
    LocalDateTime inicio = LocalDateTime.of(fecha, hora);
    LocalDateTime ahora = LocalDateTime.now(ZONA);
    if (inicio.isBefore(ahora.plusHours(anticipacionMinimaHoras))) {
      throw new ReglaNegocioException(
        "Debes agendar con al menos " + anticipacionMinimaHoras + " horas de anticipación");
    }
    if (inicio.isAfter(ahora.plusWeeks(anticipacionMaximaSemanas))) {
      throw new ReglaNegocioException(
        "Solo se puede agendar hasta con " + anticipacionMaximaSemanas + " semanas de anticipación");
    }

    List<LocalTime[]> tramos = tramos(fecha.getDayOfWeek());
    if (tramos.isEmpty()) {
      throw new ReglaNegocioException("La clínica no atiende ese día");
    }
    LocalTime fin = hora.plusMinutes(duracionMin);
    boolean cabe = tramos.stream().anyMatch(t ->
      !hora.isBefore(t[0]) && !fin.isAfter(t[1]) && fin.isAfter(hora)
        && Duration.between(t[0], hora).toMinutes() % BLOQUE_MIN == 0);
    if (!cabe) {
      throw new ReglaNegocioException(
        "Hora fuera del horario de atención (bloques de 30 min, el servicio debe terminar antes del cierre)");
    }

    List<Cita> delDia = repository.findByFechaAndEstadoNot(fecha, Cita.CANCELADA).stream()
      .filter(c -> !Objects.equals(c.getId(), citaExcluidaId))
      .toList();

    if (mascotaId != null && delDia.stream().anyMatch(c -> mascotaId.equals(c.getMascotaId()))) {
      throw new ConflictoException("La mascota ya tiene una cita ese día");
    }

    for (LocalTime b = hora; b.isBefore(fin); b = b.plusMinutes(BLOQUE_MIN)) {
      final LocalTime bloque = b;
      long ocupados = delDia.stream().filter(c ->
        !bloque.isBefore(c.getHora())
          && bloque.isBefore(c.getHora().plusMinutes(c.getDuracionMin()))).count();
      if (ocupados >= cuposPorBloque) {
        throw new ConflictoException("No quedan cupos a las " + bloque);
      }
    }
  }
}
