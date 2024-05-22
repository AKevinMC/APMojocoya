package com.example.apmojocoya;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

public class UserActivity extends AppCompatActivity {

    Button btn_add_user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        btn_add_user = findViewById(R.id.btnadduser);
        btn_add_user.setOnClickListener(v -> {
            Intent intent = new Intent(UserActivity.this, NewUserActivity.class);
            startActivity(intent);
        });
    }
}