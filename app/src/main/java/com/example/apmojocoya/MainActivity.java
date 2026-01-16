package com.example.apmojocoya;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private Button btnRegistrar, btnVerUsuarios, btnExportarPadron;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = FirebaseFirestore.getInstance();

        btnRegistrar = findViewById(R.id.btn_registrar_usuario);
        btnVerUsuarios = findViewById(R.id.btn_ver_usuarios);
        btnExportarPadron = findViewById(R.id.btn_exportar_padron);

        btnRegistrar.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, NewUserActivity.class)));
        btnVerUsuarios.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, UserActivity.class)));

        // Acción directa sin chequeo de permisos
        btnExportarPadron.setOnClickListener(v -> {
            Toast.makeText(MainActivity.this, "Generando...", Toast.LENGTH_SHORT).show();
            // Llamada directa a tu función de exportar
            exportarDatos();
        });
    }

    private void exportarDatos() {
        Toast.makeText(this, "Generando reporte...", Toast.LENGTH_SHORT).show();

        db.collection("users").get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Map<String, Object>> listaParaExcel = new ArrayList<>();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        Map<String, Object> data = document.getData();
                        data.put("ci", document.getId());
                        listaParaExcel.add(data);
                    }

                    if (listaParaExcel.isEmpty()) {
                        Toast.makeText(this, "No hay usuarios para exportar.", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // Llamamos al generador seguro
                    String ruta = ExcelReportGenerator.guardarPadron(this, listaParaExcel);

                    if (ruta != null) {
                        // Mensaje de éxito con la ruta
                        Toast.makeText(this, "✅ Guardado en Documentos de la App", Toast.LENGTH_LONG).show();
                        // Opcional: Mostrar la ruta exacta en un segundo Toast si quieres verla
                        // Toast.makeText(this, ruta, Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(this, "❌ Ocurrió un error al guardar.", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Error de red: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                );
    }
}