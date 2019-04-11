package com.example.bookmybook;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.bookmybook.data.BookInfo;
import com.example.bookmybook.data.LocalStorageSharedPreferences;
import com.example.bookmybook.data.UserInfo;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class ViewBookActivity extends AppCompatActivity {

    BookInfo bookInfo;
    TextView name, author, edition, price, uploadedBy;
    ImageView coverPhoto;
    ChildEventListener childEventListener=null;
    String nameOfUploader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_book);
        Toast.makeText(this, "Loading Book Details for you", Toast.LENGTH_SHORT).show();
        bookInfo=(BookInfo)getIntent().getSerializableExtra("BookToShow");
        name=findViewById(R.id.bookNameDetails);
        author=findViewById(R.id.authorNameDetails);
        edition=findViewById(R.id.bookEditionDetails);
        price=findViewById(R.id.bookPriceDetails);
        uploadedBy=findViewById(R.id.bookUploadedByDetails);
        coverPhoto=findViewById(R.id.bookPhotoDetails);
        //coverPhoto.setImageBitmap(getBitmapFromURL(bookInfo.photoUrl));
        attachDatabaseReadListener();
    }

    public void setValuesToView(){
        uploadedBy.setText(nameOfUploader);
        name.setText(bookInfo.name);
        author.setText(bookInfo.authorName);
        edition.setText(bookInfo.edition);
        price.setText(bookInfo.price);
        Glide.with(coverPhoto.getContext())
                .load(bookInfo.photoUrl)
                .into(coverPhoto);
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
                FirebaseAuth.getInstance().signOut();
                LocalStorageSharedPreferences.removeLoggedUser(ViewBookActivity.this);
                Toast.makeText(this, "SignOut Successful", Toast.LENGTH_SHORT).show();
                intent=new Intent(this,LoginActivity.class);
                startActivity(intent);
                finish();
        }
        return true;
    }
    public void attachDatabaseReadListener(){
        if(childEventListener==null){
            childEventListener=new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    nameOfUploader=dataSnapshot.getValue(String.class);
                    setValuesToView();
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
            };
            FirebaseDatabase.getInstance().getReference().child("users").child(bookInfo.uploadedBy).addChildEventListener(childEventListener);
        }
    }
    /*public static Bitmap getBitmapFromURL(String src) {
        try {
            //Log.e("src",src);
            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            Log.e("Bitmap","returned");
            return myBitmap;
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("Exception",e.getMessage());
            return null;
        }
    }*/
}
