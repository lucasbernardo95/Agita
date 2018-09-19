package com.example.suelliton.agita.activity;

import android.content.Intent;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.suelliton.agita.R;
import com.example.suelliton.agita.model.Evento;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import static com.example.suelliton.agita.activity.EventoActivity.eventoClicado;

public class Detalhes extends AppCompatActivity {

    private TextView nome,hora, data, valor, local,
            bandas, estilo, casa, dono, descricao;
    private ImageView imagem;
    private Evento eventoRecebido; //Representa o evento clicado na tela de visualização

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalhes);

        //Recupera do bundle o evento clicado
        //Bundle pacote = getIntent().getExtras();
        //eventoRecebido = (Evento) pacote.getSerializable("evento");
        //Log.i("evento", "recebido: "+eventoRecebido.toString());

        nome = (TextView) findViewById(R.id.tv_evento_clicado);
        hora    = (TextView) findViewById(R.id.textHoraEventoDetalhe);
        data    = (TextView) findViewById(R.id.textDataEventoDetalhe);
        valor   = (TextView) findViewById(R.id.textValorEventoDetalhe);
        local   = (TextView) findViewById(R.id.textLocalEventoDetalhe);
        bandas  = (TextView) findViewById(R.id.textBandasEventoDetalhe);
        estilo  = (TextView) findViewById(R.id.textEstiloEventoDetalhe);
        casa    = (TextView) findViewById(R.id.textCasaEventoDetalhe);
        dono    = (TextView) findViewById(R.id.textDonoEventoDetalhe);
        descricao = (TextView) findViewById(R.id.textDescricaoEventoDetalhe);
        imagem  = (ImageView) findViewById(R.id.imageEventoDetalhe);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.butonMap);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent in = new Intent(Detalhes.this, MapsActivity.class);
               // in.putExtra("eventoLocal", eventoRecebido);
                startActivity(in);
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        setContent();
    }

    private void setContent() {
        nome.setText(eventoClicado.getNome());
        hora.setText(String.valueOf(eventoClicado.getHora()));
        data.setText(String.valueOf(eventoClicado.getData()));
        valor.setText(String.valueOf(eventoClicado.getValor()));
        local.setText(eventoClicado.getLocal());
        bandas.setText(eventoClicado.getBandas());
        estilo.setText(eventoClicado.getEstilo());
        casa.setText(eventoClicado.getCasashow());
        dono.setText(eventoClicado.getDono());
        descricao.setText(eventoClicado.getDescricao());

        //Busca a imagem do evento selecionado
        StorageReference storageReference = FirebaseStorage.getInstance().getReference("eventos");
        StorageReference islandRef = storageReference.child(eventoClicado.getNome());
        islandRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).into(imagem);
            }
        });
    }

}