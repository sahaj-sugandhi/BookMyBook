package com.example.bookmybook;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.example.bookmybook.data.LocalStorageSharedPreferences;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;

public class LoginActivity extends AppCompatActivity {

    FirebaseAuth firebaseAuth;
    EditText emailField, passwordField;
    String email, password;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        String userID= LocalStorageSharedPreferences.getLoggedUser(LoginActivity.this);
        if(userID!=null){
            //Log.i("MyLogs",userID);
            Intent intent=new Intent(LoginActivity.this,MainActivity.class);
            startActivity(intent);
            finish();
        }
        else {
            emailField=findViewById(R.id.emailLoginField);
            passwordField=findViewById(R.id.passwordLoginField);
        }
    }

    public void clickedLogin(final View view) {
        email=emailField.getText().toString();
        password=passwordField.getText().toString();
        if (TextUtils.isEmpty(email)) {
            emailField.setError("Enter Email Address");
            emailField.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(password)) {
            passwordField.setError("Enter Password");
            passwordField.requestFocus();
            return;
        }
        if(password.length()<6){
            passwordField.setError("Enter Valid Password");
            passwordField.requestFocus();
            return;
        }
        firebaseAuth=FirebaseAuth.getInstance();
        firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(!task.isSuccessful()){
                    try
                    {
                        throw task.getException();
                    }
                    // if user enters wrong email.
                    catch (FirebaseAuthInvalidUserException invalidEmail)
                    {
                        emailField.setError("Email Address is invalid");
                        emailField.requestFocus();
                    }
                    // if user enters wrong password.
                    catch (FirebaseAuthInvalidCredentialsException wrongPassword)
                    {
                        passwordField.setError("Password is incorrect");
                        passwordField.requestFocus();
                    }
                    catch (Exception e)
                    {
                        Log.i("MyLogs",e.getMessage());
                    }
                }
                else{
                    LocalStorageSharedPreferences.addUserToSharedPreferences(LoginActivity.this,firebaseAuth.getCurrentUser().getUid());
                    Snackbar.make(view, "Logging you in", Snackbar.LENGTH_LONG).show();
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        });
    }

    public void clickedSignupHere(View view) {
        Intent intent=new Intent(this,SignupActivity.class);
        startActivity(intent);
        finish();
    }

}
