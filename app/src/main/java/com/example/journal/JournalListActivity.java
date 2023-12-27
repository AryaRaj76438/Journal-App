package com.example.journal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.example.journal.databinding.ActivityJournalListBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

import model.Journal;
import ui.JournalRecylerAdapter;
import util.JournalApi;

public class JournalListActivity extends AppCompatActivity {
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser user;
    private StorageReference storageReference;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    ActivityJournalListBinding journalBinding;

    private List<Journal> journalList;
    private JournalRecylerAdapter journalRecylerAdapter;
    private CollectionReference collectionReference = db.collection("Journal");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        journalBinding = ActivityJournalListBinding.inflate(getLayoutInflater());
        setContentView(journalBinding.getRoot());

        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();

        journalList = new ArrayList<>();
        // Settingup recyclerView
        journalBinding.recyclerView.setHasFixedSize(true);
        journalBinding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.action_add){
            // Adding Journal
            if(user!=null && firebaseAuth != null){
                startActivity(new Intent(JournalListActivity.this,PostJournalActivity.class));
            }

        } else if (id == R.id.action_signout) {
            // Signout User
            if(user!=null && firebaseAuth!=null){
                startActivity(new Intent(JournalListActivity.this,MainActivity.class));
            }
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();
        collectionReference.whereEqualTo("userId",
                JournalApi.getInstance().getUserId())
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if(!queryDocumentSnapshots.isEmpty()){
                            for(QueryDocumentSnapshot journals: queryDocumentSnapshots){
                                Journal journal = journals.toObject(Journal.class);
                                journalList.add(journal);
                            }
                            journalRecylerAdapter = new JournalRecylerAdapter(JournalListActivity.this,journalList);
                            journalBinding.recyclerView.setAdapter(journalRecylerAdapter);
                            journalRecylerAdapter.notifyDataSetChanged(); // how to change itself
                        }else{
                            journalBinding.noThoughtTitle.setVisibility(View.VISIBLE);
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
    }
}