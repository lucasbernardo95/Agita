package com.example.suelliton.agita.activity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import com.example.suelliton.agita.R;
import com.example.suelliton.agita.adapter.EventAdapterAdmin;
import com.example.suelliton.agita.model.Evento;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.List;

import static com.example.suelliton.agita.activity.EventoActivity.temporarioReference;


public class AdminActivity extends AppCompatActivity {

    private RecyclerView recyler;
    public static EventAdapterAdmin adapterAdmin;
    public static List<Evento> listaEventos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        recyler = (RecyclerView) findViewById(R.id.recycler_admin);
        recyler.setHasFixedSize(true);
        recyler.setLayoutManager(new LinearLayoutManager(this));

        listaEventos = new ArrayList<>();

        iniciaLista();

        adapterAdmin = new EventAdapterAdmin(listaEventos,this);
        recyler.setAdapter(adapterAdmin);

    }

    @Override
    protected void onResume() {
        super.onResume();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 500);

            return;
        }

    }

    private void iniciaLista() {
        temporarioReference.orderByKey().addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                listaEventos.add(dataSnapshot.getValue(Evento.class));
//                adapterAdmin.notifyDataSetChanged();
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
    }

}

