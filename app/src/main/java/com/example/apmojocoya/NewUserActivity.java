package com.example.apmojocoya;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NewUserActivity extends AppCompatActivity implements DialogAddMedidor.AddMedidorListener {
    Button btn_add_medidor, btnaceptar;
    private EditText ET_nombre, ET_ci, ET_direccion, ET_celular;
    private TextView TV_nro_medidores;

    private FirebaseFirestore db;
    private List<Map<String, String>> medidores;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_user);

        // Inicializar Firestore
        db = FirebaseFirestore.getInstance();

        medidores = new ArrayList<>();

        ET_nombre = findViewById(R.id.ETnombre);
        ET_ci = findViewById(R.id.ETci);
        ET_direccion = findViewById(R.id.ETdireccion);
        ET_celular = findViewById(R.id.ETcelular);
        TV_nro_medidores = findViewById(R.id.TVnro_medidores);

        btn_add_medidor = findViewById(R.id.btnadd_medidor);
        btn_add_medidor.setOnClickListener(v -> {
            FragmentManager fm = getSupportFragmentManager();
            DialogAddMedidor dialogFragment = new DialogAddMedidor();
            dialogFragment.setAddMedidorListener(this);
            dialogFragment.show(fm, "fragment_add_medidor");
        });

        btnaceptar = findViewById(R.id.btnaceptar);
        btnaceptar.setOnClickListener(v -> {
            saveUserToFirestore();
        });
    }

    private void saveUserToFirestore() {
        String nombre = ET_nombre.getText().toString();
        String ci = ET_ci.getText().toString();
        String direccion = ET_direccion.getText().toString();
        String celular = ET_celular.getText().toString();
        int nroMedidores = medidores.size();

        Map<String, Object> user = new HashMap<>();
        user.put("nombre", nombre);
        user.put("celular", celular);
        user.put("direccion", direccion);
        user.put("estado", "activo");

        DocumentReference userRef = db.collection("users").document(ci);
        userRef.set(user).addOnSuccessListener(aVoid -> {
            for (int i = 0; i < nroMedidores; i++) {
                Map<String, String> medidorData = medidores.get(i);
                Map<String, Object> medidor = new HashMap<>();
                medidor.put("nro_medidor", medidorData.get("nro_medidor"));
                medidor.put("estado", "activo");
                medidor.put("ubicacion", medidorData.get("ubicacion"));
                medidor.put("fecha_inicio", medidorData.get("fecha_inicio"));
                medidor.put("fecha_final", null);

                userRef.collection("medidor" + (i + 1)).document("details")
                        .set(medidor)
                        .addOnSuccessListener(aVoid1 -> {
                            // Puedes manejar el éxito de cada medidor aquí
                        })
                        .addOnFailureListener(e -> {
                            // Manejar el fallo de cada medidor aquí
                        });

                Map<String, Object> historial = new HashMap<>();
                userRef.collection("medidor" + (i + 1)).document("historial")
                        .set(historial)
                        .addOnSuccessListener(aVoid1 -> {
                            // Manejar éxito
                        })
                        .addOnFailureListener(e -> {
                            // Manejar fallo
                        });
            }
        }).addOnFailureListener(e -> {
            // Manejar el fallo de guardar el usuario
        });
    }

    @Override
    public void onAddMedidor(String nroMedidor, String ubicacion, String fechaInicio) {
        Map<String, String> medidor = new HashMap<>();
        medidor.put("nro_medidor", nroMedidor);
        medidor.put("ubicacion", ubicacion);
        medidor.put("fecha_inicio", fechaInicio);
        medidores.add(medidor);

        // Actualiza el TextView con el número de medidores
        TV_nro_medidores.setText("Nro Medidores: " + medidores.size());
    }
}