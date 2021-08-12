package ar.edu.unpa.uarg.arielmachini.unparunner;

public class CalculadoraDistancias {

    private static final int RADIO_PLANETA_TIERRA_KILOMETROS = 6371;

    private CalculadoraDistancias() {
    }

    private static double gradosARadianes(double grados) {
        return grados * Math.PI / 180;
    }

    public static float obtenerDistanciaEnKM(double latitud1, double longitud1, double latitud2, double longitud2) {
        double distanciaLatitud = gradosARadianes(latitud2 - latitud1);
        double distanciaLongitud = gradosARadianes(longitud2 - longitud1);

        latitud1 = gradosARadianes(latitud1);
        latitud2 = gradosARadianes(latitud2);

        double a = Math.sin(distanciaLatitud / 2) * Math.sin(distanciaLatitud / 2) + Math.sin(distanciaLongitud / 2) * Math.sin(distanciaLongitud / 2) * Math.cos(latitud1) * Math.cos(latitud2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return (float) (CalculadoraDistancias.RADIO_PLANETA_TIERRA_KILOMETROS * c);
    }

}
