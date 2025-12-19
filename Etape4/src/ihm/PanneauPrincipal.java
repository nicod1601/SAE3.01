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
import src.metier.Attribut;
import src.metier.Couleur;
import src.metier.CreeClass;
import src.metier.Fleche;
import src.metier.Methode;
import src.metier.Multiplicite;
import src.ihm.edit.PopUp;

public class PanneauPrincipal extends JPanel implements MouseListener, MouseMotionListener
{
	static final int ECART_BORD = 20;
	static final int ESPACE_Y   = 15;
	static final int ESPACE_FL  = 25;
	
	private Controleur ctrl;
	private FrameAppli frame;
	
	private int indexSelectionner;
	private int indexFlecheSelec;
	private int sourisX;
	private int sourisY;
	
	private PopUp popup;
	
	private double offsetX;
	private double offsetY;

	private boolean rightMousePressed;
	private boolean inClass;

	private List<CreeClass> lstClass;
	private List<Fleche> lstFleches;
	private List<Integer[]> lstCordFleche;
	private Map<Integer, String> lstRole;
	
	// Controleur
	public PanneauPrincipal(Controleur ctrl, FrameAppli frame) 
	{
		this.ctrl = ctrl;
		this.frame = frame;

		this.indexSelectionner = -1;
		this.indexFlecheSelec = -1;
		this.sourisX = 0;
		this.sourisY = 0;

		this.offsetX = 0;
		this.offsetY = 0;

		this.rightMousePressed = false;
		this.inClass = false;

		this.lstClass = new ArrayList<>();
		this.lstFleches    = new ArrayList<>();
		this.lstCordFleche = new ArrayList<>();
		this.lstRole = new HashMap<Integer, String>();

		this.popup = new PopUp(this.ctrl);

		this.setLayout(new BorderLayout());
		this.setBackground(Couleur.BLANC.getColor());
		this.addMouseListener(this);
		this.addMouseMotionListener(this);
	}

	protected void paintComponent(Graphics g) 
	{
		super.paintComponent(g);
		this.lstFleches.clear();
		this.lstCordFleche.clear();
		
		Graphics2D g2 = (Graphics2D) g;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		
		// Sauvegarde du transform d'origine
		AffineTransform old = g2.getTransform();
		int xOffset = 50;
		int yOffset = 50;
		int maxHeightLigne = 0;
		int espacementX = 50;
		int espacementY = 50;
		int largeurMax = this.getWidth() - 100;
		
		
		/*---------------------------------------------*/
		/* Dessiner les associations avec associations */
		/*---------------------------------------------*/

		for(int cpt = 0; cpt < this.lstClass.size(); cpt++)
		{
			CreeClass classe = this.lstClass.get(cpt);
			if(classe == null)
				continue;

			List<Attribut> lstAttributs = classe.getLstAttribut();
			List<Methode> lstMethodes = classe.getLstMethode();

			if(lstAttributs == null && lstMethodes == null)
				continue;

			int heightTitre = !classe.getType().equals("class") ? 2 * ECART_BORD + ESPACE_Y : 2 * ECART_BORD;
			int heightAttributs = ESPACE_Y;
			int heightMethodes  = ESPACE_Y;

			if(lstAttributs != null)
				heightAttributs = lstAttributs.size() > 3 ? 4 * ESPACE_Y + ECART_BORD : lstAttributs.size() * ESPACE_Y + ECART_BORD;

			if(lstMethodes != null)
				heightMethodes  = lstMethodes.size()  > 3 ? 4 * ESPACE_Y + ECART_BORD : lstMethodes.size()  * ESPACE_Y + ECART_BORD;

			int totalHeight = heightTitre + heightAttributs + heightMethodes;

			//calculer la largeur nécessaire
			int width = calculerLargeur(classe, lstAttributs,  lstMethodes, 1);
			
			// IMPORTANT : N'initialiser la position QUE si elle est à (0,0)
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
				
				// Mettre à jour les positions pour la prochaine classe
				maxHeightLigne = Math.max(maxHeightLigne, totalHeight);
				xOffset += width + espacementX;
			}
			
			// Utiliser les positions ACTUELLES de la classe pour dessiner
			int posX = classe.getPosX();
			int posY = classe.getPosY();

			//Dessiner le rectangle de la classe
			this.dessinerRectangle(posX, posY, width, totalHeight, g2, heightTitre, heightAttributs, cpt, 1);

			// Dessiner le contenu de la classe
			this.dessinerContenu(classe, lstAttributs, lstMethodes, g2, posX, posY, width, heightTitre, heightAttributs, 1);
			
			// Mettre à jour la largeur et hauteur
			this.lstClass.get(cpt).setLargeur(width);
			this.lstClass.get(cpt).setHauteur(totalHeight);
		}

		/*---------------------------------------------*/
		/* Dessiner les associations avec associations */
		/*---------------------------------------------*/

		List<List<List<String>>> associations = new ArrayList<List<List<String>>>();
		for(CreeClass c : lstClass)
		{
			if (c != null && c.getLien() != null && c.getLien().getLstLienHeritage() != null)
			{
				for (CreeClass classeMere : c.getLien().getLstLienHeritage())
				{
					this.lstFleches.add(this.ctrl.ajouterFleche(classeMere, c, "heritage", "", "", false));
				}
			}
		}

		/*---------------------------------------------*/
		/* Dessiner les associations avec interface    */
		/*---------------------------------------------*/

		for(CreeClass c : lstClass)
		{
			if (c != null && c.getLien() != null && c.getLien().getLstLienInterface() != null)
			{
				for (CreeClass classeInterface : c.getLien().getLstLienInterface())
				{
					this.lstFleches.add(this.ctrl.ajouterFleche(classeInterface, c, "interface", "", "", false));
				}
			}
		}

		/*---------------------------------------------*/
		/* Dessiner les associations avec multiplicité */
		/*---------------------------------------------*/

		for(CreeClass c : lstClass)
		{
			Multiplicite multiC = null;
			if(c != null)
				multiC = c.getMultiplicite();

			// Class : [0..* , 1..1, ...]
			Map<CreeClass, List<List<String>>> mapMultiplC = null;
			if( multiC != null)
				mapMultiplC = multiC.getMapMultiplicites();
			
			
			if (mapMultiplC != null)
			{
				for (CreeClass c2 : mapMultiplC.keySet())
				{
					int nbFleches = 0;
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

									this.lstFleches.add(new Fleche (c,c2,"association",multi.get(0),multi.get(1), estBidirectionnel));

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

		if(!this.lstRole.isEmpty())
		{
			for (Map.Entry<Integer, String> entry : this.lstRole.entrySet())
			{
				Integer id = entry.getKey();
				String role = entry.getValue();

				this.lstFleches.get(id).setRole(role);
			}
		}

		int espaceFl = 0;
		for (Fleche fl : this.lstFleches)
		{
			fl.dessiner(g2, espaceFl);
			
			Integer[] coords = new Integer[2];
			coords[0] = fl.getPosXFin();
			coords[1] = fl.getPosYFin();
			this.lstCordFleche.add(coords);
			
			espaceFl += ESPACE_Y;
		}

		this.ctrl.setLstFleche(this.lstFleches);

		/*---------------------------------------------*/
		/* Zoom d'une Class                            */
		/*---------------------------------------------*/

		if(this.rightMousePressed)
		{
			CreeClass c = lstClass.get(this.indexSelectionner);

			List<Attribut> lstAttributs = c.getLstAttribut();
			List<Methode>  lstMethodes  = c.getLstMethode();

			int zoom = 2;

			int heightTitre     = !c.getType().equals("class") ? 2 * ECART_BORD * zoom : ECART_BORD * 2 + ESPACE_Y * zoom;
			int heightAttributs = ECART_BORD * 2 + lstAttributs.size() * ESPACE_Y * zoom;
			int heightMethodes  = ECART_BORD * 2 + lstMethodes.size()  * ESPACE_Y * zoom;
			int totalHeight = heightTitre + heightAttributs + heightMethodes;

			//calculer la largeur nécessaire
			int width = calculerLargeur(c, lstAttributs,  lstMethodes, 2);

			// Utiliser les positions ACTUELLES de la classe pour dessiner
			dessinerRectangle(c.getPosX()-c.getLargeur()/2, c.getPosY()-c.getHauteur()/2, width, totalHeight, g2, heightTitre, heightAttributs, this.indexSelectionner, zoom);
			dessinerContenu  (c, lstAttributs, lstMethodes, g2, c.getPosX()-c.getLargeur()/2, c.getPosY()-c.getHauteur()/2, width, heightTitre, heightAttributs, zoom);
		}
			
		// Restauration transform
		g2.setTransform(old);
	}



	/*--------------------------------------------------*/
	/* Gestion des événements de la souris              */
	/*--------------------------------------------------*/

	public void mouseClicked(MouseEvent e)
	{
		this.inClass = false;
		double realX = (e.getX() - offsetX);
		double realY = (e.getY() - offsetY);

		int tolerance = 10;

		for(int cpt = 0; cpt < this.lstCordFleche.size(); cpt++) 
		{
			Integer[] tabInfo = this.lstCordFleche.get(cpt);
			
			int x = tabInfo[0];
			int y = tabInfo[1];

			double distance = Math.sqrt(Math.pow(realX - x, 2) + Math.pow(realY - y, 2));
			
			if (distance <= tolerance)
			{
				this.indexFlecheSelec = cpt;
				System.out.println(">>> FLÈCHE DÉTECTÉE ! <<<");
				
				if (e.getClickCount() == 2)
				{
					System.out.println(">>> DOUBLE-CLIC DÉTECTÉ ! <<<");
					if (cpt >= 0 && cpt < this.lstFleches.size())
					{
						Fleche fleche = this.lstFleches.get(cpt);
						System.out.println(fleche);

						this.popup.setIndexFleche(this.indexFlecheSelec);

						
						// Afficher une boîte de dialogue
						/*JOptionPane.showMessageDialog(this, 
							"Id : " + this.indexFlecheSelec + "\n" +
							"De : " + fleche.getSource().getNom() + "\n" +
							"Vers : " + fleche.getCible().getNom(), 
							"Info Flèche",
							JOptionPane.INFORMATION_MESSAGE);*/
					}
					return;
				}
			}
		}



		for(int cpt = 0; cpt < this.lstClass.size(); cpt++)
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


	public void mousePressed(MouseEvent e)
	{
		this.inClass = false;

		for(int cpt = 0; cpt < this.lstClass.size(); cpt++)
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

				this.sourisX = (int)(realX - x);
				this.sourisY = (int)(realY - y);

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
	public void mouseReleased(MouseEvent e)
	{
		if (SwingUtilities.isRightMouseButton(e)) 
		{
			this.rightMousePressed = false;
			this.repaint();
		}
	}
	public void mouseDragged(MouseEvent e) 
	{
		if (SwingUtilities.isLeftMouseButton(e)) 
		if (this.inClass == true && this.indexSelectionner >= 0)
		{
			CreeClass classeSelectionnee = this.lstClass.get(this.indexSelectionner);
			
			double realX = (e.getX() - offsetX);
			double realY = (e.getY() - offsetY);

			int newX = (int)(realX - this.sourisX);
			int newY = (int)(realY - this.sourisY);
			
			int panelWidth = this.getWidth();
			int panelHeight = this.getHeight();

			int classeWidth  = classeSelectionnee.getLargeur();
			int classeHeight = classeSelectionnee.getHauteur();
			
			int marge = 5;
			
			// Limiter la position X
			if (newX < marge) 
			{
				newX = marge;
			} 
			else if (newX + classeWidth > panelWidth - marge) 
			{
				newX = panelWidth - classeWidth - marge;
			}
			
			// Limiter la position Y
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

	public void mouseEntered(MouseEvent e){}
	public void mouseExited(MouseEvent e){}
	public void mouseMoved(MouseEvent e){}
	
	/*--------------------------------------------*/
	/*                   DESSIN                   */
	/*--------------------------------------------*/


	public void dessinerRectangle(int posX, int posY, int width, int totalHeight, Graphics2D g2, int heightTitre, int heightAttributs, int cpt, int zoom)
	{
		if (zoom == 2)
		{
			g2.setColor(Couleur.BLANC.getColor());
			g2.fillRect(posX, posY, width, totalHeight);
		}
		
		if(this.indexSelectionner == cpt )
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

	public void dessinerContenu(CreeClass classe, List<Attribut> lstAttributs, List<Methode> lstMethodes, Graphics2D g2, int posX, int posY, int width, int heightTitre, int heightAttributs, int zoom)
	{
		// Texte : nom de la classe (centré)
		String typeClass = classe.getType();
		String nomClasse = classe.getNom();
		
		int largeurNom;
		int largeurTypeClass;
		int espaceZoom =  0;
		int ecartBord  =  0;

		//taille de la police en fonction du zoom
		if (zoom == 2)
		{
			g2.setFont(new Font("Arial", Font.PLAIN, 24));
			largeurNom       = g2.getFontMetrics().stringWidth(nomClasse);
			largeurTypeClass = g2.getFontMetrics().stringWidth(typeClass) + 50;
			espaceZoom = ESPACE_Y   * zoom;
			ecartBord  = ECART_BORD * zoom;
			
		}
		else
		{
			g2.setFont(new Font("Arial", Font.PLAIN, 12));
			largeurNom       = g2.getFontMetrics().stringWidth(nomClasse);
			largeurTypeClass = g2.getFontMetrics().stringWidth(typeClass) + 25;
			espaceZoom = ESPACE_Y  ;
			ecartBord  = ECART_BORD;
		}

		// Titre -------------------------------------------------------------------------------------------------------------------
		g2.drawString(nomClasse, posX + (width - largeurNom) / 2, posY + ecartBord);

		if(!typeClass.equals("class"))
			g2.drawString("<<" + typeClass + ">>", posX + (width - largeurTypeClass) / 2, posY + ecartBord + espaceZoom);

		
		int maxLargeur = this.largeurMax(classe, lstAttributs, lstMethodes, g2);
		
		int cptAttr = 1;
		// Attributs ---------------------------------------------------------------------------------------------------
		for(Attribut attr : lstAttributs) 
		{
			String symbole        = "";
			String propriete      = "";
			String typeAttr       = "";
			String AlligneGauche;
			String nomAttr;
			
			//sort de la boucle si on est au 4eme attribut
			if (cptAttr >= 4 && zoom == 1)
			{
				AlligneGauche = "...";
				g2.drawString(AlligneGauche, posX + 10, posY + heightTitre + ecartBord + (lstAttributs.indexOf(attr) * espaceZoom));
				break; // Sortir de la boucle après avoir dessiné "..."
			}

			switch (attr.getVisibilite())
			{
				case "public":    symbole = "+ "; break;
				case "private":   symbole = "- "; break;
				case "protected": symbole = "# "; break;
				default:          symbole = "~ "; break;
			}


			nomAttr        = attr.getNom();
			typeAttr       = ": " +  attr.getType() + " " ;

			if (!attr.getPropriete().isEmpty())
			{
			 typeAttr += "{" + attr.getPropriete() + "}" ;
			}
			
			AlligneGauche  = " "  + symbole        + " " + nomAttr;
			
			int xGauche = posX + 10;
			int yPos = posY + heightTitre + ecartBord + (lstAttributs.indexOf(attr) * espaceZoom);
			
			int xType = posX + width - maxLargeur - 30;

			g2.drawString(AlligneGauche, xGauche, yPos);
			g2.drawString(typeAttr, xType, yPos);


			if(attr.isEstStatic()) 
			{
				int yUnderline = yPos + 2;
				g2.setColor(Couleur.GRIS.getColor());
				g2.drawLine(xGauche, yUnderline, xGauche + g2.getFontMetrics().stringWidth(AlligneGauche), yUnderline);
				g2.setColor(Couleur.NOIR.getColor());
			}

			cptAttr++;
		}

		int cptMeth = 1;
		// Méthodes -----------------------------------------------------------------------------------------------------
		int index = 0;
		for (Methode meth : lstMethodes) 
		{
			String symbole = "";
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

				if(type.equals(" : void")){type = "";}

				AlligneGauche = symbole + " " + nomMethode + debP + listP + finP;
				int xGauche = posX + 10; 
				int xType = posX + width - maxLargeur - 10;

				int yPos = posY + heightTitre + heightAttributs + ecartBord + (index * espaceZoom);

				g2.drawString(AlligneGauche, xGauche, yPos);
				g2.drawString(type, xType, yPos);

				if(meth.isEstStatic()) 
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

	/*--------------------------------------------*/
	/*               OUTILS                       */
	/*--------------------------------------------*/

	public int calculerLargeur(CreeClass classe, List<Attribut> lstAttributs, List<Methode> lstMethodes, int zoom)
	{
		// Calculer la largeur nécessaire
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
					str += p[1] + " : " + p[0];
					str += ",   ";
					if (i > 2 && zoom == 1)
					{
						break;
					}
				}
			}

			str += ")";

			if (!classe.getNom().equals(meth.getNom()))
			{
				str += " : " + meth.getType();
			}

			if(width < str.length() * 6 * zoom)
			{
				width = str.length() * 6 * zoom;
			}
		}

		// Vérifier aussi la largeur des attributs
		for (Attribut attr : lstAttributs)
		{
			String str = attr.getNom() + " : " + attr.getType() + " " + "{" + attr.getPropriete() + "}" ;
			
			if (width < str.length() * 8 * zoom)
			{
				width = str.length() * 8 * zoom;
			}
		}

		return width;
	}

	public int largeurMax(CreeClass classe, List<Attribut> lstAttributs, List<Methode> lstMethodes,  Graphics2D g2)
	{
		// Pre-calculer la largeur maximale pour l'alignement des types d'attributs
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
			if(type.equals(" : void")){type = "";}

			int largeurType = g2.getFontMetrics().stringWidth(type);

			if (largeurType > maxLargeurType2) 
			{
				maxLargeurType2 = largeurType;
			}
		}
		
		return Math.max(maxLargeurType,maxLargeurType2);
	}


	//selectionner une class
	public void selectionner(int index)
	{
		for(int cpt = 0; cpt < this.lstClass.size(); cpt++)
		{
			if(index == cpt)
			{
				this.indexSelectionner = cpt;
			}
		}
		this.repaint();
	}

	//exporter le panneau en image
	public void exporterEnImage(String chemin, File fichier)
	{
		BufferedImage image = new BufferedImage(this.getWidth(),
		                                        this.getHeight(), 
		                                        BufferedImage.TYPE_INT_RGB);
		Graphics2D    g     = image.createGraphics();

		this.printAll(g);
		g.dispose();

		try 
		{
			ImageIO.write(image, "png", fichier);
		} catch (IOException e) 
		{
			e.printStackTrace();
		}
	}

	//mettre a jour la liste des classes
	public void majListeClasses(boolean dossier, String nomFichier)
	{
		if(dossier)
		{
			this.lstClass = this.ctrl.getLstClass();
		} 
		else
		{
			CreeClass nouvelleClasse = this.ctrl.CreerClass(nomFichier);
			if(nouvelleClasse == null && nomFichier.endsWith(".uml"))
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

	public void viderListeClasses()
	{
		this.lstClass.clear();
		this.indexSelectionner = -1;
		this.sourisX = 0;
		this.sourisY = 0;
		this.inClass = false;
		this.offsetX = 0;
		this.offsetY = 0;
		this.lstFleches.clear();
		this.lstCordFleche.clear();
		this.indexFlecheSelec = -1;
		this.lstRole.clear();
		this.repaint();
	}

	public void majDessin()
	{
		this.repaint();
	}

	public void setRole(int id, String role)
	{
		this.lstRole.put(id, role);
		
	}

}