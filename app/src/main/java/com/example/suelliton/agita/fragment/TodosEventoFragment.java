package com.example.suelliton.agita.fragment;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.example.suelliton.agita.R;
import com.example.suelliton.agita.adapter.EventoAdapter;
import com.example.suelliton.agita.model.Evento;
import com.example.suelliton.agita.model.Usuario;
import com.example.suelliton.agita.utils.MeuRecyclerViewClickListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.ArrayList;
import java.util.List;

public class TodosEventoFragment extends Fragment {

    private View v;
    private List<Evento> lista;
    private RecyclerView myrecycler;
    private FirebaseDatabase database;
    private DatabaseReference ref;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.todos_anuncios_fragment, container, false);//xml do fragment
        myrecycler = (RecyclerView) v.findViewById(R.id.anuncios_recycler);

        database = FirebaseDatabase.getInstance();
        ref = database.getReference("usuarios");
        iniciaLista();

        EventoAdapter eventoAdapter = new EventoAdapter(lista, getContext());
//        myrecycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        myrecycler.setLayoutManager( new GridLayoutManager(getContext(), 2));
        myrecycler.setAdapter(eventoAdapter);

        //implementação do click para os elementos do recyclerview
        myrecycler.addOnItemTouchListener(new MeuRecyclerViewClickListener(getActivity(), myrecycler, new MeuRecyclerViewClickListener.OnItemClickListener() {

            //quando houver um clique na tela, abrir uma tela para visualizar os detalhes
            @Override
            public void OnItemClick(View view, int i) {
                //recebe o evento que foi clicado
                Evento e = lista.get(i);
                Log.i("teste", "Single click:" + e.toString());
                FrameLayout frame = new FrameLayout(getContext());
                FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
                frame.setLayoutParams(params);

                TextView nome = new TextView(getContext());
                TextView bandas = new TextView(getContext());
                TextView baner = new TextView(getContext());
                TextView data = new TextView(getContext());
                TextView casashow = new TextView(getContext());
                TextView descriao = new TextView(getContext());
                TextView estilo = new TextView(getContext());
                TextView hora = new TextView(getContext());
                TextView latidude = new TextView(getContext());
                TextView longetude = new TextView(getContext());
                TextView liberado = new TextView(getContext());
                TextView valor = new TextView(getContext());
                TextView local = new TextView(getContext());


                nome.setText(e.getNome());//
                bandas.setText(e.getBandas());//
                baner.setText(e.getBaner());//
                casashow.setText(e.getCasashow());//
                data.setText(e.getData());//
                descriao.setText(e.getDescricao());//
                estilo.setText(e.getEstilo());//
                hora.setText(e.getHora());//
                latidude.setText(String.valueOf(e.getLatitude()));//
                longetude.setText(String.valueOf(e.getLongitude()));//
                local.setText(e.getLocal());//
                valor.setText(String.valueOf(e.getValor()));//

                frame.addView(nome);
                frame.addView(bandas);
                frame.addView(baner);
                frame.addView(casashow);
                frame.addView(data);
                frame.addView(descriao);
                frame.addView(estilo);
                frame.addView(local);
                frame.addView(valor);
                frame.addView(latidude);
                frame.addView(longetude);
                frame.addView(hora);

                getActivity().setContentView(frame);
            }

            @Override
            public void OnItemLongClick(View view, int i) {
                Evento e = lista.get(i);
                Log.i("teste", "Long click:" + e.toString());
            }

            @Override
            public void onItemClick(View view, int position) {

            }
        }));


        return v;
    }

    public void iniciaLista() {


        lista = new ArrayList<>();

//        query.addChildEventListener(new ChildEventListener() {
//            @Override
//            public void onChildAdded(@NonNull DataSnapshot data, @Nullable String s) {
//                lista.clear();
//                Usuario u = data.getValue(Usuario.class);
//
//                Query q = ref.child(u.getLogin()).child("meusEventos");
//                q.addChildEventListener(new ChildEventListener() {
//                    @Override
//                    public void onChildAdded(@NonNull DataSnapshot dataEvento, @Nullable String s) {
//                        Evento e = dataEvento.getValue(Evento.class);
//                        Log.i("teste", "evento: " + e.toString());
//                        lista.add(e);
//                    }
//
//                    @Override
//                    public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//
//                    }
//
//                    @Override
//                    public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
//
//                    }
//
//                    @Override
//                    public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                    }
//                });
//
//            }
//
//            @Override
//            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//
//            }
//
//            @Override
//            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
//
//            }
//
//            @Override
//            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });

        lista.add(new Evento("1", "20/09/2018", "20:35", "Macaíba",
                "Brega", 231321, 1231231, "Rei da cancinbinha e chiclete com banana",
                36.98, "Festa na piscina... com duda", "www.google.com/dudaDeBugre",
                true, "alí onde o vento faz a curva"));
        lista.add(new Evento("222", "22/09/2018", "10:35", "Natal",
                "Brega", 231321, 1231231, "Rei da cancinbinha e chiclete com banana",
                36.98, "Festa na piscina... com duda", "www.google.com/dudaDeBugre",
                true, "alí onde o vento faz a curva"));
        lista.add(new Evento("33333333333", "12/09/2018", "20:35", "Natal",
                "Brega", 231321, 1231231, "Rei da cancinbinha e chiclete com banana",
                36.98, "Festa na piscina... com duda", "www.google.com/dudaDeBugre",
                true, "alí onde o vento faz a curva"));


    }

}