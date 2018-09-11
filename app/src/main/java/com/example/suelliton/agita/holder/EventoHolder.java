package com.example.suelliton.agita.holder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.suelliton.agita.R;

/*
* ViewHolder para os elementos da tela de exibição dos eventos
*/

public class EventoHolder extends RecyclerView.ViewHolder {

    private ImageView imagem;
    private TextView local;
    private TextView horario;
    private TextView nome;

    public EventoHolder(View v) {
        super(v);

        imagem = (ImageView) v.findViewById(R.id.imgEvento);
        imagem.setImageResource(R.drawable.checked);
        local = (TextView) v.findViewById(R.id.textLocalEvento);
        horario = (TextView) v.findViewById(R.id.textHorarioEvento);
        nome = (TextView) v.findViewById(R.id.textNomeEvento);

    }

    public ImageView getImagem() {
        return imagem;
    }

    public void setImagem(ImageView imagem) {
        this.imagem = imagem;
    }

    public TextView getLocal() {
        return local;
    }

    public void setLocal(TextView local) {
        this.local = local;
    }

    public TextView getHorario() {
        return horario;
    }

    public void setHorario(TextView horario) {
        this.horario = horario;
    }

    public TextView getNome() {
        return nome;
    }

    public void setNome(TextView nome) {
        this.nome = nome;
    }
}
