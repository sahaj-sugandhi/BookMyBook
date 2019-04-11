package com.example.bookmybook;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.example.bookmybook.data.LocalStorageSharedPreferences;
import com.example.bookmybook.data.UserInfo;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SignupActivity extends AppCompatActivity {

    private static String branchesToList[]={"Civil","Computer Science","Electronics","Information Technology","Mechanical"};

    String email, name, password, confirmPassword, college, branch;

    EditText emailField, nameField, passwordField, confirmPasswordField, collegeField;
    Spinner branchDropDown;

    FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;

    UserInfo userInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        emailField=findViewById(R.id.emailSignupField);
        nameField=findViewById(R.id.nameSignupField);
        passwordField=findViewById(R.id.passwordSignupField);
        confirmPasswordField=findViewById(R.id.confirmPasswordSignupField);
        collegeField=findViewById(R.id.collegeSignupField);
        branchDropDown=findViewById(R.id.branchSignupField);
        ArrayAdapter<String> brancherAdapter=new ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item,branchesToList);
        branchDropDown.setAdapter(brancherAdapter);
    }

    public void clickedSignup(final View view) {
        email=emailField.getText().toString();
        name=nameField.getText().toString();
        password=passwordField.getText().toString();
        confirmPassword=confirmPasswordField.getText().toString();
        college=collegeField.getText().toString();
        branch=branchDropDown.getSelectedItem().toString();

        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\\\.+[a-z]+";
        String regEx =
                "^(([w-]+.)+[w-]+|([a-zA-Z]{1}|[w-]{2,}))@"
                        +"((([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9]).([0-1]?"
                        +"[0-9]{1,2}|25[0-5]|2[0-4][0-9])."
                        +"([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9]).([0-1]?"
                        +"[0-9]{1,2}|25[0-5]|2[0-4][0-9])){1}|"
                        +"([a-zA-Z]+[w-]+.)+[a-zA-Z]{2,4})$";
        Pattern m_pattern = Pattern.compile("([\\w\\-]([\\.\\w])+[\\w]+@([\\w\\-]+\\.)+[A-Za-z]{2,4})");
        Matcher m_matcher = m_pattern.matcher(email);
        Matcher matcherObj = Pattern.compile(regEx).matcher(email);
        if(email.isEmpty() || email.matches(emailPattern) || matcherObj.matches() || !m_matcher.matches()) {
            emailField.setError("Enter a valid Email");
            emailField.requestFocus();
            return;
        }
        if(name.isEmpty()) {
            nameField.setError("Enter your name");
            nameField.requestFocus();
            return;
        }
        if(college.isEmpty()) {
            collegeField.setError("Enter your College Name");
            collegeField.requestFocus();
            return;
        }
        if(!password.equals(confirmPassword)) {
            confirmPasswordField.setError("Passwords did not match");
            confirmPasswordField.requestFocus();
            return;
        }
        userInfo=new UserInfo(name,email,college,branch);
        //Log.i("MyLogs",userInfo.email+" "+userInfo.name+" "+password+" "+confirmPassword+" "+userInfo.college+" "+userInfo.branch);
        firebaseAuth=FirebaseAuth.getInstance();
        firebaseAuth.createUserWithEmailAndPassword(userInfo.email,password).addOnCompleteListener(SignupActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                Snackbar.make(view, "Registration Completed! Logging you in", Snackbar.LENGTH_LONG).show();
                if (task.isSuccessful()) {
                    String userID=firebaseAuth.getCurrentUser().getUid();
                    Log.i("MyLogs",userID);
                    databaseReference= FirebaseDatabase.getInstance().getReference();
                    databaseReference.child("users").child(userID).setValue(userInfo);
                    LocalStorageSharedPreferences.addUserToSharedPreferences(SignupActivity.this,userID);
                    Intent intent=new Intent(SignupActivity.this,LoginActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    try {
                        throw task.getException();
                    }
                    // if user enters wrong email.
                    catch (FirebaseAuthWeakPasswordException weakPassword) {
                        emailField.setError(weakPassword.getMessage());
                        emailField.requestFocus();
                        return;
                    }
                    // if user enters wrong password.
                    catch (FirebaseAuthInvalidCredentialsException malformedEmail) {
                        emailField.setError("Malformed Email");
                        emailField.requestFocus();
                        return;
                    } catch (FirebaseAuthUserCollisionException existEmail) {
                        emailField.setError("Email already exists");
                        emailField.requestFocus();
                        return;
                    } catch (Exception e) {
                        emailField.setError(e.getMessage());
                        emailField.requestFocus();
                        return;
                    }
                }
            }
        });
    }
}
