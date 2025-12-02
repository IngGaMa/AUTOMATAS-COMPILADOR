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
    formato_string DB "%s", 0Ah, 0
    titulo DB "=== PRUEBA DEL COMPILADOR VPR ===", 0
    saludo DB "Hola usuario, calculando suma...", 0
    n1 DD 0
    n2 DD 0
    suma DD 0
    msgSuma DB "El resultado de la suma es:", 0
    operacion DD 0
    msgOp DB "Resultado operacion (150 - 10 * 5):", 0
.code
main PROC
    ; Imprimir titulo
    invoke crt_printf, addr formato_string, addr titulo
    ; Imprimir saludo
    invoke crt_printf, addr formato_string, addr saludo
    ; Push 150
    MOV EAX, 150
    PUSH EAX
    ; Asignar a n1
    POP EAX
    MOV n1, EAX
    ; Push 50
    MOV EAX, 50
    PUSH EAX
    ; Asignar a n2
    POP EAX
    MOV n2, EAX
    ; Push n1
    MOV EAX, n1
    PUSH EAX
    ; Push n2
    MOV EAX, n2
    PUSH EAX
    ; Operacion +
    POP EBX
    POP EAX
    ADD EAX, EBX
    PUSH EAX
    ; Asignar a suma
    POP EAX
    MOV suma, EAX
    ; Imprimir msgSuma
    invoke crt_printf, addr formato_string, addr msgSuma
    ; Imprimir suma
    invoke crt_printf, addr formato_num, suma
    ; Push n1
    MOV EAX, n1
    PUSH EAX
    ; Push 10
    MOV EAX, 10
    PUSH EAX
    ; Push 5
    MOV EAX, 5
    PUSH EAX
    ; Operacion *
    POP EBX
    POP EAX
    IMUL EAX, EBX
    PUSH EAX
    ; Operacion -
    POP EBX
    POP EAX
    SUB EAX, EBX
    PUSH EAX
    ; Asignar a operacion
    POP EAX
    MOV operacion, EAX
    ; Imprimir msgOp
    invoke crt_printf, addr formato_string, addr msgOp
    ; Imprimir operacion
    invoke crt_printf, addr formato_num, operacion
    invoke ExitProcess, 0
main ENDP
END main
