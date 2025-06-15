package com.tecabix.bz.sesion.dto;

import com.tecabix.db.entity.Catalogo;
import com.tecabix.db.repository.SesionRepository;

/**
 *
 * @author Ramirez Urrutia Angel Abinadi
 */
public class Sesion004BzDTO {

    /**
     * Repositorio para acceder a la entidad Sesion.
     */
    private SesionRepository sesionRepository;

    /**
     * Estado "Eliminado" obtenido desde el catálogo.
     */
    private Catalogo eliminado;

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
