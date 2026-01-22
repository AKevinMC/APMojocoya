package com.example.apmojocoya;

import android.os.Bundle;
import android.util.SparseBooleanArray;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.WriteBatch;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DetalleCobroActivity extends AppCompatActivity {

    private TextView tvTitulo, tvTotal;
    private ListView listView;
    private Button btnCobrar;
    private FirebaseFirestore db;

    private String userId, userName;
    private List<Lectura> listaDeudas;
    private Map<String, String> mapaUbicaciones;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle_cobro);

        db = FirebaseFirestore.getInstance();
        listaDeudas = new ArrayList<>();
        mapaUbicaciones = new HashMap<>();

        tvTitulo = findViewById(R.id.tv_titulo_cobro);
        tvTotal = findViewById(R.id.tv_total_detalle);
        listView = findViewById(R.id.list_detalle_deudas);
        btnCobrar = findViewById(R.id.btn_realizar_pago);

        userId = getIntent().getStringExtra("USER_ID");
        userName = getIntent().getStringExtra("USER_NAME");

        // --- CORRECCIÓN AQUÍ: Solo ponemos el nombre, sin texto extra ---
        tvTitulo.setText(userName);

        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        listView.setOnItemClickListener((p, v, pos, id) -> calcularTotal());

        btnCobrar.setOnClickListener(v -> procesarPago());

        cargarNombresMedidores();
    }

    private void cargarNombresMedidores() {
        db.collection("users").document(userId).collection("medidores")
                .get()
                .addOnSuccessListener(snaps -> {
                    mapaUbicaciones.clear();
                    for (DocumentSnapshot doc : snaps) {
                        String nro = doc.getString("nro_medidor");
                        String ubi = doc.getString("ubicacion");
                        if (ubi == null || ubi.isEmpty()) ubi = "";

                        if (nro != null) {
                            mapaUbicaciones.put(nro, ubi);
                        }
                    }
                    cargarDeudas();
                });
    }

    private void cargarDeudas() {
        db.collection("lecturas")
                .whereEqualTo("usuarioId", userId)
                .whereEqualTo("pagado", false)
                .get()
                .addOnSuccessListener(snaps -> {
                    listaDeudas.clear();
                    List<String> displayList = new ArrayList<>();

                    for (DocumentSnapshot doc : snaps) {
                        Lectura lec = doc.toObject(Lectura.class);
                        if (lec != null) {
                            listaDeudas.add(lec);

                            String nroMedidor = lec.getIdMedidor();
                            if (nroMedidor == null) nroMedidor = lec.getIdMedidor();

                            String nombreLugar = mapaUbicaciones.get(nroMedidor);
                            String infoMedidor = "Med: " + nroMedidor;

                            if (nombreLugar != null && !nombreLugar.isEmpty()) {
                                infoMedidor += " (" + nombreLugar + ")";
                            }

                            // Usamos el diseño de dos líneas
                            String textoFila = String.format("%s %d   -------   %.2f Bs\n%s",
                                    obtenerMes(lec.getMes()).toUpperCase(),
                                    lec.getAnio(),
                                    lec.getMontoTotal(),
                                    infoMedidor);

                            displayList.add(textoFila);
                        }
                    }

                    if (displayList.isEmpty()) {
                        Toast.makeText(this, "No tiene deudas pendientes", Toast.LENGTH_LONG).show();
                    }

                    // Usamos el XML personalizado para que se vea espacioso
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(
                            this,
                            R.layout.item_deuda_custom,
                            displayList);

                    listView.setAdapter(adapter);
                });
    }

    private void calcularTotal() {
        SparseBooleanArray checked = listView.getCheckedItemPositions();
        double total = 0;
        for (int i = 0; i < listView.getCount(); i++) {
            if (checked.get(i)) total += listaDeudas.get(i).getMontoTotal();
        }
        tvTotal.setText(String.format("%.2f Bs", total));
    }

    private void procesarPago() {
        SparseBooleanArray checked = listView.getCheckedItemPositions();
        if (checked.size() == 0) return;

        WriteBatch batch = db.batch();
        Date hoy = new Date();

        for (int i = 0; i < listView.getCount(); i++) {
            if (checked.get(i)) {
                Lectura lec = listaDeudas.get(i);
                batch.update(db.collection("lecturas").document(lec.getId()),
                        "pagado", true,
                        "fechaPago", hoy
                );
            }
        }

        batch.commit().addOnSuccessListener(v -> {
            Toast.makeText(this, "¡Pago Registrado!", Toast.LENGTH_SHORT).show();
            finish();
        });
    }

    private String obtenerMes(int m) {
        String[] meses = {"", "Ene", "Feb", "Mar", "Abr", "May", "Jun", "Jul", "Ago", "Sep", "Oct", "Nov", "Dic"};
        return (m >= 1 && m <= 12) ? meses[m] : "";
    }
}