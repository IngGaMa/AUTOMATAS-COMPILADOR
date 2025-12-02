package micompiladorvpr.lexer;
import java_cup.runtime.Symbol;
import micompiladorvpr.parser.sym;
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
%%

{espacio} {/*Ignore*/}
( "//"(.)* ) {/*Ignore*/}

/* --- REGLA IMPORTANTE: TEXTO COMPLETO --- */
/* Asegura que el parser reciba el texto completo con espacios */
( \" [^\"]* \" ) {return new Symbol(sym.Texto, yyline, yycolumn, yytext());}

/* Tipos */
( int )     {return new Symbol(sym.T_int, yyline, yycolumn, yytext());}
( float )   {return new Symbol(sym.T_float, yyline, yycolumn, yytext());}
( string )  {return new Symbol(sym.T_string, yyline, yycolumn, yytext());}
( char )    {return new Symbol(sym.T_char, yyline, yycolumn, yytext());}
( bool )    {return new Symbol(sym.T_bool, yyline, yycolumn, yytext());}
( void )    {return new Symbol(sym.T_void, yyline, yycolumn, yytext());}

/* Reservadas */
( if )      {return new Symbol(sym.If, yyline, yycolumn, yytext());}
( else )    {return new Symbol(sym.Else, yyline, yycolumn, yytext());}
( do )      {return new Symbol(sym.Do, yyline, yycolumn, yytext());}
( while )   {return new Symbol(sym.While, yyline, yycolumn, yytext());}
( for )     {return new Symbol(sym.For, yyline, yycolumn, yytext());}
( switch )  {return new Symbol(sym.Switch, yyline, yycolumn, yytext());}
( case )    {return new Symbol(sym.Case, yyline, yycolumn, yytext());}
( default ) {return new Symbol(sym.Default, yyline, yycolumn, yytext());}
( break )   {return new Symbol(sym.Break, yyline, yycolumn, yytext());}
( main )    {return new Symbol(sym.Main, yyline, yycolumn, yytext());}
( return )  {return new Symbol(sym.Retorno, yyline, yycolumn, yytext());}

/* Funciones */
( printf )  {return new Symbol(sym.Imprime, yyline, yycolumn, yytext());}
( println ) {return new Symbol(sym.ImprimeLn, yyline, yycolumn, yytext());}
( scanf )   {return new Symbol(sym.Lectura, yyline, yycolumn, yytext());}
( clrscr )  {return new Symbol(sym.Limpia, yyline, yycolumn, yytext());}
( Power )   {return new Symbol(sym.F_pow, yyline, yycolumn, yytext());}
( Sqrt )    {return new Symbol(sym.F_sqrt, yyline, yycolumn, yytext());}
( Abs )     {return new Symbol(sym.F_abs, yyline, yycolumn, yytext());}
( Sin )     {return new Symbol(sym.F_sin, yyline, yycolumn, yytext());}
( Cos )     {return new Symbol(sym.F_cos, yyline, yycolumn, yytext());}

/* Operadores */
( "=" ) {return new Symbol(sym.Igual, yyline, yycolumn, yytext());}
( "+" ) {return new Symbol(sym.Suma, yyline, yycolumn, yytext());}
( "-" ) {return new Symbol(sym.Resta, yyline, yycolumn, yytext());}
( "*" ) {return new Symbol(sym.Multiplicacion, yyline, yycolumn, yytext());}
( "/" ) {return new Symbol(sym.Division, yyline, yycolumn, yytext());}
( "%" ) {return new Symbol(sym.Modulo, yyline, yycolumn, yytext());}
( "^" ) {return new Symbol(sym.Potencia, yyline, yycolumn, yytext());}

( "++" | "--" ) {return new Symbol(sym.Op_incremento, yyline, yycolumn, yytext());}
( "+=" | "-=" | "*=" | "/=" ) {return new Symbol(sym.Op_atribucion, yyline, yycolumn, yytext());}
( "&&" | "||" | "!" ) {return new Symbol(sym.Op_logico, yyline, yycolumn, yytext());}
( ">" | "<" | "==" | "!=" | ">=" | "<=" ) {return new Symbol(sym.Op_relacional, yyline, yycolumn, yytext());}
( true | false ) {return new Symbol(sym.Op_booleano, yyline, yycolumn, yytext());}

/* Signos */
( "(" ) {return new Symbol(sym.Parentesis_a, yyline, yycolumn, yytext());}
( ")" ) {return new Symbol(sym.Parentesis_c, yyline, yycolumn, yytext());}
( "{" ) {return new Symbol(sym.Llave_a, yyline, yycolumn, yytext());}
( "}" ) {return new Symbol(sym.Llave_c, yyline, yycolumn, yytext());}
( "[" ) {return new Symbol(sym.Corchete_a, yyline, yycolumn, yytext());}
( "]" ) {return new Symbol(sym.Corchete_c, yyline, yycolumn, yytext());}
( ";" ) {return new Symbol(sym.P_coma, yyline, yycolumn, yytext());}
( ":" ) {return new Symbol(sym.Dos_puntos, yyline, yycolumn, yytext());}
( "," ) {return new Symbol(sym.Coma, yyline, yycolumn, yytext());}
( "." ) {return new Symbol(sym.Punto, yyline, yycolumn, yytext());}

/* Identificadores y Numero */
{L}({L}|{D})* {return new Symbol(sym.Identificador, yyline, yycolumn, yytext());}
("(-"{D}+")")|{D}+ {return new Symbol(sym.Numero, yyline, yycolumn, yytext());}

 . {return new Symbol(sym.ERROR, yyline, yycolumn, yytext());}