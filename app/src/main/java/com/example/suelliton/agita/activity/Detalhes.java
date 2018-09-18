package com.example.suelliton.agita.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.example.suelliton.agita.R;

import static com.example.suelliton.agita.activity.EventoActivity.eventoClicado;

public class Detalhes extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalhes);

        TextView textView = (TextView) findViewById(R.id.tv_evento_clicado);
        textView.setText(eventoClicado.getNome());

        ((TextView) findViewById(R.id.textHoraEventoDetalhe)).setText(String.valueOf(eventoClicado.getHora()));
        ((TextView) findViewById(R.id.textDataEventoDetalhe)).setText(String.valueOf(eventoClicado.getData()));
        ((TextView) findViewById(R.id.textValorEventoDetalhe)).setText(String.valueOf(eventoClicado.getValor()));
        ((TextView) findViewById(R.id.textLocalEventoDetalhe)).setText(eventoClicado.getLocal());
        ((TextView) findViewById(R.id.textBandasEventoDetalhe)).setText(eventoClicado.getBandas());
        ((TextView) findViewById(R.id.textEstiloEventoDetalhe)).setText(eventoClicado.getEstilo());
        ((TextView) findViewById(R.id.textCasaEventoDetalhe)).setText(eventoClicado.getCasashow());
        ((TextView) findViewById(R.id.textDonoEventoDetalhe)).setText(eventoClicado.getDono());
        ((TextView) findViewById(R.id.textDescricaoEventoDetalhe)).setText(eventoClicado.getDescricao());

    }

}