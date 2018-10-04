package com.example.suelliton.agita.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.support.annotation.NonNull;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.example.suelliton.agita.R;
import com.example.suelliton.agita.activity.AddEventoActivity;
import com.example.suelliton.agita.activity.EditEventoActivity;
import com.example.suelliton.agita.activity.EventoActivity;
import com.example.suelliton.agita.model.Evento;
import com.example.suelliton.agita.utils.PaletteListener;
import com.github.chrisbanes.photoview.PhotoView;
import com.github.chrisbanes.photoview.PhotoViewAttacher;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;
import java.util.List;
import java.util.logging.Handler;

import static com.example.suelliton.agita.activity.EventoActivity.eventoClicado;
import static com.example.suelliton.agita.activity.EventoActivity.eventosParticiparei;
import static com.example.suelliton.agita.activity.EventoActivity.master;
import static com.example.suelliton.agita.activity.SplashActivity.database;
import static com.example.suelliton.agita.activity.SplashActivity.eventosReference;
import static com.example.suelliton.agita.activity.SplashActivity.usuarioLogado;
import static com.example.suelliton.agita.activity.SplashActivity.usuarioReference;
import static java.security.AccessController.getContext;
/*
* É necessário o uso de um adapter para:
* fornecer dados para a lista de eventos e
* fornecer os métodos necessários para usar a lista
* */

public class EventoAdapter extends RecyclerView.Adapter{

    private List<Evento> eventos; //eventos do banco
    private Context context;

    public EventoAdapter(List<Evento> eventos, Context context) {
        this.eventos = eventos;
        this.context = context;       ;
    }

    //ok
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.inflate_evento, parent, false);//filtro_eventos que tem os view a serem inflados

        EventoHolder holder = new EventoHolder(view);
        return holder;
    }

    //onBindViewHolder: Recebe o ViewHolder para setar os atributos da view.
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        final EventoHolder myHolder = (EventoHolder) holder;
        final Evento escolhido = eventos.get(position);

        myHolder.nome.setText(escolhido.getNome());
            //Se o evento pertencer ao usuário logado, ele pode editar e excluir se estiver na aba meus eventos
        if(usuarioLogado != null) {
            if(master.equals("meusEventos")){//aba meus eventos
                if (usuarioLogado.getLogin().equals(escolhido.getDono()) ) {
                    //mostra botoes de edição
                    myHolder.botaoEditar.setVisibility(View.VISIBLE);
                    myHolder.botaoExcluir.setVisibility(View.VISIBLE);
                    
                    //Oculta o botão de like e nomeDetalhe do evento para o donoDetalhe
                    myHolder.botaoLike.setVisibility(View.GONE);
                    //myHolder.nomeDetalhe.setVisibility(View.GONE);
                    //Se o evento ainda não foi verificado, mostra um botão de alerta
                    if (escolhido.isVerificado()) {
                        myHolder.botaoAlerta.setVisibility(View.GONE);
                    } else {
                        //Oculta o nomeDetalhe do evento para evitar bugs visuais
                       // myHolder.nomeDetalhe.setVisibility(View.GONE);
                        myHolder.botaoAlerta.setVisibility(View.VISIBLE);
                    }
                }
            }else{//demais abas
                    //layut padrao só com o coraçao
                    myHolder.botaoEditar.setVisibility(View.GONE);
                    myHolder.botaoExcluir.setVisibility(View.GONE);
                    //Se o evento ainda não foi verificado, mostra um botão de alerta
                    myHolder.botaoAlerta.setVisibility(View.GONE);
                    myHolder.nome.setVisibility(View.VISIBLE);
            }
        }

        myHolder.botaoEditar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                context.startActivity(new Intent(context, EditEventoActivity.class).putExtra("eventoEdit",escolhido));
            }
        });

        myHolder.botaoExcluir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertEventoDelet(context, escolhido);
            }
        });

        myHolder.botaoAlerta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(context)
                        .setTitle("Aguardando aprovação!")
                        .setMessage("Este evento ainda não foi aprovado pela administração do Agita.\nO evento só estará disponível para o público após sua aprovação.")
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                            }
                        }).show();
            }
        });

        //Vai fazer essa verificação quando carregar os eventos na tela
        //checa se o usuártio já deu like no filtro_eventos atual ou não e seta a imagemDetalhe correspondente

        //implemmenta o click do botão like
        final boolean[] like = new boolean[1];
        if(usuarioLogado != null) {
            if (eventosParticiparei != null){
                if (eventosParticiparei.contains(escolhido.getKey())) {
                    like[0] = true;
                    myHolder.botaoLike.setBackgroundResource(R.drawable.ic_action_like);
                } else {
                    like[0] = false;
                    myHolder.botaoLike.setBackgroundResource(R.drawable.ic_action_nolike);
                }
            }
        }
        myHolder.botaoLike.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                //Toast.makeText(context, "clicou", Toast.LENGTH_SHORT).show();
                if(usuarioLogado !=null) {
                    if (like[0]) {
                        like[0] = false;
                        myHolder.botaoLike.setBackgroundResource(R.drawable.ic_action_nolike);
                        eventosParticiparei.remove(escolhido.getKey());
                        eventosReference.child(escolhido.getKey()).child("qtdParticipantes").setValue(escolhido.getQtdParticipantes() - 1);
                    } else {
                        like[0] = true;
                        myHolder.botaoLike.setBackgroundResource(R.drawable.ic_action_like);
                        eventosParticiparei.add(escolhido.getKey());
                        eventosReference.child(escolhido.getKey()).child("qtdParticipantes").setValue(escolhido.getQtdParticipantes() + 1);
                    }
                    usuarioLogado.setParticiparei(eventosParticiparei);
                    usuarioReference.child(usuarioLogado.getLogin()).setValue(usuarioLogado);
                    view.requestFocus();

                }else{
                    Toast.makeText(context, "Faça login para curtir o evento", Toast.LENGTH_SHORT).show();
                }


            }
        });
        try {
            Picasso.get()
                    .load(escolhido.getUrlBanner())
                    .into(myHolder.imagem);
        } catch (RuntimeException e) {
            e.printStackTrace();
        }


        /*, new com.squareup.picasso.Callback() {
                    @Override
                    public void onSuccess() {
                        BitmapDrawable bitmapDrawable = (BitmapDrawable) myHolder.imagem.getDrawable();
                        createPaletteAsync(bitmapDrawable.getBitmap(),myHolder.imagem);
                    }

                    @Override
                    public void onError(Exception ex) {

                    }
                });*/

        myHolder.imagem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                eventoClicado = escolhido;
                EventoActivity.setContentDetalhes();
                //context.startActivity(new Intent(context, Detalhes.class));
            }
        });
    }
    public void createPaletteAsync(Bitmap bitmap, final ImageView imageView) {
        Palette.from(bitmap).generate(new Palette.PaletteAsyncListener() {
            public void onGenerated(Palette p) {
                imageView.setBackgroundColor(p.getMutedColor(0));
            }
        });
    }
    public Palette createPaletteSync(Bitmap bitmap) {
        Palette p = Palette.from(bitmap).generate();
        return p;
    }

    @Override
    public int getItemCount() {
        return eventos == null ? 0 :  eventos.size();
    }


    public class EventoHolder extends RecyclerView.ViewHolder {

        final ImageView imagem;
        final TextView nome;
        final ImageView botaoLike;
        final ImageButton botaoEditar, botaoExcluir, botaoAlerta;

        public EventoHolder(View v) {
            super(v);
            imagem = (ImageView) v.findViewById(R.id.imgEvento);
            nome = (TextView) v.findViewById(R.id.textNomeEvento);
            botaoLike = (ImageView) v.findViewById(R.id.buttonLike);
            botaoEditar = (ImageButton) v.findViewById(R.id.buttonEdit);
            botaoExcluir = (ImageButton) v.findViewById(R.id.buttonDelete);
            botaoAlerta = (ImageButton) v.findViewById(R.id.buttonAlert);





        }
    }

    private void alertEventoDelet(Context context, final Evento evento) {
        new AlertDialog.Builder(context)
                .setTitle("Deletando "+evento.getNome())
                .setMessage("Tem certeza que deseja deletar esse evento?")
                .setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        deleteEvent(evento);
                        deleteBannerEvent(evento.getKey());
                    }
                })
                .setNegativeButton("Não", null)
                .show();
    }

    private void deleteEvent(Evento evento) {
        DatabaseReference workReference;
        if(evento.isVerificado()){
            workReference = database.getReference("eventos");
        }else{
            workReference = database.getReference("eventoTemporario");
        }
        workReference.child(evento.getKey()).removeValue();
    }

    private void deleteBannerEvent(String key){
        StorageReference storage = FirebaseStorage.getInstance().getReference("eventos").child(key);
        storage.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(context, "Exluído com sucesso!", Toast.LENGTH_SHORT).show();
            }
        });
    }

}
