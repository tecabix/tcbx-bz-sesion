package com.tecabix.bz.sesion;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.when;

import com.tecabix.db.entity.Sesion;
import com.tecabix.res.b.RSB021;
import com.tecabix.sv.rq.RQSV029;

import org.springframework.http.ResponseEntity;

/**
 *
 * @author Ramirez Urrutia Angel Abinadi
 */
@ExtendWith(MockitoExtension.class)
public class Sesion005BZTest {

    /**
     * Solicitud simulada RQSV029.
     */
    @Mock
    private RQSV029 rqsv029;

    /**
     * Respuesta simulada RSB021.
     */
    @Mock
    private RSB021 rsb021;

    /**
     * Sesión simulada.
     */
    @Mock
    private Sesion sesion;

    /**
     * Instancia de la clase bajo prueba.
     */
    private Sesion005BZ sesion005BZ;

    /**
     * Inicializa la instancia de Sesion005BZ antes de cada prueba.
     */
    @BeforeEach
    void setUp() {
        sesion005BZ = new Sesion005BZ();
    }

    /**
     * Verifica que el método validar retorne una respuesta OK cuando se
     * proporciona una sesión válida en la solicitud.
     */
    @Test
    void validarDebeRetornarOkConSesion() {

        when(rqsv029.getRsb021()).thenReturn(rsb021);
        when(rqsv029.getSesion()).thenReturn(sesion);

        ResponseEntity<RSB021> expected = ResponseEntity.ok(rsb021);
        when(rsb021.ok(sesion)).thenReturn(expected);

        ResponseEntity<RSB021> response = sesion005BZ.validar(rqsv029);

        assertNotNull(response);
        assertSame(expected, response);
    }
}
