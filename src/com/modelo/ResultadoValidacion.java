package com.modelo;

import java.util.List;

public class ResultadoValidacion {

	private final Restaurante restaurante;
	private final Incidencia error;
	private final List<Incidencia> avisos;

	public ResultadoValidacion(Restaurante restaurante, Incidencia error, List<Incidencia> avisos) {
		this.restaurante = restaurante;
		this.error = error;
		this.avisos = avisos;
	}

	public Restaurante getRestaurante() {
		return restaurante;
	}

	public Incidencia getError() {
		return error;
	}

	public List<Incidencia> getAvisos() {
		return avisos;
	}

	public boolean esValido() {
		return error == null;
	}
}
