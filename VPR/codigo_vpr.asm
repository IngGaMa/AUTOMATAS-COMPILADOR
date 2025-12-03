.386
.model flat, stdcall
option casemap :none
include \masm32\include\windows.inc
include \masm32\include\kernel32.inc
include \masm32\include\msvcrt.inc
includelib \masm32\lib\kernel32.lib
includelib \masm32\lib\msvcrt.lib

.data
    formato_num DB "%d", 0
    formato_string DB "%s", 0
    formato_float DB "%d.%02d", 0
    salto_linea DB 0Ah, 0
    fmt_scan_int DB "%d", 0
    fmt_scan_str DB "%s", 0
    cmd_cls DB "cls", 0
    signal_input_int DB "___INPUT_INT___ ", 0
    signal_input_str DB "___INPUT_STR___ ", 0
    signal_input_float DB "___INPUT_FLOAT___ ", 0
    str_lit_0 DB "Probando nuevas funciones:", 0
    str_lit_0_buffer DB 256 dup(0)
    str_lit_1 DB "Esto es un println (salto de linea automatico).", 0
    str_lit_1_buffer DB 256 dup(0)
    numero DD 0
    str_lit_2 DB "Escribe un numero y presiona Enter:", 0
    str_lit_2_buffer DB 256 dup(0)
    str_lit_3 DB "El numero leido es:", 0
    str_lit_3_buffer DB 256 dup(0)
    str_lit_4 DB "Fin del programa.", 0
    str_lit_4_buffer DB 256 dup(0)
.code
main PROC
    invoke crt_printf, addr formato_string, addr str_lit_0
    invoke crt_printf, addr formato_string, addr str_lit_1
    invoke crt_printf, addr salto_linea
    invoke crt_printf, addr formato_string, addr str_lit_2
    invoke crt_printf, addr signal_input_int
    invoke crt_scanf, addr fmt_scan_int, addr numero
    invoke crt_printf, addr formato_string, addr str_lit_3
    invoke crt_printf, addr formato_num, numero
    invoke crt_printf, addr salto_linea
    invoke crt_printf, addr formato_string, addr str_lit_4
    invoke crt_printf, addr salto_linea
    invoke ExitProcess, 0
main ENDP
END main
