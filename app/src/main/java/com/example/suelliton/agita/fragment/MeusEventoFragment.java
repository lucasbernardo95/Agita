package com.example.suelliton.agita.fragment;


import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.suelliton.agita.R;
import com.example.suelliton.agita.adapter.EventoAdapter;
import com.example.suelliton.agita.model.Evento;
import com.example.suelliton.agita.utils.MeuRecyclerViewClickListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class MeusEventoFragment extends Fragment {
    View v;
    RecyclerView myrecycler;
    DatabaseReference reference;
    List<Evento> lista;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //Layout que contém o recyclerview para ser inflado
        v = inflater.inflate(R.layout.todos_anuncios_fragment, container, false);
        //Aponta para o recycler do layout a ser inflado
        myrecycler = (RecyclerView) v.findViewById(R.id.anuncios_recycler);
        //pega uma referência de uma instância do banco (Agita) do firebase
        reference = FirebaseDatabase.getInstance().getReference();

        lista = new ArrayList<>();
        buscarMeusEventos();

        //Cria o adapter para os eventos da lista
        EventoAdapter eventoAdapter = new EventoAdapter(lista, getContext());
        myrecycler.setLayoutManager(new GridLayoutManager(v.getContext(),2));
        myrecycler.setAdapter(eventoAdapter);
        eventoAdapter.notifyDataSetChanged();

        myrecycler.addOnItemTouchListener(new MeuRecyclerViewClickListener(v.getContext(), myrecycler, new MeuRecyclerViewClickListener.OnItemClickListener() {


            @Override
            public void OnItemClick(View view, int i) {
//                frame = getActivity().findViewById(R.id.frame);
//                frame.setVisibility(View.VISIBLE);


            }

            @Override
            public void OnItemLongClick(View view, int i) { }

            @Override
            public void onItemClick(View view, int position) { }
        }));


        return v;
    }

    private void buscarMeusEventos() {
        lista.add(new Evento("Duda fest", "02/02/1995", "15:00", "Natal", "Brega", 89546,
                -8532, "forró bom, forró pegado", 6.99, "só vem quem quer", "www.google.com.br",
                false, "dudahouse", false, "lucas bernardo"));

    }
}