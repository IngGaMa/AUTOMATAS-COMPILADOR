package micompiladorvpr.lexer;
import static micompiladorvpr.lexer.Tokens.*;
%%
%class Lexer
%type Tokens
L=[a-zA-Z_]+
D=[0-9]+
espacio=[ ,\t,\r,\n]+
%{
    public String lexeme;
%}
%%

{espacio} {/*Ignore*/}
( "//"(.)* ) {/*Ignore*/}

/* --- TEXTO --- */
( \"[^\"]*\" ) {lexeme=yytext(); return Texto;}

/* Tipos */
"int"       {lexeme=yytext(); return T_int;}
"float"     {lexeme=yytext(); return T_float;}
"string"    {lexeme=yytext(); return T_string;}
"char"      {lexeme=yytext(); return T_char;}
"bool"      {lexeme=yytext(); return T_bool;}
"void"      {lexeme=yytext(); return T_void;}

/* Reservadas */
"if"        {lexeme=yytext(); return If;}
"else"      {lexeme=yytext(); return Else;}
"do"        {lexeme=yytext(); return Do;}
"while"     {lexeme=yytext(); return While;}
"for"       {lexeme=yytext(); return For;}
"main"      {lexeme=yytext(); return Main;}
"return"    {lexeme=yytext(); return Retorno;}
"switch"    {lexeme=yytext(); return Switch;}
"case"      {lexeme=yytext(); return Case;}
"default"   {lexeme=yytext(); return Default;}
"break"     {lexeme=yytext(); return Break;}

/* Funciones */
"printf"    {lexeme=yytext(); return Imprime;}
"println"   {lexeme=yytext(); return ImprimeLn;}
"scanf"     {lexeme=yytext(); return Lectura;}
"clrscr"    {lexeme=yytext(); return Limpia;}

/* Operadores */
"="         {lexeme=yytext(); return Igual;}
"+"         {lexeme=yytext(); return Suma;}
"-"         {lexeme=yytext(); return Resta;}
"*"         {lexeme=yytext(); return Multiplicacion;}
"/"         {lexeme=yytext(); return Division;}

"++"        {lexeme=yytext(); return Op_incremento;}
"--"        {lexeme=yytext(); return Op_incremento;}
"+="        {lexeme=yytext(); return Op_atribucion;}
"-="        {lexeme=yytext(); return Op_atribucion;}
"*="        {lexeme=yytext(); return Op_atribucion;}
"/="        {lexeme=yytext(); return Op_atribucion;}

"&&"        {lexeme=yytext(); return Op_logico;}
"||"        {lexeme=yytext(); return Op_logico;}
"!"         {lexeme=yytext(); return Op_logico;}

">"         {lexeme=yytext(); return Op_relacional;}
"<"         {lexeme=yytext(); return Op_relacional;}
"=="        {lexeme=yytext(); return Op_relacional;}
"!="        {lexeme=yytext(); return Op_relacional;}
">="        {lexeme=yytext(); return Op_relacional;}
"<="        {lexeme=yytext(); return Op_relacional;}

"true"      {lexeme=yytext(); return Op_booleano;}
"false"     {lexeme=yytext(); return Op_booleano;}

/* Signos */
"("         {lexeme=yytext(); return Parentesis_a;}
")"         {lexeme=yytext(); return Parentesis_c;}
"{"         {lexeme=yytext(); return Llave_a;}
"}"         {lexeme=yytext(); return Llave_c;}
"["         {lexeme=yytext(); return Corchete_a;}
"]"         {lexeme=yytext(); return Corchete_c;}
";"         {lexeme=yytext(); return P_coma;}
":"         {lexeme=yytext(); return Dos_puntos;}
","         {lexeme=yytext(); return Coma;}
"."         {lexeme=yytext(); return Punto;}

/* IDs y Numeros */
{L}({L}|{D})* {lexeme=yytext(); return Identificador;}
("-"?[0-9]+)(\.[0-9]+)? {lexeme=yytext(); return Numero;}

 . {return ERROR;}