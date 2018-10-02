package com.example.suelliton.agita.holders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.suelliton.agita.R;

import com.example.suelliton.agita.utils.ItemClickListener;

public class EventoViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    boolean like;
    public ImageView imagem;
    public TextView nome;
    public ImageView botaoLike;

    ItemClickListener itemClickListener;

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public EventoViewHolder(View itemView) {
        super(itemView);
        imagem = (ImageView) itemView.findViewById(R.id.imgEvento);
        nome = (TextView) itemView.findViewById(R.id.textNomeEvento);
        botaoLike = (ImageView) itemView.findViewById(R.id.buttonLike);
        itemView.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
     //   itemClickListener.onClick(v,getAdapterPosition(),false);
    }


}
