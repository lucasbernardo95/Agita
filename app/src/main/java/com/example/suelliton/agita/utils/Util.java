package com.example.suelliton.agita.utils;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.util.Log;

import java.io.FileDescriptor;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class Util {


    public static List<Address> getNomeLocalFromEndereco(Context contexto, String local){
        Geocoder geocoder = new Geocoder(contexto);
        List<Address> enderecos = new ArrayList<>();
        try {
            enderecos = geocoder.getFromLocationName(local,1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return enderecos;
    }

    public static String convertMillisToDate(long yourmilliseconds){
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


    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }
    public static Bitmap createParcelDescriptor(Context context, Uri uri, int MAX_SIZE ) throws IOException {
        ParcelFileDescriptor parcelFileDescriptor =
                context.getContentResolver().openFileDescriptor(uri, "r");

        FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;

        Bitmap bitmap = BitmapFactory.decodeFileDescriptor(fileDescriptor, null, options);
        options.inSampleSize = calculateInSampleSize(options, MAX_SIZE, MAX_SIZE);
        options.inJustDecodeBounds = false;
        bitmap = BitmapFactory.decodeFileDescriptor(fileDescriptor, null, options);

        parcelFileDescriptor.close();
        return bitmap;
    }


}
