package ar.edu.unpa.uarg.arielmachini.unparunner;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import ar.edu.unpa.uarg.arielmachini.unparunner.entidades.Punto;
import ar.edu.unpa.uarg.arielmachini.unparunner.entidades.Recorrido;
import ar.edu.unpa.uarg.arielmachini.unparunner.servicio.ServicioLocalizacion;
import ar.edu.unpa.uarg.arielmachini.unparunner.sqlite.ConexionSQLite;
import ar.edu.unpa.uarg.arielmachini.unparunner.sqlite.ConstantesSQLite;

public class NuevoRecorridoActivity extends AppCompatActivity {

    public static final int ACCION_GUARDAR = 1;
    public static final int ACCION_SALIR = -1;
    private static final int CODIGO_SOLICITUD_UBICACION = 82596;
    private static NuevoRecorridoActivity instancia;

    private Button buttonGuardarRecorrido;
    private Button buttonIniciarRecorrido;
    private Chronometer chronometerRecorrido;
    private TextView textViewCantidadPuntos;
    private TextView textViewDistanciaKM;

    private int puntosRegistrados;

    /**
     * Este método estático retorna la instancia actual de la clase
     * NuevoRecorridoActivity (asignada en onCreate). Está pensado para ser
     * utilizado dentro de la clase ServicioUbicacion_broadcast.
     */
    public static NuevoRecorridoActivity getInstance() {
        return instancia;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_agregar_recorrido);

        /* Se inicializan los objetos referentes a la GUI: */
        Button buttonVolver = findViewById(R.id.nuevoBotonDescartar);
        this.buttonGuardarRecorrido = findViewById(R.id.nuevoBotonFinalizar);
        this.buttonIniciarRecorrido = findViewById(R.id.nuevoBotonIniciar);
        this.chronometerRecorrido = findViewById(R.id.nuevoCronometro);
        this.textViewCantidadPuntos = findViewById(R.id.nuevoCantidadPuntos);
        this.textViewDistanciaKM = findViewById(R.id.nuevoDistancia);

        /* Se inicializa el resto de las variables: */
        instancia = this;

        buttonGuardarRecorrido.setEnabled(false);

        buttonIniciarRecorrido.setOnClickListener(view -> {
            this.iniciarServicio();
        });

        buttonGuardarRecorrido.setOnClickListener(view -> {
            this.detenerServicio(ACCION_GUARDAR);
        });

        buttonVolver.setOnClickListener(view -> {
            this.detenerServicio(ACCION_SALIR);
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (grantResults.length > 0 && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(NuevoRecorridoActivity.this, "UNPA Runner no podrá registrar su recorrido si no cuenta con el permiso para acceder a la ubicación del dispositivo", Toast.LENGTH_LONG).show();

            return;
        }

        this.iniciarServicio();
    }

    private void iniciarServicio() {
        if (this.solicitarPermisosLocalizacion()) {
            Intent intentIniciarServicio = new Intent(this, ServicioLocalizacion.class);

            this.buttonIniciarRecorrido.setEnabled(false);
            this.buttonGuardarRecorrido.setEnabled(true);

            this.chronometerRecorrido.setBase(android.os.SystemClock.elapsedRealtime());
            this.chronometerRecorrido.start();

            startService(intentIniciarServicio);
        }
    }

    private void detenerServicio(int codigoAccion) {
        Intent intentDetenerServicio = new Intent(this, ServicioLocalizacion.class);

        this.buttonGuardarRecorrido.setEnabled(false);

        intentDetenerServicio.putExtra("codigoAccion", codigoAccion);

        startService(intentDetenerServicio); // Contradictorio, pero se usa el extra codigoAccion para identificar si se quiere o no detener el servicio (en este caso, sí).
    }

    /**
     * Actualiza la información del recorrido (la información mostrada en la
     * interfaz) sin importar si la aplicación está en primer o en segundo
     * plano.
     * Este método es utilizado en la clase <code>ServicioLocalizacion</code>.
     *
     * @param recorrido El recorrido que contiene nueva información.
     */
    public void actualizarInformacionRecorrido(Recorrido recorrido) {
        java.text.NumberFormat numberFormat = java.text.NumberFormat.getNumberInstance();

        if (recorrido.getPuntos().size() >= 1) {
            numberFormat.setMaximumFractionDigits(2);
            numberFormat.setRoundingMode(java.math.RoundingMode.HALF_UP);

            textViewDistanciaKM.setText(numberFormat.format(recorrido.getDistancia()).replace(",", "") + " KM");
        }

        textViewCantidadPuntos.setText(getResources().getQuantityString(R.plurals.AgregarRecorrido_puntos_registrados, ++puntosRegistrados, puntosRegistrados));

        /* Este toast valida que se están registrando puntos nuevos en el recorrido. Debería aparecer de vez en cuando, inclusive cuando la aplicación esté minimizada (siempre y cuando el usuario se esté moviendo). */
        // Toast.makeText(NuevoRecorridoActivity.this, "Nuevo punto registrado (n.° " + (puntosRegistrados) + ")", Toast.LENGTH_SHORT).show();
    }

    /**
     * Guarda un recorrido en la base de datos.
     * Este método es utilizado en la clase <code>ServicioLocalizacion</code>.
     *
     * @param recorrido El recorrido que se guardará en la base de datos.
     */
    public int guardarRecorrido(Recorrido recorrido) {
        ConexionSQLite conexionSQLite = new ConexionSQLite(this, null, 1);
        Cursor cursor;
        int idRecorrido;
        java.text.NumberFormat numberFormat = java.text.NumberFormat.getNumberInstance();
        SQLiteDatabase bd;

        recorrido.finalizar();

        /* Se redondea la distancia total del recorrido a dos decimales: */
        numberFormat.setMaximumFractionDigits(2);
        numberFormat.setRoundingMode(java.math.RoundingMode.HALF_UP);

        recorrido.setDistancia(Float.valueOf(numberFormat.format(recorrido.getDistancia()).replace(",", "")));

        /* Se detiene el cronómetro tras finalizar el recorrido: */
        this.chronometerRecorrido.stop();
        recorrido.setDuracion(this.chronometerRecorrido.getText().toString());

        /* Se guarda el recorrido en la base de datos: */
        bd = conexionSQLite.getWritableDatabase();

        bd.execSQL("INSERT INTO " + ConstantesSQLite.NOMBRE_TABLA_RECORRIDO + " VALUES (NULL, " + recorrido.getDistancia() + ", \"" + recorrido.getDuracion() + "\", \"" + recorrido.getFecha() + "\", \"" + recorrido.getHoraInicio() + "\", \"" + recorrido.getHoraFinalizacion() + "\")");

        /* Se guardan los puntos del recorrido en la base de datos y se retorna el ID del recorrido: */
        bd = conexionSQLite.getReadableDatabase();
        cursor = bd.rawQuery("SELECT last_insert_rowid()", new String[0]); // Se obtiene el identificador del recorrido que se guardó con anterioridad.

        cursor.moveToFirst();

        idRecorrido = cursor.getInt(0);

        bd.close();

        bd = conexionSQLite.getWritableDatabase();

        for (Punto punto : recorrido.getPuntos()) {
            bd.execSQL("INSERT INTO " + ConstantesSQLite.NOMBRE_TABLA_PUNTO + " VALUES (NULL, " + idRecorrido + ", " + punto.getLatitud() + ", " + punto.getLongitud() + ")");
        }

        bd.close();

        return idRecorrido;
    }

    private boolean solicitarPermisosLocalizacion() {
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            this.requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, NuevoRecorridoActivity.CODIGO_SOLICITUD_UBICACION);

            return false;
        } else {
            return true;
        }
    }

}
