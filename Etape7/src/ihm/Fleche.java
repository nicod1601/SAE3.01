package src.ihm;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import src.metier.CreeClass;
import src.metier.Couleur;

/**
 * Représente une flèche (liaison) entre deux classes dans l'IHM.
 * <p>
 * Cette classe contient les informations nécessaires au tracé graphique
 * d'une liaison (type, multiplicités, rôle, orientation) ainsi que
 * quelques utilitaires pour dessiner différents types de pointes.
 */
public class Fleche
{
	/** Classe source de la liaison. */
	private CreeClass source;

	/** Classe cible de la liaison. */
	private CreeClass cible;

	/** Type du lien ("association", "heritage", "interface", ...). */
	private String    typeLien;

	/** Multiplicité affichée côté source. */
	private String    multipliciteSource;

	/** Multiplicité affichée côté cible. */
	private String    multipliciteCible;

	/** Rôle affiché sur la flèche, si défini. */
	private String    role;

	/** Indique si la liaison est bidirectionnelle. */
	private boolean   bidirectionnel;

	/** Position X finale (point d'arrivée) calculée lors du dessin. */
	private int       posXFin;

	/** Position Y finale (point d'arrivée) calculée lors du dessin. */
	private int       posYFin;

	/** Identifiant facultatif de l'association. */
	private int       id;
	
	/**
	 * Constructeur de Fleche.
	 *
	 * NOTE : Les noms des variables qui étaient en anglais ont été remplacés
	 * par des noms en français (par ex. source, cible, typeLien,
	 * multipliciteSource, multipliciteCible).
	 */
	public Fleche(CreeClass source, CreeClass cible, String typeLien, String multSrc, String multCible, boolean bidir, int id)
	{
		this.source             = source;
		this.cible              = cible;
		this.typeLien           = typeLien;
		this.multipliciteSource = multSrc;
		this.multipliciteCible  = multCible;
		this.bidirectionnel     = bidir;
		this.posXFin            = 0;
		this.posYFin            = 0;
		this.role               = "";
		this.id                 = id;
	}

	/**
	 * Retourne la position X finale calculée après dessin.
	 *
	 * @return position X finale
	 */
	public int       getPosXFin()            {return posXFin           ;}

	/**
	 * Retourne la position Y finale calculée après dessin.
	 *
	 * @return position Y finale
	 */
	public int       getPosYFin()            {return posYFin           ;}

	/**
	 * Retourne l'identifiant de la flèche.
	 *
	 * @return identifiant de la flèche
	 */
	public int       getId()                 {return id                ;}

	/**
	 * Retourne la classe source de la liaison.
	 *
	 * @return classe source
	 */
	public CreeClass getSource()             {return source            ;}

	/**
	 * Retourne la classe cible de la liaison.
	 *
	 * @return classe cible
	 */
	public CreeClass getCible()              {return cible             ;}

	/**
	 * Retourne le type de lien.
	 *
	 * @return type de lien
	 */
	public String    getTypeLien()           {return typeLien          ;}

	/**
	 * Retourne la multiplicité côté source.
	 *
	 * @return multiplicité côté source
	 */
	public String    getMultipliciteSource() {return multipliciteSource;}

	/**
	 *
	 * @return multiplicité cible
	 */
	public String    getMultipliciteCible()  {return multipliciteCible ;}

	/**
	 * Retourne le rôle associé à la flèche.
	 *
	 * @return chaîne du rôle (vide si non défini)
	 */
	public String    getRole()               {return role              ;}

	/**
	 * Indique si la liaison est bidirectionnelle.
	 *
	 * @return true si bidirectionnelle
	 */
	public boolean   isBidirectionnel()      {return bidirectionnel    ;}

	/**
	 * Méthode statique pour vérifier si une liaison bidirectionnelle existe
	 */
	public static boolean estBidirectionnel(CreeClass cl1, CreeClass cl2) 
	{
		boolean cl1VersCl2 = cl1.getLien().getLstLienAttribut().contains(cl2);
		boolean cl2VersCl1 = cl2.getLien().getLstLienAttribut().contains(cl1);
		return cl1VersCl2 && cl2VersCl1;
	}

	/**
	 * Dessine la flèche avec ses multiplicités
	 */
	public void dessiner(Graphics2D g2, int decalage)
	{
		// Calcul des centres des classes
		int centreXDepart  = source.getPosX() + source.getLargeur() / 2;
		int centreYDepart  = source.getPosY() + source.getHauteur() / 2;
		int centreXArrivee = cible.getPosX()  + cible.getLargeur()  / 2;
		int centreYArrivee = cible.getPosY()  + cible.getHauteur()  / 2;
		
		// Calculer la différence de position
		double deltaX      = centreXArrivee - centreXDepart;
		double deltaY      = centreYArrivee - centreYDepart;
		
		// Déterminer les points de connexion selon la position relative des classes
		int pointXDepart  = centreXDepart;
		int pointYDepart  = centreYDepart;
		int pointXArrivee = centreXArrivee;
		int pointYArrivee = centreYArrivee;
		
		// Calculer le vecteur perpendiculaire pour le décalage
		double perpX      = 0;
		double perpY      = 0;

		if (decalage != 0) 
		{
			double longueur = Math.sqrt(deltaX * deltaX + deltaY * deltaY);
			if (longueur > 0) 
			{
				perpX = -deltaY / longueur * decalage;
				perpY =  deltaX / longueur * decalage;
			}
		}
		
		if (Math.abs(deltaY) > Math.abs(deltaX))
		{
			if (deltaY < 0) 
			{
				pointYDepart  = source.getPosY();
				pointYArrivee = cible.getPosY() + cible.getHauteur();
			}
			else 
			{
				pointYDepart  = source.getPosY() + source.getHauteur();
				pointYArrivee = cible.getPosY();
			}
			
			pointXDepart  = centreXDepart  + (int)perpX;
			pointXArrivee = centreXArrivee + (int)perpX;
		}
		else
		{
			if (deltaX < 0) 
			{
				pointXDepart = source.getPosX();
				pointXArrivee = cible.getPosX() + cible.getLargeur();
			}
			else 
			{
				pointXDepart = source.getPosX() + source.getLargeur();
				pointXArrivee = cible.getPosX();
			}

			pointYDepart = centreYDepart   + (int)perpY;
			pointYArrivee = centreYArrivee + (int)perpY;
		}
		
		// Dessiner selon le type de lien
		switch (this.typeLien) 
		{
			case "association":
				if (this.source.getNom().equals(this.cible.getNom()))
				{
					//Fleche pour une assotiation vers lui meme - Carré
					g2.setStroke(new BasicStroke(2f));
					int taille = 50;
					
					// Dessiner un carré
					g2.drawLine(pointXDepart         , pointYDepart         , pointXDepart + taille, pointYDepart         );
					g2.drawLine(pointXDepart + taille, pointYDepart         , pointXDepart + taille, pointYDepart + taille);
					g2.drawLine(pointXDepart + taille, pointYDepart + taille, pointXDepart         , pointYDepart + taille);
					
					this.dessinerPointe(g2, pointXDepart + taille, pointYDepart + taille, pointXDepart, pointYDepart + taille, false);
				}
				else if (bidirectionnel) 
				{
					// Trait simple sans flèche pour bidirectionnel
					g2.setStroke(new BasicStroke(2f));
					g2.drawLine(pointXDepart, pointYDepart, pointXArrivee, pointYArrivee);
				} 
				else
				{
					// Flèche normale pour unidirectionnel
					g2.setStroke(new BasicStroke(2f));
					g2.drawLine(pointXDepart, pointYDepart, pointXArrivee, pointYArrivee);
					this.dessinerPointe(g2, pointXDepart, pointYDepart, pointXArrivee, pointYArrivee, false);
				}
				break;
				
			case "heritage":
				g2.setStroke(new BasicStroke(2f));
				g2.drawLine(pointXDepart, pointYDepart, pointXArrivee, pointYArrivee);
				this.dessinerTriangleFerme(g2, pointXArrivee, pointYArrivee,pointXDepart ,pointYDepart );
				break;
				
			case "interface":
				float[] pattern = {6f, 6f};
				g2.setStroke(new BasicStroke(2f, BasicStroke.CAP_ROUND, 
				             BasicStroke.JOIN_ROUND, 1f, pattern, 0f));
				g2.drawLine (pointXDepart, pointYDepart, pointXArrivee, pointYArrivee);
				this.dessinerTriangleFerme(g2, pointXArrivee, pointYArrivee,pointXDepart ,pointYDepart );
				break;
		}
		
		// Réinitialiser le stroke
		g2.setStroke(new BasicStroke(1f));

		double angleRotation = Math.atan2(pointYArrivee - pointYDepart, pointXArrivee - pointXDepart);
			

		if(angleRotation > 1.5)
		{
			angleRotation -= Math.PI;
		}
		else if (angleRotation < -1.5)
		{ 
			angleRotation += Math.PI;
		}
		
		// Afficher les multiplicités
		if (this.multipliciteSource != null && !this.multipliciteSource.isEmpty()) 
		{
			if (this.source.getNom().equals(this.cible.getNom()))
			{
				g2.setColor(Couleur.NOIR.getColor());
				int xTextSrc = pointXDepart + 5;
				int yTextSrc = pointYDepart - 10;
				g2.drawString(multipliciteSource, xTextSrc, yTextSrc);
			}
			else
			{
				g2.setColor(Couleur.NOIR.getColor());
				int xTextSrc = pointXDepart + (pointXArrivee - pointXDepart) / 10;
				int yTextSrc = pointYDepart + (pointYArrivee - pointYDepart) / 10 - 5;
				g2.rotate    (angleRotation     , xTextSrc, yTextSrc);
				g2.drawString(multipliciteSource, xTextSrc, yTextSrc);
				g2.rotate    (-angleRotation    , xTextSrc, yTextSrc);
			}
		}
		
		if (this.multipliciteCible != null && !this.multipliciteCible.isEmpty()) 
		{
			if (this.source.getNom().equals(this.cible.getNom()))
			{
				g2.setColor(Couleur.NOIR.getColor());
				int xTextCible = pointXDepart + 5;
				int yTextCible = pointYDepart + 50 + 20;

				
				g2.drawString(multipliciteCible, xTextCible, yTextCible);
			}
			else
			{
				g2.setColor(Couleur.NOIR.getColor());
				int xTextCible = pointXDepart + (pointXArrivee - pointXDepart) * 90 / 100;
				int yTextCible = pointYDepart + (pointYArrivee - pointYDepart) * 90 / 100 - 5;

				g2.rotate    (angleRotation    , xTextCible, yTextCible);
				g2.drawString(multipliciteCible, xTextCible, yTextCible);
				g2.rotate    (-angleRotation   , xTextCible, yTextCible);
			}
		}

		// Afficher le rôle si défini
		if (!role.isEmpty())
		{
			g2.setColor(Couleur.NOIR.getColor());

			// Position du text
			int xTextCible = pointXDepart + (pointXArrivee - pointXDepart) * 70 / 100;
			int yTextCible = pointYDepart + (pointYArrivee - pointYDepart) * 70 / 100 - 5;
			
			// Appliquer la rotation selon l'angle de la flèche
			g2.rotate    (angleRotation , xTextCible, yTextCible);
			g2.drawString(role          , xTextCible, yTextCible);
			g2.rotate    (-angleRotation, xTextCible, yTextCible);
		}

		// Sauvegarder la position finale pour la sélection
		this.posXFin = pointXArrivee;
		this.posYFin = pointYArrivee;
	}

	/**
	 * Dessine la pointe de flèche (non remplie ou remplie selon `ferme`).
	 *
	 * @param g2 contexte graphique
	 * @param x1 coordonnée x du point de départ de la pointe
	 * @param y1 coordonnée y du point de départ de la pointe
	 * @param x2 coordonnée x du point d'arrivée (pointe)
	 * @param y2 coordonnée y du point d'arrivée (pointe)
	 * @param ferme si true la pointe est remplie
	 */
	private void dessinerPointe(Graphics2D g2, int x1, int y1, int x2, int y2, boolean ferme)
	{
		int taille = 10;
		double angle = Math.atan2(y2 - y1, x2 - x1);
		
		int xP1 = (int) (x2 - taille * Math.cos(angle - Math.PI / 6));
		int yP1 = (int) (y2 - taille * Math.sin(angle - Math.PI / 6));
		
		int xP2 = (int) (x2 - taille * Math.cos(angle + Math.PI / 6));
		int yP2 = (int) (y2 - taille * Math.sin(angle + Math.PI / 6));
		
		if (ferme)
		{
			int[] xPoints = {x2, xP1, xP2};
			int[] yPoints = {y2, yP1, yP2};
			g2.fillPolygon(xPoints, yPoints, 3);
		}
		else
		{
			g2.drawLine(x2, y2, xP1, yP1);
			g2.drawLine(x2, y2, xP2, yP2);
		}
	}

	private void dessinerTriangleFerme(Graphics2D g2, int xDepart, int yDepart, int xArrivee, int yArrivee)
	{
		g2.setStroke(new BasicStroke(2f));
		int taillePointe = 12;
		double angleDirection = Math.atan2(yArrivee - yDepart, xArrivee - xDepart);
		
		// Calcul des 3 points du triangle
		int xPointeGauche = (int)(xArrivee - taillePointe * Math.cos(angleDirection - Math.PI / 6));
		int yPointeGauche = (int)(yArrivee - taillePointe * Math.sin(angleDirection - Math.PI / 6));
		
		int xPointeDroite = (int)(xArrivee - taillePointe * Math.cos(angleDirection + Math.PI / 6));
		int yPointeDroite = (int)(yArrivee - taillePointe * Math.sin(angleDirection + Math.PI / 6));
		
		// Dessiner et remplir le triangle
		int[] coordonneesX = {xArrivee, xPointeGauche, xPointeDroite};
		int[] coordonneesY = {yArrivee, yPointeGauche, yPointeDroite};
		
		g2.setColor(Color.WHITE);
		g2.fillPolygon(coordonneesX, coordonneesY, 3);
		
		g2.setColor(Color.BLACK);
		g2.drawPolygon(coordonneesX, coordonneesY, 3);
	}

	/**
	 * Définit le rôle affiché sur la flèche.
	 *
	 * @param role texte du rôle
	 */
	public void setRole(String role) { this.role = role; }

	/**
	 * Vérifie si l'association identifiée par `id` correspond aux bornes
	 * données (`deb` + `fin`).
	 *
	 * @param id identifiant de l'association
	 * @param deb début de la chaine de comparaison
	 * @param fin fin de la chaine de comparaison
	 * @return true si l'association correspond
	 */
	public boolean getAssociationParId(int id, String deb, String fin)
	{
		String lSource = this.source.getMultiplicite().getAssociationParId(id);
		String lCible  = this.cible.getMultiplicite().getAssociationParId(id);

		String finale = ("" + deb + fin).trim();


		if(lSource != null)
		{
			System.out.println(lSource + "   |   " + finale);
			if(lSource.equals(finale))
			{
				return true;
			}
		}

		System.out.println();


		if(lCible != null)
		{
			System.out.println(lCible);
			if(lCible.equals(finale))
			{
				return true;
			}
		}
	
		return false;

	}

	/**
	 * Représentation textuelle de la flèche (utile pour le débogage).
	 *
	 * @return chaîne descriptive de l'objet
	 */
	public String toString() 
	{
		return "Fleche [source="     + source                 + ", cible="         + cible     + ", typeLien=" + typeLien + ", multipliciteSource="
			   + multipliciteSource  + ", multipliciteCible=" + multipliciteCible  + ", role=" + role
			   + ", bidirectionnel=" + bidirectionnel         + ", posXFin="       + posXFin   + ", posYFin="  + posYFin  + "]";
	}

}