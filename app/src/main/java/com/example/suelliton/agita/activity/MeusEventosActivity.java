package com.example.suelliton.agita.activity;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.suelliton.agita.R;
import com.example.suelliton.agita.adapter.EventoAdapter;
import com.example.suelliton.agita.model.Evento;
import com.example.suelliton.agita.utils.MeuRecyclerViewClickListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.List;
import static com.example.suelliton.agita.activity.SplashActivity.LOGADO;

import static com.example.suelliton.agita.activity.EventoActivity.eventoClicado;
public class MeusEventosActivity extends AppCompatActivity {

    private DatabaseReference usuarioReference;
    private DatabaseReference eventosReference;
    private FirebaseStorage storage;
    private RecyclerView myrecycler;
    private List<Evento> lista;
    private Evento eventoSelecionado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meus_eventos);

        usuarioReference = FirebaseDatabase.getInstance().getReference("usuarios");
        eventosReference = FirebaseDatabase.getInstance().getReference("eventos");
        storage = FirebaseStorage.getInstance();

        myrecycler = (RecyclerView) findViewById(R.id.meus_eventos_recycler);
        iniciaLista();
        preparaRecycler();

        EventoAdapter eventoAdapter = new EventoAdapter(lista, MeusEventosActivity.this);
        myrecycler.setLayoutManager(new GridLayoutManager(MeusEventosActivity.this,2));
        myrecycler.setAdapter(eventoAdapter);
        eventoAdapter.notifyDataSetChanged();
    }

    private void iniciaLista() {
        lista = new ArrayList<>();
        eventosReference.orderByChild("dono").equalTo(LOGADO).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Evento evento = dataSnapshot.getValue(Evento.class);
                if (evento != null){
                    lista.add(evento);
                }
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
                eventoClicado = lista.get(i);
                if (eventoClicado != null) {
                    Intent in = new Intent(MeusEventosActivity.this, Detalhes.class);
                    in.putExtra("evento", eventoSelecionado);
                    startActivity(in);
                } else {
                    Toast.makeText(MeusEventosActivity.this, "Erro ao tentar visualizar os detalhes do evento", Toast.LENGTH_LONG);
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

}
