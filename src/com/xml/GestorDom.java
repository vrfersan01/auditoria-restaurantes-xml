package com.xml;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.modelo.Campo;
import com.modelo.Restaurante;

public class GestorDom {

	//------------------------------- ESCRIBIR -------------------------------//

	public static int exportar(List<Restaurante> lista, String ruta)
			throws ParserConfigurationException, TransformerException
	{
		Document doc = nuevo();
		Element raiz = doc.createElement("restaurantes");
		doc.appendChild(raiz);

		for (Restaurante r : lista)
		{
			raiz.appendChild(crearElemento(doc, r));
		}

		guardar(doc, ruta);
		return lista.size();
	}

	//CREA UN <restaurante id="..."> CON UN HIJO POR CADA CAMPO. NO LO AÑADE AL DOCUMENTO
	public static Element crearElemento(Document doc, Restaurante r)
	{
		Element e = doc.createElement("restaurante");
		e.setAttribute("id", String.valueOf(r.getId()));

		for (Campo c : Campo.values())
		{
			crearHijo(doc, e, c.getEtiquetaXml(), r.getTexto(c));
		}

		return e;
	}

	private static void crearHijo(Document doc, Element padre, String etiqueta, String valor)
	{
		Element e = doc.createElement(etiqueta);
		e.setTextContent(valor);
		padre.appendChild(e);
	}

	//------------------------------- LEER -------------------------------//

	public static List<Restaurante> leer(String ruta)
			throws ParserConfigurationException, SAXException, IOException
	{
		Document doc = abrir(ruta);
		List<Restaurante> lista = new ArrayList<>();
		NodeList nodos = doc.getElementsByTagName("restaurante");

		for (int i = 0; i < nodos.getLength(); ++i)
		{
			lista.add(aRestaurante((Element) nodos.item(i)));
		}

		return lista;
	}

	//CONVIERTE UN <restaurante> DEL XML EN UN OBJETO Restaurante
	public static Restaurante aRestaurante(Element e)
	{
		List<String> textos = new ArrayList<>();

		for (Campo c : Campo.values())
		{
			textos.add(valor(e, c.getEtiquetaXml()));
		}

		return Restaurante.desdeTextos(Integer.parseInt(e.getAttribute("id")), textos);
	}

	private static String valor(Element e, String etiqueta)
	{
		String resultado = "";
		NodeList nodos = e.getElementsByTagName(etiqueta);

		if (nodos.getLength() > 0)
		{
			resultado = nodos.item(0).getTextContent().trim();
		}

		return resultado;
	}

	//--------------- ABRIR / CREAR / GUARDAR (PARA MODIFICAR UN XML EXISTENTE) ---------------//

	//DOCUMENTO VACÍO
	public static Document nuevo() throws ParserConfigurationException
	{
		DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		return builder.newDocument();
	}

	//CARGA UN XML DEL DISCO EN MEMORIA
	public static Document abrir(String ruta) throws ParserConfigurationException, SAXException, IOException
	{
		DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		return builder.parse(new File(ruta));
	}

	//ESCRIBE EL DOCUMENTO EN EL DISCO, CON SANGRÍA Y EN UTF-8
	public static void guardar(Document doc, String ruta) throws TransformerException
	{
		quitarEspacios(doc);
		Transformer transformer = TransformerFactory.newInstance().newTransformer();
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
		transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
		transformer.transform(new DOMSource(doc), new StreamResult(new File(ruta)));
	}
	
	//QUITA LOS TEXTOS QUE SOLO TIENEN ESPACIOS Y SALTOS DE LÍNEA.
	//EVITA LINEAS EN BLANCO EXTRAS
	private static void quitarEspacios(Node nodo)
	{
	    NodeList hijos = nodo.getChildNodes();

	    for (int i = hijos.getLength() - 1; i >= 0; i--)
	    {
	        Node hijo = hijos.item(i);

	        if (hijo.getNodeType() == Node.TEXT_NODE && hijo.getTextContent().trim().isEmpty())
	        {
	            nodo.removeChild(hijo);
	        }
	        else
	        {
	            quitarEspacios(hijo);
	        }
	    }
	}
}
