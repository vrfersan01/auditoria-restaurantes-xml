package com.app;

import java.util.Scanner;

public class Teclado {

	private static Scanner sc = new Scanner(System.in);
	
	public static String leerTexto(String mensaje)
	{
		System.out.println(mensaje);
		return sc.nextLine().trim();
	}
	
	public static String leerTextoODefecto(String mensaje, String defecto)
	{
		String resultado = leerTexto(mensaje);

		if (resultado.isEmpty())
		{
			resultado = defecto;
		}
		return resultado;
	}
	
	public static int leerEntero(String mensaje, int min, int max)
	{
		int entero = 0;
		boolean valido = false;

		while (!valido)
		{
			try
			{
				entero = Integer.parseInt(leerTexto(mensaje));
				if (entero >= min && entero <= max)
				{
					valido = true;
				}
				else
				{
					System.out.println("El número debe estar entre " + min + " y " + max + ".");
				}
			}
			catch (NumberFormatException e)
			{
				System.out.println("Número no válido.");
			}
		}
		return entero;
	}
	
	public static double leerDouble(String mensaje)
	{
		double numero = 0;
		boolean valido = false;

		while (!valido)
		{
			try
			{
				numero = Double.parseDouble(leerTexto(mensaje).replace(',', '.'));
				valido = true;
			}
			catch (NumberFormatException e)
			{
				System.out.println("Número no válido.");
			}
		}
		return numero;
	}
	
	public static boolean confirmar(String mensaje)
	{
		String respuesta = "";
		
		boolean confirmacion = false;
		while (!respuesta.equals("s") && !respuesta.equals("n"))
		{
			respuesta = leerTexto(mensaje + " (s/n): ").toLowerCase();
			if (!respuesta.equals("s") && !respuesta.equals("n"))
			{
				System.out.println("Responde s o n.");
			}
		}
		if (respuesta.equals("s"))
		{
		    confirmacion = true;
		}
		return confirmacion;
	}

	public static void cerrar()
	{
		sc.close();
	}
}


 