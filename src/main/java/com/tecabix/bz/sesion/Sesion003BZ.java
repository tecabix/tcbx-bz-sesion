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

	private SesionRepository sesionRepository;
	
	private Catalogo eliminado;

    public Sesion003BZ(Sesion003BzDTO dto) {
		this.sesionRepository = dto.getSesionRepository();
		this.eliminado = dto.getEliminado();
	}

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
