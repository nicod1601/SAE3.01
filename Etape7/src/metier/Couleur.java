package src.metier;

import java.awt.Color;

/**
 * Enume de Couleur pour avoir des couleur Près definit
 */
public enum Couleur 
{ 
	COULEUR_FOND            (new Color(45, 52, 54   )),
	COULEUR_LISTE           (new Color(52, 73, 94   )),
	COULEUR_TEXTE           (new Color(236, 240, 241)),
	COULEUR_TEXTE_SECONDAIRE(new Color(189, 195, 199)),
	COULEUR_SELECTION       (new Color(74, 105, 189 )),
	COULEUR_HOVER           (new Color(52, 152, 219 )),
	COULEUR_BORDURE         (new Color(55, 66, 77   )),
	ROUGE                   (new Color(255, 0, 0    )),
	BLEU                    (new Color(0,122,255    )),
	NOIR                    (new Color(0,0,0        )),
	VERT                    (new Color(76, 175, 80  )),
	ROSE                    (new Color(255, 105, 180)),
	CYAN                    (new Color(0, 188, 212  )),
	TURQUOISE               (new Color(0, 150, 136  )),
	BRUN                    (new Color(121, 85, 72  )),
	GRIS                    (new Color(33, 33, 33   )),
	BLANC                   (new Color(255, 255, 255)),
	COULEUR_MENU            (new Color(55, 66, 77   )),
	COULEUR_ACCENT          (new Color(52, 152, 219 )),
	COULEUR_DANGER          (new Color(231, 76, 60  )),
	COULEUR_PRIMAIRE        (new Color(63, 81, 181  )),
	COULEUR_SECONDAIRE      (new Color(92, 107, 192 )),
	COULEUR_TEXTE_G         (new Color(33, 33, 33   ));

	private final Color color;

	Couleur(Color color) 
	{
		this.color = color;
	}

	public Color getColor() 
	{
		return color;
	}
}