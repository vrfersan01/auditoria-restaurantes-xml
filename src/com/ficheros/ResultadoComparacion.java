package com.ficheros;

public class ResultadoComparacion {
	
	private final int diferencias;
    private final int primeraLinea;
    private final String esperado;
    private final String obtenido;

    public ResultadoComparacion(int diferencias, int primeraLinea,
                                String esperado, String obtenido) {
        this.diferencias = diferencias;
        this.primeraLinea = primeraLinea;
        this.esperado = esperado;
        this.obtenido = obtenido;
    }

    public boolean identicos() {
        return diferencias == 0;
    }

    public String informe() {
        String texto = "INTEGRIDAD CORRECTA";
        if (!identicos()) {
            texto = "INTEGRIDAD INCORRECTA: " + diferencias + " líneas distintas\n"
                  + "Primera diferencia en la línea " + primeraLinea + "\n"
                  + "Esperado: " + esperado + "\n"
                  + "Obtenido: " + obtenido;
        }
        return texto;
    }
}
