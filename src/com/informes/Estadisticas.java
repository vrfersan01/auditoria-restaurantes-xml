package com.informes;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

import com.modelo.Restaurante;

public class Estadisticas {

    public static String generar(List<Restaurante> lista) {
        StringBuilder texto = new StringBuilder();
        if (lista.isEmpty()) {
            texto.append("No hay datos para generar estadísticas.\n");
        } else {
            texto.append(seccionResumen(lista));
            texto.append(seccionProvincias(lista));
            texto.append(seccionCocinas(lista));
            texto.append(seccionTramos(lista));
        }
        return texto.toString();
    }

    private static String seccionResumen(List<Restaurante> lista) {
        double suma = 0;
        double maxima = lista.get(0).getPuntuacion();
        double minima = lista.get(0).getPuntuacion();
        for (Restaurante r : lista) {
            suma += r.getPuntuacion();
            if (r.getPuntuacion() > maxima) {
                maxima = r.getPuntuacion();
            }
            if (r.getPuntuacion() < minima) {
                minima = r.getPuntuacion();
            }
        }
        double media = suma / lista.size();

        StringBuilder texto = new StringBuilder();
        texto.append("=== RESUMEN ===\n");
        texto.append("Total de restaurantes: ").append(lista.size()).append("\n");
        texto.append(String.format(Locale.US, "Puntuación media: %.2f\n", media));
        texto.append("Mejor (").append(formato(maxima)).append("): ")
             .append(nombresCon(lista, maxima)).append("\n");
        texto.append("Peor (").append(formato(minima)).append("): ")
             .append(nombresCon(lista, minima)).append("\n");
        return texto.toString();
    }

    //NOMBRES DE TODOS LOS RESTAURANTES CON ESA PUNTUACIÓN (PUEDE HABER EMPATE)
    private static String nombresCon(List<Restaurante> lista, double puntuacion) {
        List<String> nombres = new ArrayList<>();
        for (Restaurante r : lista) {
            if (r.getPuntuacion() == puntuacion) {
                nombres.add(r.getNombre());
            }
        }
        return String.join(", ", nombres);
    }

    private static String formato(double puntuacion) {
        return String.format(Locale.US, "%.1f", puntuacion);
    }
    
    private static String seccionProvincias(List<Restaurante> lista) {
        Map<String, Integer> cuenta = new TreeMap<>();
        for (Restaurante r : lista) {
            String provincia = r.getCodigoPostal().substring(0, 2);
            cuenta.put(provincia, cuenta.getOrDefault(provincia, 0) + 1);
        }

        List<Map.Entry<String, Integer>> entradas = new ArrayList<>(cuenta.entrySet());
        entradas.sort((a, b) -> Integer.compare(b.getValue(), a.getValue()));

        StringBuilder texto = new StringBuilder();
        texto.append("\n=== POR PROVINCIA ===\n");
        for (Map.Entry<String, Integer> e : entradas) {
            texto.append(e.getKey()).append(": ").append(e.getValue()).append("\n");
        }
        return texto.toString();
    }

    private static String seccionCocinas(List<Restaurante> lista) {
        Map<String, double[]> cocinas = new TreeMap<>();
        for (Restaurante r : lista) {
            double[] datos = cocinas.get(r.getTipoCocina());
            if (datos == null) {
                datos = new double[2];
                cocinas.put(r.getTipoCocina(), datos);
            }
            datos[0]++;
            datos[1] += r.getPuntuacion();
        }

        StringBuilder texto = new StringBuilder();
        texto.append("\n=== POR TIPO DE COCINA ===\n");
        for (Map.Entry<String, double[]> e : cocinas.entrySet()) {
            double cuantos = e.getValue()[0];
            double media = e.getValue()[1] / cuantos;
            texto.append(String.format(Locale.US, "%s: %d restaurantes, media %.2f\n",
                    e.getKey(), (int) cuantos, media));
        }
        return texto.toString();
    }

    private static String seccionTramos(List<Restaurante> lista) {
        String[] etiquetas = {"[0,5)", "[5,7)", "[7,9)", "[9,10]"};
        int[] tramos = new int[4];
        for (Restaurante r : lista) {
            double p = r.getPuntuacion();
            if (p < 5) {
                tramos[0]++;
            } else if (p < 7) {
                tramos[1]++;
            } else if (p < 9) {
                tramos[2]++;
            } else {
                tramos[3]++;
            }
        }

        StringBuilder texto = new StringBuilder();
        texto.append("\n=== TRAMOS DE PUNTUACIÓN ===\n");
        for (int i = 0; i < tramos.length; i++) {
            texto.append(etiquetas[i]).append(": ").append(tramos[i]).append("\n");
        }
        return texto.toString();
    }
}