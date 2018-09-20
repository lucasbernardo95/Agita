package com.example.suelliton.agita.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.suelliton.agita.R;
import com.example.suelliton.agita.activity.Detalhes;
import com.example.suelliton.agita.activity.MeusEventosActivity;
import com.example.suelliton.agita.activity.SplashActivity;
import com.example.suelliton.agita.model.Evento;
import com.example.suelliton.agita.utils.MeuRecyclerViewClickListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.example.suelliton.agita.activity.EventoActivity.eventoClicado;
import static com.example.suelliton.agita.activity.SplashActivity.LOGADO;
import static com.example.suelliton.agita.activity.SplashActivity.eventosReference;
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
    boolean like;

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

        final Evento escolhido = eventos.get(position);
        myHolder.nome.setText(escolhido.getNome());

        //Oculta o botão de like se estiver na tela de meus eventos
        if (MeusEventosActivity.class == context.getClass())
            myHolder.botaoLike.setVisibility(View.GONE);


        //Vai fazer essa verificação quando carregar os eventos na tela
        //checa se o usuártio já deu like no evento atual ou não e seta a imagem correspondente
        if (escolhido.getParticipantes().contains(LOGADO)){
            like = true;//informa que deu lie
            myHolder.botaoLike.setBackgroundResource(R.drawable.ic_action_like);
        } else {
            like = false; ///indica que não deu like
            myHolder.botaoLike.setBackgroundResource(R.drawable.ic_action_nolike);
        }

        //implemmenta o click do botão like

        myHolder.botaoLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //sempre que ouver um click, altera o estado do botão e a variável like
                //altera a imagem para LIKE, indicando que o usuário deu like no evento
                if (!like) {//se não deu like ainda, muda a imagem para like
                    myHolder.botaoLike.setBackgroundResource(R.drawable.ic_action_like);
                    like = true; //passa a ser verdade, indicando que deu like
                }else {
                    myHolder.botaoLike.setBackgroundResource(R.drawable.ic_action_nolike);
                    like = false;//indica que deu deslike
                }
                Query query = eventosReference.orderByChild("nome").equalTo(escolhido.getNome());
                query.addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot data, @Nullable String s) {
                        Evento e = data.getValue(Evento.class);

                        List<String> participante = new ArrayList<>();
                        participante.add(LOGADO);
                        Map<String, Object> childUpdate = new HashMap<>();
                        //se deu like, coloca o nome do usuário na lista de participantes, senão, remove
                        if(like) {
                            childUpdate.put(data.getKey() + "/participantes", participante);
                            eventosReference.updateChildren(childUpdate);
                        } else {
                            // exemplo de url que essa linha abaixo faz ...eventos/-LMnZhJ62wp_uedUbOyd/participantes/indice/nome do usuário
                            Log.i("click", "link: "+eventosReference.child(data.getKey()+ "/participantes/"+e.getParticipantes().indexOf(LOGADO)));
                            eventosReference.child(data.getKey()+ "/participantes/"+e.getParticipantes().indexOf(LOGADO)).removeValue();
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
        });

        StorageReference storageReference = storage.getReference("eventos");

        StorageReference islandRef = storageReference.child(escolhido.getNome());
        islandRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).into(myHolder.imagem);
            }
        });


        myHolder.imagem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                eventoClicado = escolhido;
                context.startActivity(new Intent(context, Detalhes.class));
            }
        });

    }



    @Override
    public int getItemCount() {
        return eventos == null ? 0 :  eventos.size();
    }




    public class EventoHolder extends RecyclerView.ViewHolder {

        final ImageView imagem;
        final TextView nome;
        final ImageView botaoLike;

        public EventoHolder(View v) {
            super(v);

            imagem = (ImageView) v.findViewById(R.id.imgEvento);
            nome = (TextView) v.findViewById(R.id.textNomeEvento);
            botaoLike = (ImageView) v.findViewById(R.id.buttonLike);

        }


    }

}
