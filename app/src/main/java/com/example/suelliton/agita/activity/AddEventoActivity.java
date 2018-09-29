package com.example.suelliton.agita.activity;


import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextClock;
import android.widget.TextView;
import android.widget.TimePicker;
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
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import static android.widget.Toast.LENGTH_LONG;
import static com.example.suelliton.agita.activity.SplashActivity.eventosReference;
import static com.example.suelliton.agita.activity.SplashActivity.locaisReference;
import static com.example.suelliton.agita.activity.SplashActivity.usuarioLogado;

public class AddEventoActivity extends AppCompatActivity {
    private final int REQUEST_GALERIA = 2;
    EditText ed_nome;
    CalendarView cv_data;
    TextView value_ed_hora; //exibe o valor da hora
    ImageButton bt_ed_hora; //chama o relógio para editar a hora
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
    Bitmap bannerGaleria = null, bannerBACKUP = null;
    private String urlBanner = "", urlBannerBackup = "";
    private List<String> listaLocais;
    private Evento eventoEdit, novoEvento;
    ProgressBar progress;
    private DatabaseReference referenceEventoTemporario = null;
    boolean semFoto = false;
    //atributo da classe.
    private AlertDialog alerta;
    private final String TAG = "teste";

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
            urlBannerBackup = eventoEdit.getUrlBanner(); //guarda a url

            Log.i(TAG, "1 - Modo edit: "+eventoEdit.toString());
            Log.i(TAG, "1.1: "+eventoEdit.isVerificado());
            Log.i(TAG, "1.2: "+ urlBannerBackup);

            setEventoEdit();
        } //se é cadastro, cria uma nova referência para a tabela de eventos temporários
        referenceEventoTemporario = FirebaseDatabase.getInstance().getReference("eventoTemporario");
        Log.i(TAG, "2 - Modo reference edit: "+referenceEventoTemporario.getRef());
        carregaLocais();

    }

    //Método para chamar o Time Picker Dialog e setar a hora no evento
    public  void setTimeEvent() {

        Calendar calendario = Calendar.getInstance();
        int hora = calendario.get(Calendar.HOUR);
        int minuto = calendario.get(Calendar.MINUTE);

        TimePickerDialog timePicker;

        timePicker = new TimePickerDialog(AddEventoActivity.this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int horaDoDia, int minutos) {
                value_ed_hora.setText(horaDoDia + ":"+minutos); //Seta a hora no EditText
            }
        }, hora, minuto, true);

        timePicker.show();
    }

    //Seta os valores do evento a ser editado nos edittext's
    private void setEventoEdit(){
        ed_nome.setText(eventoEdit.getNome());

        Picasso.get().load(eventoEdit.getUrlBanner()).into(imageView);

        value_ed_hora.setText(eventoEdit.getHora());
        ed_local.setText(eventoEdit.getLocal());
        ed_estilo.setText(eventoEdit.getEstilo());
        ed_bandas.setText(eventoEdit.getBandas());
        ed_valor.setText(String.valueOf(eventoEdit.getValor()));
        ed_descricao.setText(eventoEdit.getDescricao());
        ed_casaShow.setText(eventoEdit.getCasashow());
        ed_liberado.setChecked(eventoEdit.isLiberado());
        btnSalvarEvento.setText(R.string.botaoEditarEvento);
        //Salva a imagem original do evento para verificar, posteriormente, se houve alguma modificação
        bannerBACKUP = ((BitmapDrawable)imageView.getDrawable()).getBitmap();

        Log.i(TAG, "4 - Modo edit ");
    }

    public void carregaLocais(){
        listaLocais = new ArrayList<>();
        locaisReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot data:dataSnapshot.getChildren() ) {
                    listaLocais.add(data.getRef().getKey());
                    Log.i(TAG,"5 - local: "+data.getRef().getKey());
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
        value_ed_hora = (TextView) findViewById(R.id.value_hora_cadastro);
        bt_ed_hora = (ImageButton) findViewById(R.id.hora_cadastro);
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

        bt_ed_hora.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setTimeEvent();
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

    public void setViewListeners(){

        btnSalvarEvento.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progress.setVisibility(View.VISIBLE);
                String nome = ed_nome.getText().toString();

                //============Verificação de campos do evento===================\\
                String hora = value_ed_hora.getText().toString();
                String data = convertMillisToDate(cv_data.getDate());
                final String local = ed_local.getText().toString();
                String estilo = ed_estilo.getText().toString();
                String bandas = ed_bandas.getText().toString();
                double valor = ed_valor.getText().toString() == null? 0 : Double.parseDouble(ed_valor.getText().toString());
                String descricao = ed_descricao.getText().toString();
                String casa = ed_casaShow.getText().toString();
                boolean liberado = ed_liberado.isChecked();//verifica o estado do botão se marcado ou não

                if (nome.equals("") || nome.length() < 4) { //verificação do nome
                    alertField("Por favor, informe um nome válido!");
                    ed_nome.requestFocus();
                    return;
                } else if (hora.equals("")) { //hora
                    alertField("Por favor, informe o horário do evento!");
                    value_ed_hora.requestFocus();
                    return;
                } else if (data.equals("")) {
                    alertField("Por favor, informe a data do evento!");
                    cv_data.requestFocus();
                    return;
                } else if (local.equals("")) {
                    alertField("Por favor, informe o local do evento!\nExemplo: Av Brasil Maranguape I, Natal, RN");
                    ed_local.requestFocus();
                    return;
                } else if (estilo.equals("")) {
                    alertField("Por favor, informe o estido do evento!");
                    ed_estilo.requestFocus();
                    return;
                }else if (valor < 0) {
                    alertField("Por favor, informe um valor válido!");
                    ed_valor.requestFocus();
                    return;
                }
                //============fim de campos do evento        ===================\\

                //Ecento sendo editado? pega a imagem que já tem
                if(eventoEdit != null) {
                    bannerGaleria = ((BitmapDrawable) imageView.getDrawable()).getBitmap();

                    Log.i(TAG, "Evento já tem foto");
                    Log.i(TAG, "bannerGaleria: "+ bannerGaleria.toString());
                    Log.i(TAG, "bannerBACKUP: " +bannerBACKUP.toString());

                    if (bannerGaleria == bannerBACKUP){
                        semFoto = true; //não irá fazer upload de foto novamente
                    }

                }else if(bannerGaleria == null){ //Se não tem nada na galeria, coloca a foto default
                    semFoto = true;
                    urlBanner = "https://firebasestorage.googleapis.com/v0/b/agita-ed061.appspot.com/o/eventos%2Fevento_sem_banner.png?alt=media&token=a6f53830-48bb-4388-b242-7cc589278e03";
                    Log.i(TAG, "pegou imagem sem banner");
                }

//                try {
//                    if (!semFoto) {
//                        Log.i(TAG, "6 - Carregando foto");
//                        uploadFirebaseBytes(bannerGaleria, nome);
//                    }
//                } catch (FileNotFoundException e) {
//                    e.printStackTrace();
//                }

                Geocoder geocoder = new Geocoder(AddEventoActivity.this);
                List<Address> enderecos = new ArrayList<>();
                try {
                    enderecos = geocoder.getFromLocationName(local,1);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                double lat,lng;
                if(enderecos.size()== 0) {//verifica se veio algum endereço
                    customAlert("Endereço não encontrado!", "Por favor, inclua um endereço válido no seguinte formato: rua ou casa de show, cidade, estado.",true);
                    progress.setVisibility(View.GONE);
                    ed_local.requestFocus();
                }else{

                    lat = enderecos.get(0).getLatitude();
                    lng = enderecos.get(0).getLongitude();


                    if (eventoEdit == null) {
                        novoEvento = new Evento(nome, data, hora, local, estilo, lat, lng, bandas, valor, descricao, urlBanner, liberado, casa, false, usuarioLogado.getLogin());

                        //Se for um cadastro, armazena numa tabela temporária para os eventos ainda não verificados por um administrador
                        referenceEventoTemporario.push().setValue(novoEvento).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Query query = referenceEventoTemporario.orderByChild("nome").startAt(novoEvento.getNome()).endAt(novoEvento.getNome()).limitToFirst(1);
                                query.addChildEventListener(new ChildEventListener() {
                                    @Override
                                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                                        referenceEventoTemporario.child(dataSnapshot.getRef().getKey()).child("key").setValue(dataSnapshot.getRef().getKey());

                                        //Após salvar o evento no firebase, verifica se não tem foto,
                                        //caso não tenha, faz o upload da foto selecionada na galeria
                                        if (!semFoto) {
                                            try {
                                                Log.i(TAG, "Novo evento - iniciando upload de banner");
                                                uploadFirebaseBytes(bannerGaleria, dataSnapshot.getRef().getKey());
                                            } catch (FileNotFoundException e) {
                                                e.printStackTrace();
                                            }
                                        } else {
                                            finish();
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
                        });
                    } else {
                        novoEvento = new Evento(nome, data, hora, local, estilo, lat, lng, bandas, valor, descricao, eventoEdit.getUrlBanner(), liberado, casa, false, usuarioLogado.getLogin());
                        novoEvento.setKey(eventoEdit.getKey());
                        novoEvento.setVerificado(eventoEdit.isVerificado());

                        Log.i(TAG, "7 - EVENTO EDIT: "+eventoEdit.toString());
                        Log.i(TAG, "8 - EVENTO NOVO: "+novoEvento.toString());
                        Log.i(TAG, "9 - EVENTO reference: "+referenceEventoTemporario.getRef());

                        //Se não for um evento verificado, muda a referência para a tabela temporária
                        if (eventoEdit.isVerificado()) {
                            eventosReference.child(eventoEdit.getKey()).setValue(novoEvento);
                        }else {
                            Log.i(TAG, "10 - chave: "+eventoEdit.getKey());
                            referenceEventoTemporario.child(eventoEdit.getKey()).setValue(novoEvento);
                        }

                        //Após salvar o evento no firebase, e se houver modificação no banner
                        //atualiza a imagem no storage
                        if (!semFoto) {
                            try {
                                Log.i(TAG, "Edit evento - uploading de banner");
                                uploadFirebaseBytes(bannerGaleria, eventoEdit.getKey());
                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                            }
                        } else {
                            finish();
                        }
                    }
                    //locaisReference.child(local).setValue(local);
                }

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

    private void customAlert(String title, String message,final boolean endereco) {

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

//    private void

    public void limpaCampos(){
        ed_nome.setText("");

        value_ed_hora.setText("");
        ed_local.setText("");
        ed_estilo.setText("");
        ed_bandas.setText("");
        ed_valor.setText("");
        ed_descricao.setText("");
        ed_casaShow.setText("");
        ed_liberado.setChecked(false);//desmarca
        btnSalvarEvento.setText("");
    }

    //Chamado quando o banner de um evento é alterado
    //Baixa o banner do evento alterado pela key
    private String atualizaImagemEvento(final String key) {
        final String[] t = {""};

        StorageReference storageReference = FirebaseStorage.getInstance().getReference("eventos");

        //Usa a chave do evento como identificador de seu banner. Com isso, ao atualizar os dados de um evento
        //o mesmo não criará uma nova imagem no banco
        StorageReference islandRef = storageReference.child(key);
        islandRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                t[0] = uri.toString();
                Log.i(TAG, "pegou a url do banner para editar");
            }
        });

        return t[0];
    }

    //Seta a url do banner no evento de acordo com a chave e url passada
    private void setUrlEvent(final String url, String key) {
        Log.i(TAG, "Atualizando URL_BANNER do evento");

        Query query = null;

        //Se não for um evento verificado, muda a referência para a tabela temporária
        if (novoEvento.isVerificado()){
            query = eventosReference.orderByKey().equalTo(key);
        }else {
            query = referenceEventoTemporario.orderByKey().equalTo(key);
        }
        query.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if(dataSnapshot.exists()) {
                    if (novoEvento.isVerificado()) {
                        eventosReference.child(dataSnapshot.getRef().getKey()).child("urlBanner").setValue(url);
                    } else {
                        referenceEventoTemporario.child(dataSnapshot.getRef().getKey()).child("urlBanner").setValue(url);
                    }
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

    /**
     * key = Chave do evento que se deseja setar o banner
     * bitmap = Imagem que deseja setar ao evento
     * */
    public void uploadFirebaseBytes(Bitmap bitmap, final String key) throws FileNotFoundException {

        storageReference = storage.getReference("eventos");
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, baos);
        byte[] data = baos.toByteArray();

        //cria uma referência no storage apontando para a key do evento ao qual a imagem pertence
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
                        if (eventoEdit == null ) { //se for um cadastro, cria uma nova url para a imagem
                            urlBanner = uri.toString();
                        } else {
                            //se estiver editando, recupera a url da imagem pela chave e atualiza a url
                            /** Nota:
                             * Caso o evento editado esteja usando o banner dejault, quando
                             * buscar pela key, vai retornar null na busca pelo banner. Então
                             * coloca esse link como
                             * retorno alternativo
                             */
                            String retorno = atualizaImagemEvento(eventoEdit.getKey());
                            if (retorno.equals("") || retorno.length() < 2) { //se for nulo cria uma nova URI para o banner novo setado
                                urlBanner = uri.toString();
                            } else {
                                //se já tem uma url direfente da default, seta no evento
                                urlBanner = retorno;
                            }
                        }
                        //Após criar uma url para a imagem do evento, ou recuperar uma existente, seta os dados da urlBanner no evento
                        setUrlEvent(urlBanner, key);//informa a url e a key do evento
                    }
                });

            }
        });

        //por fim, chama a caixa de diálogo
        if (semFoto) {
            customAlert("Evento sem banner!", "Você poderá incluir na aba 'Meus eventos'.",false);
        } else {
            customAlert("Sucesso!", "Evento salvo com sucesso!", false);
        }

    }
    public String convertMillisToDate(long yourmilliseconds){

        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yy", Locale.US);


        GregorianCalendar calendar = new GregorianCalendar(TimeZone.getTimeZone("US/Central"));
        calendar.setTimeInMillis(yourmilliseconds);

        Log.i(TAG,"12 - GregorianCalendar: "+sdf.format(calendar.getTime()));


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
