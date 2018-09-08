package com.example.suelliton.agita.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
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
import java.util.ArrayList;
import java.util.List;

public class LoginActivity extends AppCompatActivity{
    private UserLoginTask mAuthTask = null;
    private Usuario USUARIO_OBJETO_LOGADO;
    private EditText ed_password;
    private View progressView;
    private TextView linkCadastro;
    private EditText ed_login;
    private FirebaseDatabase database ;
    private DatabaseReference RootReference ;
    private Button btn_login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        database = MyDatabaseUtil.getDatabase();
        RootReference = database.getReference("usuarios");
        findView();
        setViewListeners();
    }

public void findView(){
    ed_login = (EditText) findViewById(R.id.input_user);
    ed_password = (EditText) findViewById(R.id.input_password);
    btn_login = (Button) findViewById(R.id.btn_login);
    linkCadastro = (TextView) findViewById(R.id.link_signup);
    progressView = (ProgressBar) findViewById(R.id.progress_login);

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
        btn_login.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });
        linkCadastro.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this,AddUserActivity.class));
                finish();
            }
        });

    }
    public static void hideKeyboard(Context context, View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }


    private void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }
        ed_login.setError(null);
        ed_password.setError(null);

        String login = ed_login.getText().toString();
        String password = ed_password.getText().toString();

        boolean cancel = false;
        View focusView = null;

        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
//            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = ed_password;
            cancel = true;
        }

        if (!TextUtils.isEmpty(login) && !isUserValid(login)) {
//            mUserView.setError(getString(R.string.error_invalid_user));
            focusView = ed_login;
            cancel = true;
        }
        if (cancel) {
            focusView.requestFocus();
        } else {
            showProgress(true);
            mAuthTask = new UserLoginTask(login,password);
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


    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);
            progressView.setVisibility(show ? View.VISIBLE : View.GONE);
            progressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    progressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            progressView.setVisibility(show ? View.VISIBLE : View.GONE);
        }
    }

    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

        private  String mUser;
        private  String mPassword;

        UserLoginTask(String user, String password) {
            mUser = user;
            mPassword = password;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.
            Query queryUsuario = RootReference.orderByChild("username").equalTo(mUser).limitToFirst(1);

            queryUsuario.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    if (dataSnapshot.exists()) {
                        Usuario usuario = dataSnapshot.getValue(Usuario.class);
                        if(usuario.getPassword().equals(mPassword)){
                            USUARIO_OBJETO_LOGADO = usuario;
//                            LOGADO = dataSnapshot.getKey();
                        }
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

            try {
                // Simulate network access.
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                return false;
            }


            if(USUARIO_OBJETO_LOGADO != null){
                return true;
            }

            return false;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
            showProgress(false);

            if (success) {
                SharedPreferences sharedPreferences = getSharedPreferences("sharedPreferences", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
//                editor.putString("usuarioLogado", USUARIO_OBJETO_LOGADO.getUsername());
                editor.apply();
                //LOGADO = USUARIO_OBJETO_LOGADO.getUsername();
//                startActivity(new Intent(LoginActivity.this,ExperimentoActivity.class));
                finish();
            } else {
//                mPasswordView.setError(getString(R.string.error_incorrect_password));
                ed_password.requestFocus();
                Toast.makeText(LoginActivity.this, "Usuário inválido", Toast.LENGTH_SHORT).show();

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


    }
}
