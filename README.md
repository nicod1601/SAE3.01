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
├── README.md
└── Etape7/
    ├── compile.list             # Liste des fichiers à compiler
    ├── Run.bat                  # Script d'exécution (Windows)
    ├── Run.sh                   # Script d'exécution (Linux)
    │
    ├── data/                    # Données de test et sauvegardes
    │   ├── Disque.java
    │   ├── ISurface.java
    │   ├── Musique.java
    │   ├── Point.java
    │   ├── Rond.java
    │   ├── sauvegarde01.ser
    │   ├── sauvegarde01.uml
    │   └── txt.txt
    │
    └── src/
        ├── Controleur.java      # Point d'entrée principal
        │
        ├── ihm/                 # Interface Graphique
        │   ├── Fleche.java
        │   ├── FrameAppli.java
        │   ├── IhmCui.java
        │   ├── PanneauFichier.java
        │   ├── PanneauMenu.java
        │   ├── PanneauPrincipal.java
        │   │
        │   └── edit/            # Composants d'édition
        │       ├── FrameEdit.java
        │       ├── PanneauChoix.java
        │       ├── PanneauInfo.java
        │       └── PopUp.java
        │
        └── metier/              # Logique métier
            ├── Attribut.java
            ├── Couleur.java
            ├── CreeClass.java
            ├── GererData.java
            ├── LectureFichier.java
            ├── LectureRepertoire.java
            ├── Lien.java
            ├── Methode.java
            └── Multiplicite.java
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
