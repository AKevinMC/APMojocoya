package com.example.apmojocoya;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ExcelReportGenerator {

    // Nombre de tu subcarpeta
    private static final String CARPETA_DESTINO = "usuarios";

    public static String guardarPadron(Context context, List<Map<String, Object>> listaUsuarios) {
        if (listaUsuarios == null || listaUsuarios.isEmpty()) return null;

        // 1. Crear Workbook
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Padron Usuarios");

        // 2. Estilos
        CellStyle headerStyle = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        headerStyle.setFont(font);

        // 3. Cabeceras
        String[] columnas = {"CI", "Nombre", "Apellidos", "Celular", "Dirección", "Estado"};
        Row headerRow = sheet.createRow(0);
        for (int i = 0; i < columnas.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(columnas[i]);
            cell.setCellStyle(headerStyle);
        }

        // 4. Datos
        int rowNum = 1;
        for (Map<String, Object> usuario : listaUsuarios) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(safeString(usuario.get("ci")));
            row.createCell(1).setCellValue(safeString(usuario.get("nombre")));
            row.createCell(2).setCellValue(safeString(usuario.get("apellidos")));
            row.createCell(3).setCellValue(safeString(usuario.get("celular")));
            row.createCell(4).setCellValue(safeString(usuario.get("direccion")));
            row.createCell(5).setCellValue(safeString(usuario.get("estado")));
        }

        // Ajuste de columnas (fijo para evitar errores)
        for (int i = 0; i < columnas.length; i++) {
            sheet.setColumnWidth(i, 20 * 256);
        }

        // 5. GUARDADO EN SUBCARPETA "usuarios"
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String fileName = "Padron_" + timeStamp + ".xlsx";

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                // --- ANDROID 10+ (MediaStore) ---
                ContentValues values = new ContentValues();
                values.put(MediaStore.MediaColumns.DISPLAY_NAME, fileName);
                values.put(MediaStore.MediaColumns.MIME_TYPE, "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");

                // AQUÍ ESTÁ EL CAMBIO: Agregamos "/usuarios" a la ruta
                values.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOCUMENTS + "/" + CARPETA_DESTINO);

                Uri uri = context.getContentResolver().insert(MediaStore.Files.getContentUri("external"), values);

                if (uri != null) {
                    OutputStream os = context.getContentResolver().openOutputStream(uri);
                    workbook.write(os);
                    os.close();
                    workbook.close();
                    return "Documentos/" + CARPETA_DESTINO + "/" + fileName;
                }
            } else {
                // --- ANDROID 9- (Clásico) ---
                File docFolder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);
                // AQUÍ ESTÁ EL CAMBIO: Creamos la subcarpeta
                File miCarpeta = new File(docFolder, CARPETA_DESTINO);
                if (!miCarpeta.exists()) {
                    miCarpeta.mkdirs();
                }

                File file = new File(miCarpeta, fileName);
                FileOutputStream fos = new FileOutputStream(file);
                workbook.write(fos);
                fos.close();
                workbook.close();
                return file.getAbsolutePath();
            }
        } catch (IOException e) {
            Log.e("Excel", "Error al guardar", e);
        }

        return null;
    }

    private static String safeString(Object obj) {
        return obj == null ? "" : obj.toString();
    }
}