package com.modelo;

import java.util.ArrayList;
import java.util.List;
import com.csv.Validador;
public class ResultadoCarga {

	private static final int ANCHO_ETIQUETA = 30;

	private final List<Restaurante> restaurantes;
	private final List<Incidencia> errores;
	private final List<Incidencia> avisos;
	private final int lineasLeidas;
	private final int lineasVacias;

	public ResultadoCarga(List<Restaurante> restaurantes, List<Incidencia> errores,
			List<Incidencia> avisos, int lineasLeidas, int lineasVacias) {
		this.restaurantes = restaurantes;
		this.errores = errores;
		this.avisos = avisos;
		this.lineasLeidas = lineasLeidas;
		this.lineasVacias = lineasVacias;
	}

	public List<Restaurante> getRestaurantes() {
		return restaurantes;
	}

	public List<Incidencia> getErrores() {
		return errores;
	}

	public List<Incidencia> getAvisos() {
		return avisos;
	}

	public int getLineasLeidas() {
		return lineasLeidas;
	}

	public int getLineasVacias() {
		return lineasVacias;
	}

	public boolean cuadra() {
		return lineasLeidas - lineasVacias == restaurantes.size() + errores.size();
	}

	public String resumen() {
		List<String> lineas = new ArrayList<>();

		lineas.add("=== RESUMEN DE CARGA ===");
		lineas.add(linea("Líneas de datos leídas", String.valueOf(lineasLeidas)));
		lineas.add(linea("Líneas vacías ignoradas", String.valueOf(lineasVacias)));
		lineas.add(linea("Filas válidas", String.valueOf(restaurantes.size())));
		lineas.add(linea("Filas rechazadas",
				errores.size() + " (" + desglose(errores, Validador.CODIGOS_ERROR) + ")"));
		lineas.add(linea("Avisos (filas corregidas)",
				avisos.size() + " (" + desglose(avisos, Validador.CODIGOS_AVISO) + ")"));

		return String.join(System.lineSeparator(), lineas);
	}

	private String linea(String etiqueta, String valor) {
		StringBuilder sb = new StringBuilder(etiqueta).append(' ');

		while (sb.length() < ANCHO_ETIQUETA) {
			sb.append('.');
		}

		return sb.append(' ').append(valor).toString();
	}

	private String desglose(List<Incidencia> lista, String[] codigos) {
		List<String> partes = new ArrayList<>();

		for (String codigo : codigos) {
			partes.add(codigo + ": " + contar(lista, codigo));
		}

		return String.join(", ", partes);
	}

	private int contar(List<Incidencia> lista, String codigo) {
		int total = 0;

		for (Incidencia i : lista) {
			if (i.getCodigo().equals(codigo)) {
				total++;
			}
		}

		return total;
	}
}
