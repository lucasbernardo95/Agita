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
import android.widget.Toast;

import com.example.suelliton.agita.R;
import com.example.suelliton.agita.adapter.EventoAdapter;
import com.example.suelliton.agita.model.Evento;
import com.example.suelliton.agita.model.Participante;
import com.example.suelliton.agita.model.Usuario;
import com.example.suelliton.agita.utils.MeuRecyclerViewClickListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;

import java.util.ArrayList;
import java.util.List;
import static com.example.suelliton.agita.activity.SplashActivity.LOGADO;

import static com.example.suelliton.agita.activity.EventoActivity.eventoClicado;
import static com.example.suelliton.agita.activity.SplashActivity.eventosReference;
import static com.example.suelliton.agita.activity.SplashActivity.usuarioReference;

public class MeusEventosActivity extends AppCompatActivity {

    private RecyclerView myrecycler;
    private List<Evento> listaEventos;
    private String filtroBusca;
    private boolean admin;

    EventoAdapter eventoAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meus_eventos);


        myrecycler = (RecyclerView) findViewById(R.id.meus_eventos_recycler);


        iniciaListaUsuario("meus");

        preparaRecycler();


    }



    private void iniciaListaUsuario(final String opcao) {
        listaEventos = new ArrayList<>();
        eventoAdapter = new EventoAdapter(listaEventos, MeusEventosActivity.this);
        myrecycler.setLayoutManager(new GridLayoutManager(MeusEventosActivity.this,2));
        myrecycler.setAdapter(eventoAdapter);
        eventoAdapter.notifyDataSetChanged();

            final Query query = eventosReference;
            query.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    final Evento evento = dataSnapshot.getValue(Evento.class);

                    if(opcao.equals("meus")) {
                        if (evento.getDono().equals(LOGADO)) {
                            listaEventos.add(dataSnapshot.getValue(Evento.class));
                        }
                     }else if(opcao.equals("participarei")){
                        Query query1 = eventosReference.child(evento.getKey()).child("participantes");
                        query1.addChildEventListener(new ChildEventListener() {
                            @Override
                            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                                    Participante participante = dataSnapshot.getValue(Participante.class);
                                    if(participante.getNome().equals(LOGADO)){
                                        listaEventos.add(evento);
                                        Log.i("part",participante.getNome());
                                    }
                                eventoAdapter.notifyDataSetChanged();
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
                    eventoAdapter.notifyDataSetChanged();
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

    private void preparaRecycler() {
        myrecycler.addOnItemTouchListener(new MeuRecyclerViewClickListener(MeusEventosActivity.this, myrecycler, new MeuRecyclerViewClickListener.OnItemClickListener() {

            @Override
            public void OnItemClick(View view, int i) {
                eventoClicado = listaEventos.get(i);
                if (eventoClicado != null) {
                    startActivity(new Intent(MeusEventosActivity.this, Detalhes.class));
                } else {
                    Toast.makeText(MeusEventosActivity.this, "Erro ao tentar visualizar os detalhes do filtro_todos_eventos", Toast.LENGTH_LONG);
                }
            }

            @Override
            public void OnItemLongClick(View view, int i) {

            }

            @Override
            public void onItemClick(View view, int position) {

            }
        }));

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
                iniciaListaUsuario("meus");
                return true;
            case R.id.opcao_filtro_participarei:
                iniciaListaUsuario("participarei");
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
