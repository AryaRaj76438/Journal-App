package com.example.journal;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.example.journal.databinding.ActivityLoginBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Collection;

import model.Journal;
import util.JournalApi;

public class LoginActivity extends AppCompatActivity {
    ActivityLoginBinding loginBinding;
    
    // Firebase Instance Variable
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser currentUser;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference collectionReference = db.collection("Users");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loginBinding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(loginBinding.getRoot());

        firebaseAuth = FirebaseAuth.getInstance();
        loginBinding.createAccountBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this,CreateAccountActivity.class));
            }
        });
        loginBinding.emailSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                startActivity(new Intent());
                // Move to Journal Page
                loginEmailPasswordUser(loginBinding.email.getText().toString().trim(),
                        loginBinding.password.getText().toString().trim());
            }
        });
    }

    private void loginEmailPasswordUser(String email, String password) {
        loginBinding.loginProgress.setVisibility(View.VISIBLE);
        if(!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)){
            firebaseAuth.signInWithEmailAndPassword(email,password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            FirebaseUser user = firebaseAuth.getCurrentUser();
                            assert user != null;
                            String currentUserId  = user.getUid();
                            collectionReference.whereEqualTo("userId",currentUserId)
                                    .addSnapshotListener(new EventListener<QuerySnapshot>() {
                                        @Override
                                        public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                                            loginBinding.loginProgress.setVisibility(View.INVISIBLE);
                                            assert value != null;
                                            if(error != null){}
                                            if(!value.isEmpty()){
                                                for(QueryDocumentSnapshot snapshot:value){
                                                    JournalApi journalApi = JournalApi.getInstance();
                                                    journalApi.setUsername(snapshot.getString("username"));
                                                    journalApi.setUserId(snapshot.getString("userId"));
                                                    // Go to List Activity
                                                    startActivity(new Intent(LoginActivity.this,PostJournalActivity.class));
                                                }
                                            }
                                        }
                                    });
                        }
                    }).addOnFailureListener(e -> {
                        loginBinding.loginProgress.setVisibility(View.INVISIBLE);
                        Toast.makeText(this, "Failed to Login\nTry Again!", Toast.LENGTH_SHORT).show();
                    });
        }else{
            loginBinding.loginProgress.setVisibility(View.INVISIBLE);
            Toast.makeText(LoginActivity.this,"Please enter email and address",Toast.LENGTH_SHORT).show();
        }
    }
}