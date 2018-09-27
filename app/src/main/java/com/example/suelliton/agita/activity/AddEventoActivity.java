package com.example.suelliton.agita.activity;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.suelliton.agita.R;
import com.example.suelliton.agita.model.Evento;
import com.example.suelliton.agita.utils.MyDatabaseUtil;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.SuccessContinuation;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import static com.example.suelliton.agita.activity.SplashActivity.eventosReference;
import static com.example.suelliton.agita.activity.SplashActivity.locaisReference;
import static com.example.suelliton.agita.activity.SplashActivity.usuarioLogado;

public class AddEventoActivity extends AppCompatActivity {
    private final int REQUEST_GALERIA = 2;
    EditText ed_nome;
    CalendarView cv_data;
    EditText ed_hora;
    AutoCompleteTextView ed_local;
    EditText ed_estilo;
    EditText ed_bandas;
    EditText ed_valor;
    EditText ed_descricao;
    EditText ed_casaShow;
    CheckedTextView ed_liberado;
    Button btnSalvarEvento;
    ImageView imageView;
    FirebaseStorage storage;
    StorageReference storageReference;
    Bitmap bannerGaleria;
    String urlBanner = "";
    List<String> listaLocais;
    private Evento eventoEdit;
    ProgressBar progress;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_evento);
        storage = FirebaseStorage.getInstance();
        findViews();
        setViewListeners();

        Bundle pacote = getIntent().getExtras();

        if (pacote != null) {
            eventoEdit = (Evento) pacote.getSerializable("eventoEdit");
            setEventoEdit();
        }

        carregaLocais();

    }

    private void setEventoEdit(){
        ed_nome.setText(eventoEdit.getNome());
        StorageReference storageReference = FirebaseStorage.getInstance().getReference("eventos");

        StorageReference islandRef = storageReference.child(eventoEdit.getNome());
        islandRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).into(imageView);
            }
        });
        ed_hora.setText(eventoEdit.getHora());
        ed_local.setText(eventoEdit.getLocal());
        ed_estilo.setText(eventoEdit.getEstilo());
        ed_bandas.setText(eventoEdit.getBandas());
        ed_valor.setText(String.valueOf(eventoEdit.getValor()));
        ed_descricao.setText(eventoEdit.getDescricao());
        ed_casaShow.setText(eventoEdit.getCasashow());
        ed_liberado.setChecked(false);//desmarca
        btnSalvarEvento.setText(R.string.botaoEditarEvento);
    }

    public void carregaLocais(){
        listaLocais = new ArrayList<>();
        locaisReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot data:dataSnapshot.getChildren() ) {
                    listaLocais.add(data.getRef().getKey());
                    Log.i("locais",data.getRef().getKey());
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<>(AddEventoActivity.this,
                        android.R.layout.simple_dropdown_item_1line, listaLocais);
                ed_local.setThreshold(1);
                ed_local.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void findViews(){
        progress = (ProgressBar) findViewById(R.id.progress);
        ed_nome = (EditText) findViewById(R.id.nome_cadastro);
        cv_data = (CalendarView) findViewById(R.id.cv_dataCadastro);
        ed_hora = (EditText) findViewById(R.id.hora_cadastro);
        ed_local = (AutoCompleteTextView) findViewById(R.id.local_cadastro);
        ed_estilo = (EditText) findViewById(R.id.estilo_cadastro);
        ed_bandas = (EditText) findViewById(R.id.bandas_cadastro);
        ed_valor = (EditText) findViewById(R.id.valor_cadastro);
        ed_descricao = (EditText) findViewById(R.id.descricao_cadastro);
        ed_casaShow = (EditText) findViewById(R.id.casa_show_cadastro);
        ed_liberado = (CheckedTextView) findViewById(R.id.liberado_cadastro);
        ed_liberado.setChecked(true); //inicia como true
        imageView = (ImageView) findViewById(R.id.imagem_galeria);

        btnSalvarEvento = (Button) findViewById(R.id.salvar_evento);
    }

    public void setViewListeners(){
        btnSalvarEvento.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progress.setVisibility(View.VISIBLE);

                String nome = ed_nome.getText().toString();
                Log.i("edit", "nome: " + nome);
                Log.i("edit", "bannerGaleria: " + bannerGaleria);
                try {
                    if(eventoEdit != null) {
                        bannerGaleria = ((BitmapDrawable)imageView.getDrawable()).getBitmap();
                    }
                    uploadFirebaseBytes(bannerGaleria, nome);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }


                String hora = ed_hora.getText().toString();
                String data = convertMillisToDate(cv_data.getDate());
                final String local = ed_local.getText().toString();
                String estilo = ed_estilo.getText().toString();
                String bandas = ed_bandas.getText().toString();
                double valor = Double.parseDouble(ed_valor.getText().toString());
                String descricao = ed_descricao.getText().toString();
                String casa = ed_casaShow.getText().toString();
                boolean liberado = ed_liberado.isChecked();//verifica o estado do botão se marcado ou não

                Geocoder geocoder = new Geocoder(AddEventoActivity.this);
                List<Address> enderecos = new ArrayList<>();
                try {
                    enderecos = geocoder.getFromLocationName(local,1);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                double lat,lng;
                if(enderecos.size()!= 0) {//verifica se veio algum endereço
                    lat = enderecos.get(0).getLatitude();
                    lng = enderecos.get(0).getLongitude();
                }else{
                    lat = 11111;
                    lng = 11111;
                }
                final Evento evento;

                if (eventoEdit == null ) {
                    evento  = new Evento(nome, data, hora, local, estilo, lat, lng, bandas, valor, descricao, urlBanner, liberado, casa, false, usuarioLogado.getLogin());
                    eventosReference.push().setValue(evento).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Query query = eventosReference.orderByChild("nome").startAt(evento.getNome()).endAt(evento.getNome()).limitToFirst(1);
                            query.addChildEventListener(new ChildEventListener() {
                                @Override
                                public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                                    eventosReference.child(dataSnapshot.getRef().getKey()).child("key").setValue(dataSnapshot.getRef().getKey());

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
                    });
                } else {
                    evento  = new Evento(nome, data, hora, local, estilo, lat, lng, bandas, valor, descricao, eventoEdit.getUrlBanner(), liberado, casa, false, usuarioLogado.getLogin());
                    evento.setKey(eventoEdit.getKey());
                    eventosReference.child(eventoEdit.getKey()).setValue(evento);
                }
                //locaisReference.child(local).setValue(local);


                Toast.makeText(AddEventoActivity.this, "Evento salvo com sucesso!", Toast.LENGTH_SHORT).show();
                limpaCampos();
                progress.setVisibility(View.INVISIBLE);
                finish();
            }

        });

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent,REQUEST_GALERIA);
            }
        });
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
    }
    public void limpaCampos(){
        ed_nome.setText("");

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
    public void uploadFirebaseBytes(Bitmap bitmap, final String nomeEvento) throws FileNotFoundException {
        storageReference = storage.getReference("eventos");
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, baos);
        byte[] data = baos.toByteArray();

        final UploadTask uploadTask = storageReference.child(nomeEvento).putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                StorageReference islandRef = storageReference.child(nomeEvento);
                islandRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        urlBanner = uri.toString();
                        Query query = eventosReference.orderByChild("nome").equalTo(nomeEvento);
                        query.addChildEventListener(new ChildEventListener() {
                            @Override
                            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                                if(dataSnapshot.exists()) {
                                    //Toast.makeText(AddEventoActivity.this, ""+dataSnapshot.getKey(), Toast.LENGTH_SHORT).show();
                                    eventosReference.child(dataSnapshot.getRef().getKey()).child("urlBanner").setValue(urlBanner);
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
                       // Toast.makeText(AddEventoActivity.this, "uri: "+uri.toString(), Toast.LENGTH_SHORT).show();
                    }
                });


            }
        });






    }
    public String convertMillisToDate(long yourmilliseconds){

        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yy", Locale.US);


        GregorianCalendar calendar = new GregorianCalendar(TimeZone.getTimeZone("US/Central"));
        calendar.setTimeInMillis(yourmilliseconds);

        Log.i("click","GregorianCalendar -"+sdf.format(calendar.getTime()));


        return sdf.format(calendar.getTime());
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK && requestCode == REQUEST_GALERIA){
            Uri selectedImage = data.getData();
            String[] filePath = {MediaStore.Images.Media.DATA};
            Cursor c = getContentResolver().query(selectedImage,filePath,null,null,null);
            c.moveToFirst();
            int columnIndex = c.getColumnIndex(filePath[0]);
            String picturePath = c.getString(columnIndex);
            c.close();
            bannerGaleria = (BitmapFactory.decodeFile(picturePath));
            imageView.setImageBitmap(bannerGaleria);
        }

    }
}
