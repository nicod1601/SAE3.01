# SAE 3.01 - Retro-Conception Java-UML. 

## Description
L'objectif de ce projet est d'élaborer un outil de Retro-Conception Java-UML.
Java. 

## Développeurs
- Prévost Donovan
- Paul    Gricourt
- Nicolas Delpech
- William Millereux Bienvault
- Erwan   Martin

## Structure du Projet

```
projet/
├── data/                 # Classes de données
│   ├── Point.java
│   ├── Disque.java
│   └── Rond.java
├── metier/              # Logique métier
│   ├── Attribut.java
│   ├── Methode.java
│   ├── Lien.java
│   ├── CreeClass.java
│   └── LectureRepertoire.java
├── ihm/                 # Interface utilisateur
│   └── IhmCui.java
├── class/               # Fichiers compilés (généré)
├── Controleur.java      # Classe principale
├── compile.list         # Liste des fichiers à compiler
├── Run.bat              # Script d'exécution (Windows)
└── Run.sh               # Script d'exécution (Linux)
```

## Exécution

### Windows
```bash
cd projet
Run.bat
```

### Linux
```bash
cd projet
chmod +x Run.sh
./Run.sh
```

## Compilation manuelle

```bash
cd projet
javac @compile.list -d class
java -cp class Controleur
```