package com.modelo;

import java.util.List;
import java.util.Locale;

public class ResumenLectura {

	private int total;
	private double suma;
	private Restaurante mejorRestaurante;

	public void anhadir(Restaurante r)
	{
		++total;
		suma = suma + r.getPuntuacion();

		if (mejorRestaurante == null || r.getPuntuacion() > mejorRestaurante.getPuntuacion())
		{
			mejorRestaurante = r;
		}
	}

	private double media()
	{
		double resultado = 0;

		if (total > 0)
		{
			resultado = suma / total;
		}

		return resultado;
	}

	public static ResumenLectura desdeLista(List<Restaurante> lista)
	{
		ResumenLectura resumen = new ResumenLectura();

		for (Restaurante r : lista)
		{
			resumen.anhadir(r);
		}

		return resumen;
	}

	@Override
	public String toString()
	{
		String texto = "Total de restaurantes: " + total;

		if (total > 0)
		{
			texto = texto + "\nPuntuación media: " + String.format(Locale.US, "%.2f", media())
					+ "\nMejor puntuado: " + mejorRestaurante;
		}

		return texto;
	}
}