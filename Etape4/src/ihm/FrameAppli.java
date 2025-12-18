package src.ihm;


import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.File;
import javax.swing.*;
import src.Controleur;
import src.metier.CreeClass;


public class FrameAppli extends JFrame 
{
	private PanneauPrincipal panneauPrincipal;
	private PanneauFichier   panneauFichier;
	private PanneauMenu      panneauMenu;
	private JScrollPane      scrollFrame;

	private Controleur ctrl;

	public FrameAppli(Controleur ctrl) 
	{
		this.setTitle("Création UML Java");
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		this.setSize(screenSize.width, screenSize.height);
		this.setLocationRelativeTo(null);
		this.setExtendedState(JFrame.MAXIMIZED_BOTH);
		this.setLayout(new BorderLayout());


		/*-------------------------*/
		/* Création des Composants */
		/*-------------------------*/
		this.ctrl             = ctrl;
		this.panneauFichier   = new PanneauFichier(ctrl, this);
		this.panneauMenu      = new PanneauMenu(ctrl, this);
		this.panneauPrincipal = new PanneauPrincipal(ctrl, this);

		this.panneauPrincipal.setPreferredSize(new Dimension(screenSize.width + 10, screenSize.height + 10));
		
		this.scrollFrame = new JScrollPane(this.panneauPrincipal,
		                                          JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
		                                          JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

		scrollFrame.getVerticalScrollBar().setUnitIncrement(50);
		scrollFrame.getHorizontalScrollBar().setUnitIncrement(50);

		/*-------------------------*/
		/* Position des Composants */
		/*-------------------------*/
		this.add(this.panneauFichier, BorderLayout.WEST);
		this.add(scrollFrame, BorderLayout.CENTER);
		this.add(this.panneauMenu, BorderLayout.NORTH);

		this.setVisible(true);
	}

	public int largeurTotal()
	{
		int largeurTotal = 0;
		for ( CreeClass c : this.ctrl.getLstClass() )
		{
			largeurTotal += this.panneauPrincipal.calculerLargeur(c, c.getLstAttribut(), c.getLstMethode(), 1);
		}
		return largeurTotal / (int)(Math.sqrt(this.ctrl.getLstClass().size()));
	}
	public int hauteurTotal()
	{
		int hauteurMax = 0;
		for ( CreeClass c : this.ctrl.getLstClass() )
		{
			int heightTitre = !c.getType().equals("class") ? 2 * PanneauPrincipal.ECART_BORD + panneauPrincipal.ESPACE_Y : 2 * panneauPrincipal.ECART_BORD;
			int heightAttributs = panneauPrincipal.ESPACE_Y;
			int heightMethodes  = panneauPrincipal.ESPACE_Y;

			if(c.getLstAttribut() != null)
				heightAttributs = c.getLstAttribut().size() > 3 ? 4 * panneauPrincipal.ESPACE_Y + panneauPrincipal.ECART_BORD : c.getLstAttribut().size() * panneauPrincipal.ESPACE_Y + panneauPrincipal.ECART_BORD;

			if(c.getLstMethode() != null)
				heightMethodes  = c.getLstMethode().size()  > 3 ? 4 * panneauPrincipal.ESPACE_Y + panneauPrincipal.ECART_BORD : c.getLstMethode().size()  * panneauPrincipal.ESPACE_Y + panneauPrincipal.ECART_BORD;

			int totalHeight = heightTitre + heightAttributs + heightMethodes;
			hauteurMax += totalHeight;
		}
		return hauteurMax / (int)(Math.sqrt(this.ctrl.getLstClass().size()));
	}

	public void majTaileScroll()
	{
		if (this.panneauPrincipal == null) return;

		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

		int lar = this.largeurTotal();
		int hau = this.hauteurTotal();

		this.panneauPrincipal.setPreferredSize(
			new Dimension(screenSize.width + lar, screenSize.height + hau)
		);

		// 🔴 IMPORTANT
		this.panneauPrincipal.revalidate(); // recalcul layout + scrollbars

	}
	public void majListeClasses(boolean dossier, String nomFichier)
	{
		this.panneauPrincipal.majListeClasses(dossier, nomFichier);
		
		this.majTaileScroll();

		this.panneauPrincipal.revalidate();
		this.panneauPrincipal.repaint();
	}

	public void majIHM()
	{
		this.panneauPrincipal.majDessin();
	}

	public void viderLstClass()
	{
		this.panneauPrincipal.viderListeClasses();
		this.desactiverEdit();
	}

	public void ajouterFichier(String nomFichier)
	{
		this.panneauFichier.ajouterFichier(nomFichier);
		this.activerEdit();
	}

	public void viderListe()
	{
		this.panneauFichier.viderListe();
		this.desactiverEdit();
	}

	public void selectionner(int index)
	{
		this.panneauPrincipal.selectionner(index);
	}

	public void selectionnerList(int index)
	{
		this.panneauFichier.selectionnerList(index);
	}

	public void exporterEnImage(String chemin, File nomFichier)
	{
		this.panneauPrincipal.exporterEnImage(chemin, nomFichier);
	}

	public DefaultListModel<String> getModeleFichiers()
	{
		return this.panneauFichier.getModeleFichiers();
	}

	public void activerEdit()
	{
		this.panneauMenu.activerEdit();
	}

	public void desactiverEdit()
	{
		this.panneauMenu.desactiverEdit();
	}
}
