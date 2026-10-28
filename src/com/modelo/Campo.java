package com.modelo;

//UN CAMPO DEL RESTAURANTE POR LÍNEA. EL ORDEN DE ESTA LISTA ES EL ORDEN DE LAS COLUMNAS DEL CSV
//Y DE LAS ETIQUETAS DEL XML. PARA AÑADIR UN CAMPO: UNA LÍNEA AQUÍ Y LOS case DE Restaurante
public enum Campo {

	//    columna CSV      etiqueta XML     pregunta al usuario
	NOMBRE("nombre", "nombre", "Nombre"),
	DIRECCION("direccion", "direccion", "Dirección"),
	CODIGO_POSTAL("codigo_postal", "codigoPostal", "Código postal"),
	TIPO_COCINA("tipo_cocina", "tipoCocina", "Tipo de cocina"),
	PUNTUACION("puntuacion", "puntuacion", "Puntuación"),
	ABIERTO("abierto", "abierto", "Abierto o cerrado");
	
	private final String columnaCsv;
	private final String etiquetaXml;
	private final String pregunta;

	Campo(String columnaCsv, String etiquetaXml, String pregunta) {
		this.columnaCsv = columnaCsv;
		this.etiquetaXml = etiquetaXml;
		this.pregunta = pregunta;
	}

	public String getColumnaCsv() {
		return columnaCsv;
	}

	public String getEtiquetaXml() {
		return etiquetaXml;
	}

	public String getPregunta() {
		return pregunta;
	}

	//DEVUELVE EL CAMPO QUE TIENE ESA ETIQUETA XML, O null SI NO ES NINGUNO (POR EJEMPLO "restaurante")
	public static Campo desdeEtiqueta(String etiqueta) {
		Campo encontrado = null;
		Campo[] campos = values();

		for (int i = 0; i < campos.length && encontrado == null; i++) {
			if (campos[i].etiquetaXml.equals(etiqueta)) {
				encontrado = campos[i];
			}
		}

		return encontrado;
	}
}