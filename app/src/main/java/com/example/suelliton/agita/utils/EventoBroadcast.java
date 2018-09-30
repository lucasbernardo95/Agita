package com.example.suelliton.agita.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.example.suelliton.agita.activity.EventoActivity;


public class EventoBroadcast extends BroadcastReceiver {
    public EventoBroadcast(){

    }
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().matches("android.location.PROVIDERS_CHANGED")) {
            Toast.makeText(context, "in android.location.PROVIDERS_CHANGED",
                    Toast.LENGTH_SHORT).show();
            Intent pushIntent = new Intent(context, ServicoEventoGPS.class);
            context.startService(pushIntent);
        }
        Log.i("broadcast","funcionou o broadcast pai");
    }
}
