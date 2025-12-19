package src.ihm;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.imageio.ImageIO;
import javax.swing.*;

import src.Controleur;
import src.ihm.edit.PopUp;
import src.metier.Attribut;
import src.metier.Couleur;
import src.metier.CreeClass;
import src.metier.Methode;
import src.metier.Multiplicite;

/**
 * Panneau principal de l'application où sont dessinés les diagrammes UML.
 * <p>
 * Ce panneau gère l'affichage des classes, des relations (flèches),
 * ainsi que les interactions souris (déplacement, sélection, zoom).
 */
public class PanneauPrincipal extends JPanel implements MouseListener, MouseMotionListener 
{
	/** Marge standard par rapport aux bords. */
	static final int ECART_BORD = 20;
	
	/** Espacement vertical standard entre les éléments textuels. */
	static final int ESPACE_Y   = 15;
	
	/** Espacement pour les flèches. */
	static final int ESPACE_FL  = 25;

	/*-----------------------------------------------------------------------------------------*/
	/* ATTRIBUTS                                                                               */
	/*-----------------------------------------------------------------------------------------*/

	private Controleur                      ctrl;
	private FrameAppli                      frame;

	/** Index de la classe actuellement sélectionnée (-1 si aucune). */
	private int                             indexSelectionner;
	
	/** Index de la flèche sélectionnée (-1 si aucune). */
	private int                             indexFlecheSelec;
	
	/** Position X de la souris lors du clic (pour le calcul du delta). */
	private int                             sourisX;
	
	/** Position Y de la souris lors du clic (pour le calcul du delta). */
	private int                             sourisY;

	private PopUp                           popup;

	private double                          offsetX;
	private double                          offsetY;

	/** Indique si le clic droit est maintenu (pour le mode zoom). */
	private boolean                         rightMousePressed;
	
	/** Indique si la souris est à l'intérieur d'une classe. */
	private boolean                         inClass;

	/** Liste des classes à dessiner. */
	private List<CreeClass>                 lstClass;
	
	/** Liste de toutes les flèches générées. */
	private List<Fleche>                    lstFlechesGlobal;
	
	/** Coordonnées des pointes de flèches (pour détection). */
	private List<Integer[]>                 lstCordFleche;
	
	/** Map associant des rôles à des classes et des identifiants de liens. */
	private Map<CreeClass, Map<Integer, String>> lstRole;

	private String                          classActuelleEdit;

	/*----------------------------------------------------------------------------------------------------------------*/
	/* CONSTRUCTEUR                                                     */
	/*----------------------------------------------------------------------------------------------------------------*/

	/**
	 * Constructeur du PanneauPrincipal.
	 * * @param ctrl  Le contrôleur de l'application.
	 * @param frame La fenêtre principale de l'application.
	 */
	public PanneauPrincipal(Controleur ctrl, FrameAppli frame) 
	{
		this.ctrl               = ctrl;
		this.frame              = frame;

		this.indexSelectionner  = -1;
		this.indexFlecheSelec   = -1;
		this.sourisX            = 0;
		this.sourisY            = 0;

		this.offsetX            = 0;
		this.offsetY            = 0;

		this.rightMousePressed  = false;
		this.inClass            = false;

		this.lstClass           = new ArrayList<>();
		this.lstFlechesGlobal   = new ArrayList<>();
		this.lstCordFleche      = new ArrayList<>();
		this.lstRole            = new HashMap<CreeClass, Map<Integer, String>>();
		this.popup              = new PopUp(this.ctrl);

		this.setLayout(new BorderLayout());
		this.setBackground(Couleur.BLANC.getColor());
		
		this.addMouseListener(this);
		this.addMouseMotionListener(this);
	}

	/*----------------------------------------------------------------------------------------------------------------*/
	/* DESSIN (PAINT)                                                 */
	/*----------------------------------------------------------------------------------------------------------------*/

	/**
	 * Méthode principale de dessin du composant.
	 * Gère le tracé des classes, des associations et du mode zoom.
	 */
	@Override
	protected void paintComponent(Graphics g) 
	{
		super.paintComponent(g);
		this.lstFlechesGlobal.clear();
		this.lstCordFleche.clear();
		
		Graphics2D g2 = (Graphics2D) g;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		
		// Sauvegarde du transform d'origine
		AffineTransform old = g2.getTransform();
		
		// Variables pour le placement automatique des classes
		int xOffset         = 50;
		int yOffset         = 50;
		int maxHeightLigne  = 0;
		int espacementX     = 50;
		int espacementY     = 50;
		int largeurMax      = this.getWidth() - 100;
		
		/*---------------------------------------------*/
		/* 1. Calcul et Dessin des Classes             */
		/*---------------------------------------------*/

		for(int cpt = 0; cpt < this.lstClass.size(); cpt++)
		{
			CreeClass classe = this.lstClass.get(cpt);
			if(classe == null) continue;

			List<Attribut> lstAttributs = classe.getLstAttribut();
			List<Methode>  lstMethodes  = classe.getLstMethode();

			if(lstAttributs == null && lstMethodes == null) continue;

			// Calcul des hauteurs
			int heightTitre     = !classe.getType().equals("class") ? 2 * ECART_BORD + ESPACE_Y : 2 * ECART_BORD;
			int heightAttributs = ESPACE_Y;
			int heightMethodes  = ESPACE_Y;

			if(lstAttributs != null)
				heightAttributs = lstAttributs.size() > 3 ? 4 * ESPACE_Y + ECART_BORD : lstAttributs.size() * ESPACE_Y + ECART_BORD;

			if(lstMethodes != null)
				heightMethodes  = lstMethodes.size()  > 3 ? 4 * ESPACE_Y + ECART_BORD : lstMethodes.size()  * ESPACE_Y + ECART_BORD;

			int totalHeight = heightTitre + heightAttributs + heightMethodes;

			// Calcul de la largeur nécessaire
			int width = calculerLargeur(classe, lstAttributs,  lstMethodes, 1);
			
			// Initialisation de la position (seulement si 0,0)
			if (classe.getPosX() == 0 && classe.getPosY() == 0)
			{
				// Si on dépasse la largeur max, on passe à la ligne suivante
				if (xOffset + width > largeurMax && cpt > 0)
				{
					xOffset = 50;
					yOffset += maxHeightLigne + espacementY;
					maxHeightLigne = 0;
				}
				
				this.lstClass.get(cpt).setPosX(xOffset);
				this.lstClass.get(cpt).setPosY(yOffset);
				
				// Mise à jour pour la prochaine itération
				maxHeightLigne = Math.max(maxHeightLigne, totalHeight);
				xOffset += width + espacementX;
			}
			
			// Récupération des positions actuelles
			int posX = classe.getPosX();
			int posY = classe.getPosY();

			// Dessiner le rectangle et le contenu
			this.dessinerRectangle(posX, posY, width, totalHeight, g2, heightTitre, heightAttributs, cpt, 1);
			this.dessinerContenu(classe, lstAttributs, lstMethodes, g2, posX, posY, width, heightTitre, heightAttributs, 1);
			
			// Mise à jour des dimensions dans l'objet classe
			this.lstClass.get(cpt).setLargeur(width);
			this.lstClass.get(cpt).setHauteur(totalHeight);
		}

		/*---------------------------------------------*/
		/* 2. Préparation des liens (Héritage)         */
		/*---------------------------------------------*/
		List<List<List<String>>> associations = new ArrayList<List<List<String>>>();
		
		for(CreeClass c : lstClass)
		{
			if (c != null && c.getLien() != null && c.getLien().getLstLienHeritage() != null)
			{
				for (CreeClass classeMere : c.getLien().getLstLienHeritage())
				{
					this.lstFlechesGlobal.add(this.ctrl.ajouterFleche(classeMere, c, "heritage", "", "", false, -1));
				}
			}
		}

		/*---------------------------------------------*/
		/* 3. Préparation des liens (Interface)        */
		/*---------------------------------------------*/
		for(CreeClass c : lstClass)
		{
			if (c != null && c.getLien() != null && c.getLien().getLstLienInterface() != null)
			{
				for (CreeClass classeInterface : c.getLien().getLstLienInterface())
				{
					this.lstFlechesGlobal.add(this.ctrl.ajouterFleche(classeInterface, c, "interface", "", "", false, -1));
				}
			}
		}

		/*---------------------------------------------*/
		/* 4. Préparation des liens (Association/Mult) */
		/*---------------------------------------------*/
		for(CreeClass c : lstClass)
		{
			Multiplicite multiC = null;
			if(c != null)
				multiC = c.getMultiplicite();

			Map<CreeClass, List<List<String>>> mapMultiplC = null;
			if( multiC != null)
				mapMultiplC = multiC.getMapMultiplicites();
			
			int nbFleches = 0;

			if (mapMultiplC != null)
			{
				for (CreeClass c2 : mapMultiplC.keySet())
				{
					for (List<String> multi : mapMultiplC.get(c2))
					{
						if (!multi.isEmpty())
						{
							List<String> lstClassMultiC = new ArrayList<>();
							lstClassMultiC.add(c.getNom());
							lstClassMultiC.add(multi.get(0));

							List<String> lstClassMultiClef = new ArrayList<>();
							lstClassMultiClef.add(c2.getNom());
							lstClassMultiClef.add(multi.get(1));

							if (!multi.get(0).equals("0..*") && IhmCui.verfiDoublon(lstClassMultiC, lstClassMultiClef, associations))
							{
								boolean estBidirectionnel = multi.get(0).equals("1..*") && multi.get(1).equals("1..*") ? true : false;

								this.lstFlechesGlobal.add(new Fleche(c, c2, "association", multi.get(0), multi.get(1), estBidirectionnel, nbFleches++));

								List<List<String>> classMuLti = new ArrayList<List<String>>();
								classMuLti.add(lstClassMultiC);
								classMuLti.add(lstClassMultiClef);

								associations.add(classMuLti);
							}
						}
					}
				}
			}
		}

		/*---------------------------------------------*/
		/* 5. Gestion des Rôles sur les flèches        */
		/*---------------------------------------------*/
		if(!this.lstRole.isEmpty())
		{
			for(CreeClass classRole : this.lstRole.keySet())
			{
				Map<Integer, String> hstemp = lstRole.get(classRole);

				for (int id : hstemp.keySet())
				{
					for (Fleche fl : classRole.getLstFleches())
					{
						if (fl.getId() == id)
						{
							// Recherche de la flèche correspondante dans la liste globale pour mettre à jour son rôle
							for (int cpt = 0; cpt < lstFlechesGlobal.size(); cpt++)
							{
								if(fl.getSource().getNom().equals(lstFlechesGlobal.get(cpt).getSource().getNom()) &&
								   fl.getId() == lstFlechesGlobal.get(cpt).getId())     
								{
									this.lstFlechesGlobal.get(cpt).setRole(hstemp.get(id));
								}
							}
						}
					}
				}
			}
		}

		/*---------------------------------------------*/
		/* 6. Dessin des Flèches                       */
		/*---------------------------------------------*/
		int espaceFl    = 0;
		int maxDecalage = 60; // Décalage maximum avant de recommencer

		for (Fleche fleche : this.lstFlechesGlobal)
		{
			fleche.dessiner(g2, espaceFl);
			
			Integer[] coords = new Integer[2];
			coords[0] = fleche.getPosXFin();
			coords[1] = fleche.getPosYFin();
			this.lstCordFleche.add(coords);
			
			// Incrémenter l'espace pour éviter que les flèches ne se chevauchent trop
			espaceFl += ESPACE_Y;
			
			if (espaceFl > maxDecalage) 
			{
				espaceFl = 0;
			}
		}

		this.ctrl.setLstFleche(this.lstFlechesGlobal);

		/*---------------------------------------------*/
		/* 7. Zoom sur une Classe (Clic Droit)         */
		/*---------------------------------------------*/
		if(this.rightMousePressed && this.indexSelectionner != -1)
		{
			CreeClass c = lstClass.get(this.indexSelectionner);

			List<Attribut> lstAttributs = c.getLstAttribut();
			List<Methode>  lstMethodes  = c.getLstMethode();

			int zoom = 2;

			int heightTitre     = !c.getType().equals("class") ? 2 * ECART_BORD * zoom : ECART_BORD * 2 + ESPACE_Y * zoom;
			int heightAttributs = ECART_BORD * 2 + lstAttributs.size() * ESPACE_Y * zoom;
			int heightMethodes  = ECART_BORD * 2 + lstMethodes.size()  * ESPACE_Y * zoom;
			int totalHeight     = heightTitre + heightAttributs + heightMethodes;

			// Calculer la largeur nécessaire pour le zoom
			int width = calculerLargeur(c, lstAttributs,  lstMethodes, 2);

			// Dessiner la version agrandie centrée sur la position actuelle
			dessinerRectangle(c.getPosX() - c.getLargeur()/2, c.getPosY() - c.getHauteur()/2, width, totalHeight, g2, heightTitre, heightAttributs, this.indexSelectionner, zoom);
			dessinerContenu  (c, lstAttributs, lstMethodes, g2, c.getPosX() - c.getLargeur()/2, c.getPosY() - c.getHauteur()/2, width, heightTitre, heightAttributs, zoom);
		}
			
		// Restauration du contexte graphique
		g2.setTransform(old);
	}

	/*----------------------------------------------------------------------------------------------------------------*/
	/* GESTION ÉVÉNEMENTS SOURIS                                             */
	/*----------------------------------------------------------------------------------------------------------------*/

	@Override
	public void mouseClicked(MouseEvent e) 
	{
		this.inClass = false;
		double realX = (e.getX() - offsetX);
		double realY = (e.getY() - offsetY);

		for (int cpt = 0; cpt < this.lstClass.size(); cpt++) 
		{
			CreeClass c = this.lstClass.get(cpt);

			int x = c.getPosX();
			int y = c.getPosY();
			int w = c.getLargeur();
			int h = c.getHauteur();

			if (realX >= x && realX <= x + w && realY >= y && realY <= y + h) 
			{
				this.indexSelectionner = cpt;
				this.frame.selectionnerList(this.indexSelectionner);
				break;
			} 
			else 
			{
				this.indexSelectionner = -1;
			}
		}
		this.repaint();
	}

	@Override
	public void mousePressed(MouseEvent e) 
	{
		this.inClass = false;

		for (int cpt = 0; cpt < this.lstClass.size(); cpt++) 
		{
			CreeClass c = this.lstClass.get(cpt);

			int x = c.getPosX();
			int y = c.getPosY();
			int w = c.getLargeur();
			int h = c.getHauteur();

			double realX = (e.getX() - offsetX);
			double realY = (e.getY() - offsetY);

			if (realX >= x && realX <= x + w && realY >= y && realY <= y + h) 
			{
				this.indexSelectionner = cpt;
				this.frame.selectionnerList(this.indexSelectionner);
				this.inClass = true;

				this.sourisX = (int) (realX - x);
				this.sourisY = (int) (realY - y);

				if (SwingUtilities.isRightMouseButton(e)) 
				{
					this.rightMousePressed = true;
					this.repaint();
				}
				break;
			}
		}
		this.repaint();
	}

	@Override
	public void mouseReleased(MouseEvent e) 
	{
		if (SwingUtilities.isRightMouseButton(e)) 
		{
			this.rightMousePressed = false;
			this.repaint();
		}
	}

	@Override
	public void mouseDragged(MouseEvent e) 
	{
		if (SwingUtilities.isLeftMouseButton(e))
		{
			if (this.inClass == true && this.indexSelectionner >= 0) 
			{
				CreeClass classeSelectionnee = this.lstClass.get(this.indexSelectionner);

				double realX = (e.getX() - offsetX);
				double realY = (e.getY() - offsetY);

				int newX = (int) (realX - this.sourisX);
				int newY = (int) (realY - this.sourisY);

				int panelWidth   = this.getWidth();
				int panelHeight  = this.getHeight();

				int classeWidth  = classeSelectionnee.getLargeur();
				int classeHeight = classeSelectionnee.getHauteur();

				int marge = 5;

				// Limiter la position X pour rester dans le panneau
				if (newX < marge) 
				{
					newX = marge;
				} 
				else if (newX + classeWidth > panelWidth - marge) 
				{
					newX = panelWidth - classeWidth - marge;
				}

				// Limiter la position Y pour rester dans le panneau
				if (newY < marge) 
				{
					newY = marge;
				} 
				else if (newY + classeHeight > panelHeight - marge) 
				{
					newY = panelHeight - classeHeight - marge;
				}

				classeSelectionnee.setPosX(newX);
				classeSelectionnee.setPosY(newY);

				repaint();
			}
		}
	}

	@Override
	public void mouseEntered(MouseEvent e) {}

	@Override
	public void mouseExited(MouseEvent e) {}

	@Override
	public void mouseMoved(MouseEvent e) {}

	/*----------------------------------------------------------------------------------------------------------------*/
	/* MÉTHODES DE DESSIN                                                 */
	/*----------------------------------------------------------------------------------------------------------------*/

	/**
	 * Dessine le rectangle conteneur d'une classe.
	 * @param posX            Position X du coin haut gauche.
	 * @param posY            Position Y du coin haut gauche.
	 * @param width           Largeur du rectangle.
	 * @param totalHeight     Hauteur totale.
	 * @param g2              Contexte graphique.
	 * @param heightTitre     Hauteur de la zone titre.
	 * @param heightAttributs Hauteur de la zone attributs.
	 * @param cpt             Index de la classe courante.
	 * @param zoom            Facteur de zoom (1 ou 2).
	 */
	public void dessinerRectangle(int posX, int posY, int width, int totalHeight, Graphics2D g2, int heightTitre,
			int heightAttributs, int cpt, int zoom) 
	{
		// Effacer le fond si on est en mode zoom
		if (zoom == 2) 
		{
			g2.setColor(Couleur.BLANC.getColor());
			g2.fillRect(posX, posY, width, totalHeight);
		}

		// Si la classe est sélectionnée, on la dessine en bleu
		if (this.indexSelectionner == cpt) 
		{
			g2.setColor(Couleur.BLEU.getColor());
			g2.drawRect(posX, posY, width, totalHeight);
			g2.setColor(Couleur.NOIR.getColor());

			g2.setColor(Couleur.BLEU.getColor());
			g2.drawLine(posX, posY + heightTitre, posX + width, posY + heightTitre);
			g2.drawLine(posX, posY + heightTitre + heightAttributs, posX + width, posY + heightTitre + heightAttributs);
			g2.setColor(Couleur.NOIR.getColor());
		} 
		else 
		{
			g2.drawRect(posX, posY, width, totalHeight);
			g2.drawLine(posX, posY + heightTitre, posX + width, posY + heightTitre);
			g2.drawLine(posX, posY + heightTitre + heightAttributs, posX + width, posY + heightTitre + heightAttributs);
		}
	}

	/**
	 * Dessine le contenu textuel d'une classe (Nom, stéréotypes, attributs, méthodes).
	 */
	public void dessinerContenu(CreeClass classe, List<Attribut> lstAttributs, List<Methode> lstMethodes, Graphics2D g2,
			int posX, int posY, int width, int heightTitre, int heightAttributs, int zoom) 
	{
		// Texte : nom de la classe (centré)
		String typeClass = classe.getType();
		String nomClasse = classe.getNom();

		int largeurNom;
		int largeurTypeClass;
		int espaceZoom;
		int ecartBord;

		// Configuration de la police en fonction du zoom
		if (zoom == 2) 
		{
			g2.setFont(new Font("Arial", Font.PLAIN, 24));
			largeurNom       = g2.getFontMetrics().stringWidth(nomClasse);
			largeurTypeClass = g2.getFontMetrics().stringWidth(typeClass) + 50;
			espaceZoom       = ESPACE_Y * zoom;
			ecartBord        = ECART_BORD * zoom;
		} 
		else 
		{
			g2.setFont(new Font("Arial", Font.PLAIN, 12));
			largeurNom       = g2.getFontMetrics().stringWidth(nomClasse);
			largeurTypeClass = g2.getFontMetrics().stringWidth(typeClass) + 25;
			espaceZoom       = ESPACE_Y;
			ecartBord        = ECART_BORD;
		}

		// --- Titre ---
		g2.drawString(nomClasse, posX + (width - largeurNom) / 2, posY + ecartBord);

		if (!typeClass.equals("class"))
			g2.drawString("<<" + typeClass + ">>", posX + (width - largeurTypeClass) / 2, posY + ecartBord + espaceZoom);

		int maxLargeur = this.largeurMax(classe, lstAttributs, lstMethodes, g2);

		// --- Attributs ---
		int cptAttr = 1;
		for (Attribut attr : lstAttributs) 
		{
			String symbole;
			String typeAttr;
			String AlligneGauche;
			String nomAttr;

			// Limitation de l'affichage à 4 attributs hors zoom
			if (cptAttr >= 4 && zoom == 1) 
			{
				AlligneGauche = "...";
				g2.drawString(AlligneGauche, posX + 10, posY + heightTitre + ecartBord + (lstAttributs.indexOf(attr) * espaceZoom));
				break;
			}

			switch (attr.getVisibilite()) 
			{
				case "public":    symbole = "+ "; break;
				case "private":   symbole = "- "; break;
				case "protected": symbole = "# "; break;
				default:          symbole = "~ "; break;
			}

			nomAttr = attr.getNom();
			typeAttr = ": " + attr.getType() + " ";

			if (!attr.getPropriete().isEmpty()) 
			{
				typeAttr += "{" + attr.getPropriete() + "}";
			}

			AlligneGauche = " " + symbole + " " + nomAttr;

			int xGauche = posX + 10;
			int yPos    = posY + heightTitre + ecartBord + (lstAttributs.indexOf(attr) * espaceZoom);
			int xType   = posX + width - maxLargeur - 30;

			g2.drawString(AlligneGauche, xGauche, yPos);
			g2.drawString(typeAttr, xType, yPos);

			// Souligner si static
			if (attr.isEstStatic()) 
			{
				int yUnderline = yPos + 2;
				g2.setColor(Couleur.GRIS.getColor());
				g2.drawLine(xGauche, yUnderline, xGauche + g2.getFontMetrics().stringWidth(AlligneGauche), yUnderline);
				g2.setColor(Couleur.NOIR.getColor());
			}

			cptAttr++;
		}

		// --- Méthodes ---
		int cptMeth = 1;
		int index   = 0;
		
		for (Methode meth : lstMethodes) 
		{
			String symbole;
			String AlligneGauche;

			if (cptMeth >= 4 && zoom == 1) 
			{
				g2.drawString("...", posX + 10, posY + heightTitre + heightAttributs + ecartBord + (index * espaceZoom));
			} 
			else 
			{
				switch (meth.getVisibilite()) 
				{
					case "public":    symbole = "+ "; break;
					case "private":   symbole = "- "; break;
					case "protected": symbole = "# "; break;
					default:          symbole = "~ "; break;
				}

				String nomMethode = meth.getNom();
				String debP = " (";

				List<String[]> params = meth.getLstParametres();
				String listP = "";
				
				if (params != null) 
				{
					for (int i = 0; i < params.size(); i++) 
					{
						String[] p = params.get(i);
						if (i == 2 && zoom == 1 && params.size() >= 3) 
						{
							listP += "... ";
							break;
						} 
						else 
						{
							listP += p[1] + " : " + p[0];
							if (i < params.size() - 1)
								listP += ",   ";
						}
					}
				}

				String finP = ")";
				String type = "";
				if (!classe.getNom().equals(meth.getNom())) 
				{
					type = " : " + meth.getType();
				}

				if (type.equals(" : void")) 
				{
					type = "";
				}

				AlligneGauche = symbole + " " + nomMethode + debP + listP + finP;
				int xGauche   = posX + 10;
				int xType     = posX + width - maxLargeur - 10;
				int yPos      = posY + heightTitre + heightAttributs + ecartBord + (index * espaceZoom);

				g2.drawString(AlligneGauche, xGauche, yPos);
				g2.drawString(type, xType, yPos);

				if (meth.isEstStatic()) 
				{
					int yUnderline = posY + heightTitre + heightAttributs + ecartBord + (lstMethodes.indexOf(meth) * espaceZoom) + 2;
					g2.setColor(Couleur.GRIS.getColor());
					g2.drawLine(xGauche, yUnderline, xGauche + g2.getFontMetrics().stringWidth(AlligneGauche), yUnderline);
					g2.setColor(Couleur.NOIR.getColor());
				}

				index++;
			}
			cptMeth++;
		}
	}

	/*----------------------------------------------------------------*/
	/* OUTILS / UTILITAIRES                                           */
	/*----------------------------------------------------------------*/

	/**
	 * Calcule la largeur optimale pour une classe en fonction de ses attributs et méthodes.
	 *
	 * @param classe       La classe concernée.
	 * @param lstAttributs Liste des attributs.
	 * @param lstMethodes  Liste des méthodes.
	 * @param zoom         Facteur de zoom.
	 * @return La largeur en pixels.
	 */
	public int calculerLargeur(CreeClass classe, List<Attribut> lstAttributs, List<Methode> lstMethodes, int zoom) 
	{
		int width = 200;
		for (Methode meth : lstMethodes) 
		{
			String str = "";

			switch (meth.getVisibilite()) 
			{
				case "public":    str += "+ "; break;
				case "private":   str += "- "; break;
				case "protected": str += "# "; break;
				default:          str += "~ "; break;
			}

			str += meth.getNom() + " (";

			List<String[]> params = meth.getLstParametres();
			if (params != null && !params.isEmpty())
			{
				for (int i = 0; i < params.size(); i++)
				{
					String[] p = params.get(i);
					String pType = p.length > 0 ? p[0] : "";
					String pName = p.length > 1 ? p[1] : (p.length == 1 ? p[0] : "");
					str += pName + " : " + pType;
					if (i < params.size() - 1  && i < 2)
					{
						str += ",   ";
					}
				}
			}

			str += ")";

			if (!classe.getNom().equals(meth.getNom())) 
			{
				str += " : " + meth.getType();
			}

			if (width < str.length() * 6 * zoom) 
			{
				width = str.length() * 6 * zoom;
			}
		}

		// Vérifier aussi la largeur des attributs
		for (Attribut attr : lstAttributs) 
		{
			String str = attr.getNom() + " : " + attr.getType() + " " + "{" + attr.getPropriete() + "}";

			if (width < str.length() * 8 * zoom) 
			{
				width = str.length() * 8 * zoom;
			}
		}

		return width;
	}

	/**
	 * Calcule la largeur maximale nécessaire pour l'alignement des types.
	 */
	public int largeurMax(CreeClass classe, List<Attribut> lstAttributs, List<Methode> lstMethodes, Graphics2D g2) 
	{
		// Pré-calculer la largeur maximale pour l'alignement des types d'attributs
		int maxLargeurType = 0;
		for (Attribut attr : lstAttributs) 
		{
			String typeAttr = ": " + attr.getType() + attr.getPropriete();
			int largeurType = g2.getFontMetrics().stringWidth(typeAttr);

			if (largeurType > maxLargeurType) 
			{
				maxLargeurType = largeurType;
			}
		}

		int maxLargeurType2 = 0;
		for (Methode meth : lstMethodes) 
		{
			String type = "";
			if (!classe.getNom().equals(meth.getNom())) 
			{
				type = " : " + meth.getType();
			}
			if (type.equals(" : void")) 
			{
				type = "";
			}

			int largeurType = g2.getFontMetrics().stringWidth(type);

			if (largeurType > maxLargeurType2) 
			{
				maxLargeurType2 = largeurType;
			}
		}

		return Math.max(maxLargeurType, maxLargeurType2);
	}

	/**
	 * Sélectionne une classe par son index.
	 * @param index Index de la classe à sélectionner.
	 */
	public void selectionner(int index) 
	{
		for (int cpt = 0; cpt < this.lstClass.size(); cpt++) 
		{
			if (index == cpt) 
			{
				this.indexSelectionner = cpt;
			}
		}
		this.repaint();
	}

	/**
	 * Exporte le contenu actuel du panneau dans une image.
	 * * @param chemin Chemin de sauvegarde.
	 * @param fichier Fichier cible.
	 */
	public void exporterEnImage(String chemin, File fichier) 
	{
		BufferedImage image = new BufferedImage(this.getWidth(), this.getHeight(), BufferedImage.TYPE_INT_RGB);
		Graphics2D g = image.createGraphics();

		this.printAll(g);
		g.dispose();

		try 
		{
			ImageIO.write(image, "png", fichier);
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
	}

	/**
	 * Met à jour la liste des classes affichées (chargement fichier ou ajout).
	 * * @param dossier Si vrai, récupère la liste du contrôleur.
	 * @param nomFichier Nom du fichier à charger.
	 */
	public void majListeClasses(boolean dossier, String nomFichier) 
	{
		if (dossier) 
		{
			this.lstClass = this.ctrl.getLstClass();
		} 
		else 
		{
			CreeClass nouvelleClasse = this.ctrl.CreerClass(nomFichier);
			if (nouvelleClasse == null && nomFichier.endsWith(".uml")) 
			{
				this.lstClass = this.ctrl.chargerUML();
			} 
			else if (nouvelleClasse == null && nomFichier.endsWith(".ser")) 
			{
				this.lstClass = this.ctrl.chargerSER();
			} 
			else 
			{
				for (CreeClass classe : this.lstClass) 
				{
					if (classe.getNom().equals(nouvelleClasse.getNom())) 
					{
						return;
					}
				}
				this.lstClass.add(nouvelleClasse);
			}
		}
		this.repaint();
	}

	/**
	 * Réinitialise le panneau (vide toutes les listes et sélections).
	 */
	public void viderListeClasses() 
	{
		this.lstClass.clear();
		this.indexSelectionner = -1;
		this.sourisX           = 0;
		this.sourisY           = 0;
		this.inClass           = false;
		this.offsetX           = 0;
		this.offsetY           = 0;
		this.lstFlechesGlobal.clear();
		this.lstCordFleche.clear();
		this.indexFlecheSelec  = -1;
		this.lstRole.clear();
		this.repaint();
	}

	public void majDessin() 
	{
		this.repaint();
	}

	/**
	 * Définit un rôle pour une association spécifique.
	 * * @param id Identifiant de l'association.
	 * @param role Nom du rôle.
	 * @param source Classe source.
	 */
	public void setRole(int id, String role, CreeClass source) 
	{
		Map<Integer, String> hasTemp = new HashMap<Integer, String>();

		// Copie des entrées existantes si la source existe déjà
		if (this.lstRole.containsKey(source))
		{
			hasTemp.putAll(this.lstRole.get(source));
		}
		hasTemp.put(id, role);

		this.lstRole.put(source, hasTemp);
	}

	public List<Fleche> getLstFlecheGlobal() 
	{
		return this.lstFlechesGlobal;
	}
}