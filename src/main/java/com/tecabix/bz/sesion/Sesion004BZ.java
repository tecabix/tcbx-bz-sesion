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

	private SesionRepository sesionRepository;
	
	private Catalogo eliminado;
	
	private String NO_SE_ENCONTRO_LA_SESION = "No se encontró la sesión.";

    public Sesion004BZ(Sesion004BzDTO dto) {
		this.sesionRepository = dto.getSesionRepository();
		this.eliminado = dto.getEliminado();
	}

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
