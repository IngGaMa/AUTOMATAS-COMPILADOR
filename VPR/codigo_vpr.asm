.386
.model flat, stdcall
option casemap :none
include \masm32\include\windows.inc
include \masm32\include\kernel32.inc
include \masm32\include\msvcrt.inc
includelib \masm32\lib\kernel32.lib
includelib \masm32\lib\msvcrt.lib

.data
    formato_num DB "%d", 0Ah, 0
    a DD 0
    b DD 0
    suma DD 0
    x DD 0
    y DD 0
    resta DD 0
.code
main PROC
    ; Asignacion: a = 155
    MOV EAX, 155
    MOV a, EAX
    ; Asignacion: b = 419
    MOV EAX, 419
    MOV b, EAX
    ; Operacion: suma = a + b
    MOV EAX, a
    ADD EAX, b
    MOV suma, EAX
    ; Asignacion: x = 376
    MOV EAX, 376
    MOV x, EAX
    ; Asignacion: y = 233
    MOV EAX, 233
    MOV y, EAX
    ; Operacion: resta = x - y
    MOV EAX, x
    SUB EAX, y
    MOV resta, EAX
    ; Imprimir suma
    invoke crt_printf, addr formato_num, suma
    ; Imprimir resta
    invoke crt_printf, addr formato_num, resta
    invoke ExitProcess, 0
main ENDP
END main
