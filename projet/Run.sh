#!/bin/bash
echo "Compilation en cours..."
javac @compile.list -d class
if [ $? -ne 0 ]; then
    echo "Erreur de compilation!"
    exit 1
fi
echo "Compilation reussie!"
echo
echo "Execution du programme..."
cd class
java projet.Controleur
cd ..