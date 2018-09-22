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
import android.support.v4.app.FragmentManager;
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
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.suelliton.agita.R;
import com.example.suelliton.agita.adapter.EventoAdapter;
import com.example.suelliton.agita.model.Evento;
import com.example.suelliton.agita.utils.MeuRecyclerViewClickListener;
import com.example.suelliton.agita.utils.MyDatabaseUtil;
import com.example.suelliton.agita.utils.PermissionUtils;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.squareup.picasso.Picasso;
import com.synnapps.carouselview.CarouselView;
import com.synnapps.carouselview.ImageListener;

import java.util.ArrayList;
import java.util.List;

import static com.example.suelliton.agita.activity.SplashActivity.LOGADO;
import static com.example.suelliton.agita.activity.SplashActivity.eventosReference;
import static com.example.suelliton.agita.activity.SplashActivity.usuarioReference;

public class EventoActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    DrawerLayout drawer;
    Toolbar toolbar;
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
    FirebaseStorage storage;
    Evento[] eventosCarousel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_evento);
        setSupportActionBar(toolbar);
        database =  MyDatabaseUtil.getDatabase();
        eventosReference = database.getReference("eventos");
        iniciaLista();
        findViews();
        setViewListener();



        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                carregaCarrossel();
                carrossel.setImageListener(clickImagem);/*implementa a listagem de eventosCarousel do carrossel.*/
                carrossel.setPageCount(eventosCarousel.length); /*informa a quantidade de elementos que irá conter no carrossel*/
                //carrossel.notify();
            }
        },2000);



        EventoAdapter eventoAdapter = new EventoAdapter(listaEventos, EventoActivity.this);
        myrecycler.setLayoutManager(new GridLayoutManager(EventoActivity.this,2));
        myrecycler.setAdapter(eventoAdapter);
        eventoAdapter.notifyDataSetChanged();

    }

    //Método temporário para exibir as eventosCarousel no carrossel
    ImageListener clickImagem = new ImageListener() {
        @Override
        public void setImageForPosition(final int position, ImageView imageView) {
            try {
                Picasso.get().load(eventosCarousel[position].getUrlBanner()).into(imageView);
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        eventoClicado = eventosCarousel[position];
                        startActivity(new Intent(EventoActivity.this,Detalhes.class));
                    }
                });
            }catch (Exception e){

            }
        }
    };

    public void carregaCarrossel(){

        int qtd = 0;
        if(listaEventos.size()>5){
            qtd = 5;
        }else{
            qtd = listaEventos.size();
        }
        eventosCarousel = new Evento[qtd];
        for(int i = 0 ; i < qtd; i++){
            Evento evento = listaEventos.get(i);
            eventosCarousel[i] = evento;
        }
    }

    public void iniciaLista() {
        listaEventos = new ArrayList<>();
        eventosReference.orderByKey().addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Log.i("evento", "--->"+dataSnapshot.getValue(Evento.class));
                evento = dataSnapshot.getValue(Evento.class);
                if (evento != null){
                    if(evento.getKey().equals("")){
                        evento.setKey(dataSnapshot.getRef().getKey());
                        eventosReference.child(dataSnapshot.getRef().getKey()).child("key").setValue(dataSnapshot.getRef().getKey());
                    }
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
        getMenuInflater().inflate(R.menu.evento, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
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
        } else if (id == R.id.nav_logout) {
            SharedPreferences sharedPreferences = getSharedPreferences("sharedPreferences", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("usuarioLogado", "");
            editor.apply();
            LOGADO="";
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