package com.example.suelliton.agita.activity;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.suelliton.agita.R;
import com.example.suelliton.agita.model.Evento;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

public class DeleteEventoActivity extends AppCompatActivity {

    private TextView nome, valor, local, data, hora, bandas, estilo, casa, dono, descricao;
    private Evento evento;
    private ImageView imagem;
    private ImageButton confirma;
    private DatabaseReference eventoAprova;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_evento);

        //recebe o evento clicado
        Bundle pacote = getIntent().getExtras();

        findViews();

        if (pacote != null) {
            evento = (Evento) pacote.getSerializable("eventoDelete");

            eventoAprova = FirebaseDatabase.getInstance().getReference("eventos");

            setContentEvent();
            setClickBotoes();

        } else {
            nome.setText(R.string.smsErroLoad);
        }

    }

    private void findViews() {
        nome        = (TextView) findViewById(R.id.nomeEventoDelete);
        valor       = (TextView) findViewById(R.id.valorEventoDelete);
        data        = (TextView) findViewById(R.id.dataEventoDelete);
        local       = (TextView) findViewById(R.id.localEventoDelete);
        hora        = (TextView) findViewById(R.id.horaEventoDelete);
        bandas      = (TextView) findViewById(R.id.bandasEventoDelete);
        estilo      = (TextView) findViewById(R.id.estiloEventoDelete);
        casa        = (TextView) findViewById(R.id.casaEventoDelete);
        dono        = (TextView) findViewById(R.id.donoEventoDelete);
        descricao   = (TextView) findViewById(R.id.descricaoEventoDelete);
        imagem      = (ImageView) findViewById(R.id.imageEventoDelete);
        confirma    = (ImageButton) findViewById(R.id.botaoConfirma);
    }

    private void setContentEvent() {
        nome.setText(evento.getNome());
        valor.setText(String.valueOf(evento.getValor()));
        data.setText(String.valueOf(evento.getData()));
        local.setText(evento.getLocal());
        hora.setText(evento.getHora());
        bandas.setText(evento.getBandas());
        estilo.setText(evento.getEstilo());
        casa.setText(evento.getCasashow());
        dono.setText(evento.getDono());
        descricao.setText(evento.getDescricao());

        StorageReference storageReference = FirebaseStorage.getInstance().getReference("eventos");

        StorageReference islandRef = storageReference.child(evento.getNome());
        islandRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).into(imagem);
            }
        });

    }

    private void setClickBotoes() {
        confirma.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                eventoAprova.orderByChild("nome").equalTo(evento.getNome()).addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                        eventoAprova.child(dataSnapshot.getKey()).removeValue();
                        startActivity(new Intent(DeleteEventoActivity.this, MeusEventosActivity.class));
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


    }
}
