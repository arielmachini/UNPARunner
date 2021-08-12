package ar.edu.unpa.uarg.arielmachini.unparunner.sqlite;

import android.database.sqlite.SQLiteDatabase;

import androidx.annotation.Nullable;

public class ConexionSQLite extends android.database.sqlite.SQLiteOpenHelper {

    public ConexionSQLite(@Nullable android.content.Context context, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, ConstantesSQLite.NOMBRE_ESQUEMA, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(ConstantesSQLite.CONSULTA_CREAR_TABLA_RECORRIDO);
        db.execSQL(ConstantesSQLite.CONSULTA_CREAR_TABLA_PUNTO);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + ConstantesSQLite.NOMBRE_TABLA_PUNTO);
        db.execSQL("DROP TABLE IF EXISTS " + ConstantesSQLite.NOMBRE_TABLA_RECORRIDO);
        this.onCreate(db);
    }

}
