package com.xml;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;

import com.modelo.ResumenLectura;

public class GestorSax {
	
	public static ResumenLectura leer(String ruta, boolean mostrar) throws ParserConfigurationException, SAXException, IOException
	{
		ManejadorSax manejador = new ManejadorSax(mostrar);
		SAXParser parser = SAXParserFactory.newInstance().newSAXParser();
		parser.parse(new File(ruta), manejador);
		return manejador.getResumen();
	}
}
