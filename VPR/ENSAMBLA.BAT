@echo off
REM Recibe el nombre del archivo como argumento
SET NAME=%1

REM *** Set path and files ***
SET THIS_DIR=%~dp0
SET THIS_UNIT=%~d0
SET OBJ_FILE=%THIS_DIR%%NAME%.obj
SET ASM_FILE=%THIS_DIR%%NAME%.asm
SET EXE_FILE=%THIS_DIR%%NAME%.exe

%THIS_UNIT%
cd %THIS_DIR%

REM *** Assemble and link ***

if exist "%OBJ_FILE%" del "%OBJ_FILE%"
if exist "%EXE_FILE%" del "%EXE_FILE%"

\masm32\bin\ml /c /coff "%ASM_FILE%"
if errorlevel 1 goto errasm

\masm32\bin\PoLink /SUBSYSTEM:CONSOLE "%OBJ_FILE%"
if errorlevel 1 goto errlink
goto success

:errlink
echo _
echo !!!Link error
goto TheEnd

:errasm
echo _
echo !!!Assembly Error
goto TheEnd

:success
del "%OBJ_FILE%"
echo "Ejecutando %EXE_FILE%..."
"%EXE_FILE%"
goto TheEnd

:TheEnd