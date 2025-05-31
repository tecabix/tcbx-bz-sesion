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
	
	private UsuarioRepository usuarioRepository;
	private LicenciaRepository licenciaRepository;
	private SesionRepository sesionRepository;
	
	private Catalogo licenciaMulti;
	private Catalogo licenciaMono;
	private Catalogo licenciaMultiMono;
	
	private Catalogo activo;
	private Catalogo eliminado;
	
	
	private String NO_SE_ENCONTRO_EL_USUARIO = "No se encontró el usuario.";
	private String NO_SE_ENCONTRO_LA_LICENCIA = "No se encontró la licencia.";
	private String PETICIONES_AGOTADAS = "Peticiones agotadas.";
	private String MUCHOS_USUARIOS_CONECTADOS = "Muchos usuarios conectados.";
	
	private int HORA = 23;
	private int MIN = 59;
	private int MAXSESION = 15;
	private int TREINTA = 30;
	private int OCHO = 8;

    public Sesion001BZ(Sesion001BzDTO dto) {
		this.usuarioRepository = dto.getUsuarioRepository();
		this.licenciaRepository = dto.getLicenciaRepository();
		this.sesionRepository = dto.getSesionRepository();
		this.licenciaMulti = dto.getLicenciaMulti();
		this.licenciaMono = dto.getLicenciaMono();
		this.licenciaMultiMono = dto.getLicenciaMono();
		this.activo = dto.getActivo();
		this.eliminado = dto.getEliminado();
	}

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

            List<Sesion> sesionesAbiertas = sesionRepository
                    .findByUsuarioAndActive(id, usr, pageable).getContent();
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
