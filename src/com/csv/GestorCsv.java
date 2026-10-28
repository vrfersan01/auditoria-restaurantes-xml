package com.csv;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

import com.ficheros.GestorFicheros;
import com.modelo.Incidencia;
import com.modelo.Restaurante;
import com.modelo.ResultadoCarga;
import com.modelo.ResultadoValidacion;

public class GestorCsv {

	public static ResultadoCarga cargar(String ruta) throws IOException
	{
	    List<Restaurante> restaurantes = new ArrayList<>();
	    List<Incidencia> errores = new ArrayList<>();
	    List<Incidencia> avisos = new ArrayList<>();
	    int lineasLeidas = 0;
	    int lineasVacias = 0;
	    int numeroLinea = 1;

	    try (BufferedReader br = new BufferedReader(
	            new InputStreamReader(new FileInputStream(ruta), "UTF-8")))
	    {
	        br.readLine();

	        String texto;

	        while ((texto = br.readLine()) != null)
	        {
	            numeroLinea++;
	            lineasLeidas++;

	            if (texto.trim().isEmpty())
	            {
	                lineasVacias++;
	            }
	            else
	            {
	                List<String> campos = AnalizadorCsv.partirLinea(texto);
	                ResultadoValidacion resultado =
	                        Validador.validar(campos, numeroLinea, texto, restaurantes, -1);

	                if (resultado.esValido())
	                {
	                    Restaurante restaurante = resultado.getRestaurante();
	                    restaurante.setId(restaurantes.size() + 1);
	                    restaurantes.add(restaurante);
	                    avisos.addAll(resultado.getAvisos());
	                }
	                else
	                {
	                    errores.add(resultado.getError());
	                }
	            }
	        }
	    }

	    return new ResultadoCarga(restaurantes, errores, avisos, lineasLeidas, lineasVacias);
	}
	
	public static void escribir(List<Restaurante> lista, String ruta) throws IOException
	{
	    try (BufferedWriter bw = new BufferedWriter(
	            new OutputStreamWriter(new FileOutputStream(ruta), "UTF-8")))
	    {
	        bw.write(AnalizadorCsv.CABECERA);
	        bw.newLine();

	        for (Restaurante r : lista)
	        {
	            bw.write(AnalizadorCsv.formatearLinea(r));
	            bw.newLine();
	        }
	    }
	}

	public static void escribirIncidencias(List<Incidencia> lista, String ruta) throws IOException
	{
	    try (BufferedWriter bw = new BufferedWriter(
	            new OutputStreamWriter(new FileOutputStream(ruta), "UTF-8")))
	    {
	        for (Incidencia i : lista)
	        {
	            bw.write(i.toString());
	            bw.newLine();
	        }
	    }
	}
	
	//SI ALGO FALLA LANZA IOException CON UN MENSAJE QUE DICE QUÉ PASO HA FALLADO
	public static void guardarSeguro(List<Restaurante> lista, String ruta) throws IOException
	{
	    File original = new File(ruta);
	    File temporal = new File(ruta + ".tmp");

	    //a. contenido nuevo completo
	    escribir(lista, temporal.getPath());

	    if (original.exists())
	    {
	        //b. copia de seguridad
	        GestorFicheros.copiaConMarcaTiempo(original, "copias");

	        //c. borrar el antiguo
	        if (!original.delete())
	        {
	            throw new IOException("No se ha podido borrar " + original.getPath()
	                    + ". Los datos nuevos están en " + temporal.getPath());
	        }
	    }

	    //c. renombrar el .tmp
	    if (!temporal.renameTo(original))
	    {
	        throw new IOException("No se ha podido renombrar " + temporal.getPath()
	                + ". Los datos nuevos están en ese fichero y hay copia del antiguo en copias/");
	    }
	}
	
	
}
