package micompiladorvpr.asm_compiler;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.InputStreamReader;
import javax.swing.JOptionPane;

public class ManejadorEjecucion {

    public void compilarYEjecutar(String codigoASM, String nombreArchivo) {
        // 1. Guardar el archivo .asm en la raíz del proyecto
        if (guardarArchivo(codigoASM, nombreArchivo)) {
            // 2. Ejecutar el BAT
            ejecutarBat(nombreArchivo);
        }
    }

    private boolean guardarArchivo(String codigo, String nombre) {
        try {
            // Se guarda como codigo_vpr.asm en la carpeta raíz del proyecto
            FileWriter writer = new FileWriter(nombre + ".asm");
            writer.write(codigo);
            writer.close();
            return true;
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error guardando ASM: " + e.getMessage());
            return false;
        }
    }

    private void ejecutarBat(String nombreArchivo) {
        try {
            // Llamamos al BAT pasando el nombre del archivo (sin extensión)
            // "cmd /c" asegura que se ejecute en la consola de Windows
            Process p = Runtime.getRuntime().exec("cmd /c ENSAMBLA.BAT " + nombreArchivo);

            // Capturamos la salida del programa (stdout)
            BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
            StringBuilder salida = new StringBuilder();
            String linea;
            
            while ((linea = reader.readLine()) != null) {
                salida.append(linea).append("\n");
            }
            p.waitFor(); // Esperar a que termine

            // --- FILTRADO DE SALIDA ---
            // El BAT imprime cosas como "Ensamblando...", "Linkeando...", queremos solo el resultado.
            String resultadoLimpio = limpiarSalida(salida.toString(), nombreArchivo);

            // 3. MOSTRAR RESULTADO EN JOPTIONPANE
            if (resultadoLimpio.isEmpty()) {
                 JOptionPane.showMessageDialog(null, "Ejecución finalizada (Sin salida en consola).", "Consola", JOptionPane.INFORMATION_MESSAGE);
            } else {
                 JOptionPane.showMessageDialog(null, "RESULTADO DE LA EJECUCIÓN:\n\n" + resultadoLimpio, "Consola del Compilador", JOptionPane.INFORMATION_MESSAGE);
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error ejecutando ensamblador: " + e.getMessage());
        }
    }
    
    // Método para quitar el texto "basura" del BAT y dejar solo lo que imprimió el programa
    private String limpiarSalida(String textoCompleto, String nombreExe) {
        // El BAT dice: echo "Ejecutando %EXE_FILE%..."
        String marcaInicio = "Ejecutando"; 
        
        int indice = textoCompleto.lastIndexOf(marcaInicio);
        if (indice != -1) {
            // Cortamos desde donde dice "Ejecutando..." hacia abajo
            // Y quitamos la primera línea que contiene el nombre del exe
            String temp = textoCompleto.substring(indice);
            int saltoLinea = temp.indexOf("\n");
            if (saltoLinea != -1) {
                return temp.substring(saltoLinea + 1).trim();
            }
        }
        return textoCompleto; // Si no encuentra la marca, devuelve todo (para depurar errores)
    }
}