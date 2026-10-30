package com.csv;

import java.util.ArrayList;
import java.util.List;

import com.modelo.Campo;
import com.modelo.Incidencia;
import com.modelo.Restaurante;
import com.modelo.ResultadoValidacion;

public class Validador {

	//CÓDIGOS QUE SALEN EN EL RESUMEN DE CARGA. SI AÑADES UNA REGLA, AÑADE AQUÍ SU CÓDIGO
	public static final String[] CODIGOS_ERROR = {"E01", "E02", "E03", "E04", "E05", "E06", "E07"};
	public static final String[] CODIGOS_AVISO = {"W01", "W02", "W03"};

	//APLICA LAS REGLAS EN EL ORDEN DEL ENUNCIADO. CADA REGLA SOLO SE EJECUTA SI NO HAY ERROR TODAVÍA.
	//PARA AÑADIR UNA REGLA: ESCRIBE SU MÉTODO MÁS ABAJO Y AÑADE SU LLAMADA EN EL SITIO QUE LE TOQUE
	public static ResultadoValidacion validar(List<String> campos, int linea, String original,
	        List<Restaurante> aceptados, int idIgnorar)
	{
	    List<String> valores = new ArrayList<>(campos);
	    List<Incidencia> avisos = new ArrayList<>();
	    Restaurante restaurante = null;

	    Incidencia error = reglaE01(valores, linea, original);
	    if (error == null)
	    {
	        error = reglaE02(valores, linea, original);
	    }
	    if (error == null)
	    {
	        avisoW01(valores, linea, avisos);
	    }
	    if (error == null)
	    {
	        error = reglaE03(valores, linea, original);
	    }
	    if (error == null)
	    {
	        avisoW02(valores, linea, avisos);
	    }
	    if (error == null)
	    {
	        error = reglaE04(valores, linea, original);
	    }
	    if (error == null)
	    {
	        error = reglaE05(valores, linea, original);
	    }
	    if (error == null)
	    {
	        error = reglaE06(valores, linea, original);
	    }
	    if (error == null)
	    {
	        avisoW03(valores, linea, avisos);
	    }
	    if (error == null)
	    {
	        restaurante = Restaurante.desdeTextos(0, valores);
	        error = reglaE07(restaurante, aceptados, idIgnorar, linea, original);
	    }

	    if (error != null)
	    {
	        restaurante = null;
	        avisos = new ArrayList<>();
	    }

	    return new ResultadoValidacion(restaurante, error, avisos);
	}

	//------------------------------- REGLAS ---------------------------------------------//
	//LAS DE ERROR DEVUELVEN LA Incidencia (O null SI LA FILA LA CUMPLE).
	//LAS DE AVISO CORRIGEN EL VALOR EN LA LISTA Y AÑADEN EL AVISO.

	private static Incidencia reglaE01(List<String> valores, int linea, String original)
	{
	    Incidencia error = null;
	    int esperados = Campo.values().length;

	    if (valores.size() != esperados)
	    {
	        error = new Incidencia(linea, "E01",
	                "La fila no tiene " + esperados + " campos (tiene " + valores.size() + ")", original);
	    }
	    return error;
	}

	private static Incidencia reglaE02(List<String> valores, int linea, String original)
	{
	    Incidencia error = null;

	    if (valor(valores, Campo.NOMBRE).isEmpty())
	    {
	        error = new Incidencia(linea, "E02", "El nombre está vacío", original);
	    }
	    return error;
	}

	private static void avisoW01(List<String> valores, int linea, List<Incidencia> avisos)
	{
	    String cp = valor(valores, Campo.CODIGO_POSTAL);
	    String corregido = corregirCodigoPostal(cp);

	    if (!corregido.equals(cp))
	    {
	        avisos.add(new Incidencia(linea, "W01",
	                "Código postal corregido: \"" + cp + "\" -> \"" + corregido + "\"", ""));
	        cambiar(valores, Campo.CODIGO_POSTAL, corregido);
	    }
	}

	private static Incidencia reglaE03(List<String> valores, int linea, String original)
	{
	    Incidencia error = null;
	    String cp = valor(valores, Campo.CODIGO_POSTAL);

	    if (!codigoPostalValido(cp))
	    {
	        error = new Incidencia(linea, "E03", "Código postal inválido: \"" + cp + "\"", original);
	    }
	    return error;
	}

	private static void avisoW02(List<String> valores, int linea, List<Incidencia> avisos)
	{
	    String texto = valor(valores, Campo.PUNTUACION);
	    String corregido = corregirPuntuacion(texto);

	    if (!corregido.equals(texto))
	    {
	        avisos.add(new Incidencia(linea, "W02",
	                "Puntuación con coma decimal corregida: \"" + texto + "\" -> \"" + corregido + "\"", ""));
	        cambiar(valores, Campo.PUNTUACION, corregido);
	    }
	}

	private static Incidencia reglaE04(List<String> valores, int linea, String original)
	{
	    Incidencia error = null;
	    String texto = valor(valores, Campo.PUNTUACION);

	    try
	    {
	        Double.parseDouble(texto);
	    }
	    catch (NumberFormatException e)
	    {
	        error = new Incidencia(linea, "E04", "La puntuación no es un número: \"" + texto + "\"", original);
	    }
	    return error;
	}

	//SOLO SE LLAMA SI E04 HA PASADO, ASÍ QUE parseDouble NO FALLA
	private static Incidencia reglaE05(List<String> valores, int linea, String original)
	{
	    Incidencia error = null;
	    String texto = valor(valores, Campo.PUNTUACION);
	    double puntuacion = Double.parseDouble(texto);

	    if (!(puntuacion >= 0 && puntuacion <= 10))
	    {
	        error = new Incidencia(linea, "E05", "Puntuación fuera de rango: \"" + texto + "\"", original);
	    }
	    return error;
	}

	private static Incidencia reglaE06(List<String> valores, int linea, String original)
	{
	    Incidencia error = null;

	    if (valor(valores, Campo.TIPO_COCINA).isEmpty())
	    {
	        error = new Incidencia(linea, "E06", "El tipo de cocina está vacío", original);
	    }
	    return error;
	}

	private static void avisoW03(List<String> valores, int linea, List<Incidencia> avisos)
	{
	    String cocina = valor(valores, Campo.TIPO_COCINA);
	    String corregido = normalizarCocina(cocina);

	    if (!corregido.equals(cocina))
	    {
	        avisos.add(new Incidencia(linea, "W03",
	                "Tipo de cocina normalizado: \"" + cocina + "\" -> \"" + corregido + "\"", ""));
	        cambiar(valores, Campo.TIPO_COCINA, corregido);
	    }
	}

	private static Incidencia reglaE07(Restaurante candidato, List<Restaurante> aceptados, int idIgnorar,
	        int linea, String original)
	{
	    Incidencia error = null;

	    if (esDuplicado(candidato, aceptados, idIgnorar))
	    {
	        error = new Incidencia(linea, "E07", "Restaurante duplicado: \"" + candidato.getNombre()
	                + "\" en \"" + candidato.getDireccion() + "\"", original);
	    }
	    return error;
	}
	
	//-------------------------------- AYUDAS PARA LEER Y CAMBIAR VALORES --------------------------------//

	private static String valor(List<String> valores, Campo campo)
	{
	    return valores.get(campo.ordinal());
	}

	private static void cambiar(List<String> valores, Campo campo, String nuevo)
	{
	    valores.set(campo.ordinal(), nuevo);
	}

	//------------------------------------ COMPROBACIONES SUELTAS ------------------------------------//
	
	private static boolean soloDigitos(String t)
	{
	    boolean correcto = true;

	    for (int i = 0; i < t.length() && correcto; ++i)
	    {
	        char c = t.charAt(i);

	        if (c < '0' || c > '9')
	        {
	            correcto = false;
	        }
	    }

	    return correcto;
	}
	
	private static String corregirCodigoPostal(String cp)
	{
		String cpCorregido = cp;
		if(cp.length() == 4 && soloDigitos(cp))
		{
			cpCorregido = "0"+cp;
		}
		return cpCorregido;
	}
	
	//si empieza por minuscula devuelve empieza por mayuscula
	private static String normalizarCocina(String t)
	{
	    String inicial = t.substring(0, 1).toUpperCase();
	    String resto = t.substring(1).toLowerCase();

	    return inicial + resto;
	}
	
	private static boolean esDuplicado(Restaurante r, List<Restaurante> aceptados, int idIgnorar)
	{
	    boolean duplicado = false;

	    for (int i = 0; i < aceptados.size() && !duplicado; ++i)
	    {
	        Restaurante existente = aceptados.get(i);

	        if (existente.getId() != idIgnorar && existente.mismoNombreYDireccion(r))
	        {
	            duplicado = true;
	        }
	    }

	    return duplicado;
	}
	
	private static boolean codigoPostalValido(String cp)
	{
		boolean valido = false;
		if(cp.length() == 5 && soloDigitos(cp))
		{
			int provincia = Integer.parseInt(cp.substring(0,2));
			valido = provincia >=1 && provincia <=52;
		}
		
		return valido;
	}
	
	private static String corregirPuntuacion(String t)
	{
		return t.replace(',', '.');
	}
}
