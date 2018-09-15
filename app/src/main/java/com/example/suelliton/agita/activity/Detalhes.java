package com.example.suelliton.agita.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.example.suelliton.agita.R;

import static com.example.suelliton.agita.fragment.TodosEventoFragment.eventoClicado;

public class Detalhes extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalhes);

        TextView textView = (TextView) findViewById(R.id.tv_evento_clicado);

        textView.setText(eventoClicado.getNome());


    }
}
