package com.example.suelliton.agita.fragment;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.suelliton.agita.R;
import com.example.suelliton.agita.activity.Detalhes;
import com.example.suelliton.agita.activity.EventoActivity;
import com.example.suelliton.agita.adapter.EventoAdapter;
import com.example.suelliton.agita.model.Evento;
import com.example.suelliton.agita.utils.MeuRecyclerViewClickListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.synnapps.carouselview.CarouselView;
import com.synnapps.carouselview.ImageListener;

import java.util.ArrayList;
import java.util.List;

import static com.example.suelliton.agita.activity.EventoActivity.eventosReference;

public class TodosEventoFragment extends Fragment {

    private View v;
    private Evento evento;
    public static Evento eventoClicado;
    private List<Evento> listaEventos;
    RecyclerView myrecycler;
    FrameLayout frame;

    CarouselView carrossel;

    int[] imagens = {
            R.drawable.img1,
            R.drawable.img2,
            R.drawable.img3,
            R.drawable.img4,
            R.drawable.img5,
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.todos_anuncios_fragment, container, false);//xml do fragment
        myrecycler = (RecyclerView) v.findViewById(R.id.anuncios_recycler);

        carrossel = (CarouselView) v.findViewById(R.id.carrosselView);
        /*informa a quantidade de elementos que irá conter no carrossel*/
        carrossel.setPageCount(imagens.length);
        /*implementa a listagem de imagens do carrossel.*/
        carrossel.setImageListener(clickImagem);
        carrossel.notifyAll();

        iniciaLista();


        EventoAdapter eventoAdapter = new EventoAdapter(listaEventos, getContext());
        myrecycler.setLayoutManager(new GridLayoutManager(v.getContext(),2));
        myrecycler.setAdapter(eventoAdapter);
        eventoAdapter.notifyDataSetChanged();

        myrecycler.addOnItemTouchListener(new MeuRecyclerViewClickListener(v.getContext(), myrecycler, new MeuRecyclerViewClickListener.OnItemClickListener() {

            @Override
            public void OnItemClick(View view, int i) {
                eventoClicado = listaEventos.get(i);
                startActivity(new Intent(v.getContext(),Detalhes.class));

                //frame = getActivity().findViewById(R.id.frame);
                //frame.setVisibility(View.VISIBLE);

                //ImageView imageView = getActivity().findViewById(R.id.logo_evento);
                //imageView.setImageBitmap();
            }

            @Override
            public void OnItemLongClick(View view, int i) {

            }

            @Override
            public void onItemClick(View view, int position) {

            }
        }));

        return v;
    }

    //Método temporário para exibir as imagens no carrossel
    ImageListener clickImagem = new ImageListener() {
        @Override
        public void setImageForPosition(int position, ImageView imageView) {
            imageView.setImageResource(imagens[position]);//seta a imagem na posição informada
        }
    };

    public void iniciaLista() {
        listaEventos = new ArrayList<>();
        eventosReference.orderByKey().addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                evento = dataSnapshot.getValue(Evento.class);
                if (evento != null){
                    listaEventos.add(evento);
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


}