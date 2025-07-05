package com.tecabix.bz.sesion;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.tecabix.bz.sesion.dto.Sesion004BzDTO;
import com.tecabix.db.entity.Catalogo;
import com.tecabix.db.entity.Sesion;
import com.tecabix.db.entity.Usuario;
import com.tecabix.db.repository.SesionRepository;
import com.tecabix.res.b.RSB020;
import com.tecabix.sv.rq.RQSV028;

import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

/**
 *
 * @author Ramirez Urrutia Angel Abinadi
 */
@ExtendWith(MockitoExtension.class)
public class Sesion004BZTest {

    /**
     * Repositorio simulado para sesiones.
     */
    @Mock
    private SesionRepository sesionRepository;

    /**
     * Catálogo simulado para representar estado eliminado.
     */
    @Mock
    private Catalogo eliminado;

    /**
     * DTO simulado para pruebas de Sesion004BZ.
     */
    @Mock
    private Sesion004BzDTO dto;

    /**
     * Solicitud simulada RQSV028.
     */
    @Mock
    private RQSV028 rqsv028;

    /**
     * Respuesta simulada RSB020.
     */
    @Mock
    private RSB020 rsb020;

    /**
     * Sesión auxiliar simulada.
     */
    @Mock
    private Sesion sesionAux;

    /**
     * Usuario simulado.
     */
    @Mock
    private Usuario usuario;

    /**
     * Sesión simulada.
     */
    @Mock
    private Sesion sesion;

    /**
     * Instancia de la clase bajo prueba.
     */
    private Sesion004BZ sesion004BZ;

    /**
     * UUID generado para pruebas.
     */
    private UUID uuid;

    /**
     * Id del usuario de pruebas.
     */
    private static final long USR_PRUEBA = 42L;

    /**
     * Representa el estado HTTP 404 (No encontrado).
     */
    private static final int STATUS_NOT_FOUND = 404;

    /**
     * Configura los mocks y la instancia de Sesion004BZ antes de cada prueba.
     */
    @BeforeEach
    void setUp() {

        when(dto.getSesionRepository()).thenReturn(sesionRepository);
        when(dto.getEliminado()).thenReturn(eliminado);
        sesion004BZ = new Sesion004BZ(dto);
        uuid = UUID.randomUUID();
    }

    @Test
    void actualizarEliminarDebeRetornarOkCuandoSesionExiste() {

        when(rqsv028.getRsb020()).thenReturn(rsb020);
        when(rqsv028.getSesion()).thenReturn(sesion);
        when(rsb020.getClave()).thenReturn(uuid);
        when(sesionRepository.findByClave(uuid))
            .thenReturn(Optional.of(sesionAux));
        when(sesion.getUsuario()).thenReturn(usuario);
        when(usuario.getId()).thenReturn(USR_PRUEBA);
        when(sesionRepository.save(sesionAux)).thenReturn(sesionAux);

        ResponseEntity<RSB020> expected = ResponseEntity.ok(rsb020);
        when(rsb020.ok()).thenReturn(expected);

        ResponseEntity<RSB020> response;
        response = sesion004BZ.actualizarEliminar(rqsv028);

        assertNotNull(response);
        assertSame(expected, response);
        verify(sesionAux).setFechaDeModificacion(any(LocalDateTime.class));
        verify(sesionAux).setIdUsuarioModificado(USR_PRUEBA);
        verify(sesionAux).setEstatus(eliminado);
        verify(sesionRepository).save(sesionAux);
        verify(rsb020).ok();
    }

    @Test
    void actualizarEliminarDebeRetornarNotFoundCuandoSesionNoExiste() {

        when(rqsv028.getRsb020()).thenReturn(rsb020);
        when(rsb020.getClave()).thenReturn(uuid);
        when(sesionRepository.findByClave(uuid)).thenReturn(Optional.empty());

        ResponseEntity<RSB020> expected;
        expected = ResponseEntity.status(STATUS_NOT_FOUND).body(rsb020);
        when(rsb020.notFound("No se encontró la sesión.")).thenReturn(expected);

        ResponseEntity<RSB020> response;
        response = sesion004BZ.actualizarEliminar(rqsv028);

        assertNotNull(response);
        assertSame(expected, response);
        verify(rsb020).notFound("No se encontró la sesión.");
        verify(sesionRepository, never()).save(any());
    }

    @Test
    void testGettersAndSetters() {

        Sesion004BzDTO dtoSesion = new Sesion004BzDTO();

        SesionRepository mockRepo = mock(SesionRepository.class);
        Catalogo mockCatalogo = mock(Catalogo.class);

        dtoSesion.setSesionRepository(mockRepo);
        dtoSesion.setEliminado(mockCatalogo);

        assertEquals(mockRepo, dtoSesion.getSesionRepository());
        assertEquals(mockCatalogo, dtoSesion.getEliminado());
    }

}
