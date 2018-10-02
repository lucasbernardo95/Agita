package com.example.suelliton.agita.activity;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.example.suelliton.agita.R;
import com.example.suelliton.agita.adapter.EventoAdapter;
import com.example.suelliton.agita.holders.EventoViewHolder;
import com.example.suelliton.agita.model.Evento;
import com.example.suelliton.agita.utils.MyDatabaseUtil;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;


import static com.example.suelliton.agita.activity.EventoActivity.eventoClicado;
import static com.example.suelliton.agita.activity.SplashActivity.database;
import static com.example.suelliton.agita.activity.SplashActivity.eventosReference;
import static com.example.suelliton.agita.activity.SplashActivity.usuarioLogado;
import static com.example.suelliton.agita.activity.SplashActivity.usuarioReference;

public class MeusEventosActivity extends AppCompatActivity {

    private RecyclerView myrecycler;
    private List<Evento> listaEventos;
    FirebaseRecyclerAdapter<Evento,EventoViewHolder> adapter;
    FirebaseDatabase database ;
    DatabaseReference eventosReference;
    EventoAdapter eventoAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meus_eventos);
        database = MyDatabaseUtil.getDatabase();
        eventosReference = database.getReference("eventos");

        myrecycler = (RecyclerView) findViewById(R.id.meus_eventos_recycler);


    }

    @Override
    protected void onStart() {
        super.onStart();
        iniciaLista("meus");

    }

    public void iniciaLista(final String fieldOrder) {
        myrecycler.setLayoutManager(new GridLayoutManager(MeusEventosActivity.this,2));

        Query query = null;
        if(fieldOrder.equals("meus")) {
            query = eventosReference.orderByChild("donoDetalhe").equalTo(usuarioLogado.getLogin());
            List<String> keyEventos = usuarioLogado.getParticiparei();
            listaEventos = new ArrayList<>();
            eventoAdapter = new EventoAdapter(listaEventos,MeusEventosActivity.this);
            myrecycler.setAdapter(eventoAdapter);

            query.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    listaEventos.add(dataSnapshot.getValue(Evento.class));
                    eventoAdapter.notifyDataSetChanged();
                }

                @Override
                public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                }

                @Override
                public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                    for(int i=0;i<listaEventos.size();i++){
                        if(listaEventos.get(i).getKey().equals(dataSnapshot.getValue(Evento.class).getKey())){
                            listaEventos.remove(listaEventos.get(i));
                        }
                    }
                    eventoAdapter.notifyDataSetChanged();
                }

                @Override
                public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });


        }else if(fieldOrder.equals("participarei")){
            List<String> keyEventos = usuarioLogado.getParticiparei();
            if(keyEventos == null) keyEventos = new ArrayList<>();
            listaEventos = new ArrayList<>();
            eventoAdapter = new EventoAdapter(listaEventos,MeusEventosActivity.this);
            myrecycler.setAdapter(eventoAdapter);
            for (String key : keyEventos ) {//parei aqui
                query = eventosReference.child(key);
                query.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.getValue(Evento.class) != null) {
                            listaEventos.add(dataSnapshot.getValue(Evento.class));
                            Log.i("part", dataSnapshot.getValue(Evento.class).getNome());
                            eventoAdapter.notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }

        }

    }

    public void setValuesViewHolder(final EventoViewHolder viewHolder,Evento model){
        viewHolder.nome.setText(model.getNome());
        if(!model.getUrlBanner().equals("")) {
            Picasso.get().load(model.getUrlBanner()).into(viewHolder.imagem);
        }
        final Evento evento = model;
        final boolean[] like = new boolean[1];

        viewHolder.imagem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                eventoClicado = evento;
                startActivity(new Intent(MeusEventosActivity.this,Detalhes.class));
            }
        });



    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.filtro_meus_eventos, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.opcao_filtro_meus:
                iniciaLista("meus");
                return true;
            case R.id.opcao_filtro_participarei:
                iniciaLista("participarei");
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
