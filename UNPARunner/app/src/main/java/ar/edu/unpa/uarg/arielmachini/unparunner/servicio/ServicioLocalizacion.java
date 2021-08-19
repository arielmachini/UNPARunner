package ar.edu.unpa.uarg.arielmachini.unparunner.servicio;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.IBinder;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import ar.edu.unpa.uarg.arielmachini.unparunner.Aplicacion;
import ar.edu.unpa.uarg.arielmachini.unparunner.CalculadoraDistancias;
import ar.edu.unpa.uarg.arielmachini.unparunner.MainActivity;
import ar.edu.unpa.uarg.arielmachini.unparunner.NuevoRecorridoActivity;
import ar.edu.unpa.uarg.arielmachini.unparunner.R;
import ar.edu.unpa.uarg.arielmachini.unparunner.VerRecorridoActivity;
import ar.edu.unpa.uarg.arielmachini.unparunner.entidades.Punto;
import ar.edu.unpa.uarg.arielmachini.unparunner.entidades.Recorrido;

public class ServicioLocalizacion extends Service {

    public static final int ID_SERVICIO = 1308;

    private FusedLocationProviderClient fusedLocationProviderClient;
    private Intent intentNotificacion;
    private PendingIntent pendingIntent;
    private Recorrido recorrido;

    private final LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            super.onLocationResult(locationResult);

            if (recorrido != null) {
                Location location = locationResult.getLastLocation();
                Punto nuevoPunto = new Punto((float) location.getLatitude(), (float) location.getLongitude());

                if (recorrido.getPuntos().size() >= 1) {
                    Punto ultimoPunto = recorrido.getPuntos().get(recorrido.getPuntos().size() - 1);
                    float distanciaTotal = recorrido.getDistancia() + CalculadoraDistancias.obtenerDistanciaEnKM(nuevoPunto.getLatitud(), nuevoPunto.getLongitud(), ultimoPunto.getLatitud(), ultimoPunto.getLongitud());

                    recorrido.setDistancia(distanciaTotal);
                }

                recorrido.agregarPunto(nuevoPunto);
                NuevoRecorridoActivity.getInstance().actualizarInformacionRecorrido(recorrido);
            }
        }
    };

    public ServicioLocalizacion() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        this.intentNotificacion = new Intent(this, NuevoRecorridoActivity.class);
        this.pendingIntent = PendingIntent.getActivity(this, 0, intentNotificacion, 0);

        this.fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        this.recorrido = new Recorrido();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        int codigoAccion = intent.getIntExtra("codigoAccion", 0); // El código por defecto 0 indica que se quiere iniciar el servicio.

        if (codigoAccion == 0) {
            Notification notificacionRecorrido = new NotificationCompat.Builder(this, Aplicacion.ID_CANAL_NOTIFICACION)
                    .setContentTitle(getString(R.string.NuevoRecorrido_Notificacion_Titulo))
                    .setContentText(getString(R.string.NuevoRecorrido_Notificacion_Contenido))
                    .setColor(ContextCompat.getColor(this, R.color.colorPrimary))
                    .setContentIntent(pendingIntent)
                    .setSmallIcon(R.drawable.ic_notificacion_recorrido)
                    .build();

            startForeground(ID_SERVICIO, notificacionRecorrido);

            this.actualizarUbicacion();
        } else {
            this.fusedLocationProviderClient.removeLocationUpdates(this.locationCallback);

            if (codigoAccion == NuevoRecorridoActivity.ACCION_GUARDAR) {
                if (this.recorrido.getPuntos().size() >= 2) {
                    int idRecorrido = NuevoRecorridoActivity.getInstance().guardarRecorrido(this.recorrido);
                    Toast.makeText(this, R.string.NuevoRecorrido_Toast_Guardado, Toast.LENGTH_SHORT).show();

                    this.redireccionarVerRecorrido(idRecorrido);
                } else {
                    Toast.makeText(this, R.string.NuevoRecorrido_Toast_No_guardado, Toast.LENGTH_LONG).show();

                    this.redireccionarMainActivity();
                }
            } else {
                this.redireccionarMainActivity();
            }

            stopSelf();
        }

        return START_STICKY;
    }

    @SuppressLint("MissingPermission")
    public void actualizarUbicacion() {
        LocationRequest locationRequest = new LocationRequest();

        locationRequest.setFastestInterval(1000);
        locationRequest.setInterval(2000);
        locationRequest.setMaxWaitTime(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setSmallestDisplacement(5);

        this.fusedLocationProviderClient.requestLocationUpdates(locationRequest, this.locationCallback, null);
    }

    private void redireccionarMainActivity() {
        Intent intent = new Intent(NuevoRecorridoActivity.getInstance(), MainActivity.class);

        NuevoRecorridoActivity.getInstance().startActivity(intent);
        NuevoRecorridoActivity.getInstance().finish();
    }

    private void redireccionarVerRecorrido(int idRecorrido) {
        Intent intent = new Intent(this, VerRecorridoActivity.class);

        intent.putExtra("idRecorrido", idRecorrido);

        NuevoRecorridoActivity.getInstance().startActivity(intent);
        NuevoRecorridoActivity.getInstance().finish();
    }

}
