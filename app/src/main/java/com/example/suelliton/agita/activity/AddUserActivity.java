package com.example.suelliton.agita.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.example.suelliton.agita.R;
import com.example.suelliton.agita.model.Usuario;
import com.example.suelliton.agita.utils.MyDatabaseUtil;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;

import static com.example.suelliton.agita.activity.SplashActivity.LOGADO;
import static com.example.suelliton.agita.activity.SplashActivity.usuarioReference;

public class AddUserActivity extends AppCompatActivity{
    private UserLoginTask mAuthTask = null;
    // UI references.
    private EditText ac_nome;
    private EditText ac_email;
    private EditText ed_contato;
    private EditText ac_cpfcnpj;
    private EditText ed_login;
    private EditText ed_password;
    private Button salvarUsuario;
    private View progressView;
    private View cadastroFormView;

    private ValueEventListener listener;
    boolean passUser = false;
    boolean passEmail = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_user);

        findViews();
        setViewListeners();
    }

    public void findViews(){
        ac_nome = (EditText) findViewById(R.id.nome_cadastro);
        ac_email = (EditText) findViewById(R.id.email_cadastro);
        ed_contato = (EditText) findViewById(R.id.contato_cadastro);
        ac_cpfcnpj = (EditText) findViewById(R.id.cpfcnpj_cadastro);
        ed_login = (EditText) findViewById(R.id.login_cadastro);
        ed_password = (EditText) findViewById(R.id.password_cadastro);

        salvarUsuario = (Button) findViewById(R.id.salvar_usuario);
        cadastroFormView = findViewById(R.id.form_cadastro);
        progressView = findViewById(R.id.progress_login);
    }
    public void setViewListeners(){
        ed_login.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        ed_password.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        salvarUsuario.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

    }


    private void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }
        // Reset errors.
        ac_nome.setError(null);
        ac_email.setError(null);
        ed_contato.setError(null);
        ac_cpfcnpj.setError(null);
        ed_login.setError(null);
        ed_password.setError(null);
        // Store values at the time of the login attempt.
        String nome = ac_nome.getText().toString();
        String email = ac_email.getText().toString();
        String contato = ed_contato.getText().toString();
        String cpfcnpj = ac_cpfcnpj.getText().toString();
        String login = ed_login.getText().toString();
        String password = ed_password.getText().toString();

        boolean cancel = false;
        View focusView = null;
        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            ed_password.setError(getString(R.string.error_invalid_password));
            focusView = ed_password;
            cancel = true;
        }

        // Check for a valid user, if the user entered one.
        if (!TextUtils.isEmpty(login) && !isUserValid(login)) {
            ed_login.setError(getString(R.string.error_invalid_user));
            focusView = ed_login;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            ac_email.setError(getString(R.string.error_field_required));
            focusView = ac_email;
            cancel = true;
        } else if (!isEmailValid(email)) {
            ac_email.setError(getString(R.string.error_invalid_email));
            focusView = ac_email;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);
            mAuthTask = new UserLoginTask(nome,email,contato,cpfcnpj,login, password);
            mAuthTask.execute((Void) null);
        }
    }

    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 4;
    }
    private boolean isUserValid(String user) {
        //TODO: Replace this with your own logic
        return user.length() > 4;
    }


    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

        private final String nome;
        private final String email;
        private final String contato;
        private final String cpfcnpj;
        private final String login;
        private final String password;

        UserLoginTask(String nome,String email,String contato, String cpfcnpj, String login,String password) {
            this.nome = nome;
            this.email = email;
            this.contato = contato;
            this.cpfcnpj = cpfcnpj;
            this.login = login;
            this.password = password;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.
            final List<Usuario> listaUsuarios = new ArrayList<>();
            //busca usuários
            listener = usuarioReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    listaUsuarios.removeAll(listaUsuarios);
                    if (dataSnapshot.exists()) {//verifica se a snapshot existe
                        for (DataSnapshot u: dataSnapshot.getChildren()) {//itera sobre todos usuários
                            Usuario usuario = u.getValue(Usuario.class);////instancia um novo usuario com a snapshot atual
                            listaUsuarios.add(usuario);//adiciona na lista local o usuário vindo do firebase
                        }
                    }
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
            //tempo necessário para dar tempo do listener carregar os dados
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                return false;
            }
            //iterar sobre os usuários procurando se ja existe o login e o email cadastrados
            for (Usuario usuario: listaUsuarios ) {
                if(usuario.getLogin().equals(login)){
                    passUser = false;//variaveis de controle para validar o cadastro
                    return false;
                }else{
                    if(usuario.getEmail().equals(email)){
                        passEmail = false;
                        return false;
                    }else{
                        passUser = true;//variaveis de controle para validar o cadastro
                        passEmail = true;
                    }
                }
            }
            //se nao tiver nenhum usuario cadastrado com login e email, ele cadastra o atual
            if(listaUsuarios.size() == 0){
                passUser = true;//diz que pode cadastrar
                passEmail = true;
            }

            if(passEmail && passUser) {
                Usuario novoUsuario = new Usuario(nome,contato,cpfcnpj,login,password,false,email);
                usuarioReference.child(novoUsuario.getLogin()).setValue(novoUsuario);
                //coloca o usuario no shared preferences
                SharedPreferences sharedPreferences = getSharedPreferences("sharedPreferences", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("usuarioLogado", novoUsuario.getLogin());
                editor.apply();
                LOGADO = novoUsuario.getLogin();
                return true;//salva usuario
            }else{
                return  false;//erro pra salvar
            }
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
            showProgress(false);
            if (success) {
                Toast.makeText(AddUserActivity.this, "Usuário  cadastrado com sucesso!", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(AddUserActivity.this,EventoActivity.class));
                finish();
            } else {
                    if(!passUser){
                        ed_login.requestFocus();
                        Toast.makeText(AddUserActivity.this, "Username já existe tente outro", Toast.LENGTH_SHORT).show();
                    }
                    if(!passEmail){
                        ac_email.requestFocus();
                        Toast.makeText(AddUserActivity.this, "Já existe um usuário para este email ", Toast.LENGTH_SHORT).show();

                    }
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(listener != null){
            usuarioReference.removeEventListener(listener);
        }
    }
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            cadastroFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            cadastroFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    cadastroFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            progressView.setVisibility(show ? View.VISIBLE : View.GONE);
            progressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    progressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            progressView.setVisibility(show ? View.VISIBLE : View.GONE);
            cadastroFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

}

