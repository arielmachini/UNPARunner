package ar.edu.unpa.uarg.arielmachini.unparunner;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import ar.edu.unpa.uarg.arielmachini.unparunner.sqlite.ConexionSQLite;
import ar.edu.unpa.uarg.arielmachini.unparunner.sqlite.ConstantesSQLite;

public class HistorialRecorridosActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historial_recorridos);
        setTitle("Mis recorridos");

        /* Se inicializan los objetos referentes a la GUI: */
        Button buttonVolver = findViewById(R.id.historialBotonVolver);
        RecyclerView listaRecorridos = findViewById(R.id.historialLista);

        buttonVolver.setOnClickListener(view -> {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        });

        listaRecorridos.setLayoutManager(new LinearLayoutManager(this));

        /* Se inicializa el resto de las variables: */
        ConexionSQLite conexionSQLite = new ConexionSQLite(this, null, 1);
        SQLiteDatabase bd = conexionSQLite.getReadableDatabase();
        Cursor cursor = bd.rawQuery("SELECT `" + ConstantesSQLite.RECORRIDO_PRIMARY_KEY + "`, `" + ConstantesSQLite.RECORRIDO_FECHA + "` FROM " + ConstantesSQLite.NOMBRE_TABLA_RECORRIDO + " ORDER BY " + ConstantesSQLite.RECORRIDO_PRIMARY_KEY + " DESC", new String[0]);
        RecorridoAdapter recorridoAdapter = new RecorridoAdapter(this, cursor);

        listaRecorridos.setAdapter(recorridoAdapter);
    }

}
