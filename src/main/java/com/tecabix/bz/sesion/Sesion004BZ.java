package com.tecabix.bz.sesion;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.http.ResponseEntity;

import com.tecabix.bz.sesion.dto.Sesion004BzDTO;
import com.tecabix.db.entity.Catalogo;
import com.tecabix.db.entity.Sesion;
import com.tecabix.db.repository.SesionRepository;
import com.tecabix.res.b.RSB020;
import com.tecabix.sv.rq.RQSV028;

/**
 *
 * @author Ramirez Urrutia Angel Abinadi
 */
public class Sesion004BZ {

    /**
     * Repositorio para acceder a la entidad Sesion.
     */
    private final SesionRepository sesionRepository;

    /**
     * Estado "Eliminado" obtenido desde el catálogo.
     */
    private final Catalogo eliminado;

    /**
     * Sesión no encontrada.
     */
    private static final String NO_SE_ENCONTRO_LA_SESION;

    static {
        NO_SE_ENCONTRO_LA_SESION = "No se encontró la sesión.";
    }

    /**
     * Construye una nueva instancia de {@code Sesion004BZ} utilizando los datos
     * proporcionados en el objeto {@link Sesion004BzDTO}.
     *
     * @param dto objeto {@link Sesion004BzDTO} que contiene el repositorio de
     *            sesión y el indicador de eliminado para inicializar esta
     *            instancia.
     */
    public Sesion004BZ(final Sesion004BzDTO dto) {
        this.sesionRepository = dto.getSesionRepository();
        this.eliminado = dto.getEliminado();
    }

    /**
     * Método para eliminar otra sesión usuario.
     *
     * @param rqsv028 datos de actualizar y eliminar.
     * @return {@link ResponseEntity} con un objeto {@link RSB020} que contiene
     *         información para eliminar sesión de otro usuario.
     */
    public ResponseEntity<RSB020> actualizarEliminar(final RQSV028 rqsv028) {

        RSB020 rsb020 = rqsv028.getRsb020();
        Sesion sesion = rqsv028.getSesion();
        UUID uuid = rsb020.getClave();

        Sesion sesionAux = sesionRepository.findByClave(uuid).orElse(null);
        if (sesionAux == null) {
            return rsb020.notFound(NO_SE_ENCONTRO_LA_SESION);
        }
        sesionAux.setFechaDeModificacion(LocalDateTime.now());
        sesionAux.setIdUsuarioModificado(sesion.getUsuario().getId());
        sesionAux.setEstatus(eliminado);
        sesionRepository.save(sesionAux);
        return rsb020.ok();
    }
}
