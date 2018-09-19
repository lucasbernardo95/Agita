package com.example.suelliton.agita.activity;

import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.suelliton.agita.R;
import com.example.suelliton.agita.model.Evento;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

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
        Bundle pacote = getIntent().getExtras();
        eventoRecebido = (Evento) pacote.getSerializable("evento");
        Log.i("evento", "recebido: "+eventoRecebido.toString());

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

    }

    @Override
    protected void onResume() {
        super.onResume();
        setContent();
    }

    private void setContent() {
        nome.setText(eventoRecebido.getNome());
        nome.setText(String.valueOf(eventoRecebido.getHora()));
        data.setText(String.valueOf(eventoRecebido.getData()));
        valor.setText(String.valueOf(eventoRecebido.getValor()));
        local.setText(eventoRecebido.getLocal());
        bandas.setText(eventoRecebido.getBandas());
        estilo.setText(eventoRecebido.getEstilo());
        casa.setText(eventoRecebido.getCasashow());
        dono.setText(eventoRecebido.getDono());
        descricao.setText(eventoRecebido.getDescricao());

        //Busca a imagem do evento selecionado
        StorageReference storageReference = FirebaseStorage.getInstance().getReference("eventos");
        StorageReference islandRef = storageReference.child(eventoRecebido.getNome());
        islandRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).into(imagem);
            }
        });
    }

}