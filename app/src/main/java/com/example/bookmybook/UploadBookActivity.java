package com.example.bookmybook;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

public class UploadBookActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_book);

        FloatingActionButton chat = findViewById(R.id.uploadBookConfirm);
        chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Saving the Book", Snackbar.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater= getMenuInflater();
        menuInflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent=null;
        switch(item.getItemId()){
            case R.id.uploadMenuOption:
                intent=new Intent(this,UploadBookActivity.class);
                startActivity(intent);
                finish();
                break;
            case R.id.manageUploadsMenuOption:
                intent=new Intent(this,MainActivity.class);
                startActivity(intent);
                finish();
                break;
            case R.id.signOutMenuOption:
                Toast.makeText(this, "SignOut Successful", Toast.LENGTH_SHORT).show();
                intent=new Intent(this,LoginActivity.class);
                startActivity(intent);
                finish();
        }
        return true;
    }
}
