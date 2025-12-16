package src.ihm.edit;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import src.Controleur;
import src.ihm.Couleur;
import src.metier.CreeClass;

public class PanneauChoix extends JPanel
{
	private Controleur ctrl;
	private JList<String> listeFichiers;
	private DefaultListModel<String> modeleFichiers;
	private FrameEdit frame;

	public PanneauChoix(Controleur ctrl, FrameEdit frame) 
	{
		this.ctrl = ctrl;
		this.frame = frame;
		
		this.configurerPanneau();
		this.creerComposants();
	}
	
	private void configurerPanneau()
	{
		this.setLayout(new BorderLayout());
		this.setPreferredSize(new Dimension(250, 0));
		this.setBackground(Couleur.COULEUR_FOND.getColor());
		this.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 2, Couleur.COULEUR_BORDURE.getColor()));
	}
	
	private void creerComposants()
	{
		// Label d'en-tête
		JLabel lblFichiers = this.creerLabelEnTete();
		
		// Liste des fichiers
		this.modeleFichiers = new DefaultListModel<>();
		this.listeFichiers = this.creerListeFichiers();
		
		JScrollPane scrollFichiers = new JScrollPane(listeFichiers);
		scrollFichiers.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		scrollFichiers.getViewport().setBackground(Couleur.COULEUR_LISTE.getColor());
		scrollFichiers.setBackground(Couleur.COULEUR_FOND.getColor());
		this.listeFichiers.setEnabled(false);
		
		// Ajout des composants
		this.add(lblFichiers, BorderLayout.NORTH);
		this.add(scrollFichiers, BorderLayout.CENTER);

		//Activation des composants

		//this.listeFichiers.addMouseListener(this);

	}
	
	private JLabel creerLabelEnTete()
	{
		JLabel label = new JLabel("<html><center>📁 Graph UML chargés<br><br>" +
								  "<span style='font-size:10px; color:#bdc3c7;'>" +
								  "Graph UML<br>sélectionné" +
								  "</span></center></html>");
		label.setFont(new Font("Segoe UI Emoji", Font.BOLD, 13));
		label.setBorder(new EmptyBorder(15, 10, 15, 10));
		label.setHorizontalAlignment(SwingConstants.CENTER);
		label.setForeground(Couleur.COULEUR_TEXTE.getColor());
		label.setBackground(Couleur.COULEUR_FOND.getColor());
		label.setOpaque(true);
		
		return label;
	}
	
	private JList<String> creerListeFichiers()
	{
		JList<String> liste = new JList<>(modeleFichiers);
		liste.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		liste.setFont(new Font("Consolas", Font.PLAIN, 12));
		liste.setBorder(new EmptyBorder(5, 5, 5, 5));
		liste.setBackground(Couleur.COULEUR_LISTE.getColor());
		liste.setForeground(Couleur.COULEUR_TEXTE.getColor());
		liste.setSelectionBackground(Couleur.COULEUR_SELECTION.getColor());
		liste.setSelectionForeground(Couleur.BLANC.getColor());
		liste.setFixedCellHeight(28);
		
		return liste;
	}

	
	/**
	 * Retourne le modèle de liste pour permettre au PanneauMenu d'ajouter des fichiers
	 */
	public DefaultListModel<String> getModeleFichiers()
	{
		return this.modeleFichiers;
	}
	
	/**
	 * Ajoute un fichier à la liste
	 */
	public void ajouterFichier()
	{
		if (this.ctrl.getLstClass() == null){ return;}
		for (CreeClass classe : this.ctrl.getLstClass())
		{
			String nomFichier = classe.getNom();
			if (!this.modeleFichiers.contains(nomFichier))
			{
				this.modeleFichiers.addElement(nomFichier);
			}
		}
	}
	
	/**
	 * Vide la liste des fichiers
	 */
	public void viderListe()
	{
		this.modeleFichiers.clear();
	}
	
	/**
	 * Retourne les fichiers sélectionnés
	 */
	public java.util.List<String> getFichiersSelectionnes()
	{
		return this.listeFichiers.getSelectedValuesList();
	}
	
	/**
	 * Retourne tous les fichiers de la liste
	 */
	public java.util.List<String> getTousFichiers()
	{
		java.util.List<String> fichiers = new java.util.ArrayList<>();
		for (int i = 0; i < this.modeleFichiers.size(); i++)
		{
			fichiers.add(this.modeleFichiers.getElementAt(i));
		}
		return fichiers;
	}

	public void selectionnerList(int index)
	{
		if(index == -1)
		{
			this.listeFichiers.clearSelection();
		}
		else
		{
			this.listeFichiers.setSelectedIndex(index);
		}

	}
}