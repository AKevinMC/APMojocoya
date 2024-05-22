package com.example.apmojocoya;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

public class DialogAddMedidor extends DialogFragment {
    private EditText nroMedidor, ubicacion, fechaInicio;
    private Button btnAceptarDialog;
    private AddMedidorListener listener;

    public interface AddMedidorListener {
        void onAddMedidor(String nroMedidor, String ubicacion, String fechaInicio);
    }

    public void setAddMedidorListener(AddMedidorListener listener) {
        this.listener = listener;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_dialog_add_medidor, container, false);

        nroMedidor = view.findViewById(R.id.nro_medidor);
        ubicacion = view.findViewById(R.id.ubicacion);
        fechaInicio = view.findViewById(R.id.fecha_inicio);
        btnAceptarDialog = view.findViewById(R.id.btnAceptarDialog);

        btnAceptarDialog.setOnClickListener(v -> {
            String nro = nroMedidor.getText().toString();
            String ubi = ubicacion.getText().toString();
            String fecha = fechaInicio.getText().toString();

            if (listener != null) {
                listener.onAddMedidor(nro, ubi, fecha);
            }

            dismiss();
        });

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
    }
}
