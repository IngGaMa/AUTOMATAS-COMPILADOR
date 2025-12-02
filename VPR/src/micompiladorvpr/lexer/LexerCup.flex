package micompiladorvpr.lexer;
import java_cup.runtime.Symbol;
%%
%class LexerCup
%type java_cup.runtime.Symbol
%cup
%full
%line
%char
L=[a-zA-Z_]+
D=[0-9]+
espacio=[ ,\t,\r,\n]+
// Bloque de código de usuario ELIMINADO para evitar errores de caracteres
%%

/* Espacios en blanco */
{espacio} {/*Ignore*/}

/* Comillas */
( "\"" ) {return new Symbol(sym.Comillas, yyline, yycolumn, yytext());}

/* TIPOS DE DATOS UNIFICADOS (T_dato) */
( byte | int | float | double | long | char ) {return new Symbol(sym.T_dato, yyline, yycolumn, yytext());}

/* Tipo de dato String */
( String ) {return new Symbol(sym.Cadena, yyline, yycolumn, yytext());}

/* PALABRAS RESERVADAS */
( if ) {return new Symbol(sym.If, yyline, yycolumn, yytext());}
( else ) {return new Symbol(sym.Else, yyline, yycolumn, yytext());}
( do ) {return new Symbol(sym.Do, yyline, yycolumn, yytext());}
( while ) {return new Symbol(sym.While, yyline, yycolumn, yytext());}
( for ) {return new Symbol(sym.For, yyline, yycolumn, yytext());}
( "main" ) {return new Symbol(sym.Main, yyline, yycolumn, yytext());}
( "printf" ) {return new Symbol(sym.Imprime, yyline, yycolumn, yytext());}
( "scanf" ) {return new Symbol(sym.Lectura, yyline, yycolumn, yytext());}
( "return" ) {return new Symbol(sym.retorno, yyline, yycolumn, yytext());}
( "break" ) {return new Symbol(sym.brek, yyline, yycolumn, yytext());}
( "case" ) {return new Symbol(sym.cas, yyline, yycolumn, yytext());}
( "include" ) {return new Symbol(sym.Ilibrerias, yyline, yycolumn, yytext());}
( true | false ) {return new Symbol(sym.Op_booleano, yyline, yycolumn, yytext());}
( "void" | "Void" ) {return new Symbol(sym.nulo, yyline, yycolumn, yytext());}


/* OPERADORES Y PUNTUACIÓN */
( "=" ) {return new Symbol(sym.Igual, yyline, yycolumn, yytext());}
( "+" ) {return new Symbol(sym.Suma, yyline, yycolumn, yytext());}
( "-" ) {return new Symbol(sym.Resta, yyline, yycolumn, yytext());}
( "*" ) {return new Symbol(sym.Multiplicacion, yyline, yycolumn, yytext());}
( "/" ) {return new Symbol(sym.Division, yyline, yycolumn, yytext());}
( "&&" | "||" | "!" | "&" | "|" ) {return new Symbol(sym.Op_logico, yyline, yycolumn, yytext());}
( ">" | "<" | "==" | "!=" | ">=" | "<=" | "<<" | ">>" ) {return new Symbol(sym.Op_relacional, yyline, yycolumn, yytext());}
( "+=" | "-="  | "*=" | "/=" | "%=" ) {return new Symbol(sym.Op_atribucion, yyline, yycolumn, yytext());}
( "++" | "--" ) {return new Symbol(sym.Op_incremento, yyline, yycolumn, yytext());}
( "(" ) {return new Symbol(sym.Parentesis_a, yyline, yycolumn, yytext());}
( ")" ) {return new Symbol(sym.Parentesis_c, yyline, yycolumn, yytext());}
( "{" ) {return new Symbol(sym.Llave_a, yyline, yycolumn, yytext());}
( "}" ) {return new Symbol(sym.Llave_c, yyline, yycolumn, yytext());}
( "[" ) {return new Symbol(sym.Corchete_a, yyline, yycolumn, yytext());}
( "]" ) {return new Symbol(sym.Corchete_c, yyline, yycolumn, yytext());}
( ";" ) {return new Symbol(sym.P_coma, yyline, yycolumn, yytext());}
( ":" ) {return new Symbol(sym.Puntos, yyline, yycolumn, yytext());}
( "#" ) {return new Symbol(sym.Hashtag, yyline, yycolumn, yytext());}
( "stdio.h" ) {return new Symbol(sym.LEntrada_Salida, yyline, yycolumn, yytext());}
( "\\n" ) {return new Symbol(sym.saltoL, yyline, yycolumn, yytext());}
( "%d" ) {return new Symbol(sym.formatoE, yyline, yycolumn, yytext());}
( "%s" ) {return new Symbol(sym.formatoC, yyline, yycolumn, yytext());}
( "//"(.)* ) {return new Symbol(sym.LC, yyline, yycolumn, yytext());}
( "." ) {return new Symbol(sym.Punto, yyline, yycolumn, yytext());}


/* IDENTIFICADOR y NUMERO deben ir al final */
{L}({L}|{D})* {return new Symbol(sym.Identificador, yyline, yycolumn, yytext());}
("(-"{D}+")")|{D}+ {return new Symbol(sym.Numero, yyline, yycolumn, yytext());}


/* Error de analisis */
. {return new Symbol(sym.ERROR, yyline, yycolumn, yytext());}