package com.example.bookmybook;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

public class SignupActivity extends AppCompatActivity {

    Spinner branchDropDown;
    private static String branchesToList[]={"Civil","Computer Science","Electronics","Information Technology","Mechanical"};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        branchDropDown=findViewById(R.id.branchSignupField);
        ArrayAdapter<String> brancherAdapter=new ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item,branchesToList);
        branchDropDown.setAdapter(brancherAdapter);
    }

    public void clickedSignup(View view) {
    }
}
