package com.example.journal;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import com.example.journal.databinding.ActivityPostJournalBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.StorageReference;

import util.JournalApi;

public class PostJournalActivity extends AppCompatActivity implements View.OnClickListener {
    private static final int GALLERY_CODE = 1;
    private String currentUserId;
    private String currentUserName;

    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser user;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private StorageReference storageReference;

    private CollectionReference collectionReference = db.collection("Journal");
    ActivityPostJournalBinding journalBinding;
    private Uri imageURi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        journalBinding = ActivityPostJournalBinding.inflate(getLayoutInflater());
        setContentView(journalBinding.getRoot());

// We have done this using Journal API signleton Class
//        Bundle bundle = getIntent().getExtras();
//        if(bundle != null){
//            String username = bundle.getString("username");
//            String userId = bundle.getString("userId");
//        }

        firebaseAuth = FirebaseAuth.getInstance();
        journalBinding.postCameraButton.setOnClickListener(this);
        journalBinding.postSaveJournalButton.setOnClickListener(this);

        journalBinding.postProgressBar.setVisibility(View.INVISIBLE);

        if(JournalApi.getInstance()!=null){
            currentUserName = JournalApi.getInstance().getUsername();
            currentUserId = JournalApi.getInstance().getUserId();
            journalBinding.postUsernameTextview.setText(currentUserName);
        }

        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                user = firebaseAuth.getCurrentUser();

            }
        };
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if(id == R.id.post_save_journal_button){
            //
        } else if (R.id.postCameraButton == id) {
            //get image from gallery
            Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT); // implicit intent
            galleryIntent.setType("image/*");
            startActivityForResult(galleryIntent,GALLERY_CODE);


        }
    }

    // when calling image content intent
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == GALLERY_CODE && resultCode == RESULT_OK){
            if(data != null){
                imageURi = data.getData();
                journalBinding.postImageView.setImageURI(imageURi);
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        user = firebaseAuth.getCurrentUser();
        firebaseAuth.addAuthStateListener(authStateListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(firebaseAuth != null){
            firebaseAuth.removeAuthStateListener(authStateListener);
        }
    }
}