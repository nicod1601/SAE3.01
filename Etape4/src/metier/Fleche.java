package src.metier;

import java.awt.BasicStroke;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class Fleche
{
	private CreeClass source;
	private CreeClass cible;

	private String typeLien;
	private String multipliciteSource;
	private String multipliciteCible;
	
	private boolean bidirectionnel;
	
	private int posXFin;
	private int posYFin;
	
	public Fleche(CreeClass source, CreeClass cible, String typeLien, String multSrc, String multCible, boolean bidir)
	{
		this.source             = source;
		this.cible              = cible;
		this.typeLien           = typeLien;
		this.multipliciteSource = multSrc;
		this.multipliciteCible  = multCible;
		this.bidirectionnel     = bidir;
		this.posXFin            = 0;
		this.posYFin            = 0;
	}

	// Getters
	public int getPosXFin()      { return posXFin;  }
	public int getPosYFin()      { return posYFin;  }
	public CreeClass getSource() { return source;   }
	public CreeClass getCible()  { return cible;    }
	public String getTypeLien()  { return typeLien; }

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
	 * Crée une clé normalisée pour deux classes (ordre alphabétique)
	 */
	private static String creerCleNormalisee(String nom1, String nom2, String typeLien)
	{
		if (nom1.compareTo(nom2) < 0) {
			return nom1 + "<->" + nom2 + ":" + typeLien;
		} else {
			return nom2 + "<->" + nom1 + ":" + typeLien;
		}
	}

	/**
	 * Dessine la flèche avec ses multiplicités
	 */
	public void dessiner(Graphics2D g2, int decalage)
	{
		// Calcul des centres des classes
		int centreXDepart = source.getPosX() + source.getLargeur() / 2;
		int centreYDepart = source.getPosY() + source.getHauteur() / 2;
		int centreXArrivee = cible.getPosX() + cible.getLargeur() / 2;
		int centreYArrivee = cible.getPosY() + cible.getHauteur() / 2;
		
		// Calculer la différence de position
		double deltaX = centreXArrivee - centreXDepart;
		double deltaY = centreYArrivee - centreYDepart;
		
		// Déterminer les points de connexion selon la position relative des classes
		int pointXDepart = centreXDepart;
		int pointYDepart = centreYDepart;
		int pointXArrivee = centreXArrivee;
		int pointYArrivee = centreYArrivee;
		
		// Calculer le vecteur perpendiculaire pour le décalage
		double perpX = 0;
		double perpY = 0;
		if (decalage != 0) 
		{
			double longueur = Math.sqrt(deltaX * deltaX + deltaY * deltaY);
			if (longueur > 0) 
			{
				// Vecteur perpendiculaire normalisé
				perpX = -deltaY / longueur * decalage;
				perpY = deltaX / longueur * decalage;
			}
		}
		
		// Si l'angle est plus vertical qu'horizontal
		if (Math.abs(deltaY) > Math.abs(deltaX))
		{
			// Connexion verticale (haut/bas)
			if (deltaY < 0) 
			{
				// La classe d'arrivée est AU-DESSUS de la classe de départ
				pointYDepart = source.getPosY(); // Haut de la classe de départ
				pointYArrivee = cible.getPosY() + cible.getHauteur(); // Bas de la classe d'arrivée
			}
			else 
			{
				// La classe d'arrivée est EN-DESSOUS de la classe de départ
				pointYDepart = source.getPosY() + source.getHauteur(); // Bas de la classe de départ
				pointYArrivee = cible.getPosY(); // Haut de la classe d'arrivée
			}
			
			// Pour les connexions verticales, appliquer le décalage uniquement sur X
			pointXDepart = centreXDepart + (int)perpX;
			pointXArrivee = centreXArrivee + (int)perpX;
		}
		else
		{
			// Connexion horizontale (gauche/droite)
			if (deltaX < 0) 
			{
				// La classe d'arrivée est À GAUCHE de la classe de départ
				pointXDepart = source.getPosX(); // Gauche de la classe de départ
				pointXArrivee = cible.getPosX() + cible.getLargeur(); // Droite de la classe d'arrivée
			}
			else 
			{
				// La classe d'arrivée est À DROITE de la classe de départ
				pointXDepart = source.getPosX() + source.getLargeur(); // Droite de la classe de départ
				pointXArrivee = cible.getPosX(); // Gauche de la classe d'arrivée
			}
			
			// Pour les connexions horizontales, appliquer le décalage uniquement sur Y
			pointYDepart = centreYDepart + (int)perpY;
			pointYArrivee = centreYArrivee + (int)perpY;
		}
		
		// Dessiner selon le type de lien
		switch (typeLien) 
		{
			case "association":
				if (bidirectionnel) 
				{
					// Trait simple sans flèche pour bidirectionnel
					g2.setColor(Couleur.GRIS.getColor());
					g2.setStroke(new BasicStroke(2f));
					g2.drawLine(pointXDepart, pointYDepart, pointXArrivee, pointYArrivee);
				} 
				else
				{
					// Flèche normale pour unidirectionnel
					g2.setColor(Couleur.GRIS.getColor());
					g2.setStroke(new BasicStroke(2f));
					g2.drawLine(pointXDepart, pointYDepart, pointXArrivee, pointYArrivee);
					dessinerPointe(g2, pointXDepart, pointYDepart, pointXArrivee, pointYArrivee, false);
				}
				break;
				
			case "heritage":
				// Flèche fermée (triangle plein)
				g2.setColor(Couleur.CYAN.getColor());
				g2.setStroke(new BasicStroke(2f));
				g2.drawLine(pointXDepart, pointYDepart, pointXArrivee, pointYArrivee);
				dessinerPointe(g2, pointXDepart, pointYDepart, pointXArrivee, pointYArrivee, true);
				break;
				
			case "interface":
				// Flèche en pointillés
				float[] pattern = {6f, 6f};
				g2.setStroke(new BasicStroke(2f, BasicStroke.CAP_ROUND, 
				               BasicStroke.JOIN_ROUND, 1f, pattern, 0f));
				g2.setColor(Couleur.ROUGE.getColor());
				g2.drawLine(pointXDepart, pointYDepart, pointXArrivee, pointYArrivee);
				dessinerPointe(g2, pointXDepart, pointYDepart, pointXArrivee, pointYArrivee, true);
				break;
		}
		
		// Réinitialiser le stroke
		g2.setStroke(new BasicStroke(1f));
		
		// Afficher les multiplicités
		if (multipliciteSource != null && !multipliciteSource.isEmpty()) 
		{
			g2.setColor(Couleur.NOIR.getColor());
			int xTextSrc = pointXDepart + (pointXArrivee - pointXDepart) / 10;
			int yTextSrc = pointYDepart + (pointYArrivee - pointYDepart) / 10 - 5;
			g2.drawString(multipliciteSource, xTextSrc, yTextSrc);
		}
		
		if (multipliciteCible != null && !multipliciteCible.isEmpty()) 
		{
			g2.setColor(Couleur.NOIR.getColor());
			int xTextCible = pointXDepart + (pointXArrivee - pointXDepart) * 9 / 10;
			int yTextCible = pointYDepart + (pointYArrivee - pointYDepart) * 9 / 10 - 5;
			g2.drawString(multipliciteCible, xTextCible, yTextCible);
		}

		// Sauvegarder la position finale pour la sélection
		this.posXFin = pointXArrivee;
		this.posYFin = pointYArrivee;
	}

	/**
	 * Dessine la pointe de flèche
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
			// Triangle fermé
			int[] xPoints = {x2, xP1, xP2};
			int[] yPoints = {y2, yP1, yP2};
			g2.fillPolygon(xPoints, yPoints, 3);
		} 
		else
		{
			// Flèche ouverte
			g2.drawLine(x2, y2, xP1, yP1);
			g2.drawLine(x2, y2, xP2, yP2);
		}
	}

	/**
	 * Méthode statique pour créer et dessiner toutes les flèches
	 * @return Liste des flèches créées (pour la sélection)
	 */
	public ArrayList<Fleche> dessinerToutesLesFleches(Graphics2D g2, ArrayList<CreeClass> lstClass, Multiplicite multiplicite) 
	{
		ArrayList<Fleche> lstFleches = new ArrayList<>();
		HashMap<String, Integer> compteurLiaisons = new HashMap<>();
		HashSet<String> flechesDessinées = new HashSet<>();
		
		for (CreeClass cl1 : lstClass) 
		{
			for (CreeClass cl2 : cl1.getLien().getLstLienAttribut()) 
			{
				boolean bidir = estBidirectionnel(cl1, cl2);
				
				String cleFleche = cl1.getNom() + "->" + cl2.getNom() + ":association";
				String cleFlecheInverse = cl2.getNom() + "->" + cl1.getNom() + ":association";
				
				// Si bidirectionnel, ne dessiner qu'une fois
				if (bidir && flechesDessinées.contains(cleFlecheInverse))
				{
					continue;
				}
				flechesDessinées.add(cleFleche);
				
				// Calculer le décalage avec clé normalisée
				String cleBase = creerCleNormalisee(cl1.getNom(), cl2.getNom(), "association");
				int numLiaison = compteurLiaisons.getOrDefault(cleBase, 0);
				
				// Décalage alterné : 0, +15, -15, +30, -30, etc.
				int decalage = (numLiaison == 0) ? 0 : 
				               (numLiaison % 2 == 1) ? (numLiaison + 1) / 2 * 15 : 
				               -(numLiaison / 2 * 15);
				
				compteurLiaisons.put(cleBase, numLiaison + 1);
				
				// Récupérer les multiplicités depuis la classe Multiplicite
				String multSrc   = "1";
				String multCible = "1..*";
				
				// Essayer de récupérer les vraies multiplicités si disponibles
				if (multiplicite != null && multiplicite.getMapMultiplicites() != null)
				{
					HashMap<CreeClass, List<List<String>>> map = multiplicite.getMapMultiplicites();
					if (map.containsKey(cl2))
					{
						List<List<String>> lstMult = map.get(cl2);
						if (lstMult != null && !lstMult.isEmpty())
						{
							List<String> paireMult = lstMult.get(0);
							if (paireMult.size() >= 2)
							{
								multSrc = paireMult.get(0);
								multCible = paireMult.get(1);
							}
						}
					}
				}
				
				Fleche fleche = new Fleche(cl1, cl2, "association", multSrc, multCible, bidir);
				fleche.dessiner(g2, decalage);
				lstFleches.add(fleche);
			}
			
			for (CreeClass cl2 : cl1.getLien().getLstLienHeritage()) 
			{
				String cleFleche = cl1.getNom() + "->" + cl2.getNom() + ":heritage";
				
				if (flechesDessinées.contains(cleFleche)) {
					continue;
				}
				flechesDessinées.add(cleFleche);
				
				String cleBase = creerCleNormalisee(cl1.getNom(), cl2.getNom(), "heritage");
				int numLiaison = compteurLiaisons.getOrDefault(cleBase, 0);
				
				int decalage = (numLiaison == 0) ? 0 : 
				               (numLiaison % 2 == 1) ? (numLiaison + 1) / 2 * 15 : 
				               -(numLiaison / 2 * 15);
				
				compteurLiaisons.put(cleBase, numLiaison + 1);
				
				Fleche fleche = new Fleche(cl1, cl2, "heritage", null, null, false);
				fleche.dessiner(g2, decalage);
				lstFleches.add(fleche);
			}
			
			for (CreeClass cl2 : cl1.getLien().getLstLienInterface()) 
			{
				String cleFleche = cl1.getNom() + "->" + cl2.getNom() + ":interface";
				
				if (flechesDessinées.contains(cleFleche)) {
					continue;
				}
				flechesDessinées.add(cleFleche);
				
				String cleBase = creerCleNormalisee(cl1.getNom(), cl2.getNom(), "interface");
				int numLiaison = compteurLiaisons.getOrDefault(cleBase, 0);
				
				int decalage = (numLiaison == 0) ? 0 : 
				               (numLiaison % 2 == 1) ? (numLiaison + 1) / 2 * 15 : 
				               -(numLiaison / 2 * 15);
				
				compteurLiaisons.put(cleBase, numLiaison + 1);
				
				Fleche fleche = new Fleche(cl1, cl2, "interface", null, null, false);
				fleche.dessiner(g2, decalage);
				lstFleches.add(fleche);
			}
		}
		
		return lstFleches;
	}
}