package com.example.bookmybook;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.bookmybook.data.BookInfo;
import com.example.bookmybook.data.LocalStorageSharedPreferences;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class UploadBookActivity extends AppCompatActivity {

    private static final int RC_PHOTO_PICKER =  2;

    EditText bookNameField, authorNameField, bookEditionField, bookPriceField;
    Button bookPhotoUploadButton;
    FloatingActionButton chat;
    Uri bookPhotoUri=null;
    BookInfo bookInfo;

    ProgressDialog progressDialog;

    FirebaseStorage firebaseStorage;
    StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_book);
        bookNameField=findViewById(R.id.bookNameUploadField);
        authorNameField=findViewById(R.id.authorNameUploadField);
        bookEditionField=findViewById(R.id.bookEditionUploadField);
        bookPriceField=findViewById(R.id.bookPriceUploadField);
        bookPhotoUploadButton=findViewById(R.id.bookPhotoUploadButton);
        chat= findViewById(R.id.uploadBookConfirm);
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
                LocalStorageSharedPreferences.removeLoggedUser(UploadBookActivity.this);
                Toast.makeText(this, "SignOut Successful", Toast.LENGTH_SHORT).show();
                intent=new Intent(this,LoginActivity.class);
                startActivity(intent);
                finish();
        }
        return true;
    }

    public void uploadBookButtonConfirm(View view) {
        bookInfo=new BookInfo(bookNameField.getText().toString(),
                authorNameField.getText().toString(),
                bookEditionField.getText().toString(),
                bookPriceField.getText().toString(),
                LocalStorageSharedPreferences.getLoggedUser(UploadBookActivity.this));
        //Log.i("MyLogs",bookInfo.name+" "+bookInfo.authorName+" "+bookInfo.edition+" "+bookInfo.price);
        if(TextUtils.isEmpty(bookInfo.name)){
            bookNameField.setError("Enter Book Name");
            bookNameField.requestFocus();
            return;
        }
        if(TextUtils.isEmpty(bookInfo.authorName)){
            authorNameField.setError("Enter Author name");
            authorNameField.requestFocus();
            return;
        }
        if(TextUtils.isEmpty(bookInfo.edition)){
            bookEditionField.setError("Enter Book Edition");
            bookEditionField.requestFocus();
            return;
        }
        if(!TextUtils.isDigitsOnly(bookInfo.edition)){
            bookEditionField.setError("Enter Book Edition in numeric form");
            bookEditionField.requestFocus();
            return;
        }
        if(TextUtils.isEmpty(bookInfo.price)){
            bookPriceField.setError("Enter Price you are looking for");
            bookPriceField.requestFocus();
            return;
        }
        if(!TextUtils.isDigitsOnly(bookInfo.price)){
            bookPriceField.setError("Enter price in digits only");
            bookPriceField.requestFocus();
            return;
        }
        if(bookPhotoUri==null){
            bookPhotoUploadButton.setError("Select Picture from here");
            bookPhotoUploadButton.requestFocus();
            Snackbar.make(view, "Select book cover's picture", Snackbar.LENGTH_LONG).show();
            return;
        }
        if(bookPhotoUploadButton.getText().toString().equals("Upload Picture From Gallery")){
            bookPhotoUploadButton.setError("Select Picture from here");
            bookPhotoUploadButton.requestFocus();
            Snackbar.make(view, "Select book cover's picture", Snackbar.LENGTH_LONG).show();
            return;
        }
        Snackbar.make(view, "Uploading the Book", Snackbar.LENGTH_LONG).show();
        progressDialog=new ProgressDialog(UploadBookActivity.this);
        progressDialog.setTitle("UPLOADING...");
        progressDialog.show();
        //uploadPhotoAndGetUrl();
        firebaseStorage=FirebaseStorage.getInstance();
        storageReference=firebaseStorage.getReference().child("book_covers").child(LocalStorageSharedPreferences.getLoggedUser(UploadBookActivity.this)).child(bookPhotoUri.getLastPathSegment());
        storageReference.putFile(bookPhotoUri).addOnSuccessListener(UploadBookActivity.this, new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Task<Uri> urlTask = taskSnapshot.getMetadata().getReference().getDownloadUrl();
                while(!urlTask.isSuccessful());
                Uri downloadUrl = urlTask.getResult();
                Log.i("MyLogs",downloadUrl.toString());
                bookInfo.addPhotoUrl(downloadUrl.toString());
            }
        }).addOnFailureListener(UploadBookActivity.this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(UploadBookActivity.this, "Problem in uploading picture", Toast.LENGTH_SHORT).show();
                Log.i("MyLogs", "It got failed "+e.getMessage()+" "+e.getStackTrace());
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                //displaying the upload progress
                double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                progressDialog.setMessage("Uploaded " + ((int) progress) + "%...");
            }
        });
        firebaseStorage=FirebaseStorage.getInstance();
        storageReference=firebaseStorage.getReference().child("book_covers").
                child(LocalStorageSharedPreferences.getLoggedUser(UploadBookActivity.this))
                .child(bookPhotoUri.getLastPathSegment());
        storageReference.putFile(bookPhotoUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Task<Uri> urlTask = taskSnapshot.getMetadata().getReference().getDownloadUrl();
                while (!urlTask.isSuccessful()) ;
                Uri downloadUrl = urlTask.getResult();
                bookInfo.photoUrl=downloadUrl.toString();
                DatabaseReference databaseReference=FirebaseDatabase.getInstance().getReference().child("books").push();
                databaseReference.setValue(bookInfo);
            }
        });
        //Log.i("MyLogsReference",databaseReference.getKey());
        Toast.makeText(UploadBookActivity.this, "Book Uploaded Successfully", Toast.LENGTH_SHORT).show();
        finish();
    }

    public void getPhotoFromDevice(View view) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/jpeg");
        intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
        startActivityForResult(Intent.createChooser(intent, "Complete action using"), RC_PHOTO_PICKER);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_PHOTO_PICKER) {
            if (resultCode == RESULT_OK) {
                bookPhotoUri=data.getData();
                Toast.makeText(UploadBookActivity.this,"Picture Selected",Toast.LENGTH_LONG).show();
                bookPhotoUploadButton.setText("Change Selected Picture");
                /*StorageReference photoRef = mChatPhotosStorageReference.child(selectedImageUri.getLastPathSegment());
                photoRef.putFile(selectedImageUri).addOnSuccessListener(this, new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Log.i("uploadImage", "to storage");
                        // When the image has successfully uploaded, we get its download URL
                        //Uri downloadUrl = taskSnapshot.getDownloadUrl();
                        Task<Uri> urlTask = taskSnapshot.getMetadata().getReference().getDownloadUrl();
                        while (!urlTask.isSuccessful()) ;
                        Uri downloadUrl = urlTask.getResult();

                        //FriendlyMessage friendlyMessage = new FriendlyMessage(null, mUsername, downloadUrl.toString());
                        //mMessagesDatabaseReference.push().setValue(friendlyMessage);
                        // Set the download URL to the message box, so that the user can send it to the database
                        //FriendlyMessage friendlyMessage = new FriendlyMessage(null, mUsername, downloadUrl.toString());
                        //mMessagesDatabaseReference.push().setValue(friendlyMessage);
                    }
                });*/
            }
        }
    }

    private void uploadPhotoAndGetUrl(){
        //Log.i("MyLogs",""+bookPhotoUri);
        //Log.i("MyLogs",""+bookPhotoUri.getLastPathSegment());
        firebaseStorage=FirebaseStorage.getInstance();
        storageReference=firebaseStorage.getReference().child("book_covers").child(LocalStorageSharedPreferences.getLoggedUser(UploadBookActivity.this)).child(bookPhotoUri.getLastPathSegment());
        /*storageReference.putFile(bookPhotoUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                bookInfo.photoUrl=task.toString();
                Task<Uri> urlTask = taskSnapshot.getMetadata().getReference().getDownloadUrl();
                while(!urlTask.isSuccessful());
                Uri downloadUrl = urlTask.getResult();
                Log.i("MyLogs",downloadUrl.toString());
                bookInfo.addPhotoUrl(downloadUrl.toString());
                //Log.i("MyLogs",""+downloadUri);
                //Log.i("MyLogs",""+downloadUri.toString());
            }
        });*/
        /*storageReference.putFile(bookPhotoUri).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }
                // Continue with the task to get the download URL
                return storageReference.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    Uri downloadUri = task.getResult();
                    //Log.i("MyLogs",""+downloadUri);
                    //Log.i("MyLogs",""+downloadUri.toString());
                    bookInfo.photoUrl=downloadUri.toString();
                } else {
                    // Handle failures
                    // ...
                }
            }
        });*/
        storageReference.putFile(bookPhotoUri).addOnSuccessListener(UploadBookActivity.this, new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Task<Uri> urlTask = taskSnapshot.getMetadata().getReference().getDownloadUrl();
                while(!urlTask.isSuccessful());
                Uri downloadUrl = urlTask.getResult();
                Log.i("MyLogs",downloadUrl.toString());
                bookInfo.addPhotoUrl(downloadUrl.toString());
            }
        }).addOnFailureListener(UploadBookActivity.this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(UploadBookActivity.this, "Problem in uploading picture", Toast.LENGTH_SHORT).show();
                Log.i("MyLogs", "It got failed "+e.getMessage()+" "+e.getStackTrace());
            }
        });
    }
}
/*
 @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            filePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                imageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
 */