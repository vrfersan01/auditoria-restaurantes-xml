package com.modelo;

public class Incidencia {

	private final int linea;
	private final String codigo;
	private final String mensaje;
	private final String contenido;

	public Incidencia(int linea, String codigo, String mensaje, String contenido) {
		this.linea = linea;
		this.codigo = codigo;
		this.mensaje = mensaje;
		this.contenido = contenido;
	}

	public int getLinea() {
		return linea;
	}

	public String getCodigo() {
		return codigo;
	}

	public String getMensaje() {
		return mensaje;
	}

	public String getContenido() {
		return contenido;
	}

	@Override
	public String toString() {
		String texto = "Línea " + linea + " | " + codigo + " | " + mensaje;

		if (!contenido.isEmpty()) {
			texto = texto + " | " + contenido;
		}

		return texto;
	}
}
