package com.example.apmojocoya;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NewUserActivity extends AppCompatActivity implements DialogAddMedidor.AddMedidorListener {
    Button btn_add_medidor, btnaceptar;
    private EditText ET_nombre, ET_apellidos, ET_ci, ET_direccion, ET_celular;
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
        ET_apellidos = findViewById(R.id.ETapellidos);
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
            checkIfUserExists();
        });
    }

    private void checkIfUserExists() {
        String ci = ET_ci.getText().toString();

        DocumentReference userRef = db.collection("users").document(ci);
        userRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    // Usuario ya existe
                    Toast.makeText(NewUserActivity.this, "El usuario ya existe", Toast.LENGTH_SHORT).show();
                } else {
                    // Usuario no existe, proceder a guardar
                    saveUserToFirestore();
                }
            } else {
                // Error al verificar la existencia del usuario
                Toast.makeText(NewUserActivity.this, "Error al verificar el usuario", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveUserToFirestore() {
        String nombre = ET_nombre.getText().toString();
        String apellidos = ET_apellidos.getText().toString();
        String ci = ET_ci.getText().toString();
        String direccion = ET_direccion.getText().toString();
        String celular = ET_celular.getText().toString();
        int nroMedidores = medidores.size();

        Map<String, Object> user = new HashMap<>();
        user.put("nombre", nombre);
        user.put("apellidos", apellidos);
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
                            // Éxito en guardar cada medidor
                        })
                        .addOnFailureListener(e -> {
                            // Error al guardar cada medidor
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

            // Una vez guardado todo, ir a UserActivity
            Intent intent = new Intent(NewUserActivity.this, UserActivity.class);
            startActivity(intent);

        }).addOnFailureListener(e -> {
            // Manejar el fallo de guardar el usuario
            Toast.makeText(NewUserActivity.this, "Error al guardar el usuario", Toast.LENGTH_SHORT).show();
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