package com.xml;

import java.util.ArrayList;
import java.util.List;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.modelo.Campo;
import com.modelo.Restaurante;
import com.modelo.ResumenLectura;

public class ManejadorSax extends DefaultHandler {

	private final boolean mostrar;
	private final StringBuilder texto;
	private final ResumenLectura resumen;
	private int id;
	private List<String> textos;

	public ManejadorSax(boolean mostrar)
	{
		this.mostrar = mostrar;
		this.texto = new StringBuilder();
		this.resumen = new ResumenLectura();
	}

	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes)
			throws SAXException
	{
		texto.setLength(0);

		if (qName.equals("restaurante"))
		{
			id = Integer.parseInt(attributes.getValue("id"));
			textos = new ArrayList<>();

			for (int i = 0; i < Campo.values().length; i++)
			{
				textos.add("");
			}
		}
	}

	@Override
	public void characters(char[] ch, int start, int length) throws SAXException
	{
		texto.append(ch, start, length);
	}

	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException
	{
		String valor = texto.toString().trim();
		Campo campo = Campo.desdeEtiqueta(qName);

		if (campo != null)
		{
			textos.set(campo.ordinal(), valor);
		}
		else if (qName.equals("restaurante"))
		{
			procesar(Restaurante.desdeTextos(id, textos));
		}
	}

	//QUÉ SE HACE CON CADA RESTAURANTE COMPLETO. PARA OTRO CÁLCULO EN SAX, SE CAMBIA SOLO ESTE MÉTODO
	private void procesar(Restaurante r)
	{
		if (mostrar)
		{
			System.out.println(r);
		}
		resumen.anhadir(r);
	}

	public ResumenLectura getResumen()
	{
		return resumen;
	}
}