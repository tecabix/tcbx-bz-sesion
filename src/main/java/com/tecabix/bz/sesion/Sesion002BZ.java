package com.tecabix.bz.sesion;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import com.tecabix.db.entity.Sesion;
import com.tecabix.db.repository.SesionRepository;
import com.tecabix.res.b.RSB018;
import com.tecabix.sv.rq.RQSV026;

/**
 *
 * @author Ramirez Urrutia Angel Abinadi
 */
public class Sesion002BZ {

    /**
     * Repositorio para acceder a la entidad Sesion.
     */
    private final SesionRepository sesionRepository;

    /**
     * Mensaje filtro no válido.
     */
    private static final String EL_TIPO_DE_FILTRO_NO_ES_VALIDO;

    static {
        EL_TIPO_DE_FILTRO_NO_ES_VALIDO = "El tipo de filtro no es válido.";
    }

    /**
     * Constructor que inicializa la clase {@code Sesion002BZ} con el
     * repositoriode sesiones.
     *
     * @param repository Repositorio utilizado para acceder a l
     *                         entidad {@code Sesion}.
     */
    public Sesion002BZ(final SesionRepository repository) {
        this.sesionRepository = repository;
    }

    /**
     * Método para crear una nueva sesión de usuario.
     *
     * @param rqsv026 datos de búsqueda
     * @return {@link ResponseEntity} con un objeto {@link RSB019} que contiene
     *         información para consultar la sesión del usuario.
     */
    public ResponseEntity<RSB018> buscar(final RQSV026 rqsv026) {

        RSB018 rsb018 = rqsv026.getRsb018();
        byte elementos = rqsv026.getElementos();
        short pagina = rqsv026.getPagina();
        Optional<String> texto = rqsv026.getTexto();
        String tipo = rqsv026.getTipo();

        Pageable pageable = PageRequest.of(pagina, elementos);
        if (texto.isEmpty()) {
            return rsb018.ok(sesionRepository.findByActive(pageable));
        }

        StringBuilder text = new StringBuilder("%");
        text.append(texto);
        text.append("%");
        String txt = text.toString();
        Page<Sesion> usuario;
        usuario = sesionRepository.findByActiveAndLikeUsuario(txt, pageable);
        Page<Sesion> licencia;
        licencia = sesionRepository.findByActiveAndLikeLicencia(txt, pageable);
        Page<Sesion> servicio;
        servicio = sesionRepository.findByActiveAndLikeServicio(txt, pageable);
        switch (tipo) {
        case "USUARIO":
            return rsb018.ok(usuario);
        case "LICENCIA":
            return rsb018.ok(licencia);
        case "SERVICIO":
            return rsb018.ok(servicio);
        default:
            return rsb018.badRequest(EL_TIPO_DE_FILTRO_NO_ES_VALIDO);
        }
    }
}
