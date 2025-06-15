package com.tecabix.bz.sesion.dto;

import com.tecabix.db.entity.Catalogo;
import com.tecabix.db.repository.LicenciaRepository;
import com.tecabix.db.repository.SesionRepository;
import com.tecabix.db.repository.UsuarioRepository;

/**
 *
 * @author Ramirez Urrutia Angel Abinadi
 */
public class Sesion001BzDTO {

    /**
     * Repositorio para acceder a la entidad Usuario.
     */
    private UsuarioRepository usuarioRepository;

    /**
     * Repositorio para acceder a la entidad Licencia.
     */
    private LicenciaRepository licenciaRepository;

    /**
     * Repositorio para acceder a la entidad Sesion.
     */
    private SesionRepository sesionRepository;

    /**
     * Catálogo de licencia multiusuario.
     */
    private Catalogo licenciaMulti;

    /**
     * Catálogo de licencia monousuario.
     */
    private Catalogo licenciaMono;

    /**
     * Catálogo de licencia multimonousuario.
     */
    private Catalogo licenciaMultiMono;

    /**
     * Estado "Activo" obtenido desde el catálogo.
     */
    private Catalogo activo;

    /**
     * Estado "Eliminado" obtenido desde el catálogo.
     */
    private Catalogo eliminado;

    /**
     * Obtiene el repositorio de usuarios.
     *
     * @return el repositorio de usuarios.
     */
    public UsuarioRepository getUsuarioRepository() {
        return usuarioRepository;
    }

    /**
     * Establece el repositorio de usuarios.
     *
     * @param repository el repositorio de usuarios a establecer.
     */
    public void setUsuarioRepository(final UsuarioRepository repository) {
        this.usuarioRepository = repository;
    }

    /**
     * Obtiene el repositorio de licencias.
     *
     * @return el repositorio de licencias.
     */
    public LicenciaRepository getLicenciaRepository() {
        return licenciaRepository;
    }

    /**
     * Establece el repositorio de licencias.
     *
     * @param repository el repositorio de licencias a establecer.
     */
    public void setLicenciaRepository(final LicenciaRepository repository) {
        this.licenciaRepository = repository;
    }

    /**
     * Obtiene el repositorio de sesiones.
     *
     * @return el repositorio de sesiones.
     */
    public SesionRepository getSesionRepository() {
        return sesionRepository;
    }

    /**
     * Establece el repositorio de sesiones.
     *
     * @param repository el repositorio de sesiones a establecer.
     */
    public void setSesionRepository(final SesionRepository repository) {
        this.sesionRepository = repository;
    }

    /**
     * Obtiene el catálogo de licencia multiusuario.
     *
     * @return el catálogo de licencia multiusuario.
     */
    public Catalogo getLicenciaMulti() {
        return licenciaMulti;
    }

    /**
     * Establece el catálogo de licencia multiusuario.
     *
     * @param licencia el catálogo de licencia multiusuario a establecer.
     */
    public void setLicenciaMulti(final Catalogo licencia) {
        this.licenciaMulti = licencia;
    }

    /**
     * Obtiene el catálogo de licencia monousuario.
     *
     * @return el catálogo de licencia monousuario.
     */
    public Catalogo getLicenciaMono() {
        return licenciaMono;
    }

    /**
     * Establece el catálogo de licencia monousuario.
     *
     * @param licencia el catálogo de licencia monousuario a establecer.
     */
    public void setLicenciaMono(final Catalogo licencia) {
        this.licenciaMono = licencia;
    }

    /**
     * Obtiene el catálogo de licencia multimonousuario.
     *
     * @return el catálogo de licencia multimonousuario.
     */
    public Catalogo getLicenciaMultiMono() {
        return licenciaMultiMono;
    }

    /**
     * Establece el catálogo de licencia multimonousuario.
     *
     * @param licencia el catálogo de licencia multimonousuario
     *        a establecer.
     */
    public void setLicenciaMultiMono(final Catalogo licencia) {
        this.licenciaMultiMono = licencia;
    }

    /**
     * Obtiene el estado "Activo" del catálogo.
     *
     * @return el estado "Activo".
     */
    public Catalogo getActivo() {
        return activo;
    }

    /**
     * Establece el estado "Activo" del catálogo.
     *
     * @param estatus el estado "Activo" a establecer.
     */
    public void setActivo(final Catalogo estatus) {
        this.activo = estatus;
    }

    /**
     * Obtiene el estado "Eliminado" del catálogo.
     *
     * @return el estado "Eliminado".
     */
    public Catalogo getEliminado() {
        return eliminado;
    }

    /**
     * Establece el estado "Eliminado" del catálogo.
     *
     * @param estatus el estado "Eliminado" a establecer.
     */
    public void setEliminado(final Catalogo estatus) {
        this.eliminado = estatus;
    }

}
