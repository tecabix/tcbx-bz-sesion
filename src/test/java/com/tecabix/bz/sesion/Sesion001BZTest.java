package com.tecabix.bz.sesion;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.tecabix.bz.sesion.dto.Sesion001BzDTO;
import com.tecabix.db.entity.Catalogo;
import com.tecabix.db.entity.Licencia;
import com.tecabix.db.entity.Servicio;
import com.tecabix.db.entity.Sesion;
import com.tecabix.db.entity.Usuario;
import com.tecabix.db.repository.LicenciaRepository;
import com.tecabix.db.repository.SesionRepository;
import com.tecabix.db.repository.UsuarioRepository;
import com.tecabix.res.b.RSB019;
import com.tecabix.sv.rq.RQSV025;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 *
 * @author Ramirez Urrutia Angel Abinadi
 */
@ExtendWith(MockitoExtension.class)
public class Sesion001BZTest {

    /**
     * Repositorio para acceder a la entidad Usuario.
     */
    @Mock
    private UsuarioRepository usuarioRepository;

    /**
     * Repositorio para acceder a la entidad Licencia.
     */
    @Mock
    private LicenciaRepository licenciaRepository;

    /**
     * Repositorio para acceder a la entidad Sesion.
     */
    @Mock
    private SesionRepository sesionRepository;

    /**
     * Mock del servicio RQSV025 utilizado para pruebas unitarias.
     */
    @Mock
    private RQSV025 rqsv025;

    /**
     * Mock del objeto Usuario utilizado en pruebas.
     */
    @Mock
    private Usuario usuario;

    /**
     * Mock del objeto Licencia utilizado en pruebas.
     */
    @Mock
    private Licencia licencia;

    /**
     * Mock del objeto Servicio utilizado en pruebas.
     */
    @Mock
    private Servicio servicio;

    /**
     * Mock del objeto Sesion utilizado en pruebas.
     */
    @Mock
    private Sesion sesion;

    /**
     * Servicio simulado RSB002 utilizado en pruebas.
     */
    @Mock
    private RSB019 rsb019;

    /**
     * Instancia de la sesión de negocio utilizada en el flujo.
     */
    private Sesion001BZ sesionBZ;

    /**
     * Identificador único utilizado como clave en el proceso.
     */
    private UUID clave;

    /**
     * Catálogo simulado que representa la licencia multi.
     */
    @Mock
    private Catalogo licenciaMulti;

    /**
     * Catálogo simulado que representa la licencia mono.
     */
    @Mock
    private Catalogo licenciaMono;

    /**
     * Catálogo simulado que representa la licencia multi mono.
     */
    @Mock
    private Catalogo licenciaMultiMono;

    /**
     * Catálogo simulado que representa estatus activo.
     */
    @Mock
    private Catalogo activo;

    /**
     * Catálogo simulado que representa estatus eliminado.
     */
    @Mock
    private Catalogo eliminado;

    /**
     * Representa el estado HTTP 404 (No encontrado).
     */
    private static final int STATUS_NOT_FOUND = 404;

    /**
     * Representa el estado HTTP 401 (No autorizado).
     */
    private static final int STATUS_UNAUTHORIZED = 401;

    /**
     * Representa al número cincuenta.
     */
    private static final int CINCUENTA = 50;

    /**
     * Representa al número veinticinco.
     */
    private static final int VEINTICINCO = 25;

    /**
     * Representa al número máximo de sesiones abiertas que se permiten.
     */
    private static final int MAXSESION = 15;

    /**
     * Representa al número diez.
     */
    private static final int DIEZ = 10;

    /**
     * Representa al número cinco.
     */
    private static final int CINCO = 5;

    /**
     * Límite máximo de sesiones activas permitidas.
     */
    private static final long MAX_ACTIVAS = 20L;

    /**
     * Mensaje peticiones agotadas.
     */
    private static final String PETICIONES_AGOTADAS = "Peticiones agotadas.";

    /**
     * Usuario de prueba.
     */
    private static final String USR = "usuarioTest";

    /**
     * Configura el entorno antes de cada prueba unitaria.
     *
     * Inicializa el DTO con los repositorios y configuraciones necesarias,
     * asigna claves UUID a los objetos mockeados y prepara la instancia
     * de negocio `Sesion001BZ`. También se configuran los comportamientos
     * esperados de los mocks utilizados en las pruebas.
     */
    @BeforeEach
    void setUp() {

        Sesion001BzDTO dto = new Sesion001BzDTO();
        dto.setUsuarioRepository(usuarioRepository);
        dto.setLicenciaRepository(licenciaRepository);
        dto.setSesionRepository(sesionRepository);
        dto.setLicenciaMulti(licenciaMulti);
        dto.setLicenciaMono(licenciaMono);
        dto.setLicenciaMultiMono(licenciaMultiMono);
        dto.setActivo(activo);
        dto.setEliminado(eliminado);

        licencia.setClave(UUID.randomUUID());
        usuario.setClave(UUID.randomUUID());
        servicio.setClave(UUID.randomUUID());
        sesion.setClave(UUID.randomUUID());

        sesionBZ = new Sesion001BZ(dto);

        clave = UUID.randomUUID();
        when(rqsv025.getUsuario()).thenReturn(USR);
        when(rqsv025.getRsb019()).thenReturn(rsb019);
        when(rsb019.getClave()).thenReturn(clave);
    }

    @Test
    void cuandoDatosValidosDebeRetornarOk() {

        Page<Sesion> sesionesVacias = new PageImpl<>(Collections.emptyList());
        when(rqsv025.getUsuario()).thenReturn(USR);
        when(rqsv025.getRsb019()).thenReturn(rsb019);
        when(usuarioRepository.findByNombre(USR))
            .thenReturn(Optional.of(usuario));
        when(licenciaRepository.findByToken(clave)).thenReturn(licencia);
        when(licencia.getId()).thenReturn(1L);
        when(usuario.getId()).thenReturn(1L);
        when(licencia.getServicio()).thenReturn(servicio);
        when(servicio.getTipo()).thenReturn(licenciaMulti);
        when(servicio.getPeticiones()).thenReturn(DIEZ);
        when(sesionRepository.findByUsuarioAndNow(
                anyLong(),
                anyLong(),
                any(Pageable.class))
        ).thenReturn(sesionesVacias);

        when(sesionRepository.findByUsuarioAndActive(
                anyLong(),
                anyLong(),
                any(Pageable.class))
        ).thenReturn(sesionesVacias);

        when(sesionRepository.save(any(Sesion.class))).thenReturn(sesion);

        ResponseEntity<RSB019> expectedResponse = ResponseEntity.ok(rsb019);
        when(rsb019.ok(any(Sesion.class))).thenReturn(expectedResponse);

        ResponseEntity<RSB019> response = sesionBZ.crear(rqsv025);

        assertNotNull(response, "La respuesta no debe ser null");
        assertSame(expectedResponse, response, "Respuesta debe ser la misma instancia esperada");
        verify(rsb019).ok(any(Sesion.class));
    }

    @Test
    void cuandoSesionesActivasMarcarComoEliminadas() {

        Page<Sesion> sesionesVacias = new PageImpl<>(Collections.emptyList());
        when(usuarioRepository.findByNombre(USR))
            .thenReturn(Optional.of(usuario));
        when(licenciaRepository.findByToken(clave)).thenReturn(licencia);
        when(licencia.getId()).thenReturn(1L);
        when(usuario.getId()).thenReturn(1L);
        when(licencia.getServicio()).thenReturn(servicio);
        when(servicio.getTipo()).thenReturn(licenciaMulti);
        when(servicio.getPeticiones()).thenReturn(DIEZ);
        when(sesionRepository.findByUsuarioAndNow(
                anyLong(),
                anyLong(),
                any(Pageable.class))
        ).thenReturn(sesionesVacias);

        Sesion sesionActiva = new Sesion();
        Page<Sesion> usrActivo;
        usrActivo  = sesionRepository
            .findByUsuarioAndActive(anyLong(), anyLong(), any(Pageable.class));
        sesionActiva.setUsuario(usuario);

        when(usrActivo).thenReturn(new PageImpl<>(List.of(sesionActiva)));
        when(sesionRepository.save(any(Sesion.class))).thenReturn(sesion);
        when(rsb019.ok(any(Sesion.class)))
            .thenReturn(ResponseEntity.ok(rsb019));

        ResponseEntity<RSB019> response = sesionBZ.crear(rqsv025);

        assertNotNull(response);

        ArgumentCaptor<Sesion> captor = ArgumentCaptor.forClass(Sesion.class);
        verify(sesionRepository, times(2)).save(captor.capture());

        List<Sesion> sesionesGuardadas = captor.getAllValues();

        assertEquals(2, sesionesGuardadas.size());
        assertTrue(
            sesionesGuardadas.stream()
                .anyMatch(s -> eliminado.equals(s.getEstatus())),
            "Debe haber una sesión eliminada"
        );

        assertTrue(
            sesionesGuardadas.stream()
                .anyMatch(s -> activo.equals(s.getEstatus())),
            "Debe haber una sesión activa"
        );

        verify(rsb019).ok(any(Sesion.class));
    }

    @Test
    void cuandoLicenciaMonoSesionesHoyActivasCreaSesion() {

        when(usuarioRepository.findByNombre(USR))
            .thenReturn(Optional.of(usuario));
        when(licenciaRepository.findByToken(clave)).thenReturn(licencia);
        when(licencia.getId()).thenReturn(1L);
        when(usuario.getId()).thenReturn(1L);
        when(licencia.getServicio()).thenReturn(servicio);
        when(servicio.getTipo()).thenReturn(licenciaMono);

        Sesion sesionHoy = new Sesion();
        sesionHoy.setPeticionesRestantes(CINCO);
        Page<Sesion> paginaConSesionHoy = new PageImpl<>(List.of(sesionHoy));

        when(sesionRepository.findByNow(
                anyLong(),
                any(Pageable.class))
        ).thenReturn(paginaConSesionHoy);

        Sesion sesionActiva = new Sesion();
        sesionActiva.setUsuario(usuario);

        Page<Sesion> paginaSesiones = new PageImpl<>(List.of(sesionActiva));

        when(sesionRepository
                .findByLicenciaAndActive(anyLong(), any(Pageable.class)))
            .thenReturn(paginaSesiones);
        when(sesionRepository.save(any(Sesion.class))).thenReturn(sesion);
        when(rsb019.ok(any(Sesion.class)))
            .thenReturn(ResponseEntity.ok(rsb019));

        ResponseEntity<RSB019> response = sesionBZ.crear(rqsv025);

        assertNotNull(response);

        ArgumentCaptor<Sesion> captor = ArgumentCaptor.forClass(Sesion.class);
        verify(sesionRepository, times(2)).save(captor.capture());

        List<Sesion> sesionesGuardadas = captor.getAllValues();
        assertEquals(2, sesionesGuardadas.size());

        assertTrue(
            sesionesGuardadas.stream()
                .anyMatch(s -> eliminado.equals(s.getEstatus()))
        );

        assertTrue(
            sesionesGuardadas.stream()
                .anyMatch(s -> activo.equals(s.getEstatus()))
        );
    }

    @Test
    void cuandoLicenciaMonoNoSesionesDeHoyUsaPeticionesServicio() {

        Optional<Usuario> usrOpt;
        usrOpt = usuarioRepository.findByNombre(USR);
        when(usrOpt).thenReturn(Optional.of(usuario));
        when(licenciaRepository.findByToken(clave)).thenReturn(licencia);
        when(licencia.getId()).thenReturn(1L);
        when(usuario.getId()).thenReturn(1L);
        when(licencia.getServicio()).thenReturn(servicio);
        when(servicio.getTipo()).thenReturn(licenciaMono);
        when(servicio.getPeticiones()).thenReturn(DIEZ);

        Page<Sesion> paginaVacia = new PageImpl<>(Collections.emptyList());
        when(sesionRepository.findByNow(anyLong(), any(Pageable.class)))
            .thenReturn(paginaVacia);

        Sesion sesionActiva = new Sesion();
        sesionActiva.setUsuario(usuario);
        Page<Sesion> licenciaActiva;
        licenciaActiva = sesionRepository
            .findByLicenciaAndActive(anyLong(), any(Pageable.class));
        when(licenciaActiva).thenReturn(new PageImpl<>(List.of(sesionActiva)));
        when(sesionRepository.save(any(Sesion.class))).thenReturn(sesion);
        when(rsb019.ok(any(Sesion.class)))
            .thenReturn(ResponseEntity.ok(rsb019));

        ResponseEntity<RSB019> response = sesionBZ.crear(rqsv025);

        assertNotNull(response);

        ArgumentCaptor<Sesion> captor = ArgumentCaptor.forClass(Sesion.class);
        verify(sesionRepository, times(2)).save(captor.capture());

        List<Sesion> sesionesGuardadas = captor.getAllValues();

        Sesion nuevaSesion;
        nuevaSesion = sesionesGuardadas.stream()
            .filter(s -> activo.equals(s.getEstatus()))
            .findFirst()
            .orElseThrow();

        assertEquals(DIEZ, nuevaSesion.getPeticionesRestantes());
        assertTrue(
            sesionesGuardadas.stream()
                .anyMatch(s -> eliminado.equals(s.getEstatus()))
        );
    }

    @Test
    void cuandoLicenciaMultiMonoNoSesionesDeHoyUsaPeticionesServicio() {

        when(rqsv025.getUsuario()).thenReturn("usuarioTest");
        when(rqsv025.getRsb019()).thenReturn(rsb019);
        when(rsb019.getClave()).thenReturn(clave);
        when(usuarioRepository.findByNombre(USR))
            .thenReturn(Optional.of(usuario));
        when(licenciaRepository.findByToken(clave)).thenReturn(licencia);
        when(licencia.getId()).thenReturn(1L);
        when(usuario.getId()).thenReturn(1L);
        when(licencia.getServicio()).thenReturn(servicio);
        when(servicio.getTipo()).thenReturn(licenciaMultiMono);
        when(servicio.getPeticiones()).thenReturn(CINCUENTA);

        List<Sesion> sesionesActivas = new ArrayList<>();
        for (int i = 0; i < MAXSESION; i++) {
            Sesion sesionMock = mock(Sesion.class);
            sesionesActivas.add(sesionMock);
        }

        Page<Sesion> page = new PageImpl<>(sesionesActivas);

        when(sesionRepository
                .findByLicenciaAndActive(anyLong(), any(Pageable.class)))
            .thenReturn(page);

        Sesion nuevaSesion = new Sesion();
        when(sesionRepository.save(any(Sesion.class))).thenReturn(nuevaSesion);

        ResponseEntity<RSB019> expectedResponse = ResponseEntity.ok(rsb019);
        when(rsb019.ok(any(Sesion.class))).thenReturn(expectedResponse);

        ResponseEntity<RSB019> response = sesionBZ.crear(rqsv025);
        assertNotNull(response);
        verify(servicio).getPeticiones();
        verify(licencia, atLeastOnce()).getServicio();
        verify(rsb019).ok(any(Sesion.class));
    }

    @Test
    void cuandoTipoLicenciaOtroNoSesionesHoyPeticionesSvcEliminaActivas() {

        when(rqsv025.getUsuario()).thenReturn(USR);
        when(rqsv025.getRsb019()).thenReturn(rsb019);
        when(rsb019.getClave()).thenReturn(clave);
        when(usuarioRepository.findByNombre(USR))
            .thenReturn(Optional.of(usuario));
        when(licenciaRepository.findByToken(clave)).thenReturn(licencia);
        when(licencia.getId()).thenReturn(1L);
        when(usuario.getId()).thenReturn(1L);

        Catalogo tipoDesconocido = mock(Catalogo.class);
        when(licencia.getServicio()).thenReturn(servicio);
        when(servicio.getTipo()).thenReturn(tipoDesconocido);

        Page<Sesion> sesionesDeHoy = new PageImpl<>(Collections.emptyList());
        when(sesionRepository
                .findByNow(anyLong(), any(Pageable.class)))
            .thenReturn(sesionesDeHoy);
        when(servicio.getPeticiones()).thenReturn(VEINTICINCO);

        Sesion sesionActiva = mock(Sesion.class);
        when(sesionActiva.getUsuario()).thenReturn(usuario);
        Page<Sesion> sesionesActivas = new PageImpl<>(List.of(sesionActiva));
        when(sesionRepository
                .findByLicenciaAndActive(anyLong(), any(Pageable.class)))
            .thenReturn(sesionesActivas);

        Sesion nuevaSesion = new Sesion();
        when(sesionRepository.save(any(Sesion.class))).thenReturn(nuevaSesion);

        ResponseEntity<RSB019> expectedResponse = ResponseEntity.ok(rsb019);
        when(rsb019.ok(any(Sesion.class))).thenReturn(expectedResponse);

        ResponseEntity<RSB019> response = sesionBZ.crear(rqsv025);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        verify(servicio).getPeticiones();
        verify(sesionRepository).findByNow(anyLong(), any(Pageable.class));
        verify(sesionRepository)
            .findByLicenciaAndActive(anyLong(), any(Pageable.class));
        verify(sesionRepository, atLeast(2)).save(any(Sesion.class));
        verify(rsb019).ok(any(Sesion.class));
    }

    @Test
    void cuandoSesionHoyConPeticionesAgotadasRetornaUnauthorizedMulti() {

        when(rqsv025.getUsuario()).thenReturn(USR);
        when(rqsv025.getRsb019()).thenReturn(rsb019);
        when(rsb019.getClave()).thenReturn(clave);
        when(usuarioRepository.findByNombre(USR))
            .thenReturn(Optional.of(usuario));
        when(licenciaRepository.findByToken(clave)).thenReturn(licencia);
        when(licencia.getServicio()).thenReturn(servicio);
        when(servicio.getTipo()).thenReturn(licenciaMulti);

        Sesion sesionAgotada = mock(Sesion.class);
        when(sesionAgotada.getPeticionesRestantes()).thenReturn(0);

        Page<Sesion> paginaAgotada = new PageImpl<>(List.of(sesionAgotada));
        when(sesionRepository
                .findByUsuarioAndNow(anyLong(), anyLong(), any(Pageable.class)))
            .thenReturn(paginaAgotada);

        ResponseEntity<RSB019> expected;
        expected = ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(rsb019);
        when(rsb019.unauthorized(PETICIONES_AGOTADAS)).thenReturn(expected);

        ResponseEntity<RSB019> response = sesionBZ.crear(rqsv025);

        assertNotNull(response);
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());

        verify(sesionRepository)
            .findByUsuarioAndNow(anyLong(), anyLong(), any(Pageable.class));

        verify(rsb019).unauthorized(PETICIONES_AGOTADAS);
    }

    @Test
    void crearDebeRetornarUnauthorizedCuandoUsuarioNoExiste() {

        String nombre = "usuarioInexistente";
        UUID claveGral = UUID.randomUUID();

        when(rqsv025.getRsb019()).thenReturn(rsb019);
        when(rqsv025.getUsuario()).thenReturn(nombre);
        when(rsb019.getClave()).thenReturn(claveGral);
        when(usuarioRepository.findByNombre(nombre))
            .thenReturn(Optional.empty());

        ResponseEntity<RSB019> expected;
        expected = ResponseEntity.status(STATUS_UNAUTHORIZED).body(rsb019);
        when(rsb019.unauthorized("No se encontró el usuario.")).thenReturn(expected);

        ResponseEntity<RSB019> response = sesionBZ.crear(rqsv025);

        assertSame(expected, response);
    }

    @Test
    void crearDebeRetornarNotFoundCuandoLicenciaNoExiste() {

        String nombre = "usuarioValido";
        UUID claveGral = UUID.randomUUID();

        Usuario usuarioTest = mock(Usuario.class);

        when(rqsv025.getRsb019()).thenReturn(rsb019);
        when(rqsv025.getUsuario()).thenReturn(nombre);
        when(rsb019.getClave()).thenReturn(claveGral);
        when(usuarioRepository.findByNombre(nombre))
            .thenReturn(Optional.of(usuarioTest));
        when(licenciaRepository.findByToken(claveGral)).thenReturn(null);

        ResponseEntity<RSB019> expected;
        expected = ResponseEntity.status(STATUS_NOT_FOUND).body(rsb019);
        when(rsb019.notFound("No se encontró la licencia.")).thenReturn(expected);

        ResponseEntity<RSB019> response = sesionBZ.crear(rqsv025);

        assertSame(expected, response);
    }

    @Test
    void crearDebeRetornarUnauthorizedCuandoPeticionesRestantesSonCero() {

        String nombre = "usuarioValido";
        UUID claveGral = UUID.randomUUID();
        Usuario usuarioTest = mock(Usuario.class);
        Licencia licenciaTest = mock(Licencia.class);
        Servicio servicioTest = mock(Servicio.class);
        Catalogo tipoLicencia = licenciaMono;
        Sesion sesionTest = mock(Sesion.class);
        Page<Sesion> sesionesHoy = mock(Page.class);

        when(rqsv025.getRsb019()).thenReturn(rsb019);
        when(rqsv025.getUsuario()).thenReturn(nombre);
        when(rsb019.getClave()).thenReturn(claveGral);
        when(usuarioRepository.findByNombre(nombre))
            .thenReturn(Optional.of(usuarioTest));
        when(licenciaRepository.findByToken(claveGral))
            .thenReturn(licenciaTest);
        when(licenciaTest.getId()).thenReturn(1L);
        when(licenciaTest.getServicio()).thenReturn(servicioTest);
        when(servicioTest.getTipo()).thenReturn(tipoLicencia);
        when(sesionTest.getPeticionesRestantes()).thenReturn(0);
        when(sesionesHoy.getContent()).thenReturn(List.of(sesionTest));
        when(sesionRepository.findByNow(eq(1L), any())).thenReturn(sesionesHoy);

        ResponseEntity<RSB019> expected;
        expected = ResponseEntity.status(STATUS_UNAUTHORIZED).body(rsb019);
        when(rsb019.unauthorized("Peticiones agotadas.")).thenReturn(expected);

        ResponseEntity<RSB019> response = sesionBZ.crear(rqsv025);

        assertSame(expected, response);
    }

    @Test
    void crearDebeRetornarUnauthorizedCuandoHayDemasiadasSesionesActivas() {

        String nombre = "usuarioValido";
        UUID claveGral = UUID.randomUUID();
        Usuario usuarioTest = mock(Usuario.class);
        Licencia licenciaTest = mock(Licencia.class);
        Servicio servicioTest = mock(Servicio.class);
        Catalogo tipoLicencia = licenciaMultiMono;

        Page<Sesion> sesionesActivas = mock(Page.class);

        when(rqsv025.getRsb019()).thenReturn(rsb019);
        when(rqsv025.getUsuario()).thenReturn(nombre);
        when(rsb019.getClave()).thenReturn(claveGral);
        when(usuarioRepository.findByNombre(nombre))
            .thenReturn(Optional.of(usuarioTest));
        when(licenciaRepository.findByToken(claveGral))
            .thenReturn(licenciaTest);
        when(licenciaTest.getId()).thenReturn(1L);
        when(licenciaTest.getServicio()).thenReturn(servicioTest);
        when(servicioTest.getTipo()).thenReturn(tipoLicencia);
        when(sesionRepository
                .findByLicenciaAndActive(eq(1L), any()))
            .thenReturn(sesionesActivas);
        when(sesionesActivas.getTotalElements()).thenReturn(MAX_ACTIVAS);

        ResponseEntity<RSB019> expected;
        expected = ResponseEntity.status(STATUS_UNAUTHORIZED).body(rsb019);
        when(rsb019.unauthorized("Muchos usuarios conectados.")).thenReturn(expected);

        ResponseEntity<RSB019> response = sesionBZ.crear(rqsv025);

        assertSame(expected, response);
    }

    @Test
    void crearDebeConsultarSesionesDeHoyYValidarPeticionesRestantes() {

        String nombre = "usuarioValido";
        UUID claveGral = UUID.randomUUID();
        Usuario usuarioTest = mock(Usuario.class);
        when(usuarioTest.getId()).thenReturn(1L);
        Licencia licenciaTest = mock(Licencia.class);
        Servicio servicioTest = mock(Servicio.class);
        Catalogo tipoOtro = mock(Catalogo.class);
        Sesion sesionExistente = mock(Sesion.class);

        when(sesionExistente.getPeticionesRestantes()).thenReturn(CINCO);
        when(sesionExistente.getUsuario()).thenReturn(usuarioTest);

        Page<Sesion> sesionesDeHoy = new PageImpl<>(List.of(sesionExistente));
        Page<Sesion> sesionesActivas = new PageImpl<>(List.of(sesionExistente));

        when(rqsv025.getRsb019()).thenReturn(rsb019);
        when(rqsv025.getUsuario()).thenReturn(nombre);
        when(rsb019.getClave()).thenReturn(claveGral);
        when(usuarioRepository.findByNombre(nombre))
            .thenReturn(Optional.of(usuarioTest));
        when(licenciaRepository.findByToken(claveGral))
            .thenReturn(licenciaTest);
        when(licenciaTest.getId()).thenReturn(1L);
        when(licenciaTest.getServicio()).thenReturn(servicioTest);
        when(servicioTest.getTipo()).thenReturn(tipoOtro);
        when(sesionRepository
                .findByNow(eq(1L), any()))
            .thenReturn(sesionesDeHoy);
        when(sesionRepository
                .findByLicenciaAndActive(eq(1L), any()))
            .thenReturn(sesionesActivas);

        ResponseEntity<RSB019> expected = ResponseEntity.ok(rsb019);
        when(rsb019.ok(any())).thenReturn(expected);

        ResponseEntity<RSB019> response = sesionBZ.crear(rqsv025);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(sesionRepository, times(2)).save(any(Sesion.class));
    }

    @Test
    void crearDebeRetornarUnauthorizedCuandoPeticionesEstanAgotadas() {

        String nombre = "usuarioValido";
        UUID claveGral = UUID.randomUUID();
        Usuario usuarioTest = mock(Usuario.class);
        Licencia licenciaTest = mock(Licencia.class);
        Servicio servicioTest = mock(Servicio.class);
        Catalogo tipoOtro = mock(Catalogo.class);
        Sesion sesionExistente = mock(Sesion.class);
        when(sesionExistente.getPeticionesRestantes()).thenReturn(0);

        Page<Sesion> sesionesDeHoy = new PageImpl<>(List.of(sesionExistente));

        when(rqsv025.getRsb019()).thenReturn(rsb019);
        when(rqsv025.getUsuario()).thenReturn(nombre);
        when(rsb019.getClave()).thenReturn(claveGral);
        when(usuarioRepository.findByNombre(nombre))
            .thenReturn(Optional.of(usuarioTest));
        when(licenciaRepository.findByToken(claveGral))
            .thenReturn(licenciaTest);
        when(licenciaTest.getId()).thenReturn(1L);
        when(licenciaTest.getServicio()).thenReturn(servicioTest);
        when(servicioTest.getTipo()).thenReturn(tipoOtro);
        when(sesionRepository
                .findByNow(eq(1L), any()))
            .thenReturn(sesionesDeHoy);

        ResponseEntity<RSB019> expected;
        expected = ResponseEntity.status(STATUS_UNAUTHORIZED).body(rsb019);
        when(rsb019.unauthorized("Peticiones agotadas.")).thenReturn(expected);

        ResponseEntity<RSB019> response = sesionBZ.crear(rqsv025);

        assertSame(expected, response);
        verify(sesionRepository, never()).save(any(Sesion.class));
    }

    @Test
    void crearDebeTomarPeticionesDelServicioCuandoSesionesDeHoyEsNull() {

        String nombre = "usuarioValido";
        UUID claveGral = UUID.randomUUID();
        Usuario usuarioTest = mock(Usuario.class);
        when(usuarioTest.getId()).thenReturn(1L);

        Licencia licenciaTest = mock(Licencia.class);
        Servicio servicioTest = mock(Servicio.class);
        when(servicioTest.getPeticiones()).thenReturn(DIEZ);
        when(servicioTest.getTipo()).thenReturn(licenciaMulti);
        when(licenciaTest.getId()).thenReturn(1L);
        when(licenciaTest.getServicio()).thenReturn(servicioTest);

        Page<Sesion> pageMock = mock(Page.class);
        when(pageMock.getContent()).thenReturn(null);

        Page<Sesion> sesionesAbiertas = new PageImpl<>(Collections.emptyList());

        when(rqsv025.getRsb019()).thenReturn(rsb019);
        when(rqsv025.getUsuario()).thenReturn(nombre);
        when(rsb019.getClave()).thenReturn(claveGral);
        when(usuarioRepository.findByNombre(nombre))
            .thenReturn(Optional.of(usuarioTest));
        when(licenciaRepository.findByToken(claveGral))
            .thenReturn(licenciaTest);
        when(sesionRepository
                .findByUsuarioAndNow(eq(1L), eq(1L), any()))
            .thenReturn(pageMock);
        when(sesionRepository
                .findByUsuarioAndActive(eq(1L), eq(1L), any()))
            .thenReturn(sesionesAbiertas);

        Sesion nuevaSesion = new Sesion();
        when(sesionRepository.save(any(Sesion.class))).thenReturn(nuevaSesion);
        ResponseEntity<RSB019> expected = ResponseEntity.ok(rsb019);
        when(rsb019.ok(nuevaSesion)).thenReturn(expected);

        ResponseEntity<RSB019> response = sesionBZ.crear(rqsv025);

        assertSame(expected, response);
        verify(sesionRepository).save(any(Sesion.class));
    }

    @Test
    void crearSesionCuandoRestantesMayoresALicenciaMulti() {

        String nombre = "usuarioValido";
        UUID claveGral = UUID.randomUUID();
        Usuario usuarioTest = mock(Usuario.class);
        when(usuarioTest.getId()).thenReturn(1L);

        Licencia licenciaTest = mock(Licencia.class);
        Servicio servicioTest = mock(Servicio.class);
        when(servicioTest.getTipo()).thenReturn(licenciaMulti);
        when(licenciaTest.getId()).thenReturn(1L);
        when(licenciaTest.getServicio()).thenReturn(servicioTest);

        Sesion sesionExistente = mock(Sesion.class);
        when(sesionExistente.getPeticionesRestantes()).thenReturn(CINCO);

        Page<Sesion> sesionesDeHoy = new PageImpl<>(List.of(sesionExistente));
        Page<Sesion> sesionesAbiertas = new PageImpl<>(Collections.emptyList());

        when(rqsv025.getRsb019()).thenReturn(rsb019);
        when(rqsv025.getUsuario()).thenReturn(nombre);
        when(rsb019.getClave()).thenReturn(claveGral);
        when(usuarioRepository.findByNombre(nombre))
            .thenReturn(Optional.of(usuarioTest));
        when(licenciaRepository.findByToken(claveGral))
            .thenReturn(licenciaTest);
        when(sesionRepository
                .findByUsuarioAndNow(eq(1L), eq(1L), any()))
            .thenReturn(sesionesDeHoy);
        when(sesionRepository.findByUsuarioAndActive(
                eq(1L), eq(1L), any()))
            .thenReturn(sesionesAbiertas);

        Sesion nuevaSesion = new Sesion();
        when(sesionRepository.save(any(Sesion.class))).thenReturn(nuevaSesion);
        ResponseEntity<RSB019> expected = ResponseEntity.ok(rsb019);
        when(rsb019.ok(nuevaSesion)).thenReturn(expected);

        ResponseEntity<RSB019> response = sesionBZ.crear(rqsv025);

        assertSame(expected, response);
        verify(sesionRepository).save(any(Sesion.class));
    }

    @Test
    void omitirCierreSesionesCuandoSesionesAbiertasEsNullLicenciaMulti() {

        String nombre = "usuarioValido";
        UUID claveGral = UUID.randomUUID();
        Usuario usuarioTest = mock(Usuario.class);
        when(usuarioTest.getId()).thenReturn(1L);

        Licencia licenciaTest = mock(Licencia.class);
        Servicio servicioTest = mock(Servicio.class);
        when(servicioTest.getTipo()).thenReturn(licenciaMulti);
        when(licenciaTest.getId()).thenReturn(1L);
        when(licenciaTest.getServicio()).thenReturn(servicioTest);

        Sesion sesionExistente = mock(Sesion.class);
        when(sesionExistente.getPeticionesRestantes()).thenReturn(CINCO);
        Page<Sesion> sesionesDeHoy = new PageImpl<>(List.of(sesionExistente));

        Page<Sesion> pageAbiertas = mock(Page.class);
        when(pageAbiertas.getContent()).thenReturn(null);

        when(rqsv025.getRsb019()).thenReturn(rsb019);
        when(rqsv025.getUsuario()).thenReturn(nombre);
        when(rsb019.getClave()).thenReturn(claveGral);
        when(usuarioRepository.findByNombre(nombre))
            .thenReturn(Optional.of(usuarioTest));
        when(licenciaRepository.findByToken(claveGral))
            .thenReturn(licenciaTest);
        when(sesionRepository.findByUsuarioAndNow(
                eq(1L),
                eq(1L),
                any()
        )).thenReturn(sesionesDeHoy);
        when(sesionRepository.findByUsuarioAndActive(
                eq(1L),
                eq(1L),
                any()
        )).thenReturn(pageAbiertas);

        Sesion nuevaSesion = new Sesion();
        when(sesionRepository.save(any(Sesion.class))).thenReturn(nuevaSesion);
        ResponseEntity<RSB019> expected = ResponseEntity.ok(rsb019);
        when(rsb019.ok(nuevaSesion)).thenReturn(expected);

        ResponseEntity<RSB019> response = sesionBZ.crear(rqsv025);

        assertSame(expected, response);
        verify(sesionRepository).save(any(Sesion.class));
    }

    @Test
    void crearDebeTomarPeticionesDelServicioCuandoSesionesDeHoyEsNullMono() {

        String nombre = "usuarioValido";
        UUID claveGral = UUID.randomUUID();
        Usuario usuarioTest = mock(Usuario.class);
        when(usuarioTest.getId()).thenReturn(1L);

        Licencia licenciaTest = mock(Licencia.class);
        Servicio servicioTest = mock(Servicio.class);
        when(servicioTest.getPeticiones()).thenReturn(DIEZ);
        when(servicioTest.getTipo()).thenReturn(licenciaMono);
        when(licenciaTest.getId()).thenReturn(1L);
        when(licenciaTest.getServicio()).thenReturn(servicioTest);

        Page<Sesion> pageMock = mock(Page.class);

        Page<Sesion> sesionesAbiertas = new PageImpl<>(Collections.emptyList());

        when(rqsv025.getRsb019()).thenReturn(rsb019);
        when(rqsv025.getUsuario()).thenReturn(nombre);
        when(rsb019.getClave()).thenReturn(claveGral);
        when(usuarioRepository.findByNombre(nombre))
            .thenReturn(Optional.of(usuarioTest));
        when(licenciaRepository.findByToken(claveGral))
            .thenReturn(licenciaTest);

        Sesion nuevaSesion = new Sesion();
        when(sesionRepository.save(any(Sesion.class))).thenReturn(nuevaSesion);
        ResponseEntity<RSB019> expected = ResponseEntity.ok(rsb019);
        when(rsb019.ok(nuevaSesion)).thenReturn(expected);

        ResponseEntity<RSB019> response = sesionBZ.crear(rqsv025);

        assertSame(expected, response);
        verify(sesionRepository).save(any(Sesion.class));
    }

    @Test
    void crearDebeTomarPeticionesDelServicioCuandoSesionesDeHoyEsNullNada() {

        String nombre = "usuarioValido";
        UUID claveGral = UUID.randomUUID();
        Usuario usuarioTest = mock(Usuario.class);
        when(usuarioTest.getId()).thenReturn(1L);

        Servicio servicioTest = mock(Servicio.class);
        when(servicioTest.getPeticiones()).thenReturn(DIEZ);
        Catalogo tipoDesconocido = mock(Catalogo.class);
        when(licencia.getServicio()).thenReturn(servicioTest);
        when(servicioTest.getTipo()).thenReturn(tipoDesconocido);
        when(licencia.getId()).thenReturn(1L);

        Page<Sesion> pageMock = mock(Page.class);

        Page<Sesion> sesionesAbiertas = new PageImpl<>(Collections.emptyList());

        when(rqsv025.getRsb019()).thenReturn(rsb019);
        when(rqsv025.getUsuario()).thenReturn(nombre);
        when(rsb019.getClave()).thenReturn(claveGral);
        when(usuarioRepository.findByNombre(nombre))
            .thenReturn(Optional.of(usuarioTest));
        when(licenciaRepository.findByToken(claveGral)).thenReturn(licencia);

        Sesion nuevaSesion = new Sesion();
        when(sesionRepository.save(any(Sesion.class))).thenReturn(nuevaSesion);
        ResponseEntity<RSB019> expected = ResponseEntity.ok(rsb019);
        when(rsb019.ok(nuevaSesion)).thenReturn(expected);

        ResponseEntity<RSB019> response = sesionBZ.crear(rqsv025);

        assertSame(expected, response);
        verify(sesionRepository).save(any(Sesion.class));
    }

}
