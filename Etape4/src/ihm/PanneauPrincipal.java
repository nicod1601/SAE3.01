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

public class PanneauPrincipal extends JPanel implements MouseListener, MouseMotionListener
{
	private Controleur ctrl;
	private FrameAppli frame;
	
	private int indexSelectionner;
	private int indexFlecheSelec;
	private int sourisX;
	private int sourisY;
	
	private double offsetX = 0;
	private double offsetY = 0;

	private boolean rightMousePressed;
	private boolean inClass;

	private List<CreeClass> lstClass;
	private List<Fleche> lstFleches;
	private List<Integer[]> lstCordFleche;
	
	// Controleur
	public PanneauPrincipal(Controleur ctrl, FrameAppli frame) 
	{
		this.setLayout(new BorderLayout());
		this.setBackground(Couleur.BLANC.getColor());
		this.ctrl = ctrl;
		this.frame = frame;
		this.lstClass = new ArrayList<>();
		this.indexSelectionner = -1;
		this.sourisX = 0;
		this.sourisY = 0;
		this.inClass = false;
		this.rightMousePressed = false;

		this.indexFlecheSelec = -1;
		this.lstFleches    = new ArrayList<>();
		this.lstCordFleche = new ArrayList<>();

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
			List<Attribut> lstAttributs = classe.getLstAttribut();
			List<Methode> lstMethodes = classe.getLstMethode();

			int heightTitre = 2 * 24;
			int heightAttributs = (lstAttributs.size() > 3 ? 4 : lstAttributs.size()) * 20 + 20;
			int heightMethodes  = (lstMethodes.size()  > 3 ? 4 : lstMethodes.size() ) * 20 + 20;
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
			if (c.getLien() != null && c.getLien().getLstLienHeritage() != null)
			{
				for (CreeClass classeMere : c.getLien().getLstLienHeritage())
				{
					this.lstFleches.add(new Fleche(classeMere, c, "heritage", "", "", false));
				}
			}
		}

		/*---------------------------------------------*/
		/* Dessiner les associations avec interface    */
		/*---------------------------------------------*/

		for(CreeClass c : lstClass)
		{
			if (c.getLien() != null && c.getLien().getLstLienInterface() != null)
			{
				for (CreeClass classeInterface : c.getLien().getLstLienInterface())
				{
					this.lstFleches.add(new Fleche(classeInterface, c, "interface", "", "", false));
				}
			}
		}

		/*---------------------------------------------*/
		/* Dessiner les associations avec multiplicité */
		/*---------------------------------------------*/

		for(CreeClass c : lstClass)
		{
			Multiplicite   multiC    = c.getMultiplicite();

			// Class : [0..* , 1..1, ...]
			Map<CreeClass, List<List<String>>> mapMultiplC = multiC.getMapMultiplicites();
			
			
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

		int espace = 0;
		for (Fleche fl : this.lstFleches)
		{
			fl.dessiner(g2,espace);
			espace += 20;
		}

		/*---------------------------------------------*/
		/* Zoom d'une Class                            */
		/*---------------------------------------------*/

		if(this.rightMousePressed)
		{
			CreeClass c = lstClass.get(this.indexSelectionner);

			List<Attribut> lstAttributs = c.getLstAttribut();
			List<Methode>  lstMethodes  = c.getLstMethode();

			int heightTitre = 2 * 40;
			int heightAttributs = (lstAttributs.size() > 3 ? 4 : lstAttributs.size()) * 40 + 40;
			int heightMethodes  = (lstMethodes.size()  > 3 ? 4 : lstMethodes.size() ) * 40 + 40;
			int totalHeight = heightTitre + heightAttributs + heightMethodes;

			//calculer la largeur nécessaire
			int width = calculerLargeur(c, lstAttributs,  lstMethodes, 2);

			// Utiliser les positions ACTUELLES de la classe pour dessiner
			dessinerRectangle(c.getPosX()-c.getLargeur()/2, c.getPosY()-c.getHauteur()/2, width, totalHeight, g2, heightTitre, heightAttributs, this.indexSelectionner, 2);
			dessinerContenu  (c, lstAttributs, lstMethodes, g2, c.getPosX()-c.getLargeur()/2, c.getPosY()-c.getHauteur()/2, width, heightTitre, heightAttributs, 2);
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

		int tolerance = 5;

		for(int cpt = 0; cpt < this.lstCordFleche.size(); cpt++) 
		{
			Integer[] tabInfo = this.lstCordFleche.get(cpt);
			
			int x = tabInfo[0];
			int y = tabInfo[1];
			
			// Calcul de la distance entre le clic et la pointe de la flèche
			double distance = Math.sqrt(Math.pow(realX - x, 2) + Math.pow(realY - y, 2));
			
			if (distance <= tolerance)
			{
				this.indexFlecheSelec = cpt;
				return;
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
		String nomClasse = classe.getNom();
		int largeurNom = g2.getFontMetrics().stringWidth(nomClasse);
		String typeClass = classe.getType();
		int largeurTypeClass = g2.getFontMetrics().stringWidth(typeClass) + 25;

		//taille de la police en fonction du zoom
		if (zoom == 2)
		{
			g2.setFont(new Font("Arial", Font.PLAIN, 16));
		}
		else
		{
			g2.setFont(new Font("Arial", Font.PLAIN, 12));
		}

		g2.drawString(nomClasse, posX + (width - largeurNom) / 2, posY + 25);

		if(!typeClass.equals("class"))
			g2.drawString("<<" + typeClass + ">>", posX + (width - largeurTypeClass) / 2, posY + 40);

		int cptAttr = 1;
		// Attributs ---------------------------------------------------------------------------------------------------
		for(Attribut attr : lstAttributs) 
		{
			String symbole = "";
			String finale = "";
			String typeAttr = "";
			String AlligneGauche;
			
			if (cptAttr >= 4 && zoom == 1)
			{
				AlligneGauche = "...";
			}
			else 
			{
				switch (attr.getVisibilite())
				{
					case "public":    symbole = "+ "; break;
					case "private":   symbole = "- "; break;
					case "protected": symbole = "# "; break;
					default:          symbole = "~ "; break;
				}

				if(attr.isEstFinal()) 
				{
					finale = " <<freeze>>";
				}

				String nomAttr = attr.getNom();
				typeAttr = ": " + attr.getType() + finale;
				AlligneGauche = " " + symbole + " " + nomAttr;
			}
			
			int largeurType = g2.getFontMetrics().stringWidth(typeAttr);
			int xGauche = posX + 10; 
			int xType = posX + width - largeurType - 10; 

			g2.drawString(AlligneGauche, xGauche, posY + heightTitre + 20 + (lstAttributs.indexOf(attr) * 20));
			g2.drawString(typeAttr, xType, posY + heightTitre + 20 + (lstAttributs.indexOf(attr) * 20));

			if (cptAttr >= 4)
			{
				break;
			}
			if(attr.isEstStatic()) 
			{
				int yUnderline = posY + heightTitre + 20 + (lstAttributs.indexOf(attr) * 20) + 2;
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
				g2.drawString("...", posX + 10, posY + heightTitre + heightAttributs + 20 + (index * 20));
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
					for (int i = 0; i < params.size() && i <= 2; i++)
					{
						String[] p = params.get(i);
						if (i == 2 && zoom == 1 && params.size() > 3)
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
				int largeurType = g2.getFontMetrics().stringWidth(type);
				int xGauche = posX + 10; 
				int xType = posX + width - largeurType - 10;

				int yPos = posY + heightTitre + heightAttributs + 20 + (index * 20);

				g2.drawString(AlligneGauche, xGauche, yPos);
				g2.drawString(type, xType, yPos);

				if(meth.isEstStatic()) 
				{
					int yUnderline = posY + heightTitre + heightAttributs + 20 + (lstMethodes.indexOf(meth) * 20) + 2;
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
		int width = 200 * zoom;
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
			for (int i = 0; i < params.size() && i <= 2; i++)
			{
				String[] p = params.get(i); 
				str += p[1] + " : " + p[0];
				if (i < params.size() - 1  && i < 2)
				{
					str += ",   ";
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
			String str = attr.getNom() + " : " + attr.getType();
			if (attr.isEstFinal())
			{
				str += " <<freeze>>";
			}
			if (width < str.length() * 8 * zoom)
			{
				width = str.length() * 8 * zoom;
			}
		}

		return width;
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
			System.out.println("Nom Fichier reçu : " + nomFichier);
			CreeClass nouvelleClasse = this.ctrl.CreerClass(nomFichier);
			
			for (CreeClass classe : this.lstClass)
			{
				if (classe.getNom().equals(nouvelleClasse.getNom()))
				{
					System.out.println("Classe déjà existante : " + nouvelleClasse.getNom());
					return;
				}
			}

			this.lstClass.add(nouvelleClasse);
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
		this.repaint();
	}
}