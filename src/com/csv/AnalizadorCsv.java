package com.csv;

import java.util.ArrayList;
import java.util.List;

import com.modelo.Campo;
import com.modelo.Restaurante;

public class AnalizadorCsv {

	public static final String CABECERA = cabecera();
	
	public static List<String> partirLinea(String linea)
	{
		List<String>datos = new ArrayList<>();
		StringBuilder sb = new StringBuilder();
		boolean entreComillas = false;
		char anterior = ',';  // cualquier carácter que no sea comilla
		
		//Prueba:
		//"Bar ""El Rincón""","Plaza de España, 3",28013,Tapas,8.2
				
		for(char c:linea.toCharArray())
		{
			if(c=='"')
			{
				if(entreComillas)
				{
					entreComillas = false;
				}
				else
				{
					if(anterior=='"')
					{
						sb.append(c);
					}
					entreComillas = true;
				}
			}
			else if(c==',' && !entreComillas)
			{
				datos.add(sb.toString().trim());
				sb.setLength(0);
			}
			else
			{
				sb.append(c);
			}
			anterior = c;
		}
		datos.add(sb.toString().trim());
		return datos;
	}
	
	private static String cabecera()
	{
		List<String> columnas = new ArrayList<>();

		for (Campo c : Campo.values())
		{
			columnas.add(c.getColumnaCsv());
		}

		return String.join(",", columnas);
	}
	
	public static String formatearCampo(String campo)
	{
		String texto = campo;
		if (campo.contains(",") || campo.contains("\""))
		{
			texto = "\"" + campo.replace("\"", "\"\"") + "\"";
		}
		
		return texto;
	}
	
	public static String formatearLinea(Restaurante r)
	{
		List<String> campos = new ArrayList<>();

		for (String texto : r.aTextos())
		{
			campos.add(formatearCampo(texto));
		}

		return String.join(",", campos);
	}
}
