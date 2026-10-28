package com.modelo;

import java.util.ArrayList;
import java.util.List;

public class Filtro {

	//TEXTO DE CADA CRITERIO. SU NÚMERO EN EL MENÚ ES LA POSICIÓN + 1 Y ES EL case DE cumpleCriterio.
	//PARA AÑADIR UN CRITERIO: UN TEXTO AL FINAL DE ESTE ARRAY Y SU case EN cumpleCriterio
	public static final String[] CRITERIOS = {
		"Código postal empieza por",	// 1
		"Tipo de cocina",				// 2
		"Puntuación mayor que"			// 3
	};

	//CADA CONDICIÓN ES UN CRITERIO (1, 2, 3...) Y EL VALOR QUE ESCRIBIÓ EL USUARIO, EN LA MISMA POSICIÓN
	private final List<Integer> criterios = new ArrayList<>();
	private final List<String> valores = new ArrayList<>();

	public void anhadir(int criterio, String valor) {
		criterios.add(criterio);
		valores.add(valor);
	}

	//CUMPLE SI CUMPLE TODAS LAS CONDICIONES (Y). SIN CONDICIONES, CUMPLE SIEMPRE
	public boolean cumple(Restaurante r) {
		boolean ok = true;

		for (int i = 0; i < criterios.size() && ok; i++) {
			ok = cumpleCriterio(r, criterios.get(i), valores.get(i));
		}

		return ok;
	}

	private static boolean cumpleCriterio(Restaurante r, int criterio, String valor) {
		boolean ok = true;

		switch (criterio) {
			case 1 -> ok = r.getCodigoPostal().startsWith(valor);
			case 2 -> ok = r.getTipoCocina().equalsIgnoreCase(valor);
			case 3 -> ok = r.getPuntuacion() > Double.parseDouble(valor);
		}

		return ok;
	}

	public List<Restaurante> aplicar(List<Restaurante> lista) {
		List<Restaurante> resultado = new ArrayList<>();

		for (Restaurante r : lista) {
			if (cumple(r)) {
				resultado.add(r);
			}
		}

		return resultado;
	}

	public String descripcion() {
		List<String> partes = new ArrayList<>();
		String texto;

		for (int i = 0; i < criterios.size(); i++) {
			partes.add(CRITERIOS[criterios.get(i) - 1] + " " + valores.get(i));
		}

		if (partes.isEmpty()) {
			texto = "Sin filtro";
		} else {
			texto = String.join(" y ", partes);
		}

		return texto;
	}
}