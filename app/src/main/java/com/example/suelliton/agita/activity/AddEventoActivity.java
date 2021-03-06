package com.example.suelliton.agita.activity;


import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.TimePicker;

import com.example.suelliton.agita.R;
import com.example.suelliton.agita.model.Evento;
import com.example.suelliton.agita.utils.Util;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Calendar;
import java.util.List;

import static com.example.suelliton.agita.activity.EditEventoActivity.listaEstilos;
import static com.example.suelliton.agita.activity.SplashActivity.database;
import static com.example.suelliton.agita.activity.SplashActivity.idEventoReference;
import static com.example.suelliton.agita.activity.SplashActivity.usuarioLogado;

public class AddEventoActivity extends AppCompatActivity {
    private final int REQUEST_GALERIA = 2;
    private final int REQUEST_MAPA_LOCAL = 3;
    EditText ed_nome;
    CalendarView cv_data;
    TextView value_ed_hora; //exibe o valorDetalhe da horaDetalhe
    ImageButton bt_ed_hora; //chama o relógio para editar a horaDetalhe
    ImageButton bt_local_evento; //Botão para abrir o mapa de marcação do local do evento
    AutoCompleteTextView ed_endereco;
    AutoCompleteTextView ed_estilo;
    EditText ed_bandas;
    EditText ed_entrada;
    EditText ed_descricao;
    EditText ed_casaShow;
    ImageView imageView;
    FirebaseStorage storage;
    StorageReference storageReference;
    Bitmap bitmapGaleria = null, bannerBACKUP = null;
    private String urlBanner = "";
    private List<String> listaLocais;
    private Evento novoEvento;
    ProgressBar progress;
    private DatabaseReference referenceEventoTemporario;

    private long idEventoCriado = 0;
    private double[] latlong;

    private final String TAG = "teste";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_evento);
        findViews();
        setViewListeners();
        storage = FirebaseStorage.getInstance();



        referenceEventoTemporario = database.getReference("eventoTemporario");
        Log.i(TAG, "2 - Modo reference edit: "+referenceEventoTemporario.getRef());
//        carregaLocais();

    }

    @Override
    protected void onResume() {
        super.onResume();
        //incrementa o id do evento (na classe SplashActivity)
        incrementaIdEvento();
    }

    //Método para chamar o Time Picker Dialog e setar a horaDetalhe no evento
    public  void setTimeEvent() {

        Calendar calendario = Calendar.getInstance();
        int hora = calendario.get(Calendar.HOUR);
        int minuto = calendario.get(Calendar.MINUTE);

        TimePickerDialog timePicker;

        timePicker = new TimePickerDialog(AddEventoActivity.this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int horaDoDia, int minutos) {
                value_ed_hora.setText(horaDoDia + ":"+minutos); //Seta a horaDetalhe no EditText
            }
        }, hora, minuto, true);

        timePicker.show();
    }

    //Seta os valores do evento a ser editado nos edittext's


//    public void carregaLocais(){
//        listaLocais = new ArrayList<>();
//        locaisReference.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                for (DataSnapshot data:dataSnapshot.getChildren() ) {
//                    listaLocais.add(data.getRef().getKey());
//                    Log.i(TAG,"5 - localDetalhe: "+data.getRef().getKey());
//                }
//                ArrayAdapter<String> adapter = new ArrayAdapter<>(AddEventoActivity.this,
//                        android.R.layout.simple_dropdown_item_1line, listaLocais);
//                ed_endereco.setThreshold(1);
//                ed_endereco.setAdapter(adapter);
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });
//    }

    public void findViews(){
        progress = (ProgressBar) findViewById(R.id.progress);
        ed_nome = (EditText) findViewById(R.id.nome_cadastro);
        cv_data = (CalendarView) findViewById(R.id.cv_dataCadastro);
        value_ed_hora = (TextView) findViewById(R.id.value_hora_cadastro);
        bt_ed_hora = (ImageButton) findViewById(R.id.hora_cadastro);
        ed_endereco = (AutoCompleteTextView) findViewById(R.id.endereco_cadastro2);

        //Seta a lista de estilos no adapter
        ed_estilo = (AutoCompleteTextView) findViewById(R.id.estilo_cadastro);
        ArrayAdapter<String> adaptadorEstilos = new
                ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item, listaEstilos);//pega a lista do EditEventActivity
        ed_estilo.setAdapter(adaptadorEstilos);
        ed_bandas = (EditText) findViewById(R.id.bandas_cadastro);
        ed_entrada = (EditText) findViewById(R.id.entrada_cadastro);
        ed_descricao = (EditText) findViewById(R.id.descricao_cadastro);
        ed_casaShow = (EditText) findViewById(R.id.casa_show_cadastro);
        imageView = (ImageView) findViewById(R.id.imagem_galeria);
        ((ImageView) findViewById(R.id.imgStatusLocal)).setBackgroundResource(R.drawable.ic_alert);
        bt_ed_hora.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setTimeEvent();
            }
        });

        //Abre um fragment para que o usuário possa marcar o local do evento no mapa
        bt_local_evento = (ImageButton) findViewById(R.id.botaoLocalEvento);
        bt_local_evento.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AddEventoActivity.this, MapsActivity.class);
                startActivityForResult(intent , REQUEST_MAPA_LOCAL);
            }
        });

    }

    private void alertField(String message){

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Dado inválido!") //seta o título e a mensagem
                .setMessage(message);

        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
        progress.setVisibility(View.GONE);
    }

    private boolean isCampoVazio(String valor) {
        //verifica se o valor passado é nulo ou se contém apenas um 'espaço' digitado
        return (TextUtils.isEmpty(valor) || valor.trim().isEmpty());
    }

    public void setViewListeners(){

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent,REQUEST_GALERIA);
            }
        });

    }

    //incrementa o ID do evento sempre que um novo evento for adicionado
    public void incrementaIdEvento(){

        final long[] id = new long[1];
        idEventoReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                idEventoCriado = (long) dataSnapshot.getValue();
                Log.i("teste", "saída incrementaIdEvento: " +idEventoCriado);
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

    public void salvarEvento(){
        progress.setVisibility(View.VISIBLE);
        String nome = ed_nome.getText().toString();
        String hora = value_ed_hora.getText().toString();
        String data = Util.convertMillisToDate(cv_data.getDate());
        final String local = ed_endereco.getText().toString();
        String estilo = ed_estilo.getText().toString();
        String bandas = ed_bandas.getText().toString();
        double entrada = ed_entrada.getText().toString() == null? 0 : Double.parseDouble(ed_entrada.getText().toString());
        String descricao = ed_descricao.getText().toString();
        String casa = ed_casaShow.getText().toString();

        if (isCampoVazio(nome)) { //verificação do nomeDetalhe
            alertField(getString(R.string.aviso_nome_validacao));
            ed_nome.requestFocus();
            return;
        } else if (isCampoVazio(hora)) { //horaDetalhe
            alertField(getString(R.string.aviso_hora_validacao));
            value_ed_hora.requestFocus();
            return;
        } else if (isCampoVazio(data)) {
            alertField(getString(R.string.aviso_data_validacao));
            cv_data.requestFocus();
            return;
        } else if (latlong == null) {
            alertField(getString(R.string.aviso_local_validacao));
            return;
        } else if (isCampoVazio(estilo)) {
            alertField(getString(R.string.aviso_estilo_validacao));
            ed_estilo.requestFocus();
            return;
        }else if (entrada < 0) {
            alertField(getString(R.string.aviso_valor_validacao));
            ed_entrada.requestFocus();
            return;
        }

//        List<Address> enderecos = Util.getNomeLocalFromEndereco(AddEventoActivity.this,local);

        if(/*enderecos.size()*/ latlong == null) {//verifica se veio algum endereço
            alertField( getString(R.string.alerta_endereco_invalido));
            progress.setVisibility(View.GONE);
            ed_endereco.requestFocus();
        }else{
//            double lat = enderecos.get(0).getLatitude();
//            double lng = enderecos.get(0).getLongitude();
            String lat = String.valueOf(latlong[0]);
            String lng = String.valueOf(latlong[1]);

            //se for um evento novo e não tiver foto, coloca a foto default antes de salvar o evento no bd
            if(bitmapGaleria == null){ //Se não tem nada na galeria, coloca a foto default
                urlBanner = "https://firebasestorage.googleapis.com/v0/b/agita-ed061.appspot.com/o/eventos%2Fevento_sem_banner.png?alt=media&token=a6f53830-48bb-4388-b242-7cc589278e03";
            }
            //incrementa o valor do id
            final long idEvento = idEventoCriado + 1;
            //monta a key do evento (id-loginUsuario) ex: 8-admin
            final String keyEvento = String.valueOf(idEvento) + "-" + usuarioLogado.getLogin();

            novoEvento = new Evento(nome, data, hora, local, estilo, lat, lng, bandas, entrada, descricao, urlBanner,  casa,usuarioLogado.getLogin());
            novoEvento.setKey(keyEvento);
            //Se for um cadastro, armazena numa tabela temporária para os eventos ainda não verificados por um administrador
            referenceEventoTemporario.child(keyEvento).setValue(novoEvento).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    //Depois atualiza o valor da variável id no firebase
                    idEventoReference.child("id").setValue(idEvento);
                    //Se não tem nada na galeria, coloca a foto default
                    if(bitmapGaleria == null){
                        customAlert("Evento sem banner!", "Você poderá incluir na aba 'Meus eventos'.");
                    }else{//Caso contrário, se tem uma foto, faz p upload
                        try {
                            uploadFirebaseBytes(bitmapGaleria, keyEvento);
                            customAlert("Evento cadastrado!", "Operação realizada com sucesso!");
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });

            //locaisReference.child(localDetalhe).setValue(localDetalhe);
    }

}

    private void customAlert(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title) //seta o título e a mensagem
                .setMessage(message);

        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                finish();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false); //não permite que o usuário click fora da caixa de diálogo
        dialog.show();

    }

    /**
     * key = Chave do evento que se deseja setar o banner
     * bitmap = Imagem que deseja setar ao evento
     * */
    public void uploadFirebaseBytes(Bitmap bitmap, final String key) throws FileNotFoundException {

        storageReference = storage.getReference("eventos");
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, baos);
        byte[] data = baos.toByteArray();

        //cria uma referência no storage apontando para a key do evento ao qual a imagemDetalhe pertence
        UploadTask uploadTask = storageReference.child(key).putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                //Verifica se o upload ocorreu com sucesso, pega a URI do banner e atualiza as informações no evento ao qual o banner percente
                StorageReference islandRef = storageReference.child(key);
                 islandRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        //se for um cadastro, cria uma nova url para o banner após o upload
                            urlBanner = uri.toString();
                            referenceEventoTemporario.child(key).child("urlBanner").setValue(urlBanner);

                    }
                });

            }
        });



    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_salvar, menu);
        return super.onCreateOptionsMenu(menu);
        //return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_confirm) {
            salvarEvento();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//        Log.i(TAG, "estou aqui");
//        Log.i(TAG, "requestCode " + requestCode);
//        Log.i(TAG, "resultCode " +resultCode);
        if(resultCode == RESULT_OK && requestCode == REQUEST_GALERIA){
            Uri uriSelectedImage = data.getData();
            CropImage.activity(uriSelectedImage)
                    .start(this);
        }else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                imageView.setImageURI(resultUri);
                try {
                    bitmapGaleria = Util.createParcelDescriptor(this,resultUri,500);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        } else if (resultCode == REQUEST_MAPA_LOCAL && requestCode == REQUEST_MAPA_LOCAL) {

            latlong = data.getDoubleArrayExtra("coordenada");
            Log.i(TAG,"0: "+ latlong[0]);
            Log.i(TAG, "1: "+ latlong[1]);
            if ( (latlong[0] == 0.0 && latlong[1] == 0.0) || latlong == null){
                ((TextView) findViewById(R.id.status_local)).setText("Informe o local do evento!");
                ((ImageView) findViewById(R.id.imgStatusLocal)).setBackgroundResource(R.drawable.ic_alert);
            }else{
                ((TextView) findViewById(R.id.status_local)).setText("Ok!");
                ((ImageView) findViewById(R.id.imgStatusLocal)).setBackgroundResource(R.drawable.ic_ok);
            }

        }

    }

}
