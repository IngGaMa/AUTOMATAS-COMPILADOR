package micompiladorvpr.main;

import java.awt.Color;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Files;
import java.util.logging.Level;
import java.util.logging.Logger;
import java_cup.runtime.Symbol;
import javax.swing.JFileChooser;
import static java.awt.Frame.MAXIMIZED_BOTH;

// Importaciones de Clases Modulares:
import micompiladorvpr.lexer.Lexer;      // Lexer simple
import micompiladorvpr.lexer.Tokens;     // Enum de Tokens
import micompiladorvpr.lexer.LexerCup;   // Lexer para CUP (necesario solo en la acción de Sintax)
import micompiladorvpr.parser.Sintax;    // Parser generado
// Nuevas importaciones para la compilación a ensamblador:
import micompiladorvpr.asm_compiler.GeneradorEnsamblador;
import micompiladorvpr.asm_compiler.ManejadorEjecucion;

public class VPR extends javax.swing.JFrame {

    // Variable para recordar el archivo abierto actualmente
    private File archivoActual = null;
    
    public VPR() {
        initComponents();
        this.setLocationRelativeTo(null); // Esto centra la ventana
    }

    // Método auxiliar para escribir el texto en el disco duro
    private void guardarArchivoEnDisco(File archivo, String contenido) {
        try {
            java.io.PrintWriter writer = new java.io.PrintWriter(archivo);
            writer.print(contenido);
            writer.close();
            
            // Actualizamos la referencia y avisamos al usuario
            archivoActual = archivo;
            this.setTitle("Mi Compilador VPR - " + archivo.getName());
            javax.swing.JOptionPane.showMessageDialog(this, "Archivo guardado exitosamente.");
            
        } catch (FileNotFoundException e) {
            javax.swing.JOptionPane.showMessageDialog(this, "Error al guardar el archivo: " + e.getMessage());
        }
    }
    
// --- ANALIZAR LÉXICO (CORREGIDO) ---
// Método corregido para mostrar los tokens en la interfaz
    private void analizarLexico() throws IOException{
        int cont = 1;
        
        String expr = (String) txtResultado.getText();
        Lexer lexer = new Lexer(new StringReader(expr));
        String resultado = "LINEA " + cont + "\t\tSIMBOLO\n";
        while (true) {
            Tokens token = lexer.yylex();
            if (token == null) {
                txtAnalizarLex.setText(resultado);
                return;
            }
            switch (token) {
                case Linea:
                    cont++;
                    resultado += "LINEA " + cont + "\n";
                    break;
                case Comillas:
                    resultado += "  <Comillas>\t\t" + lexer.lexeme + "\n";
                    break;
                // --- CAMBIOS CLAVE AQUI ---
                case T_string: // Reemplaza al antiguo 'Cadena' o 'T_dato' para strings
                    resultado += "  <Tipo de dato String>\t" + lexer.lexeme + "\n";
                    break;
                case T_int: // Reemplaza al antiguo 'T_dato' para enteros
                    resultado += "  <Tipo de dato Entero>\t" + lexer.lexeme + "\n";
                    break;
                case T_float: // Nuevo tipo
                    resultado += "  <Tipo de dato Flotante>\t" + lexer.lexeme + "\n";
                    break;
                case T_bool: // Nuevo tipo
                    resultado += "  <Tipo de dato Booleano>\t" + lexer.lexeme + "\n";
                    break;
                case Texto: // Nuevo token para cadenas completas "Hola Mundo"
                    resultado += "  <Cadena de Texto>\t" + lexer.lexeme + "\n";
                    break;
                // --------------------------
                case If:
                    resultado += "  <Reservada If>\t" + lexer.lexeme + "\n";
                    break;
                case Else:
                    resultado += "  <Reservada Else>\t" + lexer.lexeme + "\n";
                    break;
                case Do:
                    resultado += "  <Reservada Do>\t" + lexer.lexeme + "\n";
                    break;
                case While:
                    resultado += "  <Reservada While>\t" + lexer.lexeme + "\n";
                    break;
                case For:
                    resultado += "  <Reservada For>\t" + lexer.lexeme + "\n";
                    break;
                case Igual:
                    resultado += "  <Operador Igual>\t" + lexer.lexeme + "\n";
                    break;
                case Suma:
                    resultado += "  <Operador Suma>\t" + lexer.lexeme + "\n";
                    break;
                case Resta:
                    resultado += "  <Operador Resta>\t" + lexer.lexeme + "\n";
                    break;
                case Multiplicacion:
                    resultado += "  <Operador Multiplicacion>\t" + lexer.lexeme + "\n";
                    break;
                case Division:
                    resultado += "  <Operador Division>\t" + lexer.lexeme + "\n";
                    break;
                case Op_logico:
                    resultado += "  <Operador Logico>\t" + lexer.lexeme + "\n";
                    break;
                case Op_incremento:
                    resultado += "  <Operador Incremento>\t" + lexer.lexeme + "\n";
                    break;
                case Op_relacional:
                    resultado += "  <Operador Relacional>\t" + lexer.lexeme + "\n";
                    break;
                case Op_atribucion:
                    resultado += "  <Operador Atribucion>\t" + lexer.lexeme + "\n";
                    break;
                case Op_booleano:
                    resultado += "  <Valor Booleano>\t" + lexer.lexeme + "\n";
                    break;
                case Parentesis_a:
                    resultado += "  <Parentesis Apertura>\t" + lexer.lexeme + "\n";
                    break;
                case Parentesis_c:
                    resultado += "  <Parentesis Cierre>\t" + lexer.lexeme + "\n";
                    break;
                case Llave_a:
                    resultado += "  <Llave Apertura>\t" + lexer.lexeme + "\n";
                    break;
                case Llave_c:
                    resultado += "  <Llave Cierre>\t" + lexer.lexeme + "\n";
                    break;
                case Corchete_a:
                    resultado += "  <Corchete Apertura>\t" + lexer.lexeme + "\n";
                    break;
                case Corchete_c:
                    resultado += "  <Corchete Cierre>\t" + lexer.lexeme + "\n";
                    break;
                case Main:
                    resultado += "  <Reservada Main>\t" + lexer.lexeme + "\n";
                    break;
                case P_coma:
                    resultado += "  <Punto y Coma>\t" + lexer.lexeme + "\n";
                    break;
                case Identificador:
                    resultado += "  <Identificador>\t" + lexer.lexeme + "\n";
                    break;
                case Numero:
                    resultado += "  <Numero>\t\t" + lexer.lexeme + "\n";
                    break;
                case Imprime:
                    resultado += "  <Funcion Imprimir>\t" + lexer.lexeme + "\n";
                    break;
                case ERROR:
                    resultado += "  <Simbolo no definido>\n";
                    break;
                default:
                    resultado += "  < " + lexer.lexeme + " >\n";
                    break;
            }
        }
    }
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        txtAnalizarSin = new javax.swing.JTextArea();
        jScrollPane1 = new javax.swing.JScrollPane();
        txtAnalizarLex = new javax.swing.JTextArea();
        jScrollPane2 = new javax.swing.JScrollPane();
        txtResultado = new javax.swing.JTextArea();
        jLabel1 = new javax.swing.JLabel();
        btnArchivo = new javax.swing.JButton();
        btnAnalizarLex = new javax.swing.JButton();
        btnAnalizarSin = new javax.swing.JButton();
        btnEjecutar = new javax.swing.JButton();
        btnGuardar = new javax.swing.JButton();
        btnGuardarComo = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(0, 0, 51));

        txtAnalizarSin.setColumns(20);
        txtAnalizarSin.setRows(5);
        jScrollPane3.setViewportView(txtAnalizarSin);

        txtAnalizarLex.setColumns(20);
        txtAnalizarLex.setRows(5);
        jScrollPane1.setViewportView(txtAnalizarLex);

        txtResultado.setColumns(20);
        txtResultado.setRows(5);
        jScrollPane2.setViewportView(txtResultado);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addGap(36, 36, 36)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jScrollPane3)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 916, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 55, Short.MAX_VALUE)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 254, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(24, 24, 24))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(22, 22, 22)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 446, Short.MAX_VALUE)
                    .addComponent(jScrollPane1))
                .addGap(43, 43, 43)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 61, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(56, Short.MAX_VALUE))
        );

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 36)); // NOI18N
        jLabel1.setText("VPR");

        btnArchivo.setText("ABRIR ARCHIVO");
        btnArchivo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnArchivoActionPerformed(evt);
            }
        });

        btnAnalizarLex.setText("ANALIZAR LEXICO");
        btnAnalizarLex.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAnalizarLexActionPerformed(evt);
            }
        });

        btnAnalizarSin.setText("COMPILAR");
        btnAnalizarSin.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAnalizarSinActionPerformed(evt);
            }
        });

        btnEjecutar.setText("EJECUTAR");
        btnEjecutar.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnEjecutarMouseClicked(evt);
            }
        });

        btnGuardar.setText("GUARDAR");
        btnGuardar.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnGuardarMouseClicked(evt);
            }
        });
        btnGuardar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGuardarActionPerformed(evt);
            }
        });

        btnGuardarComo.setText("GUARDAR COMO");
        btnGuardarComo.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnGuardarComoMouseClicked(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addGap(37, 37, 37)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(btnArchivo)
                        .addGap(18, 18, 18)
                        .addComponent(btnGuardar)
                        .addGap(18, 18, 18)
                        .addComponent(btnGuardarComo)
                        .addGap(18, 18, 18)
                        .addComponent(btnAnalizarLex)
                        .addGap(18, 18, 18)
                        .addComponent(btnAnalizarSin)
                        .addGap(18, 18, 18)
                        .addComponent(btnEjecutar))
                    .addComponent(jLabel1))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnArchivo)
                    .addComponent(btnAnalizarLex)
                    .addComponent(btnAnalizarSin)
                    .addComponent(btnEjecutar)
                    .addComponent(btnGuardar)
                    .addComponent(btnGuardarComo))
                .addGap(9, 9, 9)
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnArchivoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnArchivoActionPerformed
        JFileChooser chooser = new JFileChooser();
        chooser.showOpenDialog(null);
        
        if (chooser.getSelectedFile() != null) {
            // Guardamos la referencia del archivo abierto
            archivoActual = chooser.getSelectedFile(); 
            
            try {
                String ST = new String(Files.readAllBytes(archivoActual.toPath()));
                txtResultado.setText(ST);
                // Opcional: Poner el nombre del archivo en el título de la ventana
                this.setTitle("Mi Compilador VPR - " + archivoActual.getName());
            } catch (FileNotFoundException ex) {
                Logger.getLogger(VPR.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(VPR.class.getName()).log(Level.SEVERE,null,ex);
            }
        }
    }//GEN-LAST:event_btnArchivoActionPerformed

    private void btnAnalizarLexActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAnalizarLexActionPerformed
        try {
            analizarLexico();
        } catch (IOException ex) {
            Logger.getLogger(micompiladorvpr.class.getName()).log(Level.SEVERE,null,ex);
        }
    }//GEN-LAST:event_btnAnalizarLexActionPerformed

    private void btnAnalizarSinActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAnalizarSinActionPerformed
        String ST = txtResultado.getText();
        
        // Limpiamos el generador por seguridad, aunque no usemos el resultado aún
        GeneradorEnsamblador.instancia.limpiar();
        
        Sintax s = new Sintax(new LexerCup(new StringReader(ST)));
        
        try {
            // Solo parseamos. Si hay error, salta al catch.
            s.parse();
            
            // Si llega aquí, es CORRECTO
            txtAnalizarSin.setText("Análisis Sintáctico Correcto. No se encontraron errores.");
            txtAnalizarSin.setForeground(new Color(25, 111, 61));
            
            // ¡AQUÍ YA NO LLAMAMOS A MANEJADOR DE EJECUCIÓN!
            
        } catch (Exception ex) {
            Symbol sym = s.getS();
            if (sym != null) {
                txtAnalizarSin.setText("Error de sintaxis. Línea: " + (sym.right + 1) + 
                                       " Columna: " + (sym.left + 1) + 
                                       ", Texto: \"" + sym.value + "\"");
            } else {
                txtAnalizarSin.setText("Error de sintaxis: " + ex.getMessage());
            }
            txtAnalizarSin.setForeground(Color.red);
        }
    }//GEN-LAST:event_btnAnalizarSinActionPerformed

    private void btnEjecutarMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnEjecutarMouseClicked
        // 1. Obtener el código fuente
        String ST = txtResultado.getText();
        
        // 2. PREPARACIÓN: Limpiar el generador para evitar basura de ejecuciones anteriores
        // Esto es vital: si no limpias, se duplicarán las variables en el .asm
        GeneradorEnsamblador.instancia.limpiar();
        
        // 3. FASE 1: ANÁLISIS SINTÁCTICO (Validación)
        // Instanciamos el Parser con el código actual
        Sintax s = new Sintax(new LexerCup(new StringReader(ST)));
        
        try {
            // Intenta analizar el código. 
            // SI HAY UN ERROR DE SINTAXIS, el método s.parse() lanza una excepción 
            // y salta inmediatamente al 'catch', ignorando el resto del código.
            s.parse(); 
            
            // --- Si llegamos a esta línea, significa que el código es VÁLIDO ---
            txtAnalizarSin.setText("✅ Análisis Correcto. Generando ejecutable...");
            txtAnalizarSin.setForeground(new java.awt.Color(25, 111, 61));
            
            // 4. FASE 2: GENERACIÓN DE ENSAMBLADOR (Backend)
            // Como el parser ya recorrió el código exitosamente, el Generador ya tiene
            // todas las instrucciones guardadas en memoria. Solo las pedimos.
            String codigoASM = GeneradorEnsamblador.instancia.obtenerCodigoCompleto();
            String nombreArchivo = GeneradorEnsamblador.obtenerNombreArchivoASM();

            // 5. FASE 3: EJECUCIÓN (Llamada al .BAT)
            ManejadorEjecucion ejecutor = new ManejadorEjecucion();
            ejecutor.compilarYEjecutar(codigoASM, nombreArchivo);
            
        } catch (Exception ex) {
            // --- SI HAY ERROR DE SINTAXIS, CAEMOS AQUÍ ---
            // No se generó ASM ni se ejecutó el BAT. Solo mostramos el error.
            
            Symbol sym = s.getS();
            if (sym != null) {
                txtAnalizarSin.setText("Error de sintaxis. No se puede ejecutar.\n" +
                                       "Línea: " + (sym.right + 1) + 
                                       " Columna: " + (sym.left + 1) + 
                                       ", Texto: \"" + sym.value + "\"");
            } else {
                txtAnalizarSin.setText("Error crítico al compilar: " + ex.getMessage());
            }
            txtAnalizarSin.setForeground(java.awt.Color.red);
        }
    }//GEN-LAST:event_btnEjecutarMouseClicked

    private void btnGuardarMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnGuardarMouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_btnGuardarMouseClicked

    private void btnGuardarComoMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnGuardarComoMouseClicked
        realizarGuardarComo();
    }//GEN-LAST:event_btnGuardarComoMouseClicked

    private void realizarGuardarComo() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Guardar Como");
        
        int userSelection = chooser.showSaveDialog(this);
        
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File archivoSeleccionado = chooser.getSelectedFile();
            String contenido = txtResultado.getText();
            
            // Verificamos extensión
            if (!archivoSeleccionado.getName().contains(".")) {
                archivoSeleccionado = new File(archivoSeleccionado.getAbsolutePath() + ".txt");
            }
            
            guardarArchivoEnDisco(archivoSeleccionado, contenido);
        }
    }
    
    private void btnGuardarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGuardarActionPerformed
        String contenido = txtResultado.getText();
        
        if (archivoActual != null) {
            // Si ya existe un archivo abierto, lo sobrescribimos
            guardarArchivoEnDisco(archivoActual, contenido);
        } else {
            // Si es un archivo nuevo (nunca guardado), llamamos a Guardar Como
            realizarGuardarComo();
        }
    }//GEN-LAST:event_btnGuardarActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(VPR.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(VPR.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(VPR.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(VPR.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new VPR().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAnalizarLex;
    private javax.swing.JButton btnAnalizarSin;
    private javax.swing.JButton btnArchivo;
    private javax.swing.JButton btnEjecutar;
    private javax.swing.JButton btnGuardar;
    private javax.swing.JButton btnGuardarComo;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JTextArea txtAnalizarLex;
    private javax.swing.JTextArea txtAnalizarSin;
    private javax.swing.JTextArea txtResultado;
    // End of variables declaration//GEN-END:variables
}
