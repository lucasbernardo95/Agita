package com.example.suelliton.agita.adapter;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.suelliton.agita.R;
import com.example.suelliton.agita.model.Evento;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.List;

/*
* É necessário o uso de um adapter para:
* fornecer dados para a lista de eventos e
* fornecer os métodos necessários para usar a lista
* */

public class EventoAdapter extends RecyclerView.Adapter{

    private List<Evento> eventos; //eventos do banco
    private Context context;
    FirebaseStorage storage;
    FrameLayout frame;

    public EventoAdapter(List<Evento> eventos, Context context) {
        this.eventos = eventos;
        this.context = context;
        storage = FirebaseStorage.getInstance();
    }

    //ok
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.inflate_evento, parent, false);//evento que tem os view a serem inflados

        EventoHolder holder = new EventoHolder(view);
        return holder;
    }

    //onBindViewHolder: Recebe o ViewHolder para setar os atributos da view.
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        final EventoHolder myHolder = (EventoHolder) holder;

        Evento escolhido = eventos.get(position);
//        myHolder.setImagem(R.);//tira a imagem até ter alguma no banco
        myHolder.local.setText(escolhido.getLocal());
        myHolder.horario.setText(escolhido.getHora());
        myHolder.nome.setText(escolhido.getNome());

        StorageReference storageReference = storage.getReference("eventos");

        StorageReference islandRef = storageReference.child(escolhido.getNome());
        islandRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).into(myHolder.imagem);
            }
        });





    }

    @Override
    public int getItemCount() {
        return eventos == null ? 0 :  eventos.size();
    }




    public class EventoHolder extends RecyclerView.ViewHolder {

        final ImageView imagem;
        final TextView local;
        final TextView horario;
        final TextView nome;

        public EventoHolder(View v) {
            super(v);

            imagem = (ImageView) v.findViewById(R.id.imgEvento);
            //imagem.setImageResource(R.drawable.checked);
            local = (TextView) v.findViewById(R.id.textLocalEvento);
            horario = (TextView) v.findViewById(R.id.textHorarioEvento);
            nome = (TextView) v.findViewById(R.id.textNomeEvento);

        }


    }

}
