package com.ficheros;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;

import java.io.FileOutputStream;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class GestorFicheros {
	
	public static List<String> prepararEntorno()
	{
		//PARA AÑADIR CARPETAS SOLO CAMBIAR EL ARRAY
	    List<String> mensajes = new ArrayList<>();
	    String[] carpetas = {"salida", "errores", "copias"};

	    for (String nombre : carpetas)
	    {
	        mensajes.add(crearCarpeta(nombre));
	    }

	    return mensajes;
	}

	//CREA LA CARPETA SI SE PUEDE Y ENVIA LA LISTA DE MENSAJES
	private static String crearCarpeta(String nombre)
	{
	    File carpeta = new File(nombre);
	    String mensaje;

	    if (carpeta.isDirectory())
	    {
	        mensaje = "La carpeta " + nombre + " ya existe";
	    }
	    else if (carpeta.exists())
	    {
	        mensaje = "Existe un fichero llamado " + nombre + " y no se puede crear la carpeta";
	    }
	    else if (carpeta.mkdir())
	    {
	        mensaje = "Se ha creado la carpeta " + nombre;
	    }
	    else
	    {
	        mensaje = "No se ha podido crear la carpeta " + nombre;
	    }

	    return mensaje;
	}
	
	public static String infoFichero(File f)
	{
	    String datos;

	    if (!f.exists())
	    {
	        datos = "No existe el archivo " + f.getName() + " en la ruta " + f.getPath();
	    }
	    else if (!f.isFile())
	    {
	        datos = f.getPath() + " existe pero no es un fichero";
	    }
	    else
	    {
	        datos = "Nombre de archivo: " + f.getName()
	                + ", Tamaño (en bytes): " + f.length()
	                + ", Permiso de lectura: " + f.canRead()
	                + ", Permiso de escritura: " + f.canWrite();
	    }

	    return datos;
	}
	
	public static void copiar(File origen, File destino) throws IOException
	{
	    try (FileInputStream entrada = new FileInputStream(origen);
	         FileOutputStream salida = new FileOutputStream(destino))
	    {
	        byte[] buffer = new byte[4096];
	        int leidos;

	        while ((leidos = entrada.read(buffer)) != -1)
	        {
	            salida.write(buffer, 0, leidos);
	        }
	    }
	}

	public static File copiaConMarcaTiempo(File origen, String carpeta) throws IOException
	{
	    String nombre = origen.getName();
	    String base = nombre;
	    String extension = "";
	    int punto = nombre.lastIndexOf('.');

	    if (punto != -1)
	    {
	        base = nombre.substring(0, punto); //corta desde el principio hasta punto
	        extension = nombre.substring(punto);//corta desde punto hasta el final
	    }

	    File destino = new File(carpeta, base + "_" + marcaTiempo() + extension);
	    copiar(origen, destino);

	    return destino;
	}

	private static String marcaTiempo()
	{
	    DateTimeFormatter formato = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
	    return LocalDateTime.now().format(formato);
	}

	public static void escribirTexto(String ruta, String texto) throws IOException
	{
	    escribirTexto(ruta, texto, false);
	}

	//CON anhadir = true ESCRIBE AL FINAL DEL FICHERO SIN BORRAR LO QUE YA TENÍA (MODO APPEND)
	public static void escribirTexto(String ruta, String texto, boolean anhadir) throws IOException
	{
	    try (BufferedWriter bw = new BufferedWriter(
	            new OutputStreamWriter(new FileOutputStream(ruta, anhadir), "UTF-8")))
	    {
	        bw.write(texto);
	    }
	}

	//DEVUELVE TODAS LAS LÍNEAS DE UN FICHERO DE TEXTO
	public static List<String> leerLineas(String ruta) throws IOException
	{
	    List<String> lineas = new ArrayList<>();

	    try (BufferedReader br = new BufferedReader(
	            new InputStreamReader(new FileInputStream(ruta), "UTF-8")))
	    {
	        String linea;

	        while ((linea = br.readLine()) != null)
	        {
	            lineas.add(linea);
	        }
	    }

	    return lineas;
	}
	
	public static ResultadoComparacion comparar(String rutaA, String rutaB) throws IOException {
	    int diferencias = 0;
	    int primeraLinea = 0;
	    String esperado = null;
	    String obtenido = null;

	    try (BufferedReader lectorA = new BufferedReader(
	                 new InputStreamReader(new FileInputStream(rutaA), "UTF-8"));
	         BufferedReader lectorB = new BufferedReader(
	                 new InputStreamReader(new FileInputStream(rutaB), "UTF-8"))) {

	        String lineaA = lectorA.readLine();
	        String lineaB = lectorB.readLine();
	        int numero = 1;

	        while (lineaA != null || lineaB != null) {
	            boolean distintas = lineaA == null || lineaB == null || !lineaA.equals(lineaB);
	            if (distintas) {
	                diferencias++;
	                if (primeraLinea == 0) {
	                    primeraLinea = numero;
	                    esperado = lineaA;
	                    obtenido = lineaB;
	                }
	            }
	            lineaA = lectorA.readLine();
	            lineaB = lectorB.readLine();
	            numero++;
	        }
	    }
	    return new ResultadoComparacion(diferencias, primeraLinea, esperado, obtenido);
	}
	
	
	
	
}
