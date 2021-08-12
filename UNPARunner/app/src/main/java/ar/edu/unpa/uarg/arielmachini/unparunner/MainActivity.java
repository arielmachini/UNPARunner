package ar.edu.unpa.uarg.arielmachini.unparunner;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import ar.edu.unpa.uarg.arielmachini.unparunner.sqlite.ConexionSQLite;
import ar.edu.unpa.uarg.arielmachini.unparunner.sqlite.ConstantesSQLite;

public class MainActivity extends AppCompatActivity {
    ConexionSQLite conexionSQLite;
    TextView textViewNumeroRecorridos;
    TextView textViewDistanciaTotalKM;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.conexionSQLite = new ConexionSQLite(this, null, 1);

        Button buttonVerHistorial = findViewById(R.id.mainBotorVerHistorial);
        Button buttonNuevoRecorrido = findViewById(R.id.mainBotonNuevoRecorrido);
        this.textViewNumeroRecorridos = findViewById(R.id.mainNumeroRecorridos);
        this.textViewDistanciaTotalKM = findViewById(R.id.mainTextoDistanciaTotal);

        buttonVerHistorial.setOnClickListener(view -> {
            startActivity(new Intent(this, HistorialRecorridosActivity.class));
        });

        buttonNuevoRecorrido.setOnClickListener(view -> {
            startActivity(new Intent(this, NuevoRecorridoActivity.class));
            finish();
        });

        this.obtenerNumeroRecorridos();
        this.obtenerDistanciaTotalRecorrida();
    }

    @Override
    protected void onResume() {
        super.onResume();

        this.obtenerNumeroRecorridos();
        this.obtenerDistanciaTotalRecorrida();
    }

    private void obtenerNumeroRecorridos() {
        SQLiteDatabase bd = this.conexionSQLite.getReadableDatabase();
        Cursor cursor = bd.rawQuery("SELECT COUNT(`" + ConstantesSQLite.RECORRIDO_PRIMARY_KEY + "`) FROM `" + ConstantesSQLite.NOMBRE_TABLA_RECORRIDO + "`", new String[0]);

        cursor.moveToFirst();
        this.textViewNumeroRecorridos.setText(cursor.getString(0));

        bd.close();
    }

    private void obtenerDistanciaTotalRecorrida() {
        SQLiteDatabase bd = this.conexionSQLite.getReadableDatabase();
        Cursor cursor = bd.rawQuery("SELECT SUM(`" + ConstantesSQLite.RECORRIDO_DISTANCIA + "`) FROM `" + ConstantesSQLite.NOMBRE_TABLA_RECORRIDO + "`", new String[0]);

        cursor.moveToFirst();
        this.textViewDistanciaTotalKM.setText(cursor.getDouble(0) + " KM en total");

        bd.close();
    }

}
