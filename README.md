# SAE 3.01 - Retro-Conception Java-UML. 

## Description
L'objectif de ce projet est d'élaborer un outil de Retro-Conception Java-UML.
En d'autres termes de générer des Diagrammes de classes respectant le formalisme UML, à partir de classes écrites en Java. 

##  Équipe 3 : Développeurs : 

- Donovan Prévost
- Paul    Gricourt
- Nicolas Delpech
- William Millereux Bienvault
- Erwan   Martin

## Structure du Projet

```
Etape4/
│ 
├── class/                   # Fichiers compilés (généré)
│              
├── data/                    # Classes de données
│   ├── Point.java
│   ├── Disque.java
│   └── Rond.java
│
│   
├── src/
│   ├──metier/               # Logique métier
│   │    ├── Attribut.java
│   │    ├── Methode.java
│   │    ├── Lien.java
│   │    ├── CreeClass.java
|   │    ├── Multiplicite.java
│   │    └── LectureRepertoire.java
│   │
│   ├── ihm/                 # Interface utilisateur
│   │    └── IhmCui.java
│   │
│   └── Controleur.java      # Classe principale
│
├── compile.list             # Liste des fichiers à compiler
├── Run.bat                  # Script d'exécution (Windows)
└── Run.sh                   # Script d'exécution (Linux)
```

## Exécution

### Windows
```bash
cd Etape7
Run.bat
```

### Linux
```bash
cd Etape7
sed -i 's/\r$//' Run.sh
chmod +x Run.sh
./Run.sh
```

## Compilation manuelle

```bash
cd Etape7
javac @compile.list -d class
java -cp class src.Controleur
```
