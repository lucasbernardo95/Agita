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
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.suelliton.agita.R;
import com.example.suelliton.agita.adapter.EventoAdapter;
import com.example.suelliton.agita.model.Evento;
import com.example.suelliton.agita.model.Usuario;
import com.example.suelliton.agita.utils.MeuRecyclerViewClickListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.List;
import static com.example.suelliton.agita.activity.SplashActivity.LOGADO;

import static com.example.suelliton.agita.activity.EventoActivity.eventoClicado;
import static com.example.suelliton.agita.activity.SplashActivity.eventosReference;
import static com.example.suelliton.agita.activity.SplashActivity.usuarioReference;

public class MeusEventosActivity extends AppCompatActivity {

    private RecyclerView myrecycler;
    private List<Evento> lista;
    private String filtroBusca;
    private boolean admin;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meus_eventos);


        myrecycler = (RecyclerView) findViewById(R.id.meus_eventos_recycler);

        //verifica se o usuário é admin ou não
        verificaUsuario();

        if (admin)
            iniciaListaAdmin();
        else
            iniciaListaUsuario("nome");

        preparaRecycler();

        EventoAdapter eventoAdapter = new EventoAdapter(lista, MeusEventosActivity.this);
        myrecycler.setLayoutManager(new GridLayoutManager(MeusEventosActivity.this,2));
        myrecycler.setAdapter(eventoAdapter);
        eventoAdapter.notifyDataSetChanged();
    }

    private void verificaUsuario(){
        usuarioReference.orderByChild("login").equalTo(LOGADO).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot data, @Nullable String s) {
                Usuario u = data.getValue(Usuario.class);
                admin = u.isAdmin();
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

    private void iniciaListaAdmin() {
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

    private void iniciaListaUsuario(String campo) {
        lista = new ArrayList<>();

        Query query = eventosReference;

        query.orderByChild(campo).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//                Evento evento = dataSnapshot.getValue(Evento.class);
                lista.add(dataSnapshot.getValue(Evento.class));

                Log.i("busca",
                        "\nroot:"+eventosReference +
                "\nparent:"+eventosReference.child(dataSnapshot.getRef().getKey()).child("participantes").getParent()+
                "\nkey: "+dataSnapshot.getKey());
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
                    startActivity(new Intent(MeusEventosActivity.this, Detalhes.class));
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
            case R.id.opcao_filtro_data:

                return true;
            case R.id.opcao_filtro_nome:

                return true;
            case R.id.opcao_filtro_estilo:

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
