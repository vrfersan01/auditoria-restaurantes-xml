package com.modelo;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class Restaurante {
	
	private int id;
	private String nombre;
	private String direccion;
	private String codigoPostal;
	private String tipoCocina;
	private double puntuacion;
	
	public Restaurante()
	{
		
	}

	public Restaurante(int id, String nombre, String direccion, String codigoPostal, String tipoCocina,
			double puntuacion) {
		
		this.id = id;
		this.nombre = nombre;
		this.direccion = direccion;
		this.codigoPostal = codigoPostal;
		this.tipoCocina = tipoCocina;
		this.puntuacion = puntuacion;
	}

	//-CONVERSIÓN A TEXTO Y DESDE TEXTO (CSV, XML Y TECLADO PASAN POR AQUÍ)//

	//DEVUELVE EL VALOR DE UN CAMPO COMO TEXTO. SI AÑADES UN CAMPO AL enum Y NO SU case, NO COMPILA
	public String getTexto(Campo campo)
	{
		String texto = switch (campo)
		{
			case NOMBRE -> nombre;
			case DIRECCION -> direccion;
			case CODIGO_POSTAL -> codigoPostal;
			case TIPO_COCINA -> tipoCocina;
			case PUNTUACION -> puntuacionTexto();
		};
		return texto;
	}

	//GUARDA EN UN CAMPO SU VALOR A PARTIR DE UN TEXTO YA VALIDADO
	public void setTexto(Campo campo, String texto)
	{
		switch (campo)
		{
			case NOMBRE -> nombre = texto;
			case DIRECCION -> direccion = texto;
			case CODIGO_POSTAL -> codigoPostal = texto;
			case TIPO_COCINA -> tipoCocina = texto;
			case PUNTUACION -> puntuacion = Double.parseDouble(texto);
		}
	}

	//TODOS LOS CAMPOS COMO TEXTO, EN EL ORDEN DEL enum Campo
	public List<String> aTextos()
	{
		List<String> textos = new ArrayList<>();

		for (Campo c : Campo.values())
		{
			textos.add(getTexto(c));
		}

		return textos;
	}

	//CREA UN RESTAURANTE A PARTIR DE SUS TEXTOS, EN EL ORDEN DEL enum Campo
	public static Restaurante desdeTextos(int id, List<String> textos)
	{
		Restaurante r = new Restaurante();
		r.setId(id);

		for (Campo c : Campo.values())
		{
			r.setTexto(c, textos.get(c.ordinal()));
		}

		return r;
	}

	//---------------------------------------------------------------------------//
	
	public String puntuacionTexto()
	{		
		return String.format(Locale.US, "%.1f", this.puntuacion);
	}

	public boolean mismoNombreYDireccion(Restaurante otro)
	{
		return otro.getNombre().equalsIgnoreCase(this.nombre) 
				&& otro.getDireccion().equalsIgnoreCase(this.direccion);
	}

	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getNombre() {
		return nombre;
	}
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	public String getDireccion() {
		return direccion;
	}
	public void setDireccion(String direccion) {
		this.direccion = direccion;
	}
	public String getCodigoPostal() {
		return codigoPostal;
	}
	public void setCodigoPostal(String codigoPostal) {
		this.codigoPostal = codigoPostal;
	}
	public String getTipoCocina() {
		return tipoCocina;
	}
	public void setTipoCocina(String tipoCocina) {
		this.tipoCocina = tipoCocina;
	}
	public double getPuntuacion() {
		return puntuacion;
	}
	public void setPuntuacion(double puntuacion) {
		this.puntuacion = puntuacion;
	}

	
	@Override
	public String toString() {
		return id + " | " + String.join(" | ", aTextos());
	}
	
	
}
