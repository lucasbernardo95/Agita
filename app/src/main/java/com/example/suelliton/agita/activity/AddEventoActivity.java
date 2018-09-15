package com.example.suelliton.agita.activity;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.suelliton.agita.R;
import com.example.suelliton.agita.model.Evento;
import com.example.suelliton.agita.utils.MyDatabaseUtil;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import static com.example.suelliton.agita.activity.EventoActivity.eventosReference;
import static com.example.suelliton.agita.activity.SplashActivity.LOGADO;

public class AddEventoActivity extends AppCompatActivity {
    private final int REQUEST_GALERIA = 2;
    EditText ed_nome;
    EditText ed_data;
    EditText ed_hora;
    EditText ed_local;
    EditText ed_estilo;
    EditText ed_bandas;
    EditText ed_valor;
    EditText ed_descricao;
    EditText ed_casaShow;
    CheckedTextView ed_liberado;
    Button btnSalvarEvento;
    View view;
    ImageView imageView;
    FirebaseStorage storage;
    Bitmap bannerGaleria;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_evento);
        storage = FirebaseStorage.getInstance();
        findViews();
        setViewListeners();

    }


    public void findViews(){
        ed_nome = (EditText) findViewById(R.id.nome_cadastro);
        ed_data = (EditText) findViewById(R.id.data_cadastro);
        ed_hora = (EditText) findViewById(R.id.hora_cadastro);
        ed_local = (EditText) findViewById(R.id.local_cadastro);
        ed_estilo = (EditText) findViewById(R.id.estilo_cadastro);
        ed_bandas = (EditText) findViewById(R.id.bandas_cadastro);
        ed_valor = (EditText) findViewById(R.id.valor_cadastro);
        ed_descricao = (EditText) findViewById(R.id.descricao_cadastro);
        ed_casaShow = (EditText) findViewById(R.id.casa_show_cadastro);
        ed_liberado = (CheckedTextView) findViewById(R.id.liberado_cadastro);
        ed_liberado.setChecked(true); //inicia como true
        imageView = (ImageView) findViewById(R.id.imagem_galeria);

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

        btnSalvarEvento = (Button) findViewById(R.id.salvar_evento);
    }
    public void setViewListeners(){
        btnSalvarEvento.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nome = ed_nome.getText().toString();
                String hora = ed_hora.getText().toString();
                String data = ed_data.getText().toString();
                String local = ed_local.getText().toString();
                String estilo = ed_estilo.getText().toString();
                String bandas = ed_bandas.getText().toString();
                double valor = Double.parseDouble(ed_valor.getText().toString());
                String descricao = ed_descricao.getText().toString();
                String casa = ed_casaShow.getText().toString();
                boolean liberado = ed_liberado.isChecked();//verifica o estado do botão se marcado ou não
                //String local, String estilo, Integer latitude, Integer longitude, String bandas, double valor, String descricao, String urlBanner, boolean liberado, String casashow, boolean cover, String dono)
                Evento evento = new Evento(nome,data,hora,local,estilo,1,1,bandas,valor,descricao,"UrlBanner",liberado,casa, false,LOGADO);
                eventosReference.push().setValue(evento);
                try {
                    uploadFirebaseBytes(bannerGaleria,nome);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                Toast.makeText(AddEventoActivity.this, "Evento salvo com sucesso!", Toast.LENGTH_SHORT).show();
                limpaCampos();
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
    }
    public void limpaCampos(){
        ed_nome.setText("");
        ed_data.setText("");
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
    public void uploadFirebaseBytes(Bitmap bitmap, String nomeEvento) throws FileNotFoundException {
        StorageReference storageReference = storage.getReference("eventos/"+nomeEvento);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        UploadTask uploadTask = storageReference.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                //Uri downloadUrl = taskSnapshot.getDownloadUrl();
            }
        });
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
