package com.tecabix.bz.sesion;

import java.time.LocalDateTime;

import org.springframework.http.ResponseEntity;

import com.tecabix.bz.sesion.dto.Sesion003BzDTO;
import com.tecabix.db.entity.Catalogo;
import com.tecabix.db.entity.Sesion;
import com.tecabix.db.repository.SesionRepository;
import com.tecabix.res.b.RSB020;
import com.tecabix.sv.rq.RQSV027;

/**
 *
 * @author Ramirez Urrutia Angel Abinadi
 */
public class Sesion003BZ {

    /**
     * Repositorio para acceder a la entidad Sesion.
     */
    private final SesionRepository sesionRepository;

    /**
     * Estado "Eliminado" obtenido desde el catálogo.
     */
    private final Catalogo eliminado;

    /**
     * Construye una nueva instancia de {@code Sesion003BZ} utilizando los datos
     * proporcionados en el objeto {@link Sesion003BzDTO}.
     *
     * @param dto el objeto {@link Sesion003BzDTO} que contiene el repositorio
     *            de sesión y el indicador de eliminado para inicializar esta
     *            instancia.
     */
    public Sesion003BZ(final Sesion003BzDTO dto) {
        this.sesionRepository = dto.getSesionRepository();
        this.eliminado = dto.getEliminado();
    }

    /**
     * Método para eliminar una sesión de usuario.
     *
     * @param rqsv027 datos de eliminación
     * @return {@link ResponseEntity} con un objeto {@link RSB020} que contiene
     *         información para eliminar la sesión del usuario.
     */
    public ResponseEntity<RSB020> eliminar(final RQSV027 rqsv027) {

        RSB020 rsb020 = rqsv027.getRsb020();
        Sesion sesion = rqsv027.getSesion();

        sesion.setFechaDeModificacion(LocalDateTime.now());
        sesion.setIdUsuarioModificado(sesion.getUsuario().getId());
        sesion.setEstatus(eliminado);
        sesion = sesionRepository.save(sesion);
        return rsb020.ok();
    }
}
