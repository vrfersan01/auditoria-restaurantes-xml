package com.app;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.xml.sax.SAXException;

import com.csv.GestorCsv;
import com.csv.Validador;
import com.ficheros.GestorFicheros;
import com.ficheros.ResultadoComparacion;
import com.informes.Estadisticas;
import com.informes.Rendimiento;
import com.modelo.Campo;
import com.modelo.Filtro;
import com.modelo.Incidencia;
import com.modelo.Restaurante;
import com.modelo.ResultadoCarga;
import com.modelo.ResultadoValidacion;
import com.modelo.ResumenLectura;
import com.xml.GestorDom;
import com.xml.GestorSax;

public class Main {

	private static List<Restaurante> restaurantes = new ArrayList<>();

	private static final String RUTA_SUCIO = "datos/restaurantes_sucio.csv";
	private static final String RUTA_LIMPIO = "salida/restaurantes_limpio.csv";
	private static final String RUTA_ERRORES = "errores/errores.txt";
	private static final String RUTA_AVISOS = "errores/avisos.txt";
	private static final String RUTA_XML = "salida/Restaurantes.xml";
	private static final String RUTA_CSV_XML = "salida/restaurantes_desde_xml.csv";
	private static final String RUTA_ESTADISTICAS = "salida/estadisticas.txt";

	private static final String RUTA_GRANDE = "datos/restaurantes_grande.csv";
	private static final String RUTA_XML_GRANDE = "salida/grande.xml";
	private static final String RUTA_RENDIMIENTO = "salida/rendimiento.txt";

	//PARA AÑADIR UNA OPCIÓN: SU TEXTO AL FINAL DE OPCIONES Y SU case EN ejecutarMenu//
	//SI NECESITA LOS DATOS CARGADOS, AÑADE SU NÚMERO A NECESITAN_DATOS//
	private static final String[] OPCIONES = {
		"Preparar entorno y hacer copia de seguridad",	// 1
		"Cargar y limpiar CSV",							// 2
		"Consultar restaurantes",						// 3
		"Añadir restaurante",							// 4
		"Modificar restaurante",						// 5
		"Eliminar restaurante",							// 6
		"Exportar a XML (DOM)",							// 7
		"Leer XML con DOM",								// 8
		"Leer XML con SAX",								// 9
		"Convertir XML a CSV y verificar integridad",	// 10
		"Estadísticas",									// 11
		"Comparar rendimiento DOM vs SAX"				// 12
	};

	private static final int[] NECESITAN_DATOS = {3, 4, 5, 6, 7, 11};

	public static void main(String[] args) {

		boolean salir = false;
		while (!salir)
		{
			mostrarMenu();
			int opcion = Teclado.leerEntero("Opción: ", 0, OPCIONES.length);
			try
			{
				salir = ejecutarMenu(opcion);
			}
			catch (IOException e)
			{
				System.out.println("Error de lectura o escritura: " + e.getMessage());
			}
			catch (SAXException | ParserConfigurationException e)
			{
				System.out.println("Error al procesar el XML: " + e.getMessage());
			}
			catch (TransformerException e)
			{
				System.out.println("Error al guardar el XML: " + e.getMessage());
			}
			catch (Exception e)
			{
				System.out.println("Error inesperado: " + e.getMessage());
			}
		}

		//CIERRA EL SCANNER
		Teclado.cerrar();
	}

	private static void mostrarMenu()
	{
		System.out.println("\n===== GESTOR DE RESTAURANTES =====");
		for (int i = 0; i < OPCIONES.length; i++)
		{
			System.out.println((i + 1) + ". " + OPCIONES[i]);
		}
		System.out.println("0. Salir");
	}

	private static boolean ejecutarMenu(int opcion)
			throws IOException, SAXException, ParserConfigurationException, TransformerException
	{
		boolean salir = false;

		if (opcion == 0)
		{
			salir = true;
		}
		else if (necesitaDatos(opcion) && !hayDatos())
		{
			System.out.println("Primero carga el CSV con la opción 2.");
		}
		else
		{
			switch (opcion)
			{
			case 1 -> opcionEntorno();
			case 2 -> opcionCargar();
			case 3 -> opcionConsultar();
			case 4 -> opcionAnhadir();
			case 5 -> opcionModificar();
			case 6 -> opcionEliminar();
			case 7 -> opcionExportar();
			case 8 -> opcionLeerDom();
			case 9 -> opcionLeerSax();
			case 10 -> opcionIntegridad();
			case 11 -> opcionEstadisticas();
			case 12 -> opcionRendimiento();
			}
		}
		return salir;
	}

	private static boolean hayDatos()
	{
		return !restaurantes.isEmpty();
	}

	private static boolean necesitaDatos(int opcion)
	{
		boolean necesita = false;

		for (int i = 0; i < NECESITAN_DATOS.length && !necesita; i++)
		{
			if (NECESITAN_DATOS[i] == opcion)
			{
				necesita = true;
			}
		}

		return necesita;
	}

	// ===================================== OPCIÓN 1 ===================================== //

	private static void opcionEntorno() throws IOException
	{
		List<String> mensajes = GestorFicheros.prepararEntorno();
		for (String s : mensajes)
		{
			System.out.println(s);
		}

		File csv = new File(RUTA_SUCIO);
		System.out.println(GestorFicheros.infoFichero(csv));

		//HACE LA COPIA DEL CSV "SUCIO" SI SE PUEDE. PARA COPIAR OTRO REPETIR PROCESO
		if (csv.isFile() && csv.canRead())
		{
			File copia = GestorFicheros.copiaConMarcaTiempo(csv, "copias");
			System.out.println("Copia creada: " + copia.getPath());

			if (copia.length() == csv.length())
			{
				System.out.println("Copia verificada: mismo tamaño");
			}
			else
			{
				System.out.println("ERROR: la copia tiene distinto tamaño");
			}
		}
		else
		{
			System.out.println("No se puede hacer la copia: falta el fichero o no se puede leer");
		}
	}

	// ===================================== OPCIÓN 2 ===================================== //

	private static void opcionCargar() throws IOException
	{
		GestorFicheros.prepararEntorno();

		ResultadoCarga resultado = GestorCsv.cargar(RUTA_SUCIO);
		restaurantes = resultado.getRestaurantes();

		GestorCsv.escribir(restaurantes, RUTA_LIMPIO);
		GestorCsv.escribirIncidencias(resultado.getErrores(), RUTA_ERRORES);
		GestorCsv.escribirIncidencias(resultado.getAvisos(), RUTA_AVISOS);

		System.out.println(resultado.resumen());

		if (resultado.cuadra())
		{
			System.out.println("Comprobación correcta: leídas - vacías = válidas + rechazadas");
		}
		else
		{
			System.out.println("ERROR: las filas no cuadran, revisa el programa");
		}
	}

	// ===================================== OPCIÓN 3 ===================================== //

	private static void opcionConsultar()
	{
		Filtro filtro = MenuFiltro.pedir(false);
		List<Restaurante> resultado = filtro.aplicar(restaurantes);

		//ORDENA DE MAYOR A MENOR PUNTUACIÓN
		resultado.sort((a, b) -> Double.compare(b.getPuntuacion(), a.getPuntuacion()));

		System.out.println("Filtro: " + filtro.descripcion());
		for (Restaurante r : resultado)
		{
			System.out.println(r);
		}
		System.out.println("Coincidencias: " + resultado.size());
	}

	// ============================ OPCIONES 4, 5 Y 6 (MANTENIMIENTO) ============================ //

	private static void opcionAnhadir()
	{
		Restaurante nuevo = pedirValido(null, -1);

		if (nuevo != null)
		{
			nuevo.setId(siguienteId());
			restaurantes.add(nuevo);

			if (guardarCambios())
			{
				System.out.println("Restaurante añadido con id " + nuevo.getId());
			}
			else
			{
				restaurantes.remove(nuevo);
				System.out.println("No se ha añadido nada.");
			}
		}
	}

	private static void opcionModificar()
	{
		int id = Teclado.leerEntero("Id del restaurante a modificar: ", 1, Integer.MAX_VALUE);
		Restaurante antiguo = buscarPorId(id);

		if (antiguo == null)
		{
			System.out.println("No existe ningún restaurante con id " + id);
		}
		else
		{
			System.out.println(antiguo);
			System.out.println("Pulsa Intro para dejar un campo como está.");
			Restaurante nuevo = pedirValido(antiguo, id);

			if (nuevo != null)
			{
				nuevo.setId(id);
				int indice = restaurantes.indexOf(antiguo);
				restaurantes.set(indice, nuevo);

				if (guardarCambios())
				{
					System.out.println("Restaurante " + id + " modificado");
				}
				else
				{
					restaurantes.set(indice, antiguo);
					System.out.println("No se ha modificado nada.");
				}
			}
		}
	}

	private static void opcionEliminar()
	{
		int id = Teclado.leerEntero("Id del restaurante a eliminar: ", 1, Integer.MAX_VALUE);
		Restaurante eliminado = buscarPorId(id);

		if (eliminado == null)
		{
			System.out.println("No existe ningún restaurante con id " + id);
		}
		else
		{
			System.out.println(eliminado);

			if (Teclado.confirmar("¿Seguro que quieres eliminarlo?"))
			{
				int indice = restaurantes.indexOf(eliminado);
				restaurantes.remove(indice);

				if (guardarCambios())
				{
					System.out.println("Restaurante " + id + " eliminado");
				}
				else
				{
					restaurantes.add(indice, eliminado);
					System.out.println("No se ha eliminado nada.");
				}
			}
			else
			{
				System.out.println("Operación cancelada.");
			}
		}
	}

	//PIDE TODOS LOS CAMPOS Y LOS VALIDA HASTA QUE SEAN CORRECTOS O EL USUARIO SE RINDA (DEVUELVE null).
	//porDefecto = null PARA AÑADIR; EL RESTAURANTE ANTIGUO PARA MODIFICAR (INTRO = DEJAR SU VALOR)
	private static Restaurante pedirValido(Restaurante porDefecto, int idIgnorar)
	{
		Restaurante valido = null;
		boolean terminado = false;

		while (!terminado)
		{
			List<String> campos = pedirCampos(porDefecto);
			String original = String.join(",", campos);
			ResultadoValidacion resultado = Validador.validar(campos, 0, original, restaurantes, idIgnorar);

			if (resultado.esValido())
			{
				for (Incidencia aviso : resultado.getAvisos())
				{
					System.out.println("Corregido: " + aviso.getMensaje());
				}
				valido = resultado.getRestaurante();
				terminado = true;
			}
			else
			{
				System.out.println("Error: " + resultado.getError().getMensaje());
				terminado = !Teclado.confirmar("¿Quieres intentarlo de nuevo?");
			}
		}

		return valido;
	}

	//UNA PREGUNTA POR CADA CAMPO DEL enum Campo
	private static List<String> pedirCampos(Restaurante porDefecto)
	{
		List<String> campos = new ArrayList<>();

		for (Campo c : Campo.values())
		{
			if (porDefecto == null)
			{
				campos.add(Teclado.leerTexto(c.getPregunta() + ": "));
			}
			else
			{
				String actual = porDefecto.getTexto(c);
				campos.add(Teclado.leerTextoODefecto(c.getPregunta() + " [" + actual + "]: ", actual));
			}
		}

		return campos;
	}

	//GUARDA LA LISTA EN EL CSV LIMPIO. SI FALLA, AVISA DE QUÉ HA FALLADO Y DEVUELVE false PARA DESHACER
	private static boolean guardarCambios()
	{
		boolean ok = true;

		try
		{
			GestorCsv.guardarSeguro(restaurantes, RUTA_LIMPIO);
		}
		catch (IOException e)
		{
			System.out.println("ERROR al guardar: " + e.getMessage());
			ok = false;
		}

		return ok;
	}

	private static int siguienteId()
	{
		int maximo = 0;

		for (Restaurante r : restaurantes)
		{
			if (r.getId() > maximo)
			{
				maximo = r.getId();
			}
		}

		return maximo + 1;
	}

	private static Restaurante buscarPorId(int id)
	{
		Restaurante encontrado = null;

		for (int i = 0; i < restaurantes.size() && encontrado == null; ++i)
		{
			if (restaurantes.get(i).getId() == id)
			{
				encontrado = restaurantes.get(i);
			}
		}

		return encontrado;
	}

	// ===================================== OPCIÓN 7 ===================================== //

	private static void opcionExportar() throws ParserConfigurationException, TransformerException
	{
		Filtro filtro = MenuFiltro.pedir(true);
		String ruta = Teclado.leerTextoODefecto("Fichero de salida [" + RUTA_XML + "]: ", RUTA_XML);

		List<Restaurante> seleccionados = filtro.aplicar(restaurantes);
		int escritos = GestorDom.exportar(seleccionados, ruta);

		System.out.println("Exportados " + escritos + " restaurantes a " + ruta);
	}

	// ===================================== OPCIÓN 8 ===================================== //

	private static void opcionLeerDom() throws ParserConfigurationException, SAXException, IOException
	{
		String ruta = Teclado.leerTextoODefecto("Fichero XML [" + RUTA_XML + "]: ", RUTA_XML);
		List<Restaurante> lista = GestorDom.leer(ruta);

		for (Restaurante r : lista)
		{
			System.out.println(r);
		}

		System.out.println(ResumenLectura.desdeLista(lista));
	}

	// ===================================== OPCIÓN 9 ===================================== //

	private static void opcionLeerSax() throws ParserConfigurationException, SAXException, IOException
	{
		String ruta = Teclado.leerTextoODefecto("Fichero XML [" + RUTA_XML + "]: ", RUTA_XML);
		ResumenLectura resumen = GestorSax.leer(ruta, true);

		System.out.println(resumen);
	}

	// ===================================== OPCIÓN 10 ===================================== //

	private static void opcionIntegridad() throws ParserConfigurationException, SAXException, IOException
	{
		String ruta = Teclado.leerTextoODefecto("Fichero XML [" + RUTA_XML + "]: ", RUTA_XML);
		List<Restaurante> desdeXml = GestorDom.leer(ruta);
		GestorCsv.escribir(desdeXml, RUTA_CSV_XML);

		ResultadoComparacion resultado = GestorFicheros.comparar(RUTA_LIMPIO, RUTA_CSV_XML);
		System.out.println(resultado.informe());
	}

	// ===================================== OPCIÓN 11 ===================================== //

	private static void opcionEstadisticas() throws IOException
	{
		String texto = Estadisticas.generar(restaurantes);
		System.out.println(texto);
		GestorFicheros.escribirTexto(RUTA_ESTADISTICAS, texto);
		System.out.println("Estadísticas guardadas en " + RUTA_ESTADISTICAS);
	}

	// ===================================== OPCIÓN 12 ===================================== //

	private static void opcionRendimiento()
			throws ParserConfigurationException, TransformerException, SAXException, IOException
	{
		GestorFicheros.prepararEntorno();

		System.out.println("Cargando el CSV grande (puede tardar)...");
		ResultadoCarga carga = GestorCsv.cargar(RUTA_GRANDE);
		GestorDom.exportar(carga.getRestaurantes(), RUTA_XML_GRANDE);

		System.out.println("Midiendo DOM y SAX...");
		String tabla = Rendimiento.tabla(Rendimiento.comparar(RUTA_XML_GRANDE));

		System.out.println(tabla);
		GestorFicheros.escribirTexto(RUTA_RENDIMIENTO, tabla);
		System.out.println("Resultados guardados en " + RUTA_RENDIMIENTO);
	}
}
