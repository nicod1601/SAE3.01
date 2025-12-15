package src.ihm;

import java.awt.BasicStroke;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import src.metier.Couleur;
import src.metier.CreeClass;
import src.metier.Multiplicite;

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
	 * Calcule un vecteur de décalage perpendiculaire à la ligne
	 */
	private static int[] calculerDecalage(int x1, int y1, int x2, int y2, int offset)
	{
		double dx = x2 - x1;
		double dy = y2 - y1;
		double longueur = Math.sqrt(dx * dx + dy * dy);
		
		if (longueur == 0) return new int[]{0, 0};
		
		// Vecteur perpendiculaire normalisé
		double perpX = -dy / longueur;
		double perpY = dx / longueur;
		
		return new int[]
		{
			(int)(perpX * offset),
			(int)(perpY * offset)
		};
	}

	/**
	 * Dessine la flèche avec ses multiplicités
	 */
	public void dessiner(Graphics2D g2, int decalage)
	{
		int xDepFleche = source.getPosX() + source.getLargeur() / 2;
		int yDepFleche = source.getPosY() + source.getHauteur() / 2;
		int xFinFleche = cible.getPosX()  + cible.getLargeur() / 2;
		int yFinFleche = cible.getPosY()  + cible.getHauteur() / 2;
		
		// Calcul du décalage perpendiculaire pour éviter les chevauchements
		int[] offset = calculerDecalage(xDepFleche, yDepFleche, xFinFleche, yFinFleche, decalage);
		xDepFleche  += offset[0];
		yDepFleche  += offset[1];
		xFinFleche  += offset[0];
		yFinFleche  += offset[1];
		
		// Ajuster les points de départ et d'arrivée aux bords des rectangles
		int dx = xFinFleche - xDepFleche;
		int dy = yFinFleche - yDepFleche;
		
		if (Math.abs(dx) > Math.abs(dy)) 
		{
			if (dx > 0) 
			{
				xDepFleche = source.getPosX() + source.getLargeur()     + offset[0];
				xFinFleche = cible.getPosX()                            + offset[0];
				yDepFleche = source.getPosY() + source.getHauteur() / 2 + offset[1];
				yFinFleche = cible.getPosY()  + cible.getHauteur()  / 2 + offset[1];
			} 
			else 
			{
				xDepFleche = source.getPosX()                           + offset[0];
				xFinFleche = cible.getPosX()  + cible.getLargeur()      + offset[0];
				yDepFleche = source.getPosY() + source.getHauteur() / 2 + offset[1];
				yFinFleche = cible.getPosY()  + cible.getHauteur()  / 2 + offset[1];
			}
		}
		else
		{
			if (dy > 0)
			{
				yDepFleche = source.getPosY() + source.getHauteur()     + offset[1];
				yFinFleche = cible.getPosY()                            + offset[1];
				xDepFleche = source.getPosX() + source.getLargeur() / 2 + offset[0];
				xFinFleche = cible.getPosX()  + cible.getLargeur()  / 2 + offset[0];
			} 
			else
			{
				yDepFleche = source.getPosY() + offset[1];
				yFinFleche = cible.getPosY()  + cible.getHauteur()       + offset[1];
				xDepFleche = source.getPosX() + source.getLargeur() / 2  + offset[0];
				xFinFleche = cible.getPosX()  + cible.getLargeur()  / 2  + offset[0];
			}
		}
		
		// Dessiner selon le type de lien
		switch (typeLien) 
		{
			case "association":
				if (bidirectionnel) 
				{
					// Trait simple sans flèche pour bidirectionnel
					g2.setColor (Couleur.GRIS.getColor());
					g2.setStroke(new BasicStroke(2f));
					g2.drawLine (xDepFleche, yDepFleche, xFinFleche, yFinFleche);
				} 
				else
				{
					// Flèche normale pour unidirectionnel
					g2.setColor   (Couleur.GRIS.getColor());
					g2.setStroke  (new BasicStroke(2f));
					g2.drawLine   (xDepFleche, yDepFleche, xFinFleche, yFinFleche);
					dessinerPointe(g2, xDepFleche, yDepFleche, xFinFleche, yFinFleche, false);
				}
				break;
				
			case "heritage":
				// Flèche fermée (triangle plein)
				g2.setColor   (Couleur.CYAN.getColor());
				g2.setStroke  (new BasicStroke(2f));
				g2.drawLine   (xDepFleche, yDepFleche, xFinFleche, yFinFleche);
				dessinerPointe(g2, xDepFleche, yDepFleche, xFinFleche, yFinFleche, true);
				break;
				
			case "interface":
				// Flèche en pointillés
				float[] pattern = {6f, 6f};
				g2.setStroke  (new BasicStroke(2f, BasicStroke.CAP_ROUND, 
				               BasicStroke.JOIN_ROUND, 1f, pattern, 0f));
				g2.setColor   (Couleur.ROUGE.getColor());
				g2.drawLine   (xDepFleche, yDepFleche, xFinFleche, yFinFleche);
				dessinerPointe(g2, xDepFleche, yDepFleche, xFinFleche, yFinFleche, true);
				break;
		}
		
		// Réinitialiser le stroke
		g2.setStroke(new BasicStroke(1f));
		
		// Afficher les multiplicités
		if (multipliciteSource != null && !multipliciteSource.isEmpty()) 
		{
			g2.setColor(Couleur.NOIR.getColor());
			int xTextSrc = xDepFleche + (xFinFleche - xDepFleche) / 10;
			int yTextSrc = yDepFleche + (yFinFleche - yDepFleche) / 10 - 5;
			g2.drawString(multipliciteSource, xTextSrc, yTextSrc);
		}
		
		if (multipliciteCible != null && !multipliciteCible.isEmpty()) 
		{
			g2.setColor(Couleur.NOIR.getColor());
			int xTextCible = xDepFleche + (xFinFleche - xDepFleche) * 9 / 10;
			int yTextCible = yDepFleche + (yFinFleche - yDepFleche) * 9 / 10 - 5;
			g2.drawString(multipliciteCible, xTextCible, yTextCible);
		}

		// Sauvegarder la position finale pour la sélection
		this.posXFin = xFinFleche;
		this.posYFin = yFinFleche;
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
		
		for (CreeClass cl1 : lstClass) 
		{
			// ===== ASSOCIATIONS (attributs) =====
			for (CreeClass cl2 : cl1.getLien().getLstLienAttribut()) 
			{
				String cle1   = cl1.getNom() + "->" + cl2.getNom();
				String cle2   = cl2.getNom() + "->" + cl1.getNom();
				
				boolean bidir = estBidirectionnel(cl1, cl2);
				
				// Si bidirectionnel, ne dessiner qu'une fois
				if (bidir && compteurLiaisons.containsKey(cle2)) {
					continue;
				}
				
				// Calculer le décalage si plusieurs liaisons entre les mêmes classes
				String cleBase = cl1.getNom() + "<->" + cl2.getNom();
				int numLiaison = compteurLiaisons.getOrDefault(cleBase, 0);
				int decalage   = numLiaison * 10;
				compteurLiaisons.put(cleBase, numLiaison + 1);
				compteurLiaisons.put(cle1, 1);
				
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
			
			// ===== HÉRITAGE =====
			for (CreeClass cl2 : cl1.getLien().getLstLienHeritage()) 
			{
				String cleBase = cl1.getNom() + "<->" + cl2.getNom();
				int numLiaison = compteurLiaisons.getOrDefault(cleBase, 0);
				int decalage   = numLiaison * 10;
				compteurLiaisons.put(cleBase, numLiaison + 1);
				
				Fleche fleche = new Fleche(cl1, cl2, "heritage", null, null, false);
				fleche.dessiner(g2, decalage);
				lstFleches.add(fleche);
			}
			
			// ===== INTERFACES =====
			for (CreeClass cl2 : cl1.getLien().getLstLienInterface()) 
			{
				String cleBase = cl1.getNom() + "<->" + cl2.getNom();
				int numLiaison = compteurLiaisons.getOrDefault(cleBase, 0);
				int decalage   = numLiaison * 10;
				compteurLiaisons.put(cleBase, numLiaison + 1);
				
				Fleche fleche = new Fleche(cl1, cl2, "interface", null, null, false);
				fleche.dessiner(g2, decalage);
				lstFleches.add(fleche);
			}
		}
		
		return lstFleches;
	}
}