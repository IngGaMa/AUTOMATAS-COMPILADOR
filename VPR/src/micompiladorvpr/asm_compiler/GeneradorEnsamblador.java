package micompiladorvpr.asm_compiler;

import java.util.HashMap;
import java.util.Map;

public class GeneradorEnsamblador {

    // Instancia estática para acceder desde el Parser
    public static GeneradorEnsamblador instancia = new GeneradorEnsamblador();

    private static final String NOMBRE_ARCHIVO_ASM = "codigo_vpr"; 
    private StringBuilder dataSection;
    private StringBuilder codeSection;
    private Map<String, String> tablaSimbolos; 

    // Constructor
    public GeneradorEnsamblador() {
        this.dataSection = new StringBuilder();
        this.codeSection = new StringBuilder();
        this.tablaSimbolos = new HashMap<>();
        inicializarSecciones();
    }
    
    // Método para reiniciar el generador en cada compilación
    public void limpiar() {
        this.dataSection = new StringBuilder();
        this.codeSection = new StringBuilder();
        this.tablaSimbolos.clear();
        inicializarSecciones();
    }

    private void inicializarSecciones() {
        dataSection.append(".data\n");
        dataSection.append("    formato_num DB \"%d\", 0Ah, 0\n"); 
        
        codeSection.append(".code\n");
        codeSection.append("main PROC\n");
    }

    // --- MÉTODOS DE TRADUCCIÓN ---

    public void declararVariable(String nombre) {
        if (!tablaSimbolos.containsKey(nombre)) {
            tablaSimbolos.put(nombre, "int");
            dataSection.append("    ").append(nombre).append(" DD 0\n");
            System.out.println("LOG: Variable declarada -> " + nombre);
        }
    }

    public void asignarValor(String variable, String valor) {
        codeSection.append("    ; Asignacion: ").append(variable).append(" = ").append(valor).append("\n");
        codeSection.append("    MOV EAX, ").append(valor).append("\n");
        codeSection.append("    MOV ").append(variable).append(", EAX\n");
    }
    
    // Operaciones Aritméticas (Variable Destino = Operando1 OP Operando2)
    
    public void operacionAritmetica(String destino, String op1, String operador, String op2) {
        codeSection.append("    ; Operacion: ").append(destino).append(" = ").append(op1).append(" ").append(operador).append(" ").append(op2).append("\n");
        
        codeSection.append("    MOV EAX, ").append(op1).append("\n"); // Cargar op1 en Acumulador
        
        switch (operador) {
            case "+":
                codeSection.append("    ADD EAX, ").append(op2).append("\n");
                break;
            case "-":
                codeSection.append("    SUB EAX, ").append(op2).append("\n");
                break;
            case "*":
                codeSection.append("    IMUL EAX, ").append(op2).append("\n"); // Multiplicación con signo
                break;
            case "/":
                // La división es especial en ASM
                codeSection.append("    MOV EBX, ").append(op2).append("\n"); // Mover divisor a EBX
                codeSection.append("    CDQ\n"); // Extender signo de EAX a EDX:EAX
                codeSection.append("    IDIV EBX\n"); // Dividir EAX por EBX
                break;
        }
        
        codeSection.append("    MOV ").append(destino).append(", EAX\n"); // Guardar resultado
    }

    public void imprimirVariable(String variable) {
        codeSection.append("    ; Imprimir ").append(variable).append("\n");
        codeSection.append("    invoke crt_printf, addr formato_num, ").append(variable).append("\n");
    }

    // --- GENERACIÓN FINAL ---

    public String obtenerCodigoCompleto() {
        StringBuilder codigoFinal = new StringBuilder();
        
        codigoFinal.append(".386\n");
        codigoFinal.append(".model flat, stdcall\n");
        codigoFinal.append("option casemap :none\n");
        
        codigoFinal.append("include \\masm32\\include\\windows.inc\n");
        codigoFinal.append("include \\masm32\\include\\kernel32.inc\n");
        codigoFinal.append("include \\masm32\\include\\msvcrt.inc\n");
        codigoFinal.append("includelib \\masm32\\lib\\kernel32.lib\n");
        codigoFinal.append("includelib \\masm32\\lib\\msvcrt.lib\n\n");

        codigoFinal.append(dataSection.toString());
        codigoFinal.append(codeSection.toString());
        
        codigoFinal.append("    invoke ExitProcess, 0\n");
        codigoFinal.append("main ENDP\n");
        codigoFinal.append("END main\n");

        return codigoFinal.toString();
    }
    
    public static String obtenerNombreArchivoASM() {
        return NOMBRE_ARCHIVO_ASM;
    }
}