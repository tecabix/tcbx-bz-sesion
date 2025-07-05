package com.tecabix.bz.sesion;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.tecabix.db.entity.Sesion;
import com.tecabix.db.repository.SesionRepository;
import com.tecabix.res.b.RSB018;
import com.tecabix.sv.rq.RQSV026;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

/**
 *
 * @author Ramirez Urrutia Angel Abinadi
 */
@ExtendWith(MockitoExtension.class)
public class Sesion002BZTest {

    /**
     * Mock del repositorio de sesiones utilizado para simular operaciones de
     * base de datos.
     */
    @Mock
    private SesionRepository sesionRepository;

    /**
     * Mock del request que contiene parámetros de paginación y acceso
     * a {@link RSB018}.
     */
    @Mock
    private RQSV026 rqsv026;

    /**
     * Mock del servicio o componente RSB018 utilizado en la lógica de negocio.
     */
    @Mock
    private RSB018 rsb018;

    /**
     * Mock de una página de resultados de sesiones.
     */
    @Mock
    private Page<Sesion> page;

    /**
     * Instancia de la clase bajo prueba.
     */
    private Sesion002BZ sesion002BZ;

    /**
     * Representa al número diez.
     */
    private static final int DIEZ = 10;

    /**
     * Configura los mocks y la instancia de prueba antes de cada test.
     */
    @BeforeEach
    void setUp() {

        sesion002BZ = new Sesion002BZ(sesionRepository);
        when(rqsv026.getRsb018()).thenReturn(rsb018);
        when(rqsv026.getElementos()).thenReturn((byte) DIEZ);
        when(rqsv026.getPagina()).thenReturn((short) 0);
    }

    @Test
    void cuandoTextoVacioDebeRetornarOk() {

        when(rqsv026.getTexto()).thenReturn(Optional.empty());
        when(sesionRepository.findByActive(any(Pageable.class)))
            .thenReturn(page);
        ResponseEntity<RSB018> expected = ResponseEntity.ok(rsb018);
        when(rsb018.ok(page)).thenReturn(expected);

        ResponseEntity<RSB018> response = sesion002BZ.buscar(rqsv026);

        assertNotNull(response);
        assertSame(expected, response);
        verify(rsb018).ok(page);
    }

    @Test
    void cuandoTipoUsuarioDebeRetornarOk() {

        when(rqsv026.getTexto()).thenReturn(Optional.of("admin"));
        when(rqsv026.getTipo()).thenReturn("USUARIO");
        when(sesionRepository.findByActiveAndLikeUsuario(
                anyString(), any(Pageable.class)))
            .thenReturn(page);
        when(sesionRepository.findByActiveAndLikeLicencia(
                anyString(), any(Pageable.class)))
            .thenReturn(Page.empty());
        when(sesionRepository.findByActiveAndLikeServicio(
                anyString(), any(Pageable.class)))
            .thenReturn(Page.empty());

        ResponseEntity<RSB018> expected = ResponseEntity.ok(rsb018);
        when(rsb018.ok(page)).thenReturn(expected);

        ResponseEntity<RSB018> response = sesion002BZ.buscar(rqsv026);

        assertNotNull(response);
        assertSame(expected, response);
        verify(rsb018).ok(page);
    }

    @Test
    void cuandoTipoLicenciaDebeRetornarOk() {

        when(rqsv026.getTexto()).thenReturn(Optional.of("licencia"));
        when(rqsv026.getTipo()).thenReturn("LICENCIA");
        when(sesionRepository.findByActiveAndLikeUsuario(
                anyString(), any(Pageable.class)))
            .thenReturn(Page.empty());
        when(sesionRepository.findByActiveAndLikeLicencia(
                anyString(), any(Pageable.class)))
            .thenReturn(page);
        when(sesionRepository.findByActiveAndLikeServicio(
                anyString(), any(Pageable.class)))
            .thenReturn(Page.empty());

        ResponseEntity<RSB018> expected = ResponseEntity.ok(rsb018);
        when(rsb018.ok(page)).thenReturn(expected);

        ResponseEntity<RSB018> response = sesion002BZ.buscar(rqsv026);

        assertNotNull(response);
        assertSame(expected, response);
        verify(rsb018).ok(page);
    }

    @Test
    void cuandoTipoServicioDebeRetornarOk() {

        when(rqsv026.getTexto()).thenReturn(Optional.of("servicio"));
        when(rqsv026.getTipo()).thenReturn("SERVICIO");
        when(sesionRepository.findByActiveAndLikeUsuario(
                anyString(), any(Pageable.class)))
            .thenReturn(Page.empty());
        when(sesionRepository.findByActiveAndLikeLicencia(
                anyString(), any(Pageable.class)))
            .thenReturn(Page.empty());
        when(sesionRepository.findByActiveAndLikeServicio(
                anyString(), any(Pageable.class)))
            .thenReturn(page);

        ResponseEntity<RSB018> expected = ResponseEntity.ok(rsb018);
        when(rsb018.ok(page)).thenReturn(expected);

        ResponseEntity<RSB018> response = sesion002BZ.buscar(rqsv026);

        assertNotNull(response);
        assertSame(expected, response);
        verify(rsb018).ok(page);
    }

    @Test
    void cuandoTipoInvalidoDebeRetornarBadRequest() {

        when(rqsv026.getTexto()).thenReturn(Optional.of("otro"));
        when(rqsv026.getTipo()).thenReturn("INVALIDO");
        when(sesionRepository.findByActiveAndLikeUsuario(
                anyString(), any(Pageable.class)))
            .thenReturn(Page.empty());
        when(sesionRepository.findByActiveAndLikeLicencia(
                anyString(), any(Pageable.class)))
            .thenReturn(Page.empty());
        when(sesionRepository.findByActiveAndLikeServicio(
                anyString(), any(Pageable.class)))
            .thenReturn(Page.empty());

        ResponseEntity<RSB018> expected;
        expected = ResponseEntity.badRequest().body(rsb018);
        when(rsb018.badRequest("El tipo de filtro no es válido.")).thenReturn(expected);

        ResponseEntity<RSB018> response = sesion002BZ.buscar(rqsv026);

        assertNotNull(response);
        assertSame(expected, response);
        verify(rsb018).badRequest("El tipo de filtro no es válido.");
    }
}
