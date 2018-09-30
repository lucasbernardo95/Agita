package com.example.suelliton.agita.utils;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class Util {




    public String convertMillisToDate(long yourmilliseconds){
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yy", Locale.US);
        GregorianCalendar calendar = new GregorianCalendar(TimeZone.getTimeZone("US/Central"));
        calendar.setTimeInMillis(yourmilliseconds);
        Log.i("click","GregorianCalendar -"+sdf.format(calendar.getTime()));
        return sdf.format(calendar.getTime());
    }
    public int getHoraAtual(){
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy-HH:mm:ss");        // OU
        SimpleDateFormat dateFormat_hora = new SimpleDateFormat("HH:mm:ss");
        Date data = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(data);
        Date data_atual = cal.getTime();
        String data_completa = dateFormat.format(data_atual);
        String hora_atual = dateFormat_hora.format(data_atual);
        Log.i("Script","Hora atual:"+ hora_atual.split(":")[0]);
        return Integer.parseInt(hora_atual.split(":")[0]);
    }
    public void dispararServicoBusca(Context context){
        Intent it = new Intent("SERVICO_EVENTO");
        it.putExtra("aas", "sdvsd");
        context.startService(createExplicitFromImplicitIntent( context,it));
    }
    public static Intent createExplicitFromImplicitIntent(Context context, Intent implicitIntent) {
        //Retrieve all services that can match the given intent
        PackageManager pm = context.getPackageManager();
        List<ResolveInfo> resolveInfo = pm.queryIntentServices(implicitIntent, 0);
        //Make sure only one match was found
        if (resolveInfo == null || resolveInfo.size() != 1) {
            return null;
        }
        //Get component info and create ComponentName
        ResolveInfo serviceInfo = resolveInfo.get(0);
        String packageName = serviceInfo.serviceInfo.packageName;
        String className = serviceInfo.serviceInfo.name;
        ComponentName component = new ComponentName(packageName, className);
        //Create a new intent. Use the old one for extras and such reuse
        Intent explicitIntent = new Intent(implicitIntent);
        //Set the component to be explicit
        explicitIntent.setComponent(component);
        return explicitIntent;
    }
}
