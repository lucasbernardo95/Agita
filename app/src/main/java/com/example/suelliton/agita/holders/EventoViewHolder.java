package com.example.suelliton.agita.holders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.suelliton.agita.R;



public class EventoViewHolder extends RecyclerView.ViewHolder{
    boolean like;
    public ImageView imagem;
    public TextView nome;
    public ImageView botaoLike;

    public EventoViewHolder(View itemView) {
        super(itemView);
        imagem = (ImageView) itemView.findViewById(R.id.imgEvento);
        nome = (TextView) itemView.findViewById(R.id.textNomeEvento);
        botaoLike = (ImageView) itemView.findViewById(R.id.buttonLike);

    }

}
