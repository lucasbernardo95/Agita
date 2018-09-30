package com.example.suelliton.agita.utils;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.suelliton.agita.activity.Detalhes;
import com.example.suelliton.agita.activity.EventoActivity;
import com.example.suelliton.agita.model.Evento;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import static com.example.suelliton.agita.activity.SplashActivity.eventosReference;

public class ServicoEventoGPS extends Service {
    public List<Worker> threads = new ArrayList<Worker>();
    private GPSTracker gpsTracker;
    private Location mlocation;
    LocationManager manager ;



    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void onCreate(){
        super.onCreate();

        gpsTracker = new GPSTracker(getApplicationContext());
        manager = (LocationManager) getSystemService( Context.LOCATION_SERVICE );
        while(mlocation==null) {
            mlocation = gpsTracker.getLocation();
        }
        Log.i("Script", "onCreate()");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId){
        Log.i("Script", "onStartCommand()");

        Worker w = new Worker(startId);
        w.start();
        threads.add(w);

        return(super.onStartCommand(intent, flags, startId));
        // START_NOT_STICKY
        // START_STICKY
        // START_REDELIVER_INTENT
    }


    class Worker extends Thread{
        public int count = 0;
        public int startId;
        public boolean ativo = true;

        public Worker(int startId){
            this.startId = startId;
        }

        public void run(){

                count++;
                //intancia a classe do GPS para pegar minha localização
                boolean isOnGps = manager.isProviderEnabled(LocationManager.GPS_PROVIDER);

                if (isOnGps) {


                    final double lat = mlocation.getLatitude();
                    final double lng = mlocation.getLongitude();
                    eventosReference.addChildEventListener(new ChildEventListener() {
                        @Override
                        public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                            Evento evento = dataSnapshot.getValue(Evento.class);
                            if (lat > evento.getLatitude() - 0.0450000 && lat < evento.getLatitude() + 0.0450000 &&
                                lng > evento.getLongitude() - 0.020000 && lng < evento.getLongitude() + 0.020000 &&
                                convertMillisToDate(Calendar.getInstance().getTimeInMillis()).equals(evento.getData()) &&
                                Math.abs(getHoraAtual() -  Integer.parseInt(evento.getHora().split(":")[0]) )
                                    < 2) {
                                int id = 1;
                                Intent intent = new Intent(ServicoEventoGPS.this, Detalhes.class);
                                intent.putExtra("msg", "Olá Leitor, como vai?");
                                String contentTitle = "Heyy, existe um evento acontecendo nas imediações";
                                String contentText = evento.getNome()+" em "+evento.getCasashow()+" com: "+evento.getBandas();
                                NotificationUtil.createHeadsUpNotification(ServicoEventoGPS.this, intent, contentTitle, contentText, id);

                                Log.i("Script", "Tem um evento perto");
                            }

                        }

                        @Override
                        public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                        }

                        @Override
                        public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                        }

                        @Override
                        public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });


                    Log.i("Script", "Serviço rodando");
                }

            stopSelf(startId);
        }
    }
    public String convertMillisToDate(long yourmilliseconds){

        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yy", Locale.US);


        GregorianCalendar calendar = new GregorianCalendar(TimeZone.getTimeZone("US/Central"));
        calendar.setTimeInMillis(yourmilliseconds);

        Log.i("click","GregorianCalendar -"+sdf.format(calendar.getTime()));


        return sdf.format(calendar.getTime());
    }
public int getHoraAtual(){
    SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy-HH:mm:ss");
    // OU
    SimpleDateFormat dateFormat_hora = new SimpleDateFormat("HH:mm:ss");

    Date data = new Date();
    Calendar  cal = Calendar.getInstance();
    cal.setTime(data);
    Date data_atual = cal.getTime();

    String data_completa = dateFormat.format(data_atual);

    String hora_atual = dateFormat_hora.format(data_atual);
    Log.i("Script","Hora atual:"+ hora_atual.split(":")[0]);
    return Integer.parseInt(hora_atual.split(":")[0]);

}

    @Override
    public void onDestroy(){
        super.onDestroy();

        for(int i = 0, tam = threads.size(); i < tam; i++){
            threads.get(i).ativo = false;
        }
    }
}
