package com.example.apmojocoya;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class UserDetailActivity extends AppCompatActivity {

    private TextView userName, userMedidores;
    private Button btnEdit, btnDelete, btnDarDeBaja, btnSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_detail);

        userName = findViewById(R.id.userNameDetail);
        userMedidores = findViewById(R.id.userMedidoresDetail);
        btnEdit = findViewById(R.id.btnEdit);
        btnDelete = findViewById(R.id.btnDelete);
        btnDarDeBaja = findViewById(R.id.btnDarDeBaja);
        btnSave = findViewById(R.id.btnSave);

        Intent intent = getIntent();
        String name = intent.getStringExtra("name");
        int medidoresCount = intent.getIntExtra("medidoresCount", 0);

        userName.setText(name);
        userMedidores.setText("Medidores: " + medidoresCount);

        // Aquí puedes añadir la lógica para los botones
        btnEdit.setOnClickListener(v -> {
            // Lógica para editar
        });

        btnDelete.setOnClickListener(v -> {
            // Lógica para eliminar
        });

        btnDarDeBaja.setOnClickListener(v -> {
            // Lógica para dar de baja
        });

        btnSave.setOnClickListener(v -> {
            // Lógica para guardar
        });
    }
}
