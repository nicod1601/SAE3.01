@echo off
echo Compilation en cours...
javac "@compile.list" -d class
if %errorlevel% neq 0 (
    echo Erreur de compilation!
    pause
    exit /b %errorlevel%
)
echo Compilation reussie!
echo.
echo Execution du programme...
cd class
java projet.Controleur
cd ..
pause