package com.tecabix.bz.sesion.dto;

import com.tecabix.db.entity.Catalogo;
import com.tecabix.db.repository.SesionRepository;

/**
 *
 * @author Ramirez Urrutia Angel Abinadi
 */
public class Sesion004BzDTO {

    private SesionRepository sesionRepository;
	
	private Catalogo eliminado;

	public SesionRepository getSesionRepository() {
		return sesionRepository;
	}

	public void setSesionRepository(SesionRepository sesionRepository) {
		this.sesionRepository = sesionRepository;
	}

	public Catalogo getEliminado() {
		return eliminado;
	}

	public void setEliminado(Catalogo eliminado) {
		this.eliminado = eliminado;
	}
}
