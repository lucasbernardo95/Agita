package com.example.suelliton.agita.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.example.suelliton.agita.R;
import com.example.suelliton.agita.activity.EditEventoActivity;
import com.example.suelliton.agita.activity.EventoActivity;
import com.example.suelliton.agita.model.Evento;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import static com.example.suelliton.agita.activity.EventoActivity.eventoClicado;
import static com.example.suelliton.agita.activity.EventoActivity.eventosCurtidos;
import static com.example.suelliton.agita.activity.EventoActivity.master;
import static com.example.suelliton.agita.activity.SplashActivity.database;
import static com.example.suelliton.agita.activity.SplashActivity.eventosReference;
import static com.example.suelliton.agita.activity.SplashActivity.usuarioLogado;
import static com.example.suelliton.agita.activity.SplashActivity.usuarioReference;
/*
* É necessário o uso de um adapter para:
* fornecer dados para a lista de eventos e
* fornecer os métodos necessários para usar a lista
* */

public class EventoAdapter extends RecyclerView.Adapter implements Filterable {

    private List<Evento> eventos, backuplista; //eventos  = lista filtrada (eventos que estão sendo exibidos) | backuplista = lista com todos os eventos
    private Context context;

    public EventoAdapter(List<Evento> eventos, Context context) {
        this.eventos = eventos;
        this.backuplista = eventos;
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
                    myHolder.botaoLike.setVisibility(View.VISIBLE);
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
                if (eventosCurtidos != null && eventosCurtidos.contains(escolhido.getKey())) {
                    like[0] = true;
                    myHolder.botaoLike.setBackgroundResource(R.drawable.ic_action_like);
                }else {
                    like[0] = false;
                    myHolder.botaoLike.setBackgroundResource(R.drawable.ic_action_nolike);
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
                        eventosCurtidos.remove(escolhido.getKey());
                        eventosReference.child(escolhido.getKey()).child("qtdParticipantes").setValue(escolhido.getQtdCurtidas() - 1);
                    } else {
                        like[0] = true;
                        myHolder.botaoLike.setBackgroundResource(R.drawable.ic_action_like);
                        eventosCurtidos.add(escolhido.getKey());
                        eventosReference.child(escolhido.getKey()).child("qtdParticipantes").setValue(escolhido.getQtdCurtidas() + 1);
                    }
                    usuarioLogado.setCurtidos(eventosCurtidos);
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

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {

                String charString = charSequence.toString();
                //Se a pesquisa for vazia, lista todos os eventos normalmente
                if (charString.isEmpty()) {
                    eventos = backuplista;
                } else {

                    ArrayList<Evento> filteredList = new ArrayList<>();

                    for (Evento evento : backuplista) {
                        //Se o que foi digitado estiver contido e qualquer um dos campos do evento
                        // listados abaixo, coloca esse evento na lista filtrada
                        if (evento.getNome().toLowerCase().contains(charString) ||
                                evento.getBandas().toLowerCase().contains(charString) ||
                                evento.getDescricao().toLowerCase().contains(charString) ||
                                evento.getCasashow().toLowerCase().contains(charString) ||
                                evento.getEstilo().toLowerCase().contains(charString) ||
                                evento.getEndereco().toLowerCase().contains(charString) ||
                                evento.getData().toLowerCase().contains(charString) ||
                                evento.getHora().toLowerCase().contains(charString) ||
                                evento.getDono().toLowerCase().contains(charString) ||
                                String.valueOf(evento.getEntrada()).toLowerCase().contains(charString) ) {

                            filteredList.add(evento);
                        }
                    }
                    //Os eventos que estão sendo exibidos recebem a nova lista de eventos filtrados
                    eventos = filteredList;
                }
                //Adiciona a lista de eventos filtrados ao retorno do método
                FilterResults filterResults = new FilterResults();
                filterResults.values = eventos;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                eventos = (ArrayList<Evento>) filterResults.values; //recebe o retorno de performFiltering, onde os resultados foram filtrados (eventos)
                notifyDataSetChanged();
            }
        };
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
