package com.example.suelliton.agita.activity;


import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.location.Address;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
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
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.suelliton.agita.R;
import com.example.suelliton.agita.model.Evento;
import com.example.suelliton.agita.utils.Util;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static com.example.suelliton.agita.activity.SplashActivity.database;
import static com.example.suelliton.agita.activity.SplashActivity.eventosReference;
import static com.example.suelliton.agita.activity.SplashActivity.locaisReference;
import static com.example.suelliton.agita.activity.SplashActivity.usuarioLogado;

public class AddEventoActivity extends AppCompatActivity {
    private final int REQUEST_GALERIA = 2;
    EditText ed_nome;
    CalendarView cv_data;
    TextView value_ed_hora; //exibe o valorDetalhe da horaDetalhe
    ImageButton bt_ed_hora; //chama o relógio para editar a horaDetalhe
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
    Bitmap bitmapGaleria = null, bannerBACKUP = null;
    private String urlBanner = "", urlBannerBackup = "";
    private List<String> listaLocais;
    private Evento eventoEdit, novoEvento;
    ProgressBar progress;
    private DatabaseReference referenceEventoTemporario;
    boolean semFoto = false;
    //atributo da classe.
    private AlertDialog alerta;
    private final String TAG = "teste";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_evento);
        findViews();
        setViewListeners();
        storage = FirebaseStorage.getInstance();
        Bundle pacote = getIntent().getExtras();

        if (pacote != null) {
            eventoEdit = (Evento) pacote.getSerializable("eventoEdit");
            urlBannerBackup = eventoEdit.getUrlBanner(); //guarda a url
            Log.i(TAG, "1 - Modo edit: "+eventoEdit.toString());
            Log.i(TAG, "1.1: "+eventoEdit.isVerificado());
            Log.i(TAG, "1.2: "+ urlBannerBackup);
            setEventoEdit();
        } //se é cadastro, cria uma nova referência para a tabela de eventos temporários
        referenceEventoTemporario = database.getReference("eventoTemporario");
        Log.i(TAG, "2 - Modo reference edit: "+referenceEventoTemporario.getRef());
        carregaLocais();

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
        //Salva a imagemDetalhe original do evento para verificar, posteriormente, se houve alguma modificação
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
                    Log.i(TAG,"5 - localDetalhe: "+data.getRef().getKey());
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

    private boolean isCampoVazio(String valor) {
        //verifica se o valor passado é nulo ou se contém apenas um 'espaço' digitado
        return (TextUtils.isEmpty(valor) || valor.trim().isEmpty());
    }

    public void setViewListeners(){

        btnSalvarEvento.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progress.setVisibility(View.VISIBLE);
                String nome = ed_nome.getText().toString();
                String hora = value_ed_hora.getText().toString();
                String data = Util.convertMillisToDate(cv_data.getDate());
                final String local = ed_local.getText().toString();
                String estilo = ed_estilo.getText().toString();
                String bandas = ed_bandas.getText().toString();
                double valor = ed_valor.getText().toString() == null? 0 : Double.parseDouble(ed_valor.getText().toString());
                String descricao = ed_descricao.getText().toString();
                String casa = ed_casaShow.getText().toString();
                boolean liberado = ed_liberado.isChecked();//verifica o estado do botão se marcado ou não

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
                } else if (isCampoVazio(local)) {
                    alertField(getString(R.string.aviso_local_validacao));
                    ed_local.requestFocus();
                    return;
                } else if (isCampoVazio(estilo)) {
                    alertField(getString(R.string.aviso_estilo_validacao));
                    ed_estilo.requestFocus();
                    return;
                }else if (valor < 0) {
                    alertField(getString(R.string.aviso_valor_validacao));
                    ed_valor.requestFocus();
                    return;
                }

                List<Address> enderecos = Util.getNomeLocalFromEndereco(AddEventoActivity.this,local);

                if(enderecos.size()== 0) {//verifica se veio algum endereço
                    alertField( getString(R.string.alerta_endereco_invalido));
                    progress.setVisibility(View.GONE);
                    ed_local.requestFocus();
                }else{
                    double lat = enderecos.get(0).getLatitude();
                    double lng = enderecos.get(0).getLongitude();

                    if (eventoEdit == null) {

                        //se for um evento novo e não tiver foto, coloca a foto default antes de salvar o evento no bd
                        if(bitmapGaleria == null){ //Se não tem nada na galeria, coloca a foto default
                            urlBanner = "https://firebasestorage.googleapis.com/v0/b/agita-ed061.appspot.com/o/eventos%2Fevento_sem_banner.png?alt=media&token=a6f53830-48bb-4388-b242-7cc589278e03";
                        }

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

                                        //Se não tem nada na galeria, coloca a foto default
                                        if(bitmapGaleria == null){
                                            customAlert("Evento sem banner!", "Você poderá incluir na aba 'Meus eventos'.");
                                        }else{//Caso contrário, se tem uma foto, faz p upload
                                            try {
                                                uploadFirebaseBytes(bitmapGaleria, dataSnapshot.getRef().getKey());
                                                customAlert("Evento cadastrado!", "Operaçãoa realizada com sucesso!");
                                            } catch (FileNotFoundException e) {
                                                e.printStackTrace();
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
                        });
                    } else {
                        novoEvento = new Evento(nome, data, hora, local, estilo, lat, lng, bandas, valor, descricao, eventoEdit.getUrlBanner(), liberado, casa, false, usuarioLogado.getLogin());
                        novoEvento.setKey(eventoEdit.getKey());
                        novoEvento.setVerificado(eventoEdit.isVerificado());

                        //Salva os dados alterados no bd
                        if (eventoEdit.isVerificado()) {
                            eventosReference.child(eventoEdit.getKey()).setValue(novoEvento);
                        }else {
                            referenceEventoTemporario.child(eventoEdit.getKey()).setValue(novoEvento);
                        }
                        //se não mudou a foto, usa a que já tem, caso contrário, faz o upload
                        if(bitmapGaleria == null) {
                            novoEvento.setUrlBanner(eventoEdit.getUrlBanner());
                        }else{
                            try {
                                uploadFirebaseBytes(bitmapGaleria, eventoEdit.getKey());
                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                            }
                        }

                        //Se não for um evento verificado, muda a referência para a tabela temporária
                        customAlert("Sucesso!", "Evento editado com sucesso!");

                    }
                    //locaisReference.child(localDetalhe).setValue(localDetalhe);
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

    //Chamado quando o banner de um evento é alterado
    //Baixa o banner do evento alterado pela key
    private String atualizaImagemEvento(final String key) {
        final String[] t = {""};

        StorageReference storageReference = FirebaseStorage.getInstance().getReference("eventos");

        //Usa a chave do evento como identificador de seu banner. Com isso, ao atualizar os dados de um evento
        //o mesmo não criará uma nova imagemDetalhe no banco
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
                        if (eventoEdit == null ) { //se for um cadastro, cria uma nova url para o banner após o upload
                            urlBanner = uri.toString();
                        } else {
                            String retorno = atualizaImagemEvento(eventoEdit.getKey());
                            if (retorno.equals("") || retorno.length() < 2) { //se for nulo cria uma nova URI para o banner novo setado
                                urlBanner = uri.toString();
                            } else {
                                //se já tem uma url direfente da default, seta no evento
                                urlBanner = retorno;
                            }
                        }
                        //Após criar uma url para a imagem do evento, ou recuperar uma existente,
                        //seta os dados da urlBanner no evento, atualizando a urlBanner com link do banner no storage
                        if (novoEvento.isVerificado())
                            eventosReference.child(key).child("urlBanner").setValue(urlBanner);

                        else
                            referenceEventoTemporario.child(key).child("urlBanner").setValue(urlBanner);
                    }
                });

            }
        });



    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK && requestCode == REQUEST_GALERIA){
            Uri uriSelectedImage = data.getData();
            CropImage.activity(uriSelectedImage)
                    .start(this);
        }else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                imageView.setImageURI(resultUri);
                bitmapGaleria = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }

    }
}
