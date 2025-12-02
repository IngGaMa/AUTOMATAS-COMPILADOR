package micompiladorvpr.main;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Clase principal para la generación de los analizadores Léxico (JFlex) 
 * y Sintáctico (Java-CUP).
 * Debe ejecutarse antes de compilar el proyecto.
 */
public class micompiladorvpr { 
   
    public static void main(String[] args) throws Exception {
        // Rutas de entrada para los archivos de especificación (.flex y .cup)
        String rutaLexerSimple = "src/micompiladorvpr/lexer/Lexer.flex";
        String rutaLexerCup = "src/micompiladorvpr/lexer/LexerCup.flex";
        
        // La ruta de CUP requiere un arreglo de Strings: -parser NombreClaseParser RutaArchivoCup
        String[] rutaParserCup = {"-parser", "Sintax", "src/micompiladorvpr/parser/Sintax.cup"};
        
        generarAnalizadores(rutaLexerSimple, rutaLexerCup, rutaParserCup);
    }
    
    /**
     * Genera los archivos Java de los analizadores a partir de sus especificaciones.
     * @param ruta1 Ruta al Lexer simple.
     * @param ruta2 Ruta al Lexer para CUP.
     * @param rutaS Rutas para el Parser de CUP.
     */
    public static void generarAnalizadores(String ruta1, String ruta2, String[] rutaS) throws IOException, Exception{
        File archivo = null;
        
        // Rutas de salida (donde se generan los archivos .java) para poder modificarlas
        String rutaSalidaLexer = "src/micompiladorvpr/lexer/Lexer.java";
        String rutaSalidaLexerCup = "src/micompiladorvpr/lexer/LexerCup.java";
        
        // 1. Generar Lexer Simple (Lexer.java)
        archivo = new File(ruta1);
        JFlex.Main.generate(archivo);
        // Modifica Lexer.java para ser public
        hacerClasePublica(rutaSalidaLexer, "Lexer"); 
        
        // 2. Generar Lexer para CUP (LexerCup.java)
        archivo = new File(ruta2);
        JFlex.Main.generate(archivo);
        // Modifica LexerCup.java para ser public Y agrega el import de sym
        hacerClasePublica(rutaSalidaLexerCup, "LexerCup"); 
        
        // 3. Generar Parser (Sintax.java y sym.java)
        java_cup.Main.main(rutaS);

        // --- MANEJO Y MOVIMIENTO DE ARCHIVOS GENERADOS POR CUP ---
        // CUP genera Sintax.java y sym.java en la raíz del proyecto.
        // Debemos moverlos a src/micompiladorvpr/parser/

        // 3.1. Mover sym.java al paquete 'parser'
        Path rutaDestinoSym = Paths.get("src/micompiladorvpr/parser/sym.java");
        if (Files.exists(rutaDestinoSym)) {
            Files.delete(rutaDestinoSym);
        }
        Files.move(
                Paths.get("sym.java"), 
                rutaDestinoSym
        );
        
        // 3.2. Mover Sintax.java al paquete 'parser'
        Path rutaDestinoSin = Paths.get("src/micompiladorvpr/parser/Sintax.java");
        if (Files.exists(rutaDestinoSin)) {
            Files.delete(rutaDestinoSin);
        }
        Files.move(
                Paths.get("Sintax.java"), 
                rutaDestinoSin
        );
        
        System.out.println("✅ Analizadores generados, corregidos y movidos correctamente.");
    }
    
    /**
     * Lee un archivo generado y lo sobrescribe, haciendo la clase y el constructor public.
     * También inyecta imports necesarios si es LexerCup.
     */
    private static void hacerClasePublica(String rutaArchivo, String nombreClase) {
        try {
            Path path = Paths.get(rutaArchivo);
            String contenido = new String(Files.readAllBytes(path));
            
            // 1. Hacer la CLASE pública: Busca "class NombreClase"
            String regexClase = "class " + nombreClase;
            contenido = contenido.replace(regexClase, "public " + regexClase);
            
            // 2. Hacer el CONSTRUCTOR(Reader) público: Busca "NombreClase("
            String regexConstructor = nombreClase + "\\("; 
            contenido = contenido.replaceFirst(regexConstructor, "public " + nombreClase + "(");

            // --- 3. LÓGICA ESPECÍFICA PARA LexerCup (Agregar Import) ---
            if (nombreClase.equals("LexerCup")) {
                // Buscamos dónde termina la declaración del paquete para insertar el import
                String marcadorPaquete = "package micompiladorvpr.lexer;";
                
                if (contenido.contains(marcadorPaquete) && !contenido.contains("import micompiladorvpr.parser.sym;")) {
                    // Insertamos el import justo después del package
                    String importInyectado = marcadorPaquete + "\nimport micompiladorvpr.parser.sym;";
                    contenido = contenido.replace(marcadorPaquete, importInyectado);
                    System.out.println("   -> Import 'micompiladorvpr.parser.sym' inyectado en LexerCup.");
                }
            }

            // 4. Sobrescribir el archivo con los cambios
            Files.write(path, contenido.getBytes());
            
            System.out.println("   -> Clase y constructor " + nombreClase + " actualizados a public.");

        } catch (IOException e) {
            System.err.println("Error al modificar el archivo " + nombreClase + ": " + e.getMessage());
        }
    }
}