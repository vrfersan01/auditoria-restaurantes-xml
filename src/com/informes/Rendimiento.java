package com.informes;

import java.io.IOException;
import java.lang.ref.Reference;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import com.modelo.ResumenLectura;
import com.modelo.Restaurante;
import com.xml.GestorDom;
import com.xml.GestorSax;

public class Rendimiento {

    private static final int REPETICIONES = 3;

    private static long memoriaUsada() {
        System.gc();
        Runtime rt = Runtime.getRuntime();
        return rt.totalMemory() - rt.freeMemory();
    }

    private static long[] medirDom(String ruta)
            throws ParserConfigurationException, SAXException, IOException {
        long antes = memoriaUsada();
        long inicio = System.nanoTime();
        List<Restaurante> lista = GestorDom.leer(ruta);
        ResumenLectura resumen = ResumenLectura.desdeLista(lista);
        long duracion = System.nanoTime() - inicio;
        long despues = memoriaUsada();
        Reference.reachabilityFence(lista);
        Reference.reachabilityFence(resumen);
        return new long[] {duracion, Math.max(0, despues - antes)};
    }

    private static long[] medirSax(String ruta)
            throws ParserConfigurationException, SAXException, IOException {
        long antes = memoriaUsada();
        long inicio = System.nanoTime();
        ResumenLectura resumen = GestorSax.leer(ruta, false);
        long duracion = System.nanoTime() - inicio;
        long despues = memoriaUsada();
        Reference.reachabilityFence(resumen);
        return new long[] {duracion, Math.max(0, despues - antes)};
    }

    public static List<Medicion> comparar(String ruta)
            throws ParserConfigurationException, SAXException, IOException {
        double nsDom = 0;
        double bytesDom = 0;
        double nsSax = 0;
        double bytesSax = 0;

        for (int i = 0; i < REPETICIONES; i++) {
            long[] dom = medirDom(ruta);
            long[] sax = medirSax(ruta);
            if (i > 0) {
                nsDom += dom[0];
                bytesDom += dom[1];
                nsSax += sax[0];
                bytesSax += sax[1];
            }
        }

        int validas = REPETICIONES - 1;
        double mb = 1024.0 * 1024.0;
        List<Medicion> resultado = new ArrayList<>();
        resultado.add(new Medicion("DOM", nsDom / validas / 1_000_000.0, bytesDom / validas / mb));
        resultado.add(new Medicion("SAX", nsSax / validas / 1_000_000.0, bytesSax / validas / mb));
        return resultado;
    }

    public static String tabla(List<Medicion> mediciones) {
        StringBuilder texto = new StringBuilder();
        texto.append("Comparativa DOM frente a SAX (").append(REPETICIONES)
             .append(" mediciones, primera descartada)\n");
        texto.append(String.format("%-8s %18s %15s\n", "Método", "Tiempo medio (ms)", "Memoria (MB)"));
        for (Medicion m : mediciones) {
            texto.append(String.format(Locale.US, "%-8s %18.2f %15.2f\n",
                    m.getMetodo(), m.getMilisegundos(), m.getMegabytes()));
        }
        return texto.toString();
    }
}