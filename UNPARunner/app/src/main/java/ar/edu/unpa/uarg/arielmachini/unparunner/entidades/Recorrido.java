package ar.edu.unpa.uarg.arielmachini.unparunner.entidades;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class Recorrido {
    private float distancia;
    private String duracion;
    private String fecha;
    private String horaInicio;
    private String horaFinalizacion;
    private final ArrayList<Punto> puntos;

    public Recorrido() {
        this.distancia = 0.0f;
        this.fecha = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date());
        this.horaInicio = new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date());
        this.puntos = new ArrayList<>();
    }

    public float getDistancia() {
        return this.distancia;
    }

    /**
     * @param distancia Distancia del recorrido en kilómetros.
     */
    public void setDistancia(float distancia) {
        this.distancia = distancia;
    }

    public String getDuracion() {
        return this.duracion;
    }

    /**
     * @param duracion Duración total del recorrido (en la forma MM:SS).
     */
    public void setDuracion(String duracion) {
        this.duracion = duracion;
    }

    public String getFecha() {
        return this.fecha;
    }

    /**
     * @param fecha Fecha en la que se registró el recorrido (en la forma DD/MM/YYYY).
     */
    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getHoraInicio() {
        return this.horaInicio;
    }

    /**
     * @param horaInicio Hora en la que inició el recorrido (en la forma HH:MM).
     */
    public void setHoraInicio(String horaInicio) {
        this.horaInicio = horaInicio;
    }

    public String getHoraFinalizacion() {
        return this.horaFinalizacion;
    }

    /**
     * @param horaFinalizacion Hora en la que finalizó el recorrido (en la forma HH:MM).
     */
    public void setHoraFinalizacion(String horaFinalizacion) {
        this.horaFinalizacion = horaFinalizacion;
    }

    public ArrayList<Punto> getPuntos() {
        return this.puntos;
    }

    /**
     * Agrega un punto nuevo al recorrido.
     *
     * @param punto Punto que se agregará al recorrido.
     */
    public void agregarPunto(Punto punto) {
        if (punto.getLatitud() == Float.MIN_VALUE || punto.getLongitud() == Float.MIN_VALUE) {
            throw new IllegalArgumentException("ERROR: No definió una latitud y/o longitud para el punto que está intentando agregar al recorrido.");
        }

        this.puntos.add(punto);
    }

    public void finalizar() {
        this.horaFinalizacion = new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date());
    }

}
