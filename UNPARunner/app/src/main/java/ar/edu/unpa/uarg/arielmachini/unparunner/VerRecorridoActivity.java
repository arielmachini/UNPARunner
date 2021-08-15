package ar.edu.unpa.uarg.arielmachini.unparunner;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import ar.edu.unpa.uarg.arielmachini.unparunner.entidades.Punto;
import ar.edu.unpa.uarg.arielmachini.unparunner.entidades.Recorrido;
import ar.edu.unpa.uarg.arielmachini.unparunner.sqlite.ConexionSQLite;
import ar.edu.unpa.uarg.arielmachini.unparunner.sqlite.ConstantesSQLite;

public class VerRecorridoActivity extends FragmentActivity implements OnMapReadyCallback {

    private static final int PUNTO_INICIO = 0, PUNTO_FIN = 1;
    private DialogInterface.OnClickListener escuchadorDialogo;
    private GoogleMap mapaRecorrido;
    private int idRecorrido;
    private Recorrido recorrido;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ver_recorrido);


        /* Se obtiene el ID del recorrido, recibido como parámetro: */
        this.idRecorrido = getIntent().getIntExtra("idRecorrido", -1);

        Log.i("ID del recorrido", String.valueOf(idRecorrido));

        if (this.idRecorrido == -1) {
            startActivity(new android.content.Intent(this, MainActivity.class));
            finish();
        }

        /* Se inicializan los objetos referentes a la GUI: */
        Button buttonVerInicio = findViewById(R.id.detallesBotonInicio);
        Button buttonVerFin = findViewById(R.id.detallesBotonFin);
        Button buttonBorrarRecorrido = findViewById(R.id.detallesBotonBorrar);
        Button buttonVolver = findViewById(R.id.detallesBotonVolver);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.detallesMapa);
        TextView textViewDistancia = findViewById(R.id.detallesDistancia);
        TextView textViewDuracion = findViewById(R.id.detallesDuracion);
        TextView textViewFecha = findViewById(R.id.detallesFecha);
        TextView textViewHoraInicio = findViewById(R.id.detallesHoraInicio);
        TextView textViewHoraFinalizacion = findViewById(R.id.detallesHoraFinalizacion);

        buttonVerInicio.setOnClickListener(view -> {
            this.centrarVistaEn(PUNTO_INICIO);
        });

        buttonVerFin.setOnClickListener(view -> {
            this.centrarVistaEn(PUNTO_FIN);
        });

        buttonBorrarRecorrido.setOnClickListener(view -> {
            new AlertDialog.Builder(VerRecorridoActivity.this).setMessage("¿Está seguro de que quiere borrar este recorrido? Esta acción no se puede deshacer.").setPositiveButton("Sí", this.escuchadorDialogo).setNegativeButton("No", this.escuchadorDialogo).show();
        });

        buttonVolver.setOnClickListener(view -> {
            this.volver();
        });

        /* Se inicializa el resto de las variables: */
        ConexionSQLite conexionSQLite = new ConexionSQLite(this, null, 1);
        SQLiteDatabase bd = conexionSQLite.getReadableDatabase();
        Cursor cursor = bd.rawQuery("SELECT * FROM " + ConstantesSQLite.NOMBRE_TABLA_RECORRIDO + " WHERE `" + ConstantesSQLite.RECORRIDO_PRIMARY_KEY + "` = ?", new String[]{String.valueOf(this.idRecorrido)});
        this.recorrido = new Recorrido();

        this.escuchadorDialogo = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int opcionSeleccionada) {
                if (opcionSeleccionada == DialogInterface.BUTTON_POSITIVE) {
                    SQLiteDatabase bd = conexionSQLite.getWritableDatabase();

                    bd.execSQL("DELETE FROM " + ConstantesSQLite.NOMBRE_TABLA_RECORRIDO + " WHERE `" + ConstantesSQLite.RECORRIDO_PRIMARY_KEY + "` = ?", new String[]{String.valueOf(idRecorrido)});
                    bd.execSQL("DELETE FROM " + ConstantesSQLite.NOMBRE_TABLA_PUNTO + " WHERE `" + ConstantesSQLite.PUNTO_ID_RECORRIDO + "` = ?", new String[]{String.valueOf(idRecorrido)});

                    bd.close();

                    Toast.makeText(VerRecorridoActivity.this, "Recorrido eliminado", Toast.LENGTH_SHORT).show();

                    volver();
                }
            }
        };

        /* Se recuperan las propiedades del recorrido y sus respectivos puntos: */
        cursor.moveToFirst();
        this.recorrido.setDistancia(cursor.getFloat(1));
        this.recorrido.setDuracion(cursor.getString(2));
        this.recorrido.setFecha(cursor.getString(3));
        this.recorrido.setHoraInicio(cursor.getString(4));
        this.recorrido.setHoraFinalizacion(cursor.getString(5));

        cursor = bd.rawQuery("SELECT * FROM " + ConstantesSQLite.NOMBRE_TABLA_PUNTO + " WHERE `" + ConstantesSQLite.PUNTO_ID_RECORRIDO + "` = ?", new String[]{String.valueOf(this.idRecorrido)});

        cursor.moveToFirst();

        do {
            Punto punto = new Punto();

            punto.setLatitud(cursor.getFloat(2));
            punto.setLongitud(cursor.getFloat(3));

            this.recorrido.agregarPunto(punto);
        } while (cursor.moveToNext());

        /* Se actualiza la vista: */
        textViewDistancia.setText(this.recorrido.getDistancia() + " KM");
        textViewDuracion.setText(this.recorrido.getDuracion());
        textViewFecha.setText(this.recorrido.getFecha());
        textViewHoraInicio.setText(this.recorrido.getHoraInicio());
        textViewHoraFinalizacion.setText(this.recorrido.getHoraFinalizacion());

        mapFragment.getMapAsync(this);
        setTitle("Recorrido #" + this.idRecorrido);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Punto puntoInicialRecorrido = this.recorrido.getPuntos().get(0);
        LatLng latLngPuntoInicial = new LatLng(puntoInicialRecorrido.getLatitud(), puntoInicialRecorrido.getLongitud());
        this.mapaRecorrido = googleMap;

        this.mapaRecorrido.moveCamera(CameraUpdateFactory.newLatLngZoom(latLngPuntoInicial, 16));
        this.mapaRecorrido.getUiSettings().setZoomGesturesEnabled(false);

        for (int i = 1; i < this.recorrido.getPuntos().size(); i++) {
            Punto puntoInicio = this.recorrido.getPuntos().get(i - 1);
            LatLng latLngInicio = new LatLng(puntoInicio.getLatitud(), puntoInicio.getLongitud());

            Punto puntoFin = this.recorrido.getPuntos().get(i);
            LatLng latLngFin = new LatLng(puntoFin.getLatitud(), puntoFin.getLongitud());

            this.mapaRecorrido.addPolyline(new PolylineOptions().add(latLngInicio, latLngFin).width(10).color(Color.RED));
        }

        Punto puntoFinalRecorrido = this.recorrido.getPuntos().get(this.recorrido.getPuntos().size() - 1);

        this.mapaRecorrido.addMarker(new MarkerOptions().position(latLngPuntoInicial).title("Inicio"));
        this.mapaRecorrido.addMarker(new MarkerOptions().position(new LatLng(puntoFinalRecorrido.getLatitud(), puntoFinalRecorrido.getLongitud())).title("Fin"));
    }

    private void centrarVistaEn(int idPunto) {
        if (idPunto == PUNTO_INICIO) {
            Punto puntoInicio = this.recorrido.getPuntos().get(0);
            LatLng latLngInicio = new LatLng(puntoInicio.getLatitud(), puntoInicio.getLongitud());

            this.mapaRecorrido.animateCamera(CameraUpdateFactory.newLatLngZoom(latLngInicio, 16));
        } else if (idPunto == PUNTO_FIN) {
            Punto puntoFin = this.recorrido.getPuntos().get(this.recorrido.getPuntos().size() - 1);
            LatLng latLngFin = new LatLng(puntoFin.getLatitud(), puntoFin.getLongitud());

            this.mapaRecorrido.animateCamera(CameraUpdateFactory.newLatLngZoom(latLngFin, 16));
        }
    }

    private void volver() {
        startActivity(new android.content.Intent(this, HistorialRecorridosActivity.class));
        finish();
    }

}