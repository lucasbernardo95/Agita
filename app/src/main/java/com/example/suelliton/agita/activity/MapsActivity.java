package com.example.suelliton.agita.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.example.suelliton.agita.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mapa;
    private static final int LOCATION_REQUEST = 500;//caso dê erro na solicitação
    private LatLng coordenada;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_maps);


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mapa = googleMap;
        mapa.getUiSettings().setZoomControlsEnabled(true);
        //pede a permissão em tempo de execução
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST);

            return;
        }
        //permite ativar minha localização
        mapa.setMyLocationEnabled(true);
        /**
         * Ao detectar um click longo no mapa, marca o ponto e pega suas coordenadas (latitude e longetude)
         */
        mapa.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {

                //Salva o ponto selecionado
                coordenada = latLng;
                //Cria a marca
                MarkerOptions marca = new MarkerOptions();
                marca.position(latLng); //cria uma marca no ponto selecionado

                if (coordenada != null){
                    //Adiciona a primeira marca no mapa
                    marca.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));////insere uma bolinha da cor verde
                }
                //insere os dois pontos no mapa
                mapa.addMarker(marca);
                confirmaLocal();
            }
        });
    }

    private void confirmaLocal() {
        final Intent intent = new Intent();
        new AlertDialog.Builder(this)
                .setTitle("Confirmação do local")
                .setMessage("Deseja confirmar o  local do evento?")
                .setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Bundle b = new Bundle();
                        double[] latlong = new double[3];
                        latlong[0] = coordenada.latitude;
                        latlong[1] = coordenada.longitude;
                        b.putDoubleArray("coordenada", latlong);
                        intent.putExtras(b);
                        setResult(3, intent);
                        finish();
                    }

                })
                .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        coordenada = null;
                        mapa.clear();
                    }
                })
                .show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_local_evento, menu);
        return super.onCreateOptionsMenu(menu);
        //return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_confirm) {
            confirmaLocal();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


}
