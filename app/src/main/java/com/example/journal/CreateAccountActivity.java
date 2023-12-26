package com.example.journal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.example.journal.databinding.ActivityCreateAccountBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

import util.JournalApi;

public class CreateAccountActivity extends AppCompatActivity {

    ActivityCreateAccountBinding createAccountBinding;

    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser currentUser;

    // Firestore connection
    private FirebaseFirestore db =FirebaseFirestore.getInstance();
    private CollectionReference collectionReference = db.collection("Users");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        createAccountBinding = ActivityCreateAccountBinding.inflate(getLayoutInflater());
        setContentView(createAccountBinding.getRoot());

        firebaseAuth = FirebaseAuth.getInstance();

        createAccountBinding.backTologinBtn.setOnClickListener(v ->
                startActivity(new Intent(CreateAccountActivity.this,LoginActivity.class)));

        // On state changed
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                currentUser = firebaseAuth.getCurrentUser();
                if(currentUser != null){
                    //User is already loggedin
                }else{
                    // no user yet
                }
            }
        };

        createAccountBinding.createAccountBtn.setOnClickListener(v -> {
            if(!TextUtils.isEmpty(createAccountBinding.emailEditAccount.getText().toString().trim()) &&
                    !TextUtils.isEmpty(createAccountBinding.passwordEditAccount.getText().toString().trim()) &&
                    !TextUtils.isEmpty(createAccountBinding.usernameEditAccount.getText().toString().trim())){

                String email = createAccountBinding.emailEditAccount.getText().toString().trim();
                String password =  createAccountBinding.passwordEditAccount.getText().toString().trim();
                String username = createAccountBinding.usernameEditAccount.getText().toString().trim();

                createUserEmailAccount(email,password,username);
            }else{
                Toast.makeText(CreateAccountActivity.this, "Empty Field Not Allowed",Toast.LENGTH_SHORT).show();
            }

        });
    }

    private void createUserEmailAccount(String email, String password, String username){
        if(!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password) && !TextUtils.isEmpty(username)){
            createAccountBinding.createActProgress.setVisibility(View.VISIBLE);

            firebaseAuth.createUserWithEmailAndPassword(email,password) // Pass the information to create login-id
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {  // task has user information
                            if(task.isSuccessful()){
                                //take them to addtoJournal
                                currentUser = firebaseAuth.getCurrentUser();
                                assert currentUser != null;
                                String currentUserId = currentUser.getUid();

                                // Create a user map so we can create a user in User application
                                Map<String,String> userObject = new HashMap<>();
                                userObject.put("userId",currentUserId);
                                userObject.put("username",username);

                                // Save to firestore database
                                collectionReference.add(userObject)
                                        .addOnSuccessListener(documentReference -> {
                                            documentReference.get()
                                                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<DocumentSnapshot> task1) {
                                                            if (task1.getResult().exists()) {
                                                                createAccountBinding.createActProgress.setVisibility(View.INVISIBLE);

                                                                // Passing information over intet to other intent
                                                                String name = task1.getResult().getString("username");

                                                                JournalApi journalApi = JournalApi.getInstance();  // Global API: Access to all
                                                                journalApi.setUsername(name);
                                                                journalApi.setUserId(currentUserId);

                                                                Intent intent = new Intent(CreateAccountActivity.this,PostJournalActivity.class);
                                                                intent.putExtra("username",name);
                                                                intent.putExtra("userId",currentUserId);
                                                                startActivity(intent);

                                                            } else {
                                                                createAccountBinding.createActProgress.setVisibility(View.VISIBLE);
                                                            }

                                                        }
                                                    });
                                        }).addOnFailureListener(e -> {

                                        });
                            }else {
                                //something wrong
                            }

                        }
                    }).addOnFailureListener(e -> {

                    });
        }
    }

    // Before anything done on screen: onStart
    @Override
    protected void onStart() {
        super.onStart();
        currentUser = firebaseAuth.getCurrentUser();
        firebaseAuth.addAuthStateListener(authStateListener);
    }
}