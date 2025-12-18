package src.ihm;


import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.File;
import javax.swing.*;
import src.Controleur;


public class FrameAppli extends JFrame 
{
	private PanneauPrincipal panneauPrincipal;
	private PanneauFichier   panneauFichier;
	private PanneauMenu      panneauMenu;

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

		this.panneauPrincipal.setPreferredSize(new Dimension(screenSize.width * 2, screenSize.height * 2));
		
		JScrollPane scrollFrame = new JScrollPane(this.panneauPrincipal,
		                                          JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
		                                          JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

		scrollFrame.getVerticalScrollBar().setUnitIncrement(50);
		scrollFrame.getHorizontalScrollBar().setUnitIncrement(50);
		scrollFrame.setPreferredSize(new Dimension(800, 600)); // Largeur, Hauteur

		/*-------------------------*/
		/* Position des Composants */
		/*-------------------------*/
		this.add(this.panneauFichier, BorderLayout.WEST);
		this.add(scrollFrame, BorderLayout.CENTER);
		this.add(this.panneauMenu, BorderLayout.NORTH);

		this.setVisible(true);
	}

	public void majListeClasses(boolean dossier, String nomFichier)
	{
		this.panneauPrincipal.majListeClasses(dossier, nomFichier);
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
