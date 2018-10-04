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
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuInflater;

import com.example.suelliton.agita.R;
import com.example.suelliton.agita.adapter.EventAdapterAdmin;
import com.example.suelliton.agita.model.Evento;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

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
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_busca_admin, menu);

        //Pega o Componente de busca do actionBar
        SearchView searchView = (SearchView) menu.findItem(R.id.ic_search_admin).getActionView();
        //Define um texto de ajuda 'texto fantasma':
        searchView.setQueryHint(getString(R.string.texto_hint_campoBusca));
        // exemplos de utilização:
        buscarEvento(searchView);
        return super.onCreateOptionsMenu(menu);
    }

    private void buscarEvento(SearchView searchView) {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String valorBuscado) {
                if (adapterAdmin != null)
                    adapterAdmin.getFilter().filter(valorBuscado); //Inicia ao filtro das buscas
                return false;
            }
        });
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

