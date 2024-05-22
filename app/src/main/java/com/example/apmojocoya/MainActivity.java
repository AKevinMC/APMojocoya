package com.example.apmojocoya;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.example.apmojocoya.LoginActivity;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;


public class MainActivity extends AppCompatActivity {

    private boolean doubleBackToExitPressedOnce = false;
    Button btn_usuarios;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Inicializar Firebase
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        btn_usuarios = findViewById(R.id.btnusuarios);
        btn_usuarios.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, UserActivity.class);
            startActivity(intent);
        });

        Button button = findViewById(R.id.buttonasd);
        button.setOnClickListener(v -> {

            // Create a new user with a first, middle, and last name
            Map<String, Object> user = new HashMap<>();
            user.put("first", "Alan");
            user.put("middle", "Mathison");
            user.put("last", "Turing");
            user.put("born", 1913);

            // Add a new document with a generated ID
            db.collection("users")
                    .add(user)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w(TAG, "Error adding document", e);
                        }
                    });

            //leer la base de datos el logcat
            db.collection("users")
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    Log.d(TAG, document.getId() + " => " + document.getData());
                                }
                            } else {
                                Log.w(TAG, "Error getting documents.", task.getException());
                            }
                        }
                    });
            checkPermission();

        });







    //verificar si hay permiso de escritura
        checkPermission();

    // Nombre del archivo xlsx y directorio
        String FILE_NAME = "test2.xlsx";
        String directoryPath = Environment.getExternalStorageDirectory() + "/Documents";

    // Crear el directorio (si no existe)
        File folder = new File(directoryPath);
    // Log
        if (folder.mkdirs() || folder.exists()) {
            Log.d("MainActivity", "Folder created: " + folder.getAbsolutePath());
        } else {
            Log.e("MainActivity", "Error creating folder: " + folder.getAbsolutePath());
        }

    // Crear el archivo xlsx
        File excelFile = new File(folder , FILE_NAME);
        Workbook libro = new XSSFWorkbook();
        Sheet hoja = libro.createSheet("hoja1");
        hoja.createRow(0).createCell(0).setCellValue("Hola mundo");
        try {
            libro.write(new FileOutputStream(excelFile));
            Log.d("MainActivity", "excelFile created: " + excelFile.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("MainActivity", "Error creating excelFile: " + excelFile.getAbsolutePath());
        }


    //cambiar el testo de textView
        //TextView textView = findViewById(R.id.textView);
        //textView.setText("Listo!");
    }
//Verificar si hay permiso de escritura
    private void checkPermission() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }
    }
    //preguntar si quiere salir despues de presionar el boton de atras

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Presione otra vez para salir", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2300);
    }
}