package com.example.suelliton.agita.activity;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.suelliton.agita.R;
import com.example.suelliton.agita.adapter.EventoAdapter;
import com.example.suelliton.agita.holders.EventoViewHolder;

import com.example.suelliton.agita.model.Evento;
import com.example.suelliton.agita.model.Participante;
import com.example.suelliton.agita.model.Usuario;
import com.example.suelliton.agita.utils.ItemClickListener;
import com.example.suelliton.agita.utils.MyDatabaseUtil;
import com.example.suelliton.agita.utils.PermissionUtils;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.squareup.picasso.Picasso;
import com.synnapps.carouselview.CarouselView;
import com.synnapps.carouselview.ImageListener;

import java.sql.Time;
import java.util.ArrayList;
import java.util.List;


import static com.example.suelliton.agita.activity.SplashActivity.eventosReference;
import static com.example.suelliton.agita.activity.SplashActivity.usuarioLogado;
import static com.example.suelliton.agita.activity.SplashActivity.usuarioReference;

public class EventoActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    DrawerLayout drawer;
    Toolbar toolbar ;
    FrameLayout frameLayout;
    FirebaseDatabase database ;


    private static String[] PERMISSIONS_STORAGE = {
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    private Evento evento;
    public static Evento eventoClicado;
    private List<Evento> listaEventos;
    RecyclerView myrecycler;

    CarouselView carrossel;
    List<Evento> eventosCarousel;
    FirebaseRecyclerAdapter<Evento,EventoViewHolder> adapter;
    int qtdAnterior = 0;
    List<String> eventosParticiparei ;
    int count = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_evento);

        database =  MyDatabaseUtil.getDatabase();
        eventosReference = database.getReference("eventos");


        findViews();
        setSupportActionBar(toolbar);//tem que ficar aqui devido a chamada da toolbar
        setViewListener();
        if(usuarioLogado != null)atualizaUsuarioLogado();
        iniciaLista("nome");

        eventosCarousel = new ArrayList<>();
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //populateCarousel();
                carrossel.setImageListener(clickImagem);/*implementa a listagem de eventosCarousel do carrossel.*/
                carrossel.setPageCount(eventosCarousel.size()); /*informa a quantidade de elementos que irá conter no carrossel*/
                //carrossel.notify();

            }
        },2000);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        Menu nav_Menu = navigationView.getMenu();
        if(usuarioLogado == null){//se o usuario for fantasma
            nav_Menu.findItem(R.id.nav_meus_anuncios).setVisible(false);
            nav_Menu.findItem(R.id.nav_add_evento).setVisible(false);
            nav_Menu.findItem(R.id.nav_logout).setVisible(false);
            nav_Menu.findItem(R.id.nav_login).setVisible(true);
            nav_Menu.findItem(R.id.nav_aprova_anuncios).setVisible(false);
            nav_Menu.findItem(R.id.nav_todos_anuncios).setVisible(false);
        }else if(usuarioLogado.isAdmin()) {//se for administrador
            nav_Menu.findItem(R.id.nav_login).setVisible(false);
            atualizaUsuarioLogado();
        }else {//se for usuário normal
            nav_Menu.findItem(R.id.nav_login).setVisible(false);
            nav_Menu.findItem(R.id.nav_aprova_anuncios).setVisible(false);
            atualizaUsuarioLogado();
        }

        myrecycler.setLayoutManager(new GridLayoutManager(EventoActivity.this,2));


    }
    //Método temporário para exibir as eventosCarousel no carrossel
    ImageListener clickImagem = new ImageListener() {
        @Override
        public void setImageForPosition(final int position, ImageView imageView) {
            try {
                Picasso.get().load(eventosCarousel.get(position).getUrlBanner()).into(imageView);
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        eventoClicado = eventosCarousel.get(position);
                        startActivity(new Intent(EventoActivity.this,Detalhes.class));
                    }
                });
            }catch (Exception e){
            }
        }
    };

    public void populateCarousel(){
        int qtd = 0;
        if(listaEventos.size()>5){
            qtd = 5;
        }else{
            qtd = listaEventos.size();
        }
        eventosCarousel = new ArrayList<>();
        for(int i = 0 ; i < qtd; i++){
            Evento evento = listaEventos.get(i);
            eventosCarousel.add(evento);
        }
    }

    public void iniciaLista(final String fieldOrder) {


        Query query;
        if(fieldOrder.equals("nome") || fieldOrder.equals("data")) {
            query = eventosReference.orderByChild(fieldOrder);
        }else{
            query = eventosReference.orderByChild("estilo").startAt(fieldOrder).endAt(fieldOrder);
        }

        adapter = new FirebaseRecyclerAdapter<Evento, EventoViewHolder>(Evento.class,
                R.layout.inflate_evento,
                EventoViewHolder.class,
                query){

            @Override
            protected void populateViewHolder(final EventoViewHolder viewHolder, final Evento model, int position) {

                setValuesViewHolder(viewHolder,model);//todos os eventos filtrados
                //myrecycler.invalidate();

                    if (model.getQtdParticipantes() >= qtdAnterior && count < 6) {
                        eventosCarousel.add(model);
                        qtdAnterior = model.getQtdParticipantes();
                        count++;
                    }
                }
        };

        myrecycler.setAdapter(adapter);


    }

    public void setValuesViewHolder(final EventoViewHolder viewHolder,Evento model){
        viewHolder.nome.setText(model.getNome());
        if(!model.getUrlBanner().equals("")) {
            Picasso.get().load(model.getUrlBanner()).into(viewHolder.imagem);
        }
        final Evento evento = model;
        final boolean[] like = new boolean[1];

        if(usuarioLogado != null) {
            if (eventosParticiparei != null){
                if (eventosParticiparei.contains(evento.getKey())) {
                    like[0] = true;
                    viewHolder.botaoLike.setBackgroundResource(R.drawable.ic_action_like);
                } else {
                    like[0] = false;
                    viewHolder.botaoLike.setBackgroundResource(R.drawable.ic_action_nolike);
                }
            }

        }
            viewHolder.botaoLike.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if(usuarioLogado !=null) {
                        if (like[0]) {
                            like[0] = false;
                            viewHolder.botaoLike.setBackgroundResource(R.drawable.ic_action_nolike);
                            eventosParticiparei.remove(evento.getKey());
                            eventosReference.child(evento.getKey()).child("qtdParticipantes").setValue(evento.getQtdParticipantes() - 1);
                        } else {
                            like[0] = true;
                            viewHolder.botaoLike.setBackgroundResource(R.drawable.ic_action_like);
                            eventosParticiparei.add(evento.getKey());
                            eventosReference.child(evento.getKey()).child("qtdParticipantes").setValue(evento.getQtdParticipantes() + 1);
                        }
                        usuarioLogado.setParticiparei(eventosParticiparei);
                        usuarioReference.child(usuarioLogado.getLogin()).setValue(usuarioLogado);
                        view.requestFocus();
                    }else{
                        Toast.makeText(EventoActivity.this, "Faça login para curtir o evento", Toast.LENGTH_SHORT).show();
                    }
                }
            });


        viewHolder.imagem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                eventoClicado = evento;
                startActivity(new Intent(EventoActivity.this, Detalhes.class));
            }
        });
    }
    public void atualizaUsuarioLogado(){
        Query query = usuarioReference.child(usuarioLogado.getLogin());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                usuarioLogado = dataSnapshot.getValue(Usuario.class);
                eventosParticiparei = usuarioLogado.getParticiparei();
                //Toast.makeText(EventoActivity.this, "nome "+usuarioLogado.getNome(), Toast.LENGTH_SHORT).show();
                if(eventosParticiparei == null){
                    eventosParticiparei = new ArrayList<>();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
    public void findViews(){
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        frameLayout = (FrameLayout) findViewById(R.id.frame);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        myrecycler = (RecyclerView) findViewById(R.id.eventos_recycler);
        carrossel = (CarouselView) findViewById(R.id.carrosselView);
    }

    public void setViewListener(){
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        frameLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.setVisibility(View.INVISIBLE);
            }
        });
    }



    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.filtro_todos_eventos, menu);

        return super.onCreateOptionsMenu(menu);
        //return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.opcao_filtro_data:
                iniciaLista("data");
                return true;
            case R.id.opcao_filtro_nome:
                iniciaLista("nome");
                return true;
            case R.id.opcao_filtro_rock:
                iniciaLista("Rock");
                return true;
            case R.id.opcao_filtro_pop:
                iniciaLista("Pop");
                return true;
            case R.id.opcao_filtro_eletronica:
                iniciaLista("Eletrônica");
                return true;
            case R.id.opcao_filtro_forro:
                iniciaLista("Forró");
                return true;
            case R.id.opcao_filtro_sertanejo:
                iniciaLista("Sertanejo");
                return true;
            case R.id.opcao_filtro_brega:
                iniciaLista("Brega");
                return true;
            case R.id.opcao_filtro_swingueira:
                iniciaLista("Swingueira");
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }



    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.

        int id = item.getItemId();

        if (id == R.id.nav_add_evento) {
            startActivity(new Intent(EventoActivity.this,AddEventoActivity.class));
        } else if (id == R.id.nav_meus_anuncios) {
            startActivity(new Intent(EventoActivity.this,MeusEventosActivity.class));
        } else if (id == R.id.nav_todos_anuncios) {
            startActivity(new Intent(EventoActivity.this,EventoActivity.class));
        } else if (id == R.id.nav_aprova_anuncios) {
            Log.i("admin", "abrindo tela admin");
            startActivity(new Intent(EventoActivity.this,AdminActivity.class));
        } else if (id == R.id.nav_logout) {
            SharedPreferences sharedPreferences = getSharedPreferences("sharedPreferences", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("usuarioLogado", "");
            editor.apply();
            usuarioLogado = null;
            startActivity(new Intent(EventoActivity.this,EventoActivity.class));
            finish();
        }else if(id == R.id.nav_login){
            startActivity(new Intent(EventoActivity.this,LoginActivity.class));
            finish();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);

        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        for (int result : grantResults) {
            if (result == PackageManager.PERMISSION_DENIED) {
                // Alguma permissão foi negada, agora é com você :-)
                //btn.setEnabled(false);
                alertAndFinish();
                return;
            }
        }
        // btn.setEnabled(true);
    }


    private void alertAndFinish(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.app_name).setMessage("Para utilizar todas as funções desse aplicativo, você precisa aceitar o acesso ao armazenamento externo.");
        // Add the buttons
        builder.setNegativeButton("Fechar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //faz nada...
            }
        });
        builder.setPositiveButton("Permitir", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package", getPackageName(), null);
                intent.setData(uri);
                startActivity(intent);
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        carrossel.requestFocus();

    }

    @Override
    protected void onStart() {
        super.onStart();
        PermissionUtils.validate(EventoActivity.this,2, PERMISSIONS_STORAGE);
    }

}