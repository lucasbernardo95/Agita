package com.example.suelliton.agita.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.example.suelliton.agita.R;
import com.example.suelliton.agita.model.Usuario;
import com.example.suelliton.agita.utils.MyDatabaseUtil;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import static com.example.suelliton.agita.activity.EventoActivity.eventosCurtidos;

public class SplashActivity extends AppCompatActivity {
    public String LOGADO;
    public static FirebaseDatabase database ;
    public static DatabaseReference usuarioReference;
    public static DatabaseReference eventosReference;
    public static DatabaseReference locaisReference;
    public static Usuario usuarioLogado;
    ChildEventListener childListener;
    ValueEventListener valueListener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);

        database = MyDatabaseUtil.getDatabase();

        usuarioReference = database.getReference("usuarios");
        eventosReference = database.getReference("eventos");
        locaisReference = database.getReference("locais");

        //Usuario admin = new Usuario("admin","","admin","admin","",true,"");
        //usuarioReference.child(admin.getLogin()).setValue(admin);

        SharedPreferences sharedPreferences = getSharedPreferences("sharedPreferences", Context.MODE_PRIVATE);
        //depois tirar esse trecho senao vai dar bode e nao guardar o usuario logado
        //SharedPreferences.Editor editor = sharedPreferences.edit();
        //editor.putString("usuarioLogado", "");
        //editor.apply();
        //depois tirar esse trecho senao vai dar bode e nao guardar o usuario logado

        LOGADO = sharedPreferences.getString("usuarioLogado", "");

    }

    @Override
    protected void onStart() {
        super.onStart();
        Query queryUsuario = usuarioReference.orderByChild("login").equalTo(LOGADO).limitToFirst(1);

        if (!LOGADO.equals("")) {
            childListener = queryUsuario.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    try {
                        Usuario usuario = dataSnapshot.getValue(Usuario.class);
                        if (usuario != null) {
                            usuarioLogado = usuario;
                            removeListenersFirebase();
                            listenerUsuarioLogado();
                            //Toast.makeText(SplashActivity.this, "usuario logado : "+usuario.getEmail(), Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(SplashActivity.this, EventoActivity.class));
                            finish();

                        }

                    } catch (Exception e) {
                        Toast.makeText(SplashActivity.this, "Erro no banco de dados, contate administrador.", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {

                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

            if (!isOnline(this)) {
                Toast.makeText(this, "Sem conexão", Toast.LENGTH_SHORT).show();
            }

        }else{
            Handler h = new Handler();
            h.postDelayed(new Runnable() {
                @Override
                public void run() {
                    startActivity(new Intent(SplashActivity.this, EventoActivity.class));
                    finish();
                }
            },200);
        }



    }
    public void listenerUsuarioLogado(){
        removeListenersFirebase();
        usuarioReference.child(usuarioLogado.getLogin()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                usuarioLogado = dataSnapshot.getValue(Usuario.class);
                eventosCurtidos = usuarioLogado.getCurtidos();
                //Toast.makeText(EventoActivity.this, "nomeDetalhe "+usuarioLogado.getNome(), Toast.LENGTH_SHORT).show();
                if(eventosCurtidos == null){
                    eventosCurtidos = new ArrayList<>();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
    public void removeListenersFirebase(){
        if(childListener != null)eventosReference.removeEventListener(childListener);
        if(valueListener != null)eventosReference.removeEventListener(valueListener);
    }

    //VERIFICA SE EXISTE WIFI
    public static boolean isOnline(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnected())
            return true;
        else
            return false;
    }
}
