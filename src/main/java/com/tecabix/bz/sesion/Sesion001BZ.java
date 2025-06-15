package com.tecabix.bz.sesion;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import com.tecabix.bz.sesion.dto.Sesion001BzDTO;
import com.tecabix.db.entity.Catalogo;
import com.tecabix.db.entity.Licencia;
import com.tecabix.db.entity.Servicio;
import com.tecabix.db.entity.Sesion;
import com.tecabix.db.entity.Usuario;
import com.tecabix.db.repository.LicenciaRepository;
import com.tecabix.db.repository.SesionRepository;
import com.tecabix.db.repository.UsuarioRepository;
import com.tecabix.res.b.RSB019;
import com.tecabix.sv.rq.RQSV025;

/**
 *
 * @author Ramirez Urrutia Angel Abinadi
 */
public class Sesion001BZ {

    /**
     * Repositorio para acceder a la entidad Usuario.
     */
    private final UsuarioRepository usuarioRepository;

    /**
     * Repositorio para acceder a la entidad Licencia.
     */
    private final LicenciaRepository licenciaRepository;

    /**
     * Repositorio para acceder a la entidad Sesion.
     */
    private final SesionRepository sesionRepository;

    /**
     * Catálogo de licencia multiusuario.
     */
    private final Catalogo licenciaMulti;

    /**
     * Catálogo de licencia monousuario.
     */
    private final Catalogo licenciaMono;

    /**
     * Catálogo de licencia multimonousuario.
     */
    private final Catalogo licenciaMultiMono;

    /**
     * Estado "activo" obtenido desde el catálogo.
     */
    private final Catalogo activo;

    /**
     * Estado "Eliminado" obtenido desde el catálogo.
     */
    private final Catalogo eliminado;

    /**
     * Mensaje usuario no encontrado.
     */
    private static final String NO_SE_ENCONTRO_EL_USUARIO;

    static {

        NO_SE_ENCONTRO_EL_USUARIO = "No se encontró el usuario.";
        NO_SE_ENCONTRO_LA_LICENCIA = "No se encontró la licencia.";
        PETICIONES_AGOTADAS = "Peticiones agotadas.";
        MUCHOS_USUARIOS_CONECTADOS = "Muchos usuarios conectados.";
    }

    /**
     * Mensaje licencia no encontrada.
     */
    private static final String NO_SE_ENCONTRO_LA_LICENCIA;

    /**
     * Mensaje peticiones agotadas.
     */
    private static final String PETICIONES_AGOTADAS;

    /**
     * Mensaje muchos usuarios conectados.
     */
    private static final String MUCHOS_USUARIOS_CONECTADOS;

    /**
     * Representa al número veintitrés en horas.
     */
    private static final int HORA = 23;

    /**
     * Representa al número cincuenta y nueve para minutos, segundos.
     */
    private static final int MIN = 59;

    /**
     * Representa al número máximo de sesiones abiertas que se permiten.
     */
    private static final int MAXSESION = 15;

    /**
     * Representa a treinta minutos.
     */
    private static final int TREINTA = 30;

    /**
     * Representa a ocho horas.
     */
    private static final int OCHO = 8;

    /**
     * Constructor que inicializa los atributos de la clase {@code Sesion001BZ}
     * utilizando los valores proporcionados por el
     * objeto {@code Sesion001BzDTO}.
     *
     * @param dto Objeto de transferencia de datos que contiene las dependencias
     *            y configuraciones necesarias para inicializar la clase.
     */
    public Sesion001BZ(final Sesion001BzDTO dto) {
        this.usuarioRepository = dto.getUsuarioRepository();
        this.licenciaRepository = dto.getLicenciaRepository();
        this.sesionRepository = dto.getSesionRepository();
        this.licenciaMulti = dto.getLicenciaMulti();
        this.licenciaMono = dto.getLicenciaMono();
        this.licenciaMultiMono = dto.getLicenciaMono();
        this.activo = dto.getActivo();
        this.eliminado = dto.getEliminado();
    }

    /**
     * Método para crear una nueva sesión de usuario.
     *
     * @param rqsv025 datos de creación
     * @return {@link ResponseEntity} con un objeto {@link RSB019} que contiene
     *         información para crear la sesión del usuario.
     */
    public ResponseEntity<RSB019> crear(final RQSV025 rqsv025) {

        RSB019 rsb019 = rqsv025.getRsb019();
        UUID key = rsb019.getClave();
        String usuarioName = rqsv025.getUsuario();

        Usuario usuario;
        usuario = usuarioRepository.findByNombre(usuarioName).orElse(null);
        if (usuario == null) {
            return rsb019.unauthorized(NO_SE_ENCONTRO_EL_USUARIO);
        }
        Licencia licencia = licenciaRepository.findByToken(key);
        if (licencia == null) {
            return rsb019.notFound(NO_SE_ENCONTRO_LA_LICENCIA);
        }
        int restantes = 0;
        LocalDateTime vencimiento = LocalDateTime.now();
        Long id = licencia.getId();

        if (licencia.getServicio().getTipo().equals(licenciaMulti)) {

            Long usr = usuario.getId();
            LocalTime hora = LocalTime.of(HORA, MIN, MIN, MIN);
            vencimiento = LocalDateTime.of(vencimiento.toLocalDate(), hora);
            Pageable pageable = PageRequest.of(0, Integer.MAX_VALUE);
            List<Sesion> sesionesDeHoy;
            Page<Sesion> page;
            page = sesionRepository.findByUsuarioAndNow(id, usr, pageable);
            sesionesDeHoy = page.getContent();

            if (sesionesDeHoy != null && !sesionesDeHoy.isEmpty()) {
                restantes = sesionesDeHoy.get(0).getPeticionesRestantes();
                if (restantes < 1) {
                    return rsb019.unauthorized(PETICIONES_AGOTADAS);
                }
            } else {
                Servicio servicio = licencia.getServicio();
                restantes = servicio.getPeticiones();
            }

            List<Sesion> sesionesAbiertas;
            Page<Sesion> pageS;
            pageS = sesionRepository.findByUsuarioAndActive(id, usr, pageable);
            sesionesAbiertas = pageS.getContent();
            if (sesionesAbiertas != null) {
                sesionesAbiertas.stream().forEach(sesion -> {
                    sesion.setFechaDeModificacion(LocalDateTime.now());
                    sesion.setIdUsuarioModificado(sesion.getUsuario().getId());
                    sesion.setEstatus(eliminado);
                    sesionRepository.save(sesion);
                });
            }
        } else if (licencia.getServicio().getTipo().equals(licenciaMono)) {

            LocalTime hora = LocalTime.of(HORA, MIN, MIN, MIN);
            vencimiento = LocalDateTime.of(vencimiento.toLocalDate(), hora);
            Pageable pageable = PageRequest.of(0, Integer.MAX_VALUE);
            Page<Sesion> sesionesDeHoy;
            sesionesDeHoy = sesionRepository.findByNow(id, pageable);

            if (sesionesDeHoy != null && !sesionesDeHoy.isEmpty()) {

                Sesion sesiones = sesionesDeHoy.getContent().get(0);
                restantes = sesiones.getPeticionesRestantes();
                if (restantes < 1) {
                    return rsb019.unauthorized(PETICIONES_AGOTADAS);
                }
            } else {
                Servicio servicio = licencia.getServicio();
                restantes = servicio.getPeticiones();
            }
            Page<Sesion> abiertas;
            abiertas = sesionRepository.findByLicenciaAndActive(id, pageable);
            if (abiertas != null) {
                abiertas.stream().forEach(sesion -> {
                    sesion.setFechaDeModificacion(LocalDateTime.now());
                    sesion.setIdUsuarioModificado(sesion.getUsuario().getId());
                    sesion.setEstatus(eliminado);
                    sesionRepository.save(sesion);
                });
            }
        } else if (licencia.getServicio().getTipo().equals(licenciaMultiMono)) {
            Pageable pageable = PageRequest.of(0, Integer.MAX_VALUE);
            Page<Sesion> abiertas;
            abiertas = sesionRepository.findByLicenciaAndActive(id, pageable);
            if (abiertas.getTotalElements() > MAXSESION) {
                return rsb019.unauthorized(MUCHOS_USUARIOS_CONECTADOS);
            }
            Servicio servicio = licencia.getServicio();
            restantes = servicio.getPeticiones();
            vencimiento = vencimiento.plusMinutes(TREINTA);
        } else {
            vencimiento = vencimiento.plusHours(OCHO);
            Pageable pageable = PageRequest.of(0, Integer.MAX_VALUE);
            Page<Sesion> sesionesDeHoy;
            sesionesDeHoy = sesionRepository.findByNow(id, pageable);
            if (sesionesDeHoy != null && !sesionesDeHoy.isEmpty()) {

                Sesion sesiones = sesionesDeHoy.getContent().get(0);
                restantes = sesiones.getPeticionesRestantes();
                if (restantes < 1) {
                    return rsb019.unauthorized(PETICIONES_AGOTADAS);
                }
            } else {
                Servicio servicio = licencia.getServicio();
                restantes = servicio.getPeticiones();
            }
            Page<Sesion> abiertas;
            abiertas = sesionRepository.findByLicenciaAndActive(id, pageable);
            if (abiertas != null) {
                abiertas.stream().forEach(sesion -> {
                    sesion.setFechaDeModificacion(LocalDateTime.now());
                    sesion.setIdUsuarioModificado(sesion.getUsuario().getId());
                    sesion.setEstatus(eliminado);
                    sesionRepository.save(sesion);
                });
            }
        }
        Sesion sesion = new Sesion();
        sesion.setUsuario(usuario);
        sesion.setEstatus(activo);
        sesion.setFechaDeModificacion(LocalDateTime.now());
        sesion.setIdUsuarioModificado(usuario.getId());
        sesion.setLicencia(licencia);
        sesion.setVencimiento(vencimiento);
        sesion.setPeticionesRestantes(restantes);
        sesion.setClave(UUID.randomUUID());
        return rsb019.ok(sesionRepository.save(sesion));
    }
}
