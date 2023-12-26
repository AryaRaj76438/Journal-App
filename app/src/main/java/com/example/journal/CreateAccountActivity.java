package com.example.journal;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.journal.databinding.ActivityCreateAccountBinding;

public class CreateAccountActivity extends AppCompatActivity {

    ActivityCreateAccountBinding createAccountBinding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        createAccountBinding = ActivityCreateAccountBinding.inflate(getLayoutInflater());
        setContentView(createAccountBinding.getRoot());

        createAccountBinding.backTologinBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(CreateAccountActivity.this,LoginActivity.class));
            }
        });
    }
}