package com.example.bookmybook;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bookmybook.data.BookInfo;
import com.example.bookmybook.data.LocalStorageSharedPreferences;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;

import java.io.Serializable;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    ChildEventListener childEventListener=null;
    ArrayList<String> listOfStoredBooks, keysList;
    ArrayList<BookInfo> bookList;
    ListView listView;
    TextView fetching;
    ArrayAdapter<String> arrayAdapter;
    final static int ALL_BOOKS_FLAG=0;
    final static int USERS_BOOKS_FLAG=1;
    int flagToSend;

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
        Intent intent=this.getIntent();
        String listType="";
        if(intent!=null) listType=intent.getStringExtra("List Type");
        //Bundle bundle=getIntent().getExtras();
        //listType=bundle.getString("List Type");
        listOfStoredBooks=new ArrayList<>();
        bookList=new ArrayList<>();
        keysList=new ArrayList<>();
        if("User's Books".equals(listType)) {
            attachDatabaseReadListenerForUsersBooks();
            flagToSend=USERS_BOOKS_FLAG;
        }
        else {
            attachDatabaseReadListenerForAllBooks();
            flagToSend=ALL_BOOKS_FLAG;
        }
        setArrayAdapterForBooks();
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
                break;
            case R.id.manageUploadsMenuOption:
                intent=new Intent(this,MainActivity.class);
                intent.putExtra("List Type","User's Books");
                startActivity(intent);
                break;
            case R.id.signOutMenuOption:
                FirebaseAuth.getInstance().signOut();
                LocalStorageSharedPreferences.removeLoggedUser(MainActivity.this);
                Toast.makeText(this, "SignOut Successful", Toast.LENGTH_SHORT).show();
                intent=new Intent(this,LoginActivity.class);
                startActivity(intent);
                finish();
        }
        return true;
    }

    public void attachDatabaseReadListenerForAllBooks(){
        if(childEventListener==null){
            childEventListener=new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    BookInfo bookInfo=dataSnapshot.getValue(BookInfo.class);
                    listOfStoredBooks.add(bookInfo.name+" by "+bookInfo.authorName+" @Rs."+bookInfo.price);
                    bookList.add(bookInfo);
                    keysList.add(dataSnapshot.getKey());
                    //printArrayList();
                    setArrayAdapterForBooks();
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
            FirebaseDatabase.getInstance().getReference().child("books").addChildEventListener(childEventListener);
        }
    }
    public void setArrayAdapterForBooks(){
        listView=findViewById(R.id.listingBooks);
        arrayAdapter=new ArrayAdapter<>(this,android.R.layout.simple_expandable_list_item_1,listOfStoredBooks);
        listView.setAdapter(arrayAdapter);
        fetching=findViewById(R.id.fetchingTextView);
        listView.setEmptyView(fetching);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Toast.makeText(MainActivity.this, "With Book: "+bookList.get(position), Toast.LENGTH_SHORT).show();
                Intent intent=new Intent(MainActivity.this,ViewBookActivity.class);
                intent.putExtra("BookToShow",bookList.get(position));
                intent.putExtra("Flag",flagToSend);
                intent.putExtra("keyValue",keysList.get(position));
                startActivity(intent);
            }
        });
    }
    public void attachDatabaseReadListenerForUsersBooks(){
        if(childEventListener==null){
            final String userID=LocalStorageSharedPreferences.getLoggedUser(MainActivity.this);
            childEventListener=new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    BookInfo bookInfo=dataSnapshot.getValue(BookInfo.class);
                    if(bookInfo.uploadedBy.equals(userID)) {
                        listOfStoredBooks.add(bookInfo.name + " by " + bookInfo.authorName + " @Rs." + bookInfo.price);
                        bookList.add(bookInfo);
                        keysList.add(dataSnapshot.getKey());
                        //printArrayList();
                        setArrayAdapterForBooks();
                    }
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
            FirebaseDatabase.getInstance().getReference().child("books").addChildEventListener(childEventListener);
        }
        else {
            Log.i("MyLogs","Object is not null");
        }
    }
    /*private void printArrayList(){
        for (int i=0;i<listOfStoredBooks.size();i++){
            Log.i("MyLogsBookList",listOfStoredBooks.get(i));
        }
    }*/
}
