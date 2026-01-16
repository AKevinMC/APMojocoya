package com.example.apmojocoya;

import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.ServerTimestamp;
import java.util.Date;

public class Lectura {

    private String id; // El ID del documento (Ej: 12345_2024_1)
    private String idMedidor;
    private String idUsuario; // CI del dueño
    private int anio;
    private int mes;
    private double lecturaAnterior;
    private double lecturaActual;
    private double consumo; // Diferencia calculada
    private String observacion;

    @ServerTimestamp // Firebase llenará esto automáticamente al guardar
    private Date fechaToma;

    // -----------------------------------------------------------
    // 1. Constructor Vacío (OBLIGATORIO para Firebase Firestore)
    // -----------------------------------------------------------
    public Lectura() {
    }

    // -----------------------------------------------------------
    // 2. Constructor Principal
    // -----------------------------------------------------------
    public Lectura(String id, String idMedidor, String idUsuario, int anio, int mes, double lecturaAnterior, double lecturaActual, String observacion) {
        this.id = id;
        this.idMedidor = idMedidor;
        this.idUsuario = idUsuario;
        this.anio = anio;
        this.mes = mes;
        this.lecturaAnterior = lecturaAnterior;
        this.lecturaActual = lecturaActual;
        this.observacion = observacion;
        this.consumo = calcularConsumo(); // Calculamos al crear
    }

    // -----------------------------------------------------------
    // 3. Lógica de Negocio (Helpers)
    // -----------------------------------------------------------

    /**
     * Calcula la diferencia. Si es negativa (error o vuelta de medidor), retorna 0 o maneja la lógica.
     */
    public double calcularConsumo() {
        double diferencia = this.lecturaActual - this.lecturaAnterior;
        return Math.max(diferencia, 0); // Evita consumos negativos
    }

    /**
     * Método estático para generar el ID Único del documento.
     * Úsalo antes de guardar en Firestore.
     * Formato: IDMEDIDOR_AÑO_MES (Ej: "554433_2024_1")
     */
    public static String generarIdUnico(String idMedidor, int anio, int mes) {
        return idMedidor + "_" + anio + "_" + mes;
    }

    // -----------------------------------------------------------
    // 4. Getters y Setters
    // -----------------------------------------------------------

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getIdMedidor() { return idMedidor; }
    public void setIdMedidor(String idMedidor) { this.idMedidor = idMedidor; }

    public String getIdUsuario() { return idUsuario; }
    public void setIdUsuario(String idUsuario) { this.idUsuario = idUsuario; }

    public int getAnio() { return anio; }
    public void setAnio(int anio) { this.anio = anio; }

    public int getMes() { return mes; }
    public void setMes(int mes) { this.mes = mes; }

    public double getLecturaAnterior() { return lecturaAnterior; }
    public void setLecturaAnterior(double lecturaAnterior) {
        this.lecturaAnterior = lecturaAnterior;
        this.consumo = calcularConsumo(); // Recalcular si cambia
    }

    public double getLecturaActual() { return lecturaActual; }
    public void setLecturaActual(double lecturaActual) {
        this.lecturaActual = lecturaActual;
        this.consumo = calcularConsumo(); // Recalcular si cambia
    }

    public double getConsumo() { return consumo; }
    public void setConsumo(double consumo) { this.consumo = consumo; }

    public String getObservacion() { return observacion; }
    public void setObservacion(String observacion) { this.observacion = observacion; }

    public Date getFechaToma() { return fechaToma; }
    public void setFechaToma(Date fechaToma) { this.fechaToma = fechaToma; }
}