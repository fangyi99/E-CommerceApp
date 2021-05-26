package com.example.e_commerceapp2;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ProfileDetailActivity extends AppCompatActivity {

    //Create & initialise FirebaseAuth & FirebaseUser
    private FirebaseFirestore database = FirebaseFirestore.getInstance();

    EditText username, password, email, phone;
    ImageView profilePic;
    Uri imageUri;
    String imageUrl;
    FirebaseStorage storage;
    StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_detail);

        username = (EditText) findViewById(R.id.editUsername);
        password = (EditText) findViewById(R.id.editPassword);
        email = (EditText) findViewById(R.id.editEmail);
        phone = (EditText) findViewById(R.id.editPhone);
        profilePic = (ImageView) findViewById(R.id.profilePic2);

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
    }

    public void clickBack(View v){
        onBackPressed();
    }

    public void clickUpdate(View v) {

        //change document path to current user
        final DocumentReference docRef = database.collection("Users").document(UserManager.getUserId());

        //store updated values in hashmap
        Map<String, Object> map = new HashMap<>();

        map.put("username", username.getText().toString());
        map.put("password", password.getText().toString());
        map.put("email", email.getText().toString());
        map.put("phone", phone.getText().toString());
        map.put("photo", imageUrl);



        docRef.update(map)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("TAG", "Profile updated successfully");
                        Toast.makeText(getApplicationContext(), "Profile Updated Successfully.", Toast.LENGTH_SHORT).show();
                        Intent reloadProfile = new Intent(getApplicationContext(), ProfileFragment.class);
                        startActivity(reloadProfile);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("TAG", "Profile update failed");
                        Toast.makeText(getApplicationContext(), "Profile Update Failed.", Toast.LENGTH_SHORT).show();
                    }
                });

    }

    public void choosePhoto(View v){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==1 && resultCode==RESULT_OK && data!=null && data.getData()!=null){
            imageUri = data.getData();
            profilePic.setImageURI(imageUri);
            uploadPicture();
        }
    }

    private void uploadPicture(){

        final ProgressDialog pd = new ProgressDialog(this);
        pd.setTitle("Uploading Image...");
        pd.show();

        final String randomKey = UUID.randomUUID().toString();
        StorageReference imageRef = storageReference.child("images/" + UserManager.getUserId() + "/" + randomKey);

        imageRef.putFile(imageUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        //get a URL to the uploaded content
                        pd.dismiss();
//                        imageUrl = taskSnapshot.getUploadSessionUri().toString();
                        imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                imageUrl = String.valueOf(uri);
                                Log.d("TAG",imageUrl);
                            }
                        });
                        Snackbar.make(findViewById(android.R.id.content),"Image Uploaded.", Snackbar.LENGTH_LONG).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //handle unsuccessful uploads
                        pd.dismiss();
                        Toast.makeText(getApplicationContext(),"Upload failed",Toast.LENGTH_SHORT).show();
                        Log.d("TAG","ERROR:" + e);
                    }
                })
                .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                        double progressPercent = (100.00 * snapshot.getBytesTransferred() / snapshot.getTotalByteCount());
                        pd.setMessage((int) progressPercent + "%");
                    }
                });
    }

}