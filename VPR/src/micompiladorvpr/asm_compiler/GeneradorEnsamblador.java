package micompiladorvpr.asm_compiler;

import java.util.HashMap;
import java.util.Map;

public class GeneradorEnsamblador {

    public static GeneradorEnsamblador instancia = new GeneradorEnsamblador();

    private static final String NOMBRE_ARCHIVO_ASM = "codigo_vpr"; 
    private StringBuilder dataSection;
    private StringBuilder codeSection;
    private Map<String, String> tablaSimbolos; 
    private int contadorStrings = 0; // Para generar nombres únicos para literales

    public GeneradorEnsamblador() {
        limpiar();
    }
    
    public void limpiar() {
        this.dataSection = new StringBuilder();
        this.codeSection = new StringBuilder();
        this.tablaSimbolos = new HashMap<>();
        this.contadorStrings = 0;
        inicializarSecciones();
    }

    private void inicializarSecciones() {
        dataSection.append(".data\n");
        dataSection.append("    formato_num DB \"%d\", 0Ah, 0\n"); 
        dataSection.append("    formato_string DB \"%s\", 0Ah, 0\n"); 
        
        codeSection.append(".code\n");
        codeSection.append("main PROC\n");
    }
    
    public void generarPie() {
        // Método placeholder por si se necesita cerrar estructuras antes del final
    }

    // --- VARIABLES ---
    public void declararVariable(String nombre) {
        if (!tablaSimbolos.containsKey(nombre)) {
            tablaSimbolos.put(nombre, "int");
            dataSection.append("    ").append(nombre).append(" DD 0\n");
        }
    }

    public void declararCadena(String nombre, String valorConComillas) {
        if (!tablaSimbolos.containsKey(nombre)) {
            tablaSimbolos.put(nombre, "string");
            String valorLimpio = valorConComillas.replace("\"", "");
            dataSection.append("    ").append(nombre).append(" DB \"").append(valorLimpio).append("\", 0\n");
        }
    }

    // --- LÓGICA DE PILA (Matemáticas) ---
    public void cargarValorEnPila(String valor) {
        codeSection.append("    ; Push ").append(valor).append("\n");
        // Si es un número o variable, lo movemos a EAX y luego PUSH
        codeSection.append("    MOV EAX, ").append(valor).append("\n");
        codeSection.append("    PUSH EAX\n");
    }

    public void operacionPila(String operador) {
        codeSection.append("    ; Operacion ").append(operador).append("\n");
        codeSection.append("    POP EBX\n"); // Operando 2 (Derecha)
        codeSection.append("    POP EAX\n"); // Operando 1 (Izquierda)
        
        switch (operador) {
            case "+": codeSection.append("    ADD EAX, EBX\n"); break;
            case "-": codeSection.append("    SUB EAX, EBX\n"); break;
            case "*": codeSection.append("    IMUL EAX, EBX\n"); break;
            case "/":
                codeSection.append("    CDQ\n");
                codeSection.append("    IDIV EBX\n");
                break;
        }
        codeSection.append("    PUSH EAX\n");
    }

    public void asignarDesdePila(String variable) {
        codeSection.append("    ; Asignar a ").append(variable).append("\n");
        codeSection.append("    POP EAX\n");
        codeSection.append("    MOV ").append(variable).append(", EAX\n");
    }
    
    public void asignarValor(String variable, String valor) {
         if(valor.equals("EAX")) {
             codeSection.append("    MOV ").append(variable).append(", EAX\n");
         } else {
             codeSection.append("    MOV EAX, ").append(valor).append("\n");
             codeSection.append("    MOV ").append(variable).append(", EAX\n");
         }
    }

    // --- IMPRESIÓN MEJORADA ---
    public void imprimirVariable(String idOValor) {
        codeSection.append("    ; Imprimir ").append(idOValor).append("\n");
        
        // Verificamos si es una variable registrada
        if (tablaSimbolos.containsKey(idOValor)) {
            String tipo = tablaSimbolos.get(idOValor);
            if (tipo.equals("string")) {
                codeSection.append("    invoke crt_printf, addr formato_string, addr ").append(idOValor).append("\n");
            } else {
                codeSection.append("    invoke crt_printf, addr formato_num, ").append(idOValor).append("\n");
            }
        } else {
            // Si NO está en la tabla, asumimos que es un literal (número o string directo)
            if (idOValor.startsWith("\"")) {
                // Es un string literal "Hola". Creamos una variable temporal para él.
                String nombreTemp = "str_temp_" + contadorStrings++;
                declararCadena(nombreTemp, idOValor);
                codeSection.append("    invoke crt_printf, addr formato_string, addr ").append(nombreTemp).append("\n");
            } else {
                // Asumimos que es un número literal
                codeSection.append("    invoke crt_printf, addr formato_num, ").append(idOValor).append("\n");
            }
        }
    }

    // --- SALIDA ---
    public String obtenerCodigoCompleto() {
        StringBuilder codigoFinal = new StringBuilder();
        codigoFinal.append(".386\n.model flat, stdcall\noption casemap :none\n");
        codigoFinal.append("include \\masm32\\include\\windows.inc\n");
        codigoFinal.append("include \\masm32\\include\\kernel32.inc\n");
        codigoFinal.append("include \\masm32\\include\\msvcrt.inc\n");
        codigoFinal.append("includelib \\masm32\\lib\\kernel32.lib\n");
        codigoFinal.append("includelib \\masm32\\lib\\msvcrt.lib\n\n");

        codigoFinal.append(dataSection.toString());
        codigoFinal.append(codeSection.toString());
        
        codigoFinal.append("    invoke ExitProcess, 0\n");
        codigoFinal.append("main ENDP\nEND main\n");
        return codigoFinal.toString();
    }
    
    public static String obtenerNombreArchivoASM() { return NOMBRE_ARCHIVO_ASM; }
}