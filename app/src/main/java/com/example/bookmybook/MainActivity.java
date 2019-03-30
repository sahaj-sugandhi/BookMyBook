package com.example.bookmybook;

import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FloatingActionButton chat = findViewById(R.id.chat);
        chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Chatting is on its way", Snackbar.LENGTH_LONG).show();
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
        switch(item.getItemId()){
            case R.id.uploadMenuOption:
                Toast.makeText(this, "Upload Your Book", Toast.LENGTH_SHORT).show();
                break;
            case R.id.manageUploadsMenuOption:
                Toast.makeText(this, "Manage Your Uploaded Books", Toast.LENGTH_SHORT).show();
                break;
            case R.id.signOutMenuOption:
                Toast.makeText(this, "SignOut Successful", Toast.LENGTH_SHORT).show();
        }
        return true;
    }
}
