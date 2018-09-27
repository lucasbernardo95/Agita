package com.example.suelliton.agita.activity;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.suelliton.agita.R;
import com.example.suelliton.agita.model.Evento;
import com.example.suelliton.agita.utils.AdminViewHolder;
import com.example.suelliton.agita.utils.ItemClickListener;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

public class AdminActivity extends AppCompatActivity {

    private RecyclerView recyler;
    private RecyclerView.LayoutManager layoutManager;
    private TextView nome, data, valor, local;
    private DatabaseReference eventoAprova;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        recyler = (RecyclerView) findViewById(R.id.recycler_admin);
        recyler.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyler.setLayoutManager(layoutManager);

        eventoAprova = FirebaseDatabase.getInstance().getReference("eventos");
        loadEventosNaoAprovados();
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    private void loadEventosNaoAprovados() {
            final FirebaseRecyclerAdapter<Evento, AdminViewHolder> adapter = new FirebaseRecyclerAdapter<Evento, AdminViewHolder>(Evento.class, R.layout.admin_content_evento, AdminViewHolder.class, eventoAprova.orderByChild("verificado").equalTo(false)) {

                @Override
                protected void populateViewHolder(final AdminViewHolder viewHolder, final Evento model, int position) {
                    viewHolder.nome.setText(model.getNome());
                    viewHolder.hora.setText(model.getHora());
                    viewHolder.bandas.setText(model.getBandas());
                    viewHolder.casa.setText(model.getCasashow());
                    viewHolder.descricao.setText(model.getDescricao());
                    viewHolder.dono.setText(model.getDono());
                    viewHolder.estilo.setText(model.getEstilo());
                    viewHolder.local.setText(model.getLocal());
                    viewHolder.data.setText(String.valueOf(model.getData()));
                    viewHolder.valor.setText(String.valueOf(model.getValor()));


                    StorageReference storageReference = FirebaseStorage.getInstance().getReference("eventos");

                    StorageReference islandRef = storageReference.child(model.getNome());
                    islandRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Picasso.get().load(uri).into(viewHolder.imagem);
                        }
                    });

                    //implementa o click no evento
                    viewHolder.setItemClickListener(new ItemClickListener() {
                        @Override
                        public void onClick(View view, int position, boolean isLongClick) {

                            viewHolder.noAprova.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                    eventoAprova.orderByChild("nome").equalTo(model.getNome()).addChildEventListener(new ChildEventListener() {
                                        @Override
                                        public void onChildAdded(@NonNull DataSnapshot data, @Nullable String s) {
                                            model.setVerificado(false); //seta como RE-aprovado
                                            Map<String, Object> att = new HashMap<>();
                                            att.put(data.getKey(), model);

                                            eventoAprova.updateChildren(att);
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
                            });

                            viewHolder.aprova.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    eventoAprova.orderByChild("nome").equalTo(model.getNome()).addChildEventListener(new ChildEventListener() {
                                        @Override
                                        public void onChildAdded(@NonNull DataSnapshot data, @Nullable String s) {

                                            model.setVerificado(true); //seta como aprovado
                                            Map<String, Object> att = new HashMap<>();
                                            att.put(data.getKey(), model);

                                            eventoAprova.updateChildren(att);

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
                            });

                            viewHolder.exclui.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {

                                    eventoAprova.orderByChild("nome").equalTo(model.getNome()).addChildEventListener(new ChildEventListener() {
                                        @Override
                                        public void onChildAdded(@NonNull DataSnapshot data, @Nullable String s) {
                                            eventoAprova.child(data.getKey()).removeValue();
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
                            });

                        } //chave do click geral
                    });
                }
            };

            recyler.setAdapter(adapter);

    }

}
