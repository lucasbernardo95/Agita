package com.example.suelliton.agita.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.suelliton.agita.R;
import com.example.suelliton.agita.model.Usuario;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;

import java.util.HashMap;
import java.util.Map;

import static com.example.suelliton.agita.activity.SplashActivity.usuarioReference;

public class RecuperaActivity extends AppCompatActivity {

    private EditText campoEmail, campoCpf, campoLogin, campoSenha;
    private Button botaoRecuperar, botaoConfirmar;
    private Usuario usuarioRecuperado;
    private String keyUsuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recupera);

        findViews();
    }

    private void findViews() {
        campoEmail = (EditText)  findViewById(R.id.inputEmail);
        campoCpf = (EditText)  findViewById(R.id.inputCPF);
        campoLogin = (EditText)  findViewById(R.id.inputLoginRecupera);
        campoSenha = (EditText)  findViewById(R.id.inputSenhaRecupera);

        botaoRecuperar = (Button) findViewById(R.id.btrecuperaUsuario);
        botaoConfirmar = (Button) findViewById(R.id.btConfirmaAlteracoes);

        botaoRecuperar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                recuperaUsuario();
            }
        });

        botaoConfirmar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                confirmaAlteracao();
            }
        });
    }

    private void recuperaUsuario() {
        final String email = campoEmail.getText().toString();
        final String cpf = campoCpf.getText().toString();

        //se o valor não for válido, coloca o foco nesse campo
        if (!isCampoVazio(cpf) /*&& cpf.length() != 11*/){
            campoCpf.requestFocus();
            createToast("CPF inválido!");
            return;
        }else if (validaEmail(email)){
            campoEmail.requestFocus();
            createToast("E-mail inválido!");
            return;
        }

        //Se estiver tudo ok com os campos, verifica se existe algum usuário com o email e spf cadastrado
        Query queryUsuario = usuarioReference.orderByChild("email").equalTo(email).limitToFirst(1);

        queryUsuario.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot data, @Nullable String s) {
                if (data.exists()) {
                    Usuario usuario = data.getValue(Usuario.class);
                    if(usuario.getCpf_cnpj().equals(cpf)){
                        usuarioRecuperado = usuario;
                        keyUsuario = data.getKey();//salva a chave
                    }
                    /*
                     * Se encontrou um usuário com o cpf e email passado, exibe os dados de login e senha para editar
                     */
                    prepararCamposConfirmar();
                } else {
                    createToast("Não há nenhuma conta com o CPF e E-mail informados!");
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

    private void prepararCamposConfirmar(){

        new AlertDialog.Builder(this)
                .setTitle("Olá " + usuarioRecuperado.getNome() + "!")
                .setMessage("Para prosseguir com a recuperação da conta, informe o novo usuário e senha, depois clique em confirmar.")
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                })
                .show();

        campoEmail.setText(R.string.texto_atualizaLogin_recuperado); //seta o login no campo email
        campoCpf.setText(R.string.texto_atualizaSenha_recuperada);

        //Esconde o botão salvar e mostra o botão confirmar para salvar as alterações
        botaoRecuperar.setVisibility(View.GONE);
        botaoConfirmar.setVisibility(View.VISIBLE);
        //mostra os campos de login e senha e esconde o de email e cpf
        campoLogin.setVisibility(View.VISIBLE);
        campoSenha.setVisibility(View.VISIBLE);

        campoEmail.setVisibility(View.GONE);
        campoCpf.setVisibility(View.GONE);
        //esconde a caixa do layout
        findViewById(R.id.textLayoutCPF).setVisibility(View.GONE);
        findViewById(R.id.textLayoutEMAIL).setVisibility(View.GONE);
    }

    private void confirmaAlteracao(){
        final String login = campoLogin.getText().toString();
        final String senha = campoSenha.getText().toString();

        if (!isCampoVazio(login)){
            campoCpf.requestFocus();
            createToast("Login inválido!");
            return;
        }else if (!isCampoVazio(senha) || !(senha.length() >= 6 && senha.length() <= 20) ){
            campoEmail.requestFocus();
            createToast("A senha deve conter entre 6 e 20 caracteres!");
            return;
        }

        //se tiver tudo ok, seta o novo login e senha e salva as modificações
        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("login", login);
        childUpdates.put("password", senha);

        usuarioReference.child(keyUsuario).updateChildren(childUpdates);
        //volta para a tela de login

        new AlertDialog.Builder(this)
                .setTitle("Sucesso!")
                .setMessage("Alterações salvas com sucesso!")
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        onBackPressed();
                        finish();
                    }
                })
                .show();
    }

    private void createToast(String mensagem) {
        Toast.makeText(this, mensagem, Toast.LENGTH_LONG).show();
    }

    private boolean isCampoVazio(String value){
        return !(TextUtils.isEmpty(value) || value.trim().isEmpty());
    }

    private boolean validaEmail(String value){
         return (!isCampoVazio(value) && Patterns.EMAIL_ADDRESS.matcher(value).matches());
    }

}
