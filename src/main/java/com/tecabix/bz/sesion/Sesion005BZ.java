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

    public ResponseEntity<RSB021> validar(final RQSV029 rqsv029) {

        RSB021 rsb021 = rqsv029.getRsb021();
        Sesion sesion = rqsv029.getSesion();

        return rsb021.ok(sesion);
    }

}
