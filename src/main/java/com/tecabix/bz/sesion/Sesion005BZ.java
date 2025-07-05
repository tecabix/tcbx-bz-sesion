package com.tecabix.bz.sesion;

import org.springframework.http.ResponseEntity;

import com.tecabix.db.entity.Sesion;
import com.tecabix.res.b.RSB021;
import com.tecabix.sv.rq.RQSV029;

/**
 *
 * @author Ramirez Urrutia Angel Abinadi
 */
public class Sesion005BZ {

    /**
     * Método para validar la sesión usuario.
     *
     * @param rqsv029 datos a validar.
     * @return {@link ResponseEntity} con un objeto {@link RSB021} que contiene
     *         información para validar la sesión del usuario.
     */
    public ResponseEntity<RSB021> validar(final RQSV029 rqsv029) {

        RSB021 rsb021 = rqsv029.getRsb021();
        Sesion sesion = rqsv029.getSesion();

        return rsb021.ok(sesion);
    }
}
