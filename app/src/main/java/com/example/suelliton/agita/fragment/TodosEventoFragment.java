package com.example.suelliton.agita.fragment;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.suelliton.agita.R;
import com.example.suelliton.agita.adapter.EventoAdapter;
import com.example.suelliton.agita.model.Evento;

import java.util.ArrayList;
import java.util.List;

public class TodosEventoFragment extends Fragment {

    private View v;
    private List<Evento> lista;
    private RecyclerView myrecycler;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.todos_anuncios_fragment, container, false);//xml do fragment
        myrecycler = (RecyclerView) v.findViewById(R.id.anuncios_recycler);
        iniciaLista();
        EventoAdapter eventoAdapter = new EventoAdapter(lista, getContext());
        myrecycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        myrecycler.setAdapter(eventoAdapter);
        return v;
    }

    public void iniciaLista() {
        Log.i("teste", "Criou a lista de eventos");
        lista = new ArrayList<>();
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