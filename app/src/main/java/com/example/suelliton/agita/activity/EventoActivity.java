package com.example.suelliton.agita.activity;
import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import com.example.suelliton.agita.R;
import com.example.suelliton.agita.adapter.EventoAdapter;
import com.example.suelliton.agita.model.Evento;
import com.example.suelliton.agita.model.Usuario;
import com.example.suelliton.agita.utils.GPSTracker;
import com.example.suelliton.agita.utils.PermissionUtils;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.synnapps.carouselview.CarouselView;
import com.synnapps.carouselview.ImageListener;
import java.util.ArrayList;
import java.util.List;

import static com.example.suelliton.agita.activity.SplashActivity.database;
import static com.example.suelliton.agita.activity.SplashActivity.eventosReference;
import static com.example.suelliton.agita.activity.SplashActivity.usuarioLogado;
import static com.example.suelliton.agita.activity.SplashActivity.usuarioReference;

public class EventoActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private static String[] PERMISSIONS_STORAGE = {
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    DrawerLayout drawer;
    static Toolbar toolbar ;


    public static List<String> eventosParticiparei ;
    public static Evento eventoClicado;
    public static String  master ;
    private List<Evento> listaEventos;
    RecyclerView myrecycler;
    EventoAdapter eventoAdapter;
    CarouselView carrossel;
    List<Evento> eventosCarousel;
    int qtdAnterior = 0;
    int count = 0;
    public static DatabaseReference temporarioReference;
    TextView text_filtro;
    ChildEventListener childListener;
    ValueEventListener valueListener;
    CardView cardView;
    //--------------
    GPSTracker gpsTracker;
    Location mlocation;
    public static FrameLayout frameLayout;
    public static TextView nomeDetalhe, horaDetalhe, dataDetalhe, valorDetalhe, localDetalhe,
            bandasDetalhe, estiloDetalhe, casaDetalhe, donoDetalhe, descricaoDetalhe;
    public static ImageView imagemDetalhe;
    public static FloatingActionButton fabMapaDetalhe;

    //Campo de busca dos eventos
    private TextView campoBusca;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_evento);
        temporarioReference = database.getReference("eventoTemporario");

        findViews();
        setSupportActionBar(toolbar);//tem que ficar aqui devido a chamada da toolbar
        setViewListener();
        if(usuarioLogado != null)atualizaUsuarioLogado();//se tiver usuario logado

        eventosCarousel = new ArrayList<>();
        listaEventos = new ArrayList<>();

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //populateCarousel();
                carrossel.setImageListener(clickImagem);/*implementa a listagem de eventosCarousel do carrossel.*/
                carrossel.setPageCount(eventosCarousel.size()); /*informa a quantidade de elementos que irá conter no carrossel*/
                //carrossel.notify();

            }
        },500);

        controlaExibicaoMenu();

        myrecycler.setLayoutManager(new GridLayoutManager(EventoActivity.this,2));
        eventoAdapter = new EventoAdapter(listaEventos,EventoActivity.this);
        myrecycler.setAdapter(eventoAdapter);

        master = "todosEventos";//aba de inicio



    }

    public void controlaExibicaoMenu(){
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        Menu nav_Menu = navigationView.getMenu();
        if(usuarioLogado == null){//se o usuario for fantasma
            nav_Menu.findItem(R.id.nav_meus_eventos).setVisible(false);
            nav_Menu.findItem(R.id.nav_add_evento).setVisible(false);
            nav_Menu.findItem(R.id.nav_logout).setVisible(false);
            nav_Menu.findItem(R.id.nav_login).setVisible(true);
            nav_Menu.findItem(R.id.nav_aprova_anuncios).setVisible(false);
            nav_Menu.findItem(R.id.nav_todos_eventos).setVisible(false);
            nav_Menu.findItem(R.id.nav_eventos_irei).setVisible(false);
            nav_Menu.findItem(R.id.nav_edit_user).setVisible(false);
        }else if(usuarioLogado.isAdmin()) {//se for administrador
            nav_Menu.findItem(R.id.nav_login).setVisible(false);
            atualizaUsuarioLogado();
        }else {//se for usuário normal
            nav_Menu.findItem(R.id.nav_login).setVisible(false);
            nav_Menu.findItem(R.id.nav_aprova_anuncios).setVisible(false);
            atualizaUsuarioLogado();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        buscaEventos("data");
        PermissionUtils.validate(EventoActivity.this,2, PERMISSIONS_STORAGE);
    }
    @Override
    protected void onResume() {
        super.onResume();
        carrossel.requestFocus();
    }
    @Override
    protected void onPause() {
        super.onPause();
        removeListenersFirebase();
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
                        setContentDetalhes();
                        //startActivity(new Intent(EventoActivity.this,Detalhes.class));
                    }
                });
            }catch (Exception e){
            }
        }
    };

    private void buscaEventosGeral() {

    }

    public void buscaEventos(String slave){

        switch (master){
            case "todosEventos":
                    getTodosEventos(slave);
                break;
            case "meusEventos":
                    getMeusEventos(slave);
                break;
            case "ireiEventos":
                    getIreiEventos(slave);
                break;
            case "pertoEventos":
                    getPertoEventos(slave);
                break;
        }

    }

    public void removeListenersFirebase(){
        if(valueListener != null)eventosReference.removeEventListener(valueListener);
        if(childListener != null)eventosReference.removeEventListener(childListener);
    }
    public void getPertoEventos(final String slave){
        removeListenersFirebase();
        listaEventos.removeAll(listaEventos);
        //verifica se foi dada permissao pra usar gps
        if (ActivityCompat.checkSelfPermission(EventoActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(EventoActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(EventoActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 20);
            return;
        }
        LocationManager manager  = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {//gps ligado ou nao

            GPSTracker gpsTracker = new GPSTracker(getApplicationContext());
            Location mlocation = null;
            while (mlocation == null) {
                mlocation = gpsTracker.getLocation();
            }
            final double lat = mlocation.getLatitude();
            final double lng = mlocation.getLongitude();
            Query query = null;
            if(slave.equals("data")){
                query = eventosReference.orderByChild("data");
            }else if(slave.equals("nome")){
                query = eventosReference.orderByChild("nome");
            }else{
                query = eventosReference;
            }
            childListener = query.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    Evento evento = dataSnapshot.getValue(Evento.class);
                    if (lat > evento.getLatitude() - 0.0450000 && lat < evento.getLatitude() + 0.0450000 &&
                            lng > evento.getLongitude() - 0.020000 && lng < evento.getLongitude() + 0.020000) {
                        if(slave.equals("") || slave.equals("data") || slave.equals("nome")){//quando clica no menu principal
                            listaEventos.add(evento);
                        }else{//quando clica no menu secundario de estilos
                            if(evento.getEstilo().equals(slave)){
                                listaEventos.add(evento);
                            }
                        }
                    }
                    eventoAdapter.notifyDataSetChanged();
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

        }else{
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            Toast.makeText(EventoActivity.this, "Ative o GPS do dispositivo", Toast.LENGTH_SHORT).show();
            startActivityForResult(intent, 10);

        }

    }
    public void getIreiEventos(final String slave){
        removeListenersFirebase();
        listaEventos.removeAll(listaEventos);
        List<String> keyEventos = usuarioLogado.getParticiparei();
        if(keyEventos == null) keyEventos = new ArrayList<>();

        for (String key : keyEventos ) {
              valueListener = eventosReference.child(key).addValueEventListener(new ValueEventListener() {
                  @Override
                  public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                      Evento evento = dataSnapshot.getValue(Evento.class);
                      if(evento != null) {
                          boolean existe = false;
                          for (int i = 0; i < listaEventos.size(); i++) {
                              if (listaEventos.get(i).getKey().equals(dataSnapshot.getValue(Evento.class).getKey())) {
                                  listaEventos.remove(i);
                                  listaEventos.add(i, evento);
                                  existe = true;
                              }
                          }
                          if (!existe) {
                              if (slave.equals("") || slave.equals("data") || slave.equals("nome")) {//quando clica no menu principal
                                  listaEventos.add(evento);
                              } else {//quando clica no menu secundario de estilos
                                  if (evento.getEstilo().equals(slave)) {
                                      listaEventos.add(evento);
                                  }
                              }
                          }
                      }
                      eventoAdapter.notifyDataSetChanged();
                  }

                  @Override
                  public void onCancelled(@NonNull DatabaseError databaseError) {

                  }
              });
        }
        eventoAdapter.notifyDataSetChanged();//necessita para quando nao tiver evento irei atualizar a tela

    }
    public void getMeusEventos(final String slave){
        removeListenersFirebase();
        listaEventos.removeAll(listaEventos);
        List<Query> querys = new ArrayList<>();//lista de queryes
        querys.add(eventosReference.orderByChild("dono").equalTo(usuarioLogado.getLogin()));//busca eventos aprovados
        querys.add(temporarioReference.orderByChild("dono").equalTo(usuarioLogado.getLogin()));//busca eventos nao aprovados
        for (Query query: querys) {

            childListener = query.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    Evento evento = dataSnapshot.getValue(Evento.class);
                    if(slave.equals("") || slave.equals("data") || slave.equals("nome")){//quando clica no menu principal
                        listaEventos.add(evento);
                    }else{//quando clica no menu secundario de estilos
                        if(evento.getEstilo().equals(slave)){
                            listaEventos.add(evento);
                        }
                    }
                    eventoAdapter.notifyDataSetChanged();
                }

                @Override
                public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                }

                @Override
                public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                    for(int i=0;i<listaEventos.size();i++){
                        if(listaEventos.get(i).getKey().equals(dataSnapshot.getValue(Evento.class).getKey())){
                            listaEventos.remove(listaEventos.get(i));
                        }
                    }
                    eventoAdapter.notifyDataSetChanged();
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
    public void getTodosEventos(String slave){
        removeListenersFirebase();
        listaEventos.removeAll(listaEventos);
        Query query = null;
        if(slave.equals("data") || slave.equals("nome")){
            query = eventosReference.orderByChild(slave);
        }else{
            query = eventosReference.orderByChild("estilo").startAt(slave).endAt(slave);
        }
        childListener = query.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Evento evento = dataSnapshot.getValue(Evento.class);
                //preenche carousel
                if (evento.getQtdParticipantes() >= qtdAnterior && count < 6) {
                    eventosCarousel.add(evento);
                    qtdAnterior = evento.getQtdParticipantes();
                    count++;
                }
                listaEventos.add(evento);
                eventoAdapter.notifyDataSetChanged();
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

    public void atualizaUsuarioLogado(){
        Query query = usuarioReference.child(usuarioLogado.getLogin());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                usuarioLogado = dataSnapshot.getValue(Usuario.class);
                eventosParticiparei = usuarioLogado.getParticiparei();
                //Toast.makeText(EventoActivity.this, "nomeDetalhe "+usuarioLogado.getNome(), Toast.LENGTH_SHORT).show();
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
        text_filtro = (TextView) findViewById(R.id.tv_filtro);
//-------campos frame detalhes
        nomeDetalhe = (TextView) findViewById(R.id.tv_evento_clicado);
        horaDetalhe = (TextView) findViewById(R.id.textHoraEventoDetalhe);
        dataDetalhe = (TextView) findViewById(R.id.textDataEventoDetalhe);
        valorDetalhe = (TextView) findViewById(R.id.textValorEventoDetalhe);
        localDetalhe = (TextView) findViewById(R.id.textLocalEventoDetalhe);
        bandasDetalhe = (TextView) findViewById(R.id.textBandasEventoDetalhe);
        estiloDetalhe = (TextView) findViewById(R.id.textEstiloEventoDetalhe);
        casaDetalhe = (TextView) findViewById(R.id.textCasaEventoDetalhe);
        donoDetalhe = (TextView) findViewById(R.id.textDonoEventoDetalhe);
        descricaoDetalhe = (TextView) findViewById(R.id.textDescricaoEventoDetalhe);
        imagemDetalhe = (ImageView) findViewById(R.id.imageEventoDetalhe);
        fabMapaDetalhe = (FloatingActionButton) findViewById(R.id.butonMap);

        cardView = (CardView) findViewById(R.id.cardView);
    }
    public static void setContentDetalhes() {//seta dados no frame de detalhes
        frameLayout.setVisibility(View.VISIBLE);//exibe o frame
        toolbar.getMenu().setGroupVisible(0,false);
        nomeDetalhe.setText(eventoClicado.getNome());
        horaDetalhe.setText(String.valueOf(eventoClicado.getHora()));
        dataDetalhe.setText(String.valueOf(eventoClicado.getData()));
        valorDetalhe.setText(String.valueOf(eventoClicado.getValor()));
        localDetalhe.setText(eventoClicado.getLocal());
        bandasDetalhe.setText(eventoClicado.getBandas());
        estiloDetalhe.setText(eventoClicado.getEstilo());
        casaDetalhe.setText(eventoClicado.getCasashow());
        donoDetalhe.setText(eventoClicado.getDono());
        descricaoDetalhe.setText(eventoClicado.getDescricao());
        Picasso.get().load(eventoClicado.getUrlBanner()).into(imagemDetalhe);
    }

    public void setViewListener(){//seta acoes para as views
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        /*frameLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                frameLayout.setVisibility(View.INVISIBLE);
                toolbar.getMenu().setGroupVisible(0,true);
            }
        });*/
        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                frameLayout.setVisibility(View.INVISIBLE);
                toolbar.getMenu().setGroupVisible(0,true);
            }
        });

        fabMapaDetalhe.setOnClickListener(new View.OnClickListener() {//botão que chama o mapa de rota
            @Override
            public void onClick(View view) {
                if (ActivityCompat.checkSelfPermission(EventoActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(EventoActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(EventoActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 20);
                    return;
                }
                gpsTracker = new GPSTracker(getApplicationContext()); //intancia a classe do GPS para pegar minha localização
                mlocation = gpsTracker.getLocation();
                LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

                boolean isOnGps = manager.isProviderEnabled(LocationManager.GPS_PROVIDER);

                if (!isOnGps) {
                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    Toast.makeText(EventoActivity.this, "Ative o GPS do dispositivo", Toast.LENGTH_SHORT).show();
                    startActivityForResult(intent, 20);
                }else{
                    try {
                        String uri = "http://maps.google.com/maps?saddr=" + mlocation.getLatitude() + "," + mlocation.getLongitude() + "&daddr=" + eventoClicado.getLatitude() + "," + eventoClicado.getLongitude();
                        Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(uri));
                        intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
                        startActivity(intent);
                    } catch (RuntimeException erro) {
                        Toast.makeText(EventoActivity.this, "Erro ao tentar encontrar o local do evento.", Toast.LENGTH_LONG).show();
                    }
                }


            }
        });
    }
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }else if(frameLayout.getVisibility() == View.VISIBLE){// se os detalhes estiver exibindo esconde
            frameLayout.setVisibility(View.INVISIBLE);
        } else {
            super.onBackPressed();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_filtro_estilos, menu);

        //Pega o Componente de busca do actionBar
        SearchView searchView = (SearchView) menu.findItem(R.id.ic_search).getActionView();
        //Define um texto de ajuda 'texto fantasma':
        searchView.setQueryHint(getString(R.string.texto_hint_campoBusca));
        // exemplos de utilização:
        buscarEvento(searchView);

//            getMenuInflater().inflate(R.menu.menu_filtro_estilos, menu);
            return super.onCreateOptionsMenu(menu);
        //return true;
    }

    private void buscarEvento(SearchView searchView) {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String valorBuscado) {
                if (eventoAdapter != null)
                    eventoAdapter.getFilter().filter(valorBuscado); //Inicia ao filtro das buscas
                return false;
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        carrossel.setVisibility(View.GONE);
        switch (item.getItemId()) {
            case R.id.opcao_filtro_data:
                buscaEventos("data");
                setFilterTextView("Filtrado por data");
                return true;
            case R.id.opcao_filtro_nome:
                buscaEventos("nome");
                setFilterTextView("Filtrado por nome");
                return true;
            case R.id.opcao_filtro_rock:
                buscaEventos("Rock".toLowerCase());
                setFilterTextView("Filtrado pelo estilo rock");
                return true;
            case R.id.opcao_filtro_pop:
                buscaEventos("Pop".toLowerCase());
                setFilterTextView("Filtrado pelo estilo pop");
                return true;
            case R.id.opcao_filtro_eletronica:
                buscaEventos("Eletrônica".toLowerCase());
                setFilterTextView("Filtrado pelo estilo eletrônica");
                return true;
            case R.id.opcao_filtro_forro:
                buscaEventos("Forró".toLowerCase());
                setFilterTextView("Filtrado pelo estilo forró");
                return true;
            case R.id.opcao_filtro_sertanejo:
                buscaEventos("Sertanejo".toLowerCase());
                setFilterTextView("Filtrado pelo estilo sertanejo");
                return true;
            case R.id.opcao_filtro_brega:
                buscaEventos("Brega".toLowerCase());
                setFilterTextView("Filtrado pelo estilo brega");
                return true;
            case R.id.opcao_filtro_swingueira:
                buscaEventos("Swingueira".toLowerCase());
                setFilterTextView("Filtrado pelo estilo swingueira");
                return true;
            case R.id.opcao_filtro_reggae:
                buscaEventos("Reggae".toLowerCase());
                setFilterTextView("Filtrado pelo estilo reggae");
                return true;
            case R.id.opcao_filtro_reggaeton:
                buscaEventos("Reggaeton".toLowerCase());
                setFilterTextView("Filtrado pelo estilo reggaeton");
                return true;
            case R.id.opcao_filtro_funk:
                buscaEventos("Funk".toLowerCase());
                setFilterTextView("Filtrado pelo estilo funk");
                return true;
            case R.id.opcao_filtro_funkCarioca:
                buscaEventos("Funk carioca".toLowerCase());
                setFilterTextView("Filtrado pelo estilo funk carioca");
                return true;
            case R.id.opcao_filtro_gospel:
                buscaEventos("Gospel".toLowerCase());
                setFilterTextView("Filtrado pelo estilo gospel");
                return true;
            case R.id.opcao_filtro_mpb:
                buscaEventos("MPB".toLowerCase());
                setFilterTextView("Filtrado pelo estilo MPB");
                return true;
            case R.id.opcao_filtro_classico:
                buscaEventos("Clássico".toLowerCase());
                setFilterTextView("Filtrado pelo estilo clássico");
                return true;
            case R.id.opcao_filtro_hiphop_rap:
                buscaEventos("Hip Hop/Rap".toLowerCase());
                setFilterTextView("Filtrado pelo estilo Hip Hop/Rap");
                return true;
            case R.id.opcao_filtro_samba:
                buscaEventos("Samba".toLowerCase());
                setFilterTextView("Filtrado pelo estilo samba");
                return true;
            case R.id.opcao_filtro_dance:
                buscaEventos("Dance".toLowerCase());
                setFilterTextView("Filtrado pelo estilo dance");
                return true;
            case R.id.opcao_filtro_axe:
                buscaEventos("Axé".toLowerCase());
                setFilterTextView("Filtrado pelo estilo axé");
                return true;
            case R.id.opcao_filtro_heavymetal:
                buscaEventos("Heavy Metal".toLowerCase());
                setFilterTextView("Filtrado pelo estilo heavy metal");
                return true;
            case R.id.opcao_filtro_instrumental:
                buscaEventos("Instrumental".toLowerCase());
                setFilterTextView("Filtrado pelo estilo instrumental");
                return true;
            case R.id.opcao_filtro_jazz:
                buscaEventos("Jazz".toLowerCase());
                setFilterTextView("Filtrado pelo estilo Jazz");
                return true;
            case R.id.opcao_filtro_pagode:
                buscaEventos("Pagode".toLowerCase());
                setFilterTextView("Filtrado pelo estilo pagode");
                return true;
            case R.id.opcao_filtro_progressivo:
                buscaEventos("Progressivo".toLowerCase());
                setFilterTextView("Filtrado pelo estilo progressivo");
                return true;
            case R.id.opcao_filtro_country:
                buscaEventos("Country".toLowerCase());
                setFilterTextView("Filtrado pelo estilo country");
                return true;
            case R.id.opcao_filtro_outros:
                buscaEventos("Outros".toLowerCase());
                setFilterTextView("Filtrado pelo estilo Outros");
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
    public static Palette createPaletteSync(Bitmap bitmap) {
        Palette p = Palette.from(bitmap).generate();
        return p;
    }
    public void setFilterTextView(String mensagem){
        text_filtro.setText(mensagem);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        frameLayout.setVisibility(View.INVISIBLE);
        int id = item.getItemId();
        if (id == R.id.nav_add_evento) {
            startActivity(new Intent(EventoActivity.this,AddEventoActivity.class));
        } else if (id == R.id.nav_meus_eventos) {
            carrossel.setVisibility(View.GONE);
            master = "meusEventos";
            buscaEventos("");
            setTitleActionBar("Meus eventos");
        } else if (id == R.id.nav_eventos_irei) {
            carrossel.setVisibility(View.GONE);
            master = "ireiEventos";
            buscaEventos("");
            setTitleActionBar("Eventos que irei");
        } else if (id == R.id.nav_todos_eventos) {
            carrossel.setVisibility(View.VISIBLE);
            master = "todosEventos";
            buscaEventos("data");
            setTitleActionBar("Eventos");
        }else if (id == R.id.nav_busca_evento_proximo) {
            master = "pertoEventos";
            buscaEventos("");
           getSupportActionBar().setTitle("Eventos perto");
        } else if (id == R.id.nav_aprova_anuncios) {
            startActivity(new Intent(EventoActivity.this,AdminActivity.class));
        } else if (id == R.id.nav_edit_user) {
            startActivity(new Intent(EventoActivity.this, AddUserActivity.class).putExtra("code",true));
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
    public void setTitleActionBar(String titulo){
        getSupportActionBar().setTitle(titulo);
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
}