package com.example.suelliton.agita.utils;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;

public class GPSTracker extends Service implements LocationListener {



    private final Context context;
    boolean isGPSEnabled = false;
    boolean isNetworkEnable = false;
    boolean canGetLocation = false;

    Location location;
    protected LocationManager locationManager;

    public GPSTracker (Context context){
        this.context = context;
    }

    //create a getlocation method


    public Location getLocation() {
        try {
            locationManager = (LocationManager) context.getSystemService(LOCATION_SERVICE);
            isGPSEnabled = locationManager.isProviderEnabled(locationManager.GPS_PROVIDER);
            isNetworkEnable = locationManager.isProviderEnabled(locationManager.NETWORK_PROVIDER);

            //verifica as permissões
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                    || ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

                if (isGPSEnabled){
                    if (location == null){
                        //tempo de atualização da localização
                        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000, 10, this);
                        if (locationManager !=null){
                            //pega o último localDetalhe onde o celular conseguiu pegar sua localização 'se teve internet ativa e gps também para isso'
                            location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                        }
                    }
                }

                //if location is not found from gps than it will foun from network
                if (location == null){
                    if (isNetworkEnable){
                        //tempo de atualização da localização
                        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 10000, 10, this);
                        if (locationManager !=null){
                            //pega o último localDetalhe onde o celular conseguiu pegar sua localização 'se teve internet ativa e gps também para isso'
                            location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        }
                    }
                }//end if

            }

        }catch (Exception ex){

        }
        return location;
    }

    //followings are the default method if we implement LocationListener
    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
