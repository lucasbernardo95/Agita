package com.example.suelliton.agita.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.example.suelliton.agita.R;
import com.example.suelliton.agita.fragment.AddEventoFragment;
import com.example.suelliton.agita.fragment.MeusEventoFragment;
import com.example.suelliton.agita.fragment.TodosEventoFragment;

import static com.example.suelliton.agita.activity.SplashActivity.LOGADO;
public class EventoActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    DrawerLayout drawer;
    Toolbar toolbar;
    FragmentManager fm;
       @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_evento);
        findViews();
        setSupportActionBar(toolbar);
           fm = getSupportFragmentManager();
           FragmentTransaction ft = fm.beginTransaction();
           ft.add(R.id.fragment_content, new TodosEventoFragment());
           ft.commit();


        setViewListener();
    }

    public void findViews(){
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }
    public void setViewListener(){
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
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

        if (id == R.id.nav_add_anuncio) {
            FragmentTransaction ft = fm.beginTransaction();
            ft.replace(R.id.fragment_content, new AddEventoFragment());
            ft.commit();
        } else if (id == R.id.nav_meus_anuncios) {
            FragmentTransaction ft = fm.beginTransaction();
            ft.replace(R.id.fragment_content, new MeusEventoFragment());
            ft.commit();
        } else if (id == R.id.nav_todos_anuncios) {
            FragmentTransaction ft = fm.beginTransaction();
            ft.replace(R.id.fragment_content, new TodosEventoFragment());
            ft.commit();
        } else if (id == R.id.nav_logout) {
            SharedPreferences sharedPreferences = getSharedPreferences("sharedPreferences", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("usuarioLogado", "");
            editor.apply();
            LOGADO="";
            startActivity(new Intent(EventoActivity.this,LoginActivity.class));
            finish();

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
