package com.example.journal;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.journal.databinding.ActivityPostJournalBinding;

public class PostJournalActivity extends AppCompatActivity {

    ActivityPostJournalBinding journalBinding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        journalBinding = ActivityPostJournalBinding.inflate(getLayoutInflater());
        setContentView(journalBinding.getRoot());



    }
}