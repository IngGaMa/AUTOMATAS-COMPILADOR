package micompiladorvpr.asm_compiler;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

public class GeneradorEnsamblador {

    public static GeneradorEnsamblador instancia = new GeneradorEnsamblador();

    private static final String NOMBRE_ARCHIVO_ASM = "codigo_vpr"; 
    private StringBuilder dataSection;
    private StringBuilder codeSection;
    private Map<String, String> tablaSimbolos; 
    private int contadorStrings = 0; 
    
    // Control de Flujo
    private int contadorEtiquetas = 0;
    private Stack<String> pilaEtiquetas = new Stack<>();
    private Stack<String> pilaBreaks = new Stack<>();

    public GeneradorEnsamblador() {
        limpiar();
    }
    
    public void limpiar() {
        this.dataSection = new StringBuilder();
        this.codeSection = new StringBuilder();
        this.tablaSimbolos = new HashMap<>();
        this.contadorStrings = 0;
        this.contadorEtiquetas = 0;
        this.pilaEtiquetas.clear();
        this.pilaBreaks.clear();
        inicializarSecciones();
    }

    private void inicializarSecciones() {
        dataSection.append(".data\n");
        dataSection.append("    formato_num DB \"%d\", 0\n"); 
        dataSection.append("    formato_string DB \"%s\", 0\n"); 
        dataSection.append("    formato_float DB \"%d.%02d\", 0\n");
        dataSection.append("    salto_linea DB 0Ah, 0\n");
        
        // Formatos para SCANF
        dataSection.append("    fmt_scan_int DB \"%d\", 0\n");
        dataSection.append("    fmt_scan_str DB \"%s\", 0\n");
        dataSection.append("    cmd_cls DB \"cls\", 0\n"); 
        
        // Señales de Input
        dataSection.append("    signal_input_int DB \"___INPUT_INT___ \", 0\n");
        dataSection.append("    signal_input_str DB \"___INPUT_STR___ \", 0\n");
        dataSection.append("    signal_input_float DB \"___INPUT_FLOAT___ \", 0\n");
        
        codeSection.append(".code\n");
        codeSection.append("main PROC\n");
    }
    
    public void generarPie() { }

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
            if (valorLimpio.isEmpty()) dataSection.append("    ").append(nombre).append(" DB 0\n");
            else dataSection.append("    ").append(nombre).append(" DB \"").append(valorLimpio).append("\", 0\n");
            // Buffer para lectura
            dataSection.append("    ").append(nombre).append("_buffer DB 256 dup(0)\n");
        }
    }
    
    public void declararFloat(String nombre, String valor) {
        if (!tablaSimbolos.containsKey(nombre)) {
            tablaSimbolos.put(nombre, "float");
            String[] partes = valor.split("\\.");
            String ent = partes[0];
            String dec = (partes.length > 1) ? partes[1] : "00";
            if (dec.length() == 1) dec += "0"; 
            if (dec.length() > 2) dec = dec.substring(0, 2); 
            dataSection.append("    ").append(nombre).append("_ent DD ").append(ent).append("\n");
            dataSection.append("    ").append(nombre).append("_dec DD ").append(dec).append("\n");
        }
    }

    public void declararChar(String nombre, String valor) {
        if (!tablaSimbolos.containsKey(nombre)) {
            tablaSimbolos.put(nombre, "char");
            String valLimpio = valor.replace("\"", "").replace("'", "");
            dataSection.append("    ").append(nombre).append(" DB \"").append(valLimpio).append("\", 0\n");
        }
    }

    public void declararBool(String nombre, String valorTexto) {
        if (!tablaSimbolos.containsKey(nombre)) {
            tablaSimbolos.put(nombre, "bool");
            String valNum = (valorTexto != null && valorTexto.equals("true")) ? "1" : "0";
            dataSection.append("    ").append(nombre).append(" DD ").append(valNum).append("\n");
        }
    }

    // --- MATEMÁTICAS ---
    public void cargarValorEnPila(String valor) {
        if (tablaSimbolos.containsKey(valor)) {
            String tipo = tablaSimbolos.get(valor);
            if (tipo.equals("float")) {
                codeSection.append("    MOV EAX, ").append(valor).append("_ent\n");
                codeSection.append("    PUSH EAX\n");
            } else {
                codeSection.append("    MOV EAX, ").append(valor).append("\n");
                codeSection.append("    PUSH EAX\n");
            }
        } else {
            if (valor.contains(".")) {
                String entero = valor.split("\\.")[0];
                codeSection.append("    MOV EAX, ").append(entero).append("\n");
                codeSection.append("    PUSH EAX\n");
            } else {
                codeSection.append("    MOV EAX, ").append(valor).append("\n");
                codeSection.append("    PUSH EAX\n");
            }
        }
    }

    public void operacionPila(String operador) {
        codeSection.append("    POP EBX\n");
        codeSection.append("    POP EAX\n");
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
        codeSection.append("    POP EAX\n");
        if (tablaSimbolos.containsKey(variable)) {
             String tipo = tablaSimbolos.get(variable);
             if (tipo.equals("float")) {
                 codeSection.append("    MOV ").append(variable).append("_ent, EAX\n");
                 codeSection.append("    MOV ").append(variable).append("_dec, 0\n");
             } else {
                 codeSection.append("    MOV ").append(variable).append(", EAX\n");
             }
        }
    }

    public void incrementarVariable(String variable) {
        codeSection.append("    INC ").append(variable).append("\n");
    }

    // --- CONTROL DE FLUJO ---

    public void comparacion(String operador) {
        codeSection.append("    POP EBX\n");
        codeSection.append("    POP EAX\n");
        codeSection.append("    CMP EAX, EBX\n");
        
        String etiquetaFalso = "L" + contadorEtiquetas++;
        pilaEtiquetas.push(etiquetaFalso);
        
        switch (operador) {
            case "<":  codeSection.append("    JGE ").append(etiquetaFalso).append("\n"); break;
            case ">":  codeSection.append("    JLE ").append(etiquetaFalso).append("\n"); break;
            case "<=": codeSection.append("    JG ").append(etiquetaFalso).append("\n"); break;
            case ">=": codeSection.append("    JL ").append(etiquetaFalso).append("\n"); break;
            case "==": codeSection.append("    JNE ").append(etiquetaFalso).append("\n"); break;
            case "!=": codeSection.append("    JE ").append(etiquetaFalso).append("\n"); break;
        }
    }
    
    public void elseIf() {
        String etiquetaFalso = pilaEtiquetas.pop();
        String etiquetaSalida = "L" + contadorEtiquetas++;
        codeSection.append("    JMP ").append(etiquetaSalida).append("\n"); 
        codeSection.append(etiquetaFalso).append(":\n"); 
        pilaEtiquetas.push(etiquetaSalida);
    }
    
    public void finIf() {
        if (!pilaEtiquetas.isEmpty()) {
            String etiqueta = pilaEtiquetas.pop();
            codeSection.append(etiqueta).append(":\n");
        }
    }

    // WHILE
    public void iniciarWhile() {
        String lblInicio = "L" + contadorEtiquetas++;
        String lblSalida = "L" + contadorEtiquetas++; 
        
        codeSection.append(lblInicio).append(":\n");
        
        pilaEtiquetas.push(lblSalida); 
        pilaEtiquetas.push(lblInicio); 
        pilaBreaks.push(lblSalida);
    }
    
    public void terminarWhile() {
        if (pilaEtiquetas.size() >= 2) {
            String lblSalida = pilaEtiquetas.pop();
            String lblInicio = pilaEtiquetas.pop();
            
            codeSection.append("    JMP ").append(lblInicio).append("\n");
            codeSection.append(lblSalida).append(":\n");
            
            if (!pilaBreaks.isEmpty()) pilaBreaks.pop(); 
        }
    }

    // DO-WHILE
    public void iniciarDo() {
        String lblInicio = "L" + contadorEtiquetas++;
        codeSection.append(lblInicio).append(":\n");
        pilaEtiquetas.push(lblInicio);
        
        String lblSalida = "L" + contadorEtiquetas++;
        pilaBreaks.push(lblSalida);
    }

    public void terminarDo() {
        // En do-while, la comparación genera un salto a L_Falso si la condición NO se cumple.
        // Pero en do-while queremos repetir si la condición SÍ se cumple.
        // La lógica aquí asume que 'comparacion' genera un salto para SALIR del bucle si es falso.
        // Entonces, si no salta (condición verdadera), debemos volver al inicio.
        
        String lblFalso = pilaEtiquetas.pop(); 
        String lblInicio = pilaEtiquetas.pop();
        
        // Si la condición falló, saltó a lblFalso.
        // Si la condición se cumplió, cae aquí -> JMP Inicio.
        codeSection.append("    JMP ").append(lblInicio).append("\n");
        codeSection.append(lblFalso).append(":\n");
        
        String lblBreak = pilaBreaks.pop();
        codeSection.append(lblBreak).append(":\n"); // Etiqueta para break
    }
    
    // BREAK
    public void generarBreak() {
        if (!pilaBreaks.isEmpty()) {
            String lblSalida = pilaBreaks.peek();
            codeSection.append("    JMP ").append(lblSalida).append("\n");
        }
    }

    // SWITCH
    public void iniciarSwitch() {
        String lblSalida = "L" + contadorEtiquetas++;
        pilaBreaks.push(lblSalida); 
    }
    
    public void terminarSwitch() {
        if (!pilaBreaks.isEmpty()) {
            String lblSalida = pilaBreaks.pop();
            codeSection.append(lblSalida).append(":\n");
        }
    }

    // --- IMPRESIÓN ---
    public void imprimirVariable(String variable) {
        generarImpresion(variable, false);
    }
    
    public void imprimirVariableLn(String variable) {
        generarImpresion(variable, true);
    }
    
    private void generarImpresion(String variable, boolean salto) {
        if (variable.startsWith("\"")) { 
            String temp = "str_lit_" + contadorStrings++;
            declararCadena(temp, variable);
            codeSection.append("    invoke crt_printf, addr formato_string, addr ").append(temp).append("\n");
        } else {
            String tipo = tablaSimbolos.getOrDefault(variable, "int");
            if (tipo.equals("float")) {
                codeSection.append("    invoke crt_printf, addr formato_float, ").append(variable).append("_ent, ").append(variable).append("_dec\n");
            } else if (tipo.equals("string")) {
                 // Verificar si tiene buffer
                 codeSection.append("    invoke crt_printf, addr formato_string, addr ").append(variable).append("\n");
            } else if (tipo.equals("char")) {
                 codeSection.append("    invoke crt_printf, addr formato_string, addr ").append(variable).append("\n");
            } else {
                codeSection.append("    invoke crt_printf, addr formato_num, ").append(variable).append("\n");
            }
        }
        
        if (salto) {
            codeSection.append("    invoke crt_printf, addr salto_linea\n");
        }
    }

    // --- LECTURA (SCANF) ---
    public void leerVariable(String variable) {
        if (!tablaSimbolos.containsKey(variable)) return;
        String tipo = tablaSimbolos.get(variable);
        
        if (tipo.equals("int")) {
            codeSection.append("    invoke crt_printf, addr signal_input_int\n"); 
            codeSection.append("    invoke crt_scanf, addr fmt_scan_int, addr ").append(variable).append("\n");
        }
        else if (tipo.equals("string")) {
            codeSection.append("    invoke crt_printf, addr signal_input_str\n");
            codeSection.append("    invoke crt_scanf, addr fmt_scan_str, addr ").append(variable).append("_buffer\n");
        }
        else if (tipo.equals("float")) {
            codeSection.append("    invoke crt_printf, addr signal_input_float\n");
            // Truco: leer dos enteros
            // OJO: El formato en dataSection debe coincidir: "%d %d" para dos enteros
            codeSection.append("    invoke crt_scanf, addr fmt_scan_int, addr ").append(variable).append("_ent\n"); // Leer parte entera
             // Simplificación: leer solo entero por ahora si scanf complejo falla
        }
    }

    public void generarClrscr() {
        codeSection.append("    invoke crt_system, addr cmd_cls\n");
    }

    public String obtenerCodigoCompleto() {
        StringBuilder finalCode = new StringBuilder();
        finalCode.append(".386\n.model flat, stdcall\noption casemap :none\n");
        finalCode.append("include \\masm32\\include\\windows.inc\n");
        finalCode.append("include \\masm32\\include\\kernel32.inc\n");
        finalCode.append("include \\masm32\\include\\msvcrt.inc\n");
        finalCode.append("includelib \\masm32\\lib\\kernel32.lib\n");
        finalCode.append("includelib \\masm32\\lib\\msvcrt.lib\n\n");
        finalCode.append(dataSection).append(codeSection);
        finalCode.append("    invoke ExitProcess, 0\nmain ENDP\nEND main\n");
        return finalCode.toString();
    }
    
    public static String obtenerNombreArchivoASM() { return NOMBRE_ARCHIVO_ASM; }
}