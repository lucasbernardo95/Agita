package com.example.suelliton.agita.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.example.suelliton.agita.R;
import com.example.suelliton.agita.fragment.TodosEventoFragment;
import com.example.suelliton.agita.holder.EventoHolder;
import com.example.suelliton.agita.model.Evento;

import java.util.ArrayList;
import java.util.List;

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
        this.context = context;
    }

    //ok
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.content_evento, parent, false);//evento que tem os view a serem inflados

        EventoHolder holder = new EventoHolder(view);
        return holder;
    }

    //onBindViewHolder: Recebe o ViewHolder para setar os atributos da view.
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        EventoHolder myHolder = (EventoHolder) holder;

        Evento escolhido = eventos.get(position);
//        myHolder.setImagem(R.);//tira a imagem até ter alguma no banco
        myHolder.getLocal().setText(escolhido.getLocal());
        myHolder.getHorario().setText(escolhido.getHora());
        myHolder.getNome().setText(escolhido.getNome());
    }

    @Override
    public int getItemCount() {
        return eventos.size();
    }
}
