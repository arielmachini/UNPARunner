package ar.edu.unpa.uarg.arielmachini.unparunner.sqlite;

public class ConstantesSQLite {

    public static final String NOMBRE_ESQUEMA = "unparunner";

    /* Detalles de la tabla «punto»: */
    public static final String NOMBRE_TABLA_PUNTO = "punto";
    public static final String PUNTO_PRIMARY_KEY = "id";
    public static final String PUNTO_ID_RECORRIDO = "idRecorrido";
    public static final String PUNTO_LATITUD = "latitud";
    public static final String PUNTO_LONGITUD = "longitud";

    /* Detalles de la tabla «recorrido»: */
    public static final String NOMBRE_TABLA_RECORRIDO = "recorrido";
    public static final String RECORRIDO_PRIMARY_KEY = "id";
    public static final String RECORRIDO_DISTANCIA = "distancia";
    public static final String RECORRIDO_DURACION = "duracion";
    public static final String RECORRIDO_FECHA = "fecha";
    public static final String RECORRIDO_HORA_INICIO = "horaInicio";
    public static final String RECORRIDO_HORA_FINALIZACION = "horaFinalizacion";

    public static final String CONSULTA_CREAR_TABLA_RECORRIDO = "CREATE TABLE `" + NOMBRE_TABLA_RECORRIDO + "` (" +
            "`" + RECORRIDO_PRIMARY_KEY + "` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
            "`" + RECORRIDO_DISTANCIA + "` REAL UNSIGNED NOT NULL, " +
            "`" + RECORRIDO_DURACION + "` TEXT NOT NULL, " +
            "`" + RECORRIDO_FECHA + "` TEXT NOT NULL, " +
            "`" + RECORRIDO_HORA_INICIO + "` TEXT NOT NULL, " +
            "`" + RECORRIDO_HORA_FINALIZACION + "` TEXT NOT NULL" +
            ")";

    public static final String CONSULTA_CREAR_TABLA_PUNTO = "CREATE TABLE `" + NOMBRE_TABLA_PUNTO + "` (" +
            "`" + PUNTO_PRIMARY_KEY + "` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
            "`" + PUNTO_ID_RECORRIDO + "` INTEGER UNSIGNED NOT NULL, " +
            "`" + PUNTO_LATITUD + "` REAL NOT NULL, " +
            "`" + PUNTO_LONGITUD + "` REAL NOT NULL, " +
            "FOREIGN KEY (`" + PUNTO_ID_RECORRIDO + "`) REFERENCES `" + NOMBRE_TABLA_RECORRIDO + "` (`" + RECORRIDO_PRIMARY_KEY + "`)" +
            ")";

    private ConstantesSQLite() {
    }

}
