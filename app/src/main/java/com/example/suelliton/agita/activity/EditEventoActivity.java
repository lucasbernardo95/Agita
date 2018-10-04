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
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.example.suelliton.agita.activity.SplashActivity.database;
import static com.example.suelliton.agita.activity.SplashActivity.eventosReference;
import static com.example.suelliton.agita.activity.SplashActivity.locaisReference;
import static com.example.suelliton.agita.activity.SplashActivity.usuarioLogado;

public class EditEventoActivity extends AppCompatActivity {

    private final int REQUEST_GALERIA = 2;
    EditText ed_nome;
    CalendarView cv_data;
    TextView value_ed_hora; //exibe o valorDetalhe da horaDetalhe
    ImageButton bt_ed_hora; //chama o relógio para editar a horaDetalhe
    AutoCompleteTextView ed_local;
    AutoCompleteTextView ed_estilo;
    private EditText ed_bandas;
    private EditText ed_valor;
    private EditText ed_descricao;
    private EditText ed_casaShow;
    private CheckedTextView ed_liberado;
    Button btnSalvarEvento;
    ImageView imageView;
    FirebaseStorage storage;
    StorageReference storageReference;
    Bitmap bitmapGaleria = null;

    private Evento eventoEdit;
    ProgressBar progress;
    private DatabaseReference referenceEventoTemporario;
    private final String TAG = "teste";
    //Lista de estilos músicais para o cadastro e ediçã ode eventos
    public static final String[] listaEstilos = new String[] {
            "Rock", "Pop", "Eletrônica", "Forró", "Sertanejo", "Brega", "Swingueira", "Reggae",
            "Gospel", "Funk", "MPB", "Clássico", "Hip Hop/Rap", "Samba", "Dance", "Axé",
            "Funk carioca", "Heavy Metal", "Instrumental", "Jazz", "Pagode", "Reggaeton",
            "Progressivo", "Country", "Outros"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_evento);

        findViews();
        //implementa o click dos botões da tela
        setViewListeners();

        storage = FirebaseStorage.getInstance();
        Bundle pacote = getIntent().getExtras();

        if (pacote != null) {
            eventoEdit = (Evento) pacote.getSerializable("eventoEdit");
            referenceEventoTemporario = database.getReference("eventoTemporario");
            setEventoEdit();//seta os campos do evento no xml
        }

    }
    //Findviews dos atributos (campos) do xml
    public void findViews(){
        progress = (ProgressBar) findViewById(R.id.progress);
        ed_nome = (EditText) findViewById(R.id.nome_cadastro);
        cv_data = (CalendarView) findViewById(R.id.cv_dataCadastro);
        value_ed_hora = (TextView) findViewById(R.id.value_hora_cadastro);
        bt_ed_hora = (ImageButton) findViewById(R.id.hora_cadastro);
        ed_local = (AutoCompleteTextView) findViewById(R.id.local_cadastro);

        //Seta a lista de estilos num adapter
        ed_estilo = (AutoCompleteTextView) findViewById(R.id.estilo_cadastro);
        ArrayAdapter<String> adaptadorEstilos = new
                ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item, listaEstilos);
        ed_estilo.setAdapter(adaptadorEstilos);

        ed_bandas = (EditText) findViewById(R.id.bandas_cadastro);
        ed_valor = (EditText) findViewById(R.id.valor_cadastro);
        ed_descricao = (EditText) findViewById(R.id.descricao_cadastro);
        ed_casaShow = (EditText) findViewById(R.id.casa_show_cadastro);
        ed_liberado = (CheckedTextView) findViewById(R.id.liberado_cadastro);
        ed_liberado.setChecked(true); //inicia como true
        imageView = (ImageView) findViewById(R.id.imagem_galeria);
        btnSalvarEvento = (Button) findViewById(R.id.salvar_evento);
    }

    //Seta os valores do evento a ser editado nos edittext's
    private void setEventoEdit(){
        ed_nome.setText(eventoEdit.getNome());

        try {
            Picasso.get().load(eventoEdit.getUrlBanner()).into(imageView);
        } catch (RuntimeException e){
            e.printStackTrace();
        }

        value_ed_hora.setText(eventoEdit.getHora());
        ed_local.setText(eventoEdit.getLocal());
        ed_estilo.setText(eventoEdit.getEstilo());
        ed_bandas.setText(eventoEdit.getBandas());
        ed_valor.setText(String.valueOf(eventoEdit.getValor()));
        ed_descricao.setText(eventoEdit.getDescricao());
        ed_casaShow.setText(eventoEdit.getCasashow());
        ed_liberado.setChecked(eventoEdit.isLiberado());
        btnSalvarEvento.setText(R.string.botaoEditarEvento);

        bt_ed_hora.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setTimeEvent();
            }
        });

    }

    //Método para chamar o Time Picker Dialog e setar a horaDetalhe no evento
    public  void setTimeEvent() {

        Calendar calendario = Calendar.getInstance();
        int hora = calendario.get(Calendar.HOUR);
        int minuto = calendario.get(Calendar.MINUTE);

        TimePickerDialog timePicker;

        timePicker = new TimePickerDialog(EditEventoActivity.this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int horaDoDia, int minutos) {
                value_ed_hora.setText(horaDoDia + ":"+minutos); //Seta a horaDetalhe no EditText
            }
        }, hora, minuto, true);

        timePicker.show();
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

                List<Address> enderecos = Util.getNomeLocalFromEndereco(EditEventoActivity.this,local);

                if(enderecos.size() == 0) {//verifica se veio algum endereço
                    alertField( getString(R.string.alerta_endereco_invalido));
                    progress.setVisibility(View.GONE);
                    ed_local.requestFocus();
                    return;
                }

                double lat = enderecos.get(0).getLatitude();
                double lng = enderecos.get(0).getLongitude();

                atualizarCamposEvento(nome, data, hora, local, estilo.toLowerCase(), lat, lng, bandas, valor, descricao, eventoEdit.getUrlBanner(), liberado, casa, false, usuarioLogado.getLogin());

                //Se tiver algo na galeria, atualiza a imagem e url no banco
                if(bitmapGaleria != null) {
                    try {
                        uploadFirebaseBytes(bitmapGaleria, eventoEdit.getKey());
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                } else {//se não, atualiza apenas os campos alterados
                    atualizaEvento();
                }
                //Se não for um evento verificado, muda a referência para a tabela temporária
                customAlert("Sucesso!", "Evento editado com sucesso!");
            }

        });

        //Click da imagem para abrir a galeria de fotos para a escolha do banner
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent,REQUEST_GALERIA);
            }
        });

        //Altera o estado do evento, se é liberado ou não
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

    //Atualiza os atributos do evento como se fosse um construtor
    private void atualizarCamposEvento(String nome, String data, String hora, String local, String estilo,
                                      double lat, double lng, String bandas, double valor, String descricao,
                                      String url, boolean liberado, String casa, boolean cover, String dono){
        eventoEdit.setNome(nome);
        eventoEdit.setData(data);
        eventoEdit.setHora(hora);
        eventoEdit.setLocal(local);
        eventoEdit.setEstilo(estilo);
        eventoEdit.setLatitude(lat);
        eventoEdit.setLongitude(lng);
        eventoEdit.setBandas(bandas);
        eventoEdit.setValor(valor);
        eventoEdit.setDescricao(descricao);
        eventoEdit.setUrlBanner(url);
        eventoEdit.setLiberado(liberado);
        eventoEdit.setCasashow(casa);
        eventoEdit.setCover(cover);
        eventoEdit.setDono(dono);
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

                StorageReference islandRef = storageReference.child(key);
                islandRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        //Atualiza a url do banner
                        eventoEdit.setUrlBanner(uri.toString());
                        atualizaEvento();
                    }
                });

            }
            //Monitora o progresso do upload
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                double progressoUpload = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                Log.i(TAG, "Upload is " + progressoUpload + "% done");
            }
        });

    }

    private void atualizaEvento() {
        Map<String, Object> update = new HashMap<>();
        update.put(eventoEdit.getKey(), eventoEdit);
        //Se o evento estiver em estado verificado, salva na tabela de eventos 'públicos', caso contrário, fica em eventos temporários
        if (eventoEdit.isVerificado()) {
            eventosReference.updateChildren(update);
        } else {
            referenceEventoTemporario.updateChildren(update);
        }
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
                try {
                    bitmapGaleria = Util.createParcelDescriptor(this,resultUri,500);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }

    }
}
