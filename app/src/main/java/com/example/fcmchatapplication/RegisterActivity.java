package com.example.fcmchatapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class RegisterActivity extends BaseActivity implements View.OnClickListener {
    private EditText editText_Email;
    private EditText editText_Password;
    private Button button_Register;
    private ProgressBar progressBar;
    private TextView textLogin;
     private FirebaseAuth mFirebaseAuth;



    @Override
    protected int getContentView() {
        return R.layout.activity_register;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onViewReady(Bundle savedInstanceState, Intent intent) {
        super.onViewReady(savedInstanceState, intent);
        showBackArrow();
        editText_Email=(EditText)findViewById(R.id.edit_email);
        editText_Password=(EditText)findViewById(R.id.edit_pass);
        button_Register=(Button) findViewById(R.id.btn_reg);
        textLogin=(TextView)findViewById(R.id.txt_login);
        progressBar=(ProgressBar)findViewById(R.id.progbar);
        button_Register.setOnClickListener(this);
        textLogin.setOnClickListener(this);

        //instance of firebase auth
        mFirebaseAuth=FirebaseAuth.getInstance();

    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        if(!isConnected){
            showErrorMessage(getResources().getText(R.string.bad_internet).toString(), Color.RED);
        }

    }

    @Override
    public void onClick(View view) {
        boolean isConnected=isNetworkConnected();
        if(!isConnected){
            showErrorMessage(getResources().getText(R.string.bad_internet).toString(), Color.RED);
        }
        else {
             switch (view.getId()){
                 case R.id.btn_reg:
                     doRegistration();
                     break;
                 case R.id.txt_login:
                     Intent intent_Login= new Intent(RegisterActivity.this,LoginActivity.class);
                     intent_Login.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                     startActivity(intent_Login);
                     break;


             }


        }


    }



    private void doRegistration() {
         String email=editText_Email.getText().toString();
         String pass=editText_Password.getText().toString();

         if(TextUtils.isEmpty(email)){
             showErrorMessage(getResources().getText(R.string.email).toString(),Color.RED);
               return;
         }
         else if(TextUtils.isEmpty(pass)){
             showErrorMessage(getResources().getText(R.string.pass).toString(), Color.RED);
              return;
         }
         else if(pass.length()<6){
             showErrorMessage(getResources().getText(R.string.pass_short).toString(),Color.RED);
              return;
         }
          progressBar.setVisibility(View.VISIBLE);

        mFirebaseAuth.createUserWithEmailAndPassword(email, pass)
                .addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(!task.isSuccessful()){
                            showErrorMessage(getResources().getText(R.string.auth_failed).toString() +task.getException(),Color.RED);
                        }
                        else {
                            Toast.makeText(getApplicationContext(),getResources().getText(R.string.reg_sucess).toString(),Toast.LENGTH_SHORT).show();
                            Intent intent_Login= new Intent(RegisterActivity.this,LoginActivity.class);
                            intent_Login.putExtra(getResources().getString(R.string.const_is_user_register),true);
                            intent_Login.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent_Login);
                            finish();
                        }
                        progressBar.setVisibility(View.GONE);
                    }
                });

    }

    @Override
    protected void onResume() {
        super.onResume();
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        menu.removeItem(R.id.menu_sign_out);
        menu.removeItem(R.id.menu_setting);
        return true;
    }


}
