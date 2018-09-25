package com.example.suelliton.agita.utils;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.suelliton.agita.R;
import com.example.suelliton.agita.model.Evento;

public class AdminViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

    private ItemClickListener itemClickListener;

    public TextView nome, valor, local, data, hora, bandas, estilo, casa, dono, descricao;
    public ImageView imagem, aprova, noAprova, exclui;

    public AdminViewHolder(View itemView) {
        super(itemView);

        nome = (TextView) itemView.findViewById(R.id.nomeEventoAdmin);
        valor = (TextView) itemView.findViewById(R.id.valorEventoAdmin);
        data = (TextView) itemView.findViewById(R.id.dataEventoAdmin);
        local = (TextView) itemView.findViewById(R.id.localEventoAdmin);
        hora = (TextView) itemView.findViewById(R.id.horaEventoAdmin);
        bandas = (TextView) itemView.findViewById(R.id.bandasEventoAdmin);
        estilo = (TextView) itemView.findViewById(R.id.estiloEventoAdmin);
        casa = (TextView) itemView.findViewById(R.id.casaEventoAdmin);
        dono = (TextView) itemView.findViewById(R.id.donoEventoAdmin);
        descricao = (TextView) itemView.findViewById(R.id.descricaoEventoAdmin);
        imagem = (ImageView) itemView.findViewById(R.id.imageEventoAdmin);
        aprova = (ImageView) itemView.findViewById(R.id.botaoAprova);
        noAprova = (ImageView) itemView.findViewById(R.id.botaoRepprova);
        exclui = (ImageView) itemView.findViewById(R.id.botaoExcluir);

        itemView.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        itemClickListener.onClick(v, getAdapterPosition(), false);
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }
}
