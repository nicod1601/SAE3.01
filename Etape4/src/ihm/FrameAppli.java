package src.ihm;


import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.File;
import java.util.ArrayList;
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
		this.setExtendedState(JFrame.MAXIMIZED_BOTH);
		this.setLocationRelativeTo(null);
		this.setLayout(new BorderLayout());


		/*-------------------------*/
		/* Création des Composants */
		/*-------------------------*/
		this.ctrl             = ctrl;
		this.panneauFichier   = new PanneauFichier(ctrl, this);
		this.panneauMenu      = new PanneauMenu(ctrl, this);
		this.panneauPrincipal = new PanneauPrincipal(ctrl, this);


		JScrollPane scrollFrame = new JScrollPane(this.panneauPrincipal,
		                                          JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
		                                          JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);

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

	public void viderLstClass()
	{
		this.panneauPrincipal.viderListeClasses();
	}

	public void ajouterFichier(String nomFichier)
	{
		this.panneauFichier.ajouterFichier(nomFichier);
	}

	public void viderListe()
	{
		this.panneauFichier.viderListe();
	}

	public void selectionner(int index)
	{
		this.panneauPrincipal.selectionner(index);
	}

	public void selectionnerList(int index)
	{
		this.panneauFichier.selectionnerList(index);
	}

	public void lstSelectionner(ArrayList<Integer> lst)
	{
		this.panneauPrincipal.lstSelectionner(lst);
	}

	public void exporterEnImage(String chemin, File nomFichier)
	{
		this.panneauPrincipal.exporterEnImage(chemin, nomFichier);
	}

	public DefaultListModel<String> getModeleFichiers()
	{
		return this.panneauFichier.getModeleFichiers();
	}
	
}
