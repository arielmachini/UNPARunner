package ar.edu.unpa.uarg.arielmachini.unparunner;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

public class Aplicacion extends Application {

    public static final String ID_CANAL_NOTIFICACION = "UNPARunner_CanalServicioLocalizacion";
    private static final String NOMBRE_CANAL_NOTIFICACION = "Notificaciones del servicio de seguimiento de localización";

    @Override
    public void onCreate() {
        super.onCreate();

        /* Desde Android Oreo para arriba se tiene que crear un canal para las notificaciones. */
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel canalNotificacionServicio = new NotificationChannel(ID_CANAL_NOTIFICACION, NOMBRE_CANAL_NOTIFICACION, NotificationManager.IMPORTANCE_HIGH);

            getSystemService(NotificationManager.class).createNotificationChannel(canalNotificacionServicio);
        }
    }

}
