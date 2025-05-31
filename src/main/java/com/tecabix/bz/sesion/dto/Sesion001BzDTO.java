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

	private UsuarioRepository usuarioRepository;
	private LicenciaRepository licenciaRepository;
	private SesionRepository sesionRepository;
	
	private Catalogo licenciaMulti;
	private Catalogo licenciaMono;
	private Catalogo licenciaMultiMono;
	
	private Catalogo activo;
	private Catalogo eliminado;

	public UsuarioRepository getUsuarioRepository() {
		return usuarioRepository;
	}
	public void setUsuarioRepository(UsuarioRepository usuarioRepository) {
		this.usuarioRepository = usuarioRepository;
	}
	public LicenciaRepository getLicenciaRepository() {
		return licenciaRepository;
	}
	public void setLicenciaRepository(LicenciaRepository licenciaRepository) {
		this.licenciaRepository = licenciaRepository;
	}
	public SesionRepository getSesionRepository() {
		return sesionRepository;
	}
	public void setSesionRepository(SesionRepository sesionRepository) {
		this.sesionRepository = sesionRepository;
	}
	public Catalogo getLicenciaMulti() {
		return licenciaMulti;
	}
	public void setLicenciaMulti(Catalogo licenciaMulti) {
		this.licenciaMulti = licenciaMulti;
	}
	public Catalogo getLicenciaMono() {
		return licenciaMono;
	}
	public void setLicenciaMono(Catalogo licenciaMono) {
		this.licenciaMono = licenciaMono;
	}
	public Catalogo getLicenciaMultiMono() {
		return licenciaMultiMono;
	}
	public void setLicenciaMultiMono(Catalogo licenciaMultiMono) {
		this.licenciaMultiMono = licenciaMultiMono;
	}
	public Catalogo getActivo() {
		return activo;
	}
	public void setActivo(Catalogo activo) {
		this.activo = activo;
	}
	public Catalogo getEliminado() {
		return eliminado;
	}
	public void setEliminado(Catalogo eliminado) {
		this.eliminado = eliminado;
	}
}
