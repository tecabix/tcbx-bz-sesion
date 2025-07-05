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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.tecabix.bz.sesion.dto.Sesion003BzDTO;
import com.tecabix.db.entity.Catalogo;
import com.tecabix.db.entity.Sesion;
import com.tecabix.db.entity.Usuario;
import com.tecabix.db.repository.SesionRepository;
import com.tecabix.res.b.RSB020;
import com.tecabix.sv.rq.RQSV027;

import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;

/**
 *
 * @author Ramirez Urrutia Angel Abinadi
 */
@ExtendWith(MockitoExtension.class)
public class Sesion003BZTest {

    /**
     * Simulación de dependencias y configuración previa para pruebas de
     * Sesion003BZ.
     */
    @Mock
    private SesionRepository sesionRepository;

    /**
     * Catálogo simulado para pruebas.
     */
    @Mock
    private Catalogo eliminado;

    /**
     * Servicio RQSV027 simulado.
     */
    @Mock
    private RQSV027 rqsv027;

    /**
     * Servicio RSB020 simulado.
     */
    @Mock
    private RSB020 rsb020;

    /**
     * Sesión simulada.
     */
    @Mock
    private Sesion sesion;

    /**
     * Usuario simulado.
     */
    @Mock
    private Usuario usuario;

    /**
     * Instancia de la clase bajo prueba.
     */
    private Sesion003BZ sesion003BZ;

    /**
     * Id del usuario de pruebas.
     */
    private static final long USR_PRUEBA = 123L;

    /**
     * Configura los mocks y la instancia de Sesion003BZ antes de cada prueba.
     */
    @BeforeEach
    void setUp() {

        Sesion003BzDTO dto = mock(Sesion003BzDTO.class);
        when(dto.getSesionRepository()).thenReturn(sesionRepository);
        when(dto.getEliminado()).thenReturn(eliminado);

        sesion003BZ = new Sesion003BZ(dto);
    }

    /**
     * Verifica que el método eliminar actualice la sesión correctamente
     * y retorne una respuesta OK.
     */
    @Test
    void eliminarDebeActualizarSesionYRetornarOk() {

        when(rqsv027.getRsb020()).thenReturn(rsb020);
        when(rqsv027.getSesion()).thenReturn(sesion);
        when(sesion.getUsuario()).thenReturn(usuario);
        when(usuario.getId()).thenReturn(USR_PRUEBA);
        when(sesionRepository.save(any(Sesion.class))).thenReturn(sesion);

        ResponseEntity<RSB020> expectedResponse = ResponseEntity.ok(rsb020);
        when(rsb020.ok()).thenReturn(expectedResponse);

        ResponseEntity<RSB020> response = sesion003BZ.eliminar(rqsv027);

        assertNotNull(response);
        assertSame(expectedResponse, response);
        verify(sesion).setFechaDeModificacion(any(LocalDateTime.class));
        verify(sesion).setIdUsuarioModificado(USR_PRUEBA);
        verify(sesion).setEstatus(eliminado);
        verify(sesionRepository).save(sesion);
        verify(rsb020).ok();

    }

    /**
     * Verifica los métodos getter y setter de Sesion003BzDTO.
     */
    @Test
    void testSesion003BzDTO() {

        Sesion003BzDTO dto = new Sesion003BzDTO();

        SesionRepository mockRepo = mock(SesionRepository.class);
        Catalogo mockCatalogo = mock(Catalogo.class);

        dto.setSesionRepository(mockRepo);
        dto.setEliminado(mockCatalogo);
        assertEquals(mockRepo, dto.getSesionRepository());
        assertEquals(mockCatalogo, dto.getEliminado());
    }
}
