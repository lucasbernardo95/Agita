package com.example.suelliton.agita.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.suelliton.agita.R;
import com.example.suelliton.agita.model.Evento;
import com.example.suelliton.agita.utils.GPSTracker;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import static com.example.suelliton.agita.activity.EventoActivity.eventoClicado;

public class Detalhes extends AppCompatActivity {
    private GPSTracker gpsTracker;
    private Location mlocation;
    private TextView nome,hora, data, valor, local,
            bandas, estilo, casa, dono, descricao;
    private ImageView imagem;
    private static final int LOCATION_REQUEST = 500;//caso dê erro na solicitação
    private static final int REQUEST_GPS = 3;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalhes);

        nome = (TextView) findViewById(R.id.tv_evento_clicado);
        hora    = (TextView) findViewById(R.id.textHoraEventoDetalhe);
        data    = (TextView) findViewById(R.id.textDataEventoDetalhe);
        valor   = (TextView) findViewById(R.id.textValorEventoDetalhe);
        local   = (TextView) findViewById(R.id.textLocalEventoDetalhe);
        bandas  = (TextView) findViewById(R.id.textBandasEventoDetalhe);
        estilo  = (TextView) findViewById(R.id.textEstiloEventoDetalhe);
        casa    = (TextView) findViewById(R.id.textCasaEventoDetalhe);
        dono    = (TextView) findViewById(R.id.textDonoEventoDetalhe);
        descricao = (TextView) findViewById(R.id.textDescricaoEventoDetalhe);
        imagem  = (ImageView) findViewById(R.id.imageEventoDetalhe);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.butonMap);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (ActivityCompat.checkSelfPermission(Detalhes.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(Detalhes.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                    ActivityCompat.requestPermissions(Detalhes.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST);

                    return;
                }


                gpsTracker = new GPSTracker(getApplicationContext()); //intancia a classe do GPS para pegar minha localização
                mlocation = gpsTracker.getLocation();
                LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

                boolean isOnGps = manager.isProviderEnabled(LocationManager.GPS_PROVIDER);

                if (!isOnGps) {
                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    Toast.makeText(Detalhes.this, "Ative o GPS do dispositivo", Toast.LENGTH_SHORT).show();
                    startActivityForResult(intent, REQUEST_GPS);
                }else{
                    try {
                        String uri = "http://maps.google.com/maps?saddr=" + mlocation.getLatitude() + "," + mlocation.getLongitude() + "&daddr=" + eventoClicado.getLatitude() + "," + eventoClicado.getLongitude();
                        Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(uri));
                        intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
                        startActivity(intent);
                    } catch (RuntimeException r) {
                        Toast.makeText(Detalhes.this, "Erro ao tentar localizar o local do evento.", Toast.LENGTH_LONG).show();
                    }

                }


            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        setContent();
    }

    private void setContent() {
        nome.setText(eventoClicado.getNome());
        hora.setText(String.valueOf(eventoClicado.getHora()));
        data.setText(String.valueOf(eventoClicado.getData()));
        valor.setText(String.valueOf(eventoClicado.getValor()));
        local.setText(eventoClicado.getLocal());
        bandas.setText(eventoClicado.getBandas());
        estilo.setText(eventoClicado.getEstilo());
        casa.setText(eventoClicado.getCasashow());
        dono.setText(eventoClicado.getDono());
        descricao.setText(eventoClicado.getDescricao());
        Picasso.get().load(eventoClicado.getUrlBanner()).into(imagem);

    }

}