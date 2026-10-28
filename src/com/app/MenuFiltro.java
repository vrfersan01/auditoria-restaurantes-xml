package com.app;

import com.modelo.Filtro;

public class MenuFiltro {

	//LOS NÚMEROS SE CALCULAN SOLOS: PRIMERO LOS CRITERIOS DE Filtro, LUEGO COMBINACIÓN Y SIN FILTRO
	public static Filtro pedir(boolean permitirSinFiltro)
	{
		Filtro filtro = new Filtro();
		int criterios = Filtro.CRITERIOS.length;
		int combinacion = criterios + 1;
		int maximo = combinacion;

		if (permitirSinFiltro)
		{
			maximo = combinacion + 1;
		}

		mostrarOpciones(permitirSinFiltro);
		int opcion = Teclado.leerEntero("Opción de filtro: ", 1, maximo);

		if (opcion <= criterios)
		{
			pedirCriterio(filtro, opcion);
		}
		else if (opcion == combinacion)
		{
			pedirCombinacion(filtro);
		}

		return filtro;
	}

	private static void mostrarOpciones(boolean permitirSinFiltro)
	{
		int criterios = Filtro.CRITERIOS.length;

		for (int i = 0; i < criterios; i++)
		{
			System.out.println((i + 1) + ". " + Filtro.CRITERIOS[i]);
		}
		System.out.println((criterios + 1) + ". Combinación de dos condiciones");

		if (permitirSinFiltro)
		{
			System.out.println((criterios + 2) + ". Sin filtro (todos)");
		}
	}

	private static void pedirCombinacion(Filtro filtro)
	{
		int criterios = Filtro.CRITERIOS.length;
		int primero = Teclado.leerEntero("Primer criterio (1-" + criterios + "): ", 1, criterios);
		int segundo = Teclado.leerEntero("Segundo criterio (1-" + criterios + "): ", 1, criterios);

		while (segundo == primero)
		{
			System.out.println("Tiene que ser distinto del primero.");
			segundo = Teclado.leerEntero("Segundo criterio (1-" + criterios + "): ", 1, criterios);
		}

		pedirCriterio(filtro, primero);
		pedirCriterio(filtro, segundo);
	}

	//PIDE EL VALOR DEL CRITERIO. LOS CRITERIOS NUMÉRICOS SE LEEN CON leerDouble PARA QUE NO FALLEN
	private static void pedirCriterio(Filtro filtro, int criterio)
	{
		String pregunta = Filtro.CRITERIOS[criterio - 1] + ": ";
		String valor;

		if (criterio == 3)
		{
			valor = String.valueOf(Teclado.leerDouble(pregunta));
		}
		else
		{
			valor = Teclado.leerTexto(pregunta);
		}

		filtro.anhadir(criterio, valor);
	}
}