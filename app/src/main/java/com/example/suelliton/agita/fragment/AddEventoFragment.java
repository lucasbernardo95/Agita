package com.example.suelliton.agita.fragment;


import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.Toast;

import com.example.suelliton.agita.R;
import com.example.suelliton.agita.model.Evento;
import com.example.suelliton.agita.utils.MyDatabaseUtil;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import static com.example.suelliton.agita.activity.SplashActivity.LOGADO;

public class AddEventoFragment extends Fragment {
    private FirebaseDatabase database ;
    private DatabaseReference usuarioReference;
    EditText ed_nome;
    EditText ed_data;
    EditText ed_hora;
    EditText ed_local;
    EditText ed_estilo;
    EditText ed_bandas;
    EditText ed_valor;
    EditText ed_descricao;
    EditText ed_casaShow;
    CheckedTextView ed_liberado;
    Button btnSalvarEvento;
    View view;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.add_anuncio_fragment, container, false);

        database = MyDatabaseUtil.getDatabase();
        usuarioReference = database.getReference("usuarios");
        findViews();
        setViewListeners();
        return view;
    }

    public void findViews(){
        ed_nome = (EditText) view.findViewById(R.id.nome_cadastro);
        ed_data = (EditText) view.findViewById(R.id.data_cadastro);
        ed_hora = (EditText) view.findViewById(R.id.hora_cadastro);
        ed_local = (EditText) view.findViewById(R.id.local_cadastro);
        ed_estilo = (EditText) view.findViewById(R.id.estilo_cadastro);
        ed_bandas = (EditText) view.findViewById(R.id.bandas_cadastro);
        ed_valor = (EditText) view.findViewById(R.id.valor_cadastro);
        ed_descricao = (EditText) view.findViewById(R.id.descricao_cadastro);
        ed_casaShow = (EditText) view.findViewById(R.id.casa_show_cadastro);
        ed_liberado = (CheckedTextView) view.findViewById(R.id.liberado_cadastro);
        ed_liberado.setChecked(true); //inicia como true


        ed_liberado.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(ed_liberado.isChecked()){
                    ed_liberado.setCheckMarkDrawable(R.drawable.unchecked);
                    ed_liberado.setChecked(false);
                }else{
                    ed_liberado.setCheckMarkDrawable(R.drawable.checked);
                    ed_liberado.setChecked(true);
                }
            }
        });

        btnSalvarEvento = (Button) view.findViewById(R.id.salvar_evento);
    }
    public void setViewListeners(){
        btnSalvarEvento.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nome = ed_nome.getText().toString();
                String hora = ed_hora.getText().toString();
                String data = ed_data.getText().toString();
                String local = ed_local.getText().toString();
                String estilo = ed_estilo.getText().toString();
                String bandas = ed_bandas.getText().toString();
                double valor = Double.parseDouble(ed_valor.getText().toString());
                String descricao = ed_descricao.getText().toString();
                String casa = ed_casaShow.getText().toString();
                boolean liberado = ed_liberado.isChecked();//verifica o estado do botão se marcado ou não

                Evento evento = new Evento(nome,data,hora,local,estilo,1,1,bandas,valor,descricao,"UrlBanner",liberado,casa);
                usuarioReference.child(LOGADO).child("meusEventos").child(evento.getNome()).setValue(evento);
                Toast.makeText(view.getContext(), "Evento salvocom sucesso!", Toast.LENGTH_SHORT).show();
                limpaCampos();
            }

        });
    }
    public void limpaCampos(){
        ed_nome.setText("");
        ed_data.setText("");
        ed_hora.setText("");
        ed_local.setText("");
        ed_estilo.setText("");
        ed_bandas.setText("");
        ed_valor.setText("");
        ed_descricao.setText("");
        ed_casaShow.setText("");
        ed_liberado.setChecked(false);//desmarca
        btnSalvarEvento.setText("");
    }

}