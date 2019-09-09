package com.example.fcmchatapplication;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.fcmchatapplication.model.CurrentAddressDecode;
import com.example.fcmchatapplication.model.LocationTrack;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.Api;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.auth.User;


public class LoginActivity extends BaseActivity  implements View.OnClickListener  {
    private EditText editText_Email;
    private EditText editText_Password;
    private Button button_Login;
    private TextView textView_Forgot;
    private TextView textView_Register;
    private ProgressBar progressBar;
    private FirebaseAuth mFirebaseAuth;
    private ImageView imageView_Gmail;
    private ImageView imageView_Fb;
    private ImageView imageView_Linkedin;
    private ImageView imageView_Twitter;
    private static String TAG=LoginActivity.class.getSimpleName();
    private int RC_SIGN_IN=12;
    private GoogleSignInClient mGoogleSignInClient;
    private FirebaseUser currentUser=null;
    private boolean isUserRegister=false;



    @Override
    protected int getContentView() {
        return R.layout.activity_login;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onViewReady(Bundle savedInstanceState, Intent intent) {
        super.onViewReady(savedInstanceState, intent);
        editText_Email=(EditText)findViewById(R.id.edit_email);
        editText_Password=(EditText)findViewById(R.id.edit_pass);
        button_Login=(Button)findViewById(R.id.btn_login);
        textView_Forgot=(TextView)findViewById(R.id.txt_forgot);
        textView_Register=(TextView)findViewById(R.id.txt_register);
        progressBar=(ProgressBar)findViewById(R.id.progbar);
        mFirebaseAuth=FirebaseAuth.getInstance();
        imageView_Gmail=(ImageView)findViewById(R.id.img_gmail);
        imageView_Fb=(ImageView)findViewById(R.id.img_fb);
        imageView_Linkedin=(ImageView)findViewById(R.id.img_linkedin);
        imageView_Twitter=(ImageView)findViewById(R.id.img_twitter);
        button_Login.setOnClickListener(this);
        textView_Forgot.setOnClickListener(this);
        textView_Register.setOnClickListener(this);
        imageView_Gmail.setOnClickListener(this);
        imageView_Fb.setOnClickListener(this);
        imageView_Linkedin.setOnClickListener(this);
        imageView_Twitter.setOnClickListener(this);
        currentUser=mFirebaseAuth.getCurrentUser();

        Intent intent_Register= getIntent();

        if(intent_Register!=null){
            isUserRegister=intent_Register.getBooleanExtra(getResources().getString(R.string.const_is_user_register),false);
        }
            if(currentUser!=null && !isUserRegister){
                Intent intent_Profile=new Intent(LoginActivity.this,UsersProfileActivity.class);
                intent_Profile.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent_Profile);
                finish();
            }




        //sign in with google account
        GoogleSignInOptions gso= new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                  .requestIdToken(this.getResources().getString(R.string.default_web_client_id)).requestEmail().build();
        mGoogleSignInClient= GoogleSignIn.getClient(this,gso);

    }

    
    @Override
    public void onClick(View view) {
         boolean isConnected=isNetworkConnected();
          if(!isConnected){
              showErrorMessage(getResources().getText(R.string.bad_internet).toString(), Color.RED);
          }
          else{
              switch (view.getId()){
                  case R.id.btn_login:
                      doLogin();
                      break;
                  case R.id.txt_register:
                      Intent  intent_Activity= new Intent(LoginActivity.this,RegisterActivity.class);
                      startActivity(intent_Activity);
                      break;
                  case R.id.txt_forgot:
                      forgotPassword();
                      break;
                  case R.id.img_gmail:
                      loginWithGmail();
                      break;
                  case R.id.img_fb:
                      loginWithFb();
                      break;
                  case R.id.img_linkedin:
                      loginWithLinkedin();
                      break;
                  case R.id.img_twitter:
                      loginWithTwitter();
              }

          }
    }
    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        if(!isConnected){
            showErrorMessage(getResources().getText(R.string.bad_internet).toString(), Color.RED);
        }
    }

    private void doLogin() {
        String email=editText_Email.getText().toString();
        String pass=editText_Password.getText().toString();

        if(TextUtils.isEmpty(email)){
            showErrorMessage(getResources().getText(R.string.email).toString(),Color.RED);
            return;
        }
        else if(TextUtils.isEmpty(pass)){
            showErrorMessage(getResources().getText(R.string.pass).toString(),Color.RED);
            return;
        }
        else if(pass.length()<6){
            showErrorMessage(getResources().getText(R.string.pass_short).toString(),Color.RED);
            return;
        }
        else{
            progressBar.setVisibility(View.VISIBLE);
       mFirebaseAuth.signInWithEmailAndPassword(email,pass).addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
           @Override
           public void onComplete(@NonNull Task<AuthResult> task) {
               if(!task.isSuccessful()){
                    showErrorMessage(getResources().getText(R.string.login_Fail).toString()+" "+task ,Color.RED);
                }
                else{
                    if(checkAndRequestPermissions()){
                        Toast.makeText(getApplicationContext(),getResources().getText(R.string.login_success).toString(),Toast.LENGTH_SHORT).show();
                        Intent intent_Users= new Intent(LoginActivity.this,UsersProfileActivity.class);
                        intent_Users.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent_Users);
                        finish();
                    }
                }
               progressBar.setVisibility(View.GONE);

           }
       });

        }
    }

    private void forgotPassword() {
        final AlertDialog.Builder builder= new AlertDialog.Builder(this);
        LayoutInflater inflater=getLayoutInflater();
        final  View dialogView= inflater.inflate(R.layout.layout_reset_password,null);
         builder.setView(dialogView);
         final EditText editText_Email=(EditText)dialogView.findViewById(R.id.reset_edit_email);
         final Button button_Reset=(Button)dialogView.findViewById(R.id.reset_btn);
         final ProgressBar reset_ProgressBar=(ProgressBar)dialogView.findViewById(R.id.reset_progressBar);
         final TextView reset_TextView=(TextView)dialogView.findViewById(R.id.reset_cancel);
         final AlertDialog dialog = builder.create();
          button_Reset.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View view) {
                  String email=editText_Email.getText().toString();
                  if(TextUtils.isEmpty(email)){
                      showErrorMessage(getResources().getText(R.string.email).toString(),Color.RED);
                      return;
                  }
                  reset_ProgressBar.setVisibility(View.VISIBLE);
                  mFirebaseAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                      @Override
                      public void onComplete(@NonNull Task<Void> task) {
                          if(!task.isSuccessful()){
                              showErrorMessage(getResources().getText(R.string.reset_failed).toString()+task.getException(),Color.RED);
                              Log.e(TAG,"Failed to send password "+task.getException());
                          }
                          else{
                              Toast.makeText(getApplicationContext(),getResources().getText(R.string.reset_success).toString(),Toast.LENGTH_SHORT).show();
                          }
                          reset_ProgressBar.setVisibility(View.GONE);
                          dialog.dismiss();

                      }
                  });
              }
          });
        dialog.show();

        reset_TextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.cancel();
            }
        });

    }

    //hide all the menu items
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        menu.removeItem(R.id.menu_sign_out);
        menu.removeItem(R.id.menu_setting);
        return true;
    }

    private void loginWithLinkedin() {
        Toast.makeText(getApplicationContext(),"Linkedin is not implemented",Toast.LENGTH_SHORT).show();
    }

    private void loginWithFb() {
        Toast.makeText(getApplicationContext(),"Facebook is not implemented",Toast.LENGTH_SHORT).show();
    }


    private void loginWithGmail() {
        if(!isNetworkConnected()){
            showErrorMessage(getResources().getText(R.string.bad_internet).toString(), Color.RED);
        }
        else{
            Intent signInIntent = mGoogleSignInClient.getSignInIntent();
            startActivityForResult(signInIntent, RC_SIGN_IN);
        }
    }
    private void loginWithTwitter(){
        Toast.makeText(getApplicationContext(),"Twitter is not implemented",Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //if the requestCode is the Google Sign In code that we defined at starting
        if(requestCode==RC_SIGN_IN){
            Task<GoogleSignInAccount> task= GoogleSignIn.getSignedInAccountFromIntent(data);
             try{
                 GoogleSignInAccount account= task.getResult(ApiException.class);
                 firebaseAuthWithGoogle(account);
             } catch (ApiException e){
                 showErrorMessage("Unable to handle sign in exception "+e.getMessage() ,Color.RED);
             }

        }
    }


    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
        Log.d(TAG,"firebaseAuthWithGoogle:" + account.getId());
        AuthCredential credential= GoogleAuthProvider.getCredential(account.getIdToken(),null);
        progressBar.setVisibility(View.VISIBLE);
        mFirebaseAuth.signInWithCredential(credential).addOnCompleteListener(this,
                new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(!task.isSuccessful()){
                            showErrorMessage( "signInWithCredential:failure"+ task.getException(),Color.RED);
                        }
                        else{
                            if(checkAndRequestPermissions()){
                                Toast.makeText(getApplicationContext(),getResources().getText(R.string.login_success).toString(),Toast.LENGTH_SHORT).show();
                                Intent intent_Users= new Intent(LoginActivity.this,UsersProfileActivity.class);
                                intent_Users.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent_Users);
                                finish();
                            }
                        }
                        progressBar.setVisibility(View.GONE);
                    }

                });
    }





}
