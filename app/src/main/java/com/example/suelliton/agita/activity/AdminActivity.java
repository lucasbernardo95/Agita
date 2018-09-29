package com.example.suelliton.agita.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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
    private DatabaseReference eventoAprovado, eventoTemporario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        recyler = (RecyclerView) findViewById(R.id.recycler_admin);
        recyler.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyler.setLayoutManager(layoutManager);

        eventoAprovado = FirebaseDatabase.getInstance().getReference("eventos");
        eventoTemporario = FirebaseDatabase.getInstance().getReference("eventoTemporario");
        loadEventosNaoAprovados();
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    private void loadEventosNaoAprovados() {
            final FirebaseRecyclerAdapter<Evento, AdminViewHolder> adapter = new FirebaseRecyclerAdapter<Evento, AdminViewHolder>(Evento.class, R.layout.admin_content_evento, AdminViewHolder.class, eventoTemporario) {

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

                    StorageReference islandRef = storageReference.child(model.getKey());
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

                            viewHolder.aprova.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    eventoTemporario.orderByChild("nome").equalTo(model.getNome()).addChildEventListener(new ChildEventListener() {
                                        @Override
                                        public void onChildAdded(@NonNull DataSnapshot data, @Nullable String s) {

                                            aletEventVerify(AdminActivity.this, model, data.getKey());

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

                                    eventoTemporario.orderByChild("nome").equalTo(model.getNome()).addChildEventListener(new ChildEventListener() {
                                        @Override
                                        public void onChildAdded(@NonNull DataSnapshot data, @Nullable String s) {
                                            alertEventoDelet(AdminActivity.this, model, data.getKey());
                                            deleteBannerEvent(data.getKey());
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

    private void alertEventoDelet(Context context, final Evento evento, final String key) {
        new AlertDialog.Builder(context)
                .setTitle("Deletando "+evento.getNome())
                .setMessage("Tem certeza que deseja deletar esse evento?")
                .setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        eventoTemporario.child(key).removeValue();
                    }
                })
                .setNegativeButton("Não", null)
                .show();
    }

    private void deleteBannerEvent(String key){
        StorageReference storage = FirebaseStorage.getInstance().getReference("eventos").child(key);
        storage.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(AdminActivity.this, "Exluído com sucesso!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void aletEventVerify(Context context, final Evento model, final  String key) {
        new AlertDialog.Builder(context)
                .setTitle("Confirmação de validação")
                .setMessage("Tem certeza que deseja prosseguir?")
                .setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        //seta como verificado
                        model.setVerificado(true);
                        //Salva o evento em outra tabela
                        eventoAprovado.push().setValue(model).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Query query = eventoAprovado.orderByChild("nome").startAt(model.getNome()).endAt(model.getNome()).limitToFirst(1);
                                query.addChildEventListener(new ChildEventListener() {
                                    @Override
                                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                                        eventoAprovado.child(dataSnapshot.getRef().getKey()).child("key").setValue(dataSnapshot.getRef().getKey());
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





                                //apaga o evento antigo
                        eventoTemporario.child(key).removeValue();





                            }

                })
                .setNegativeButton("Não", null)
                .show();
    }

}

