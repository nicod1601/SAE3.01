package src.ihm.edit;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import src.Controleur;
import src.metier.Couleur;
import src.metier.CreeClass;

/**
 * Panneau permettant de sélectionner les classes UML chargées.
 * Il affiche la liste des graphes UML et notifie le panneau d'information
 * lors d'une sélection.
 */
public class PanneauChoix extends JPanel implements MouseListener
{
	/** Contrôleur principal de l'application */
	private final Controleur controleur;

	/** Liste graphique des fichiers/classes */
	private JList<String> listeFichiers;

	/** Modèle de données de la liste des fichiers */
	private DefaultListModel<String> modeleFichiers;

	/** Fenêtre parente */
	private final FrameEdit frameEdit;

	/** Panneau d'information associé */
	private final PanneauInfo panneauInfo;

	/**
	 * Construit le panneau de choix.
	 *
	 * @param controleur le contrôleur principal
	 * @param frameEdit  la fenêtre parente
	 * @param panneauInfo le panneau d'information associé
	 */
	public PanneauChoix
	(
		Controleur controleur,
		FrameEdit  frameEdit,
		PanneauInfo panneauInfo
	)
	{
		this.controleur  = controleur;
		this.frameEdit   = frameEdit;
		this.panneauInfo = panneauInfo;

		this.configurerPanneau();
		this.creerComposants();
	}

	/**
	 * Configure les propriétés graphiques du panneau.
	 */
	private void configurerPanneau()
	{
		this.setLayout(new BorderLayout());
		this.setPreferredSize(new Dimension(250, 0));
		this.setBackground(Couleur.COULEUR_FOND.getColor());
		this.setBorder    (BorderFactory.
		                   createMatteBorder(0, 0, 0, 2,
			               Couleur.COULEUR_BORDURE.getColor()
			                        )
		);
	}

	/**
	 * Crée et ajoute les composants graphiques du panneau.
	 */
	private void creerComposants()
	{
		JLabel labelEntete = this.creerLabelEntete();

		this.modeleFichiers = new DefaultListModel<>();
		this.listeFichiers  = this.creerListeFichiers();

		JScrollPane scrollFichiers = new JScrollPane(this.listeFichiers);

		scrollFichiers.setBorder                  (new EmptyBorder(10, 10, 10, 10) );
		scrollFichiers.getViewport().setBackground(Couleur.COULEUR_LISTE.getColor());
		scrollFichiers.setBackground              (Couleur.COULEUR_FOND.getColor() );

		this.add(labelEntete,   BorderLayout.NORTH);
		this.add(scrollFichiers, BorderLayout.CENTER);

		this.listeFichiers.addMouseListener(this);
	}

	/**
	 * Crée le label d'en-tête du panneau.
	 *
	 * @return le label d'en-tête
	 */
	private JLabel creerLabelEntete()
	{
		JLabel label = new JLabel(
			"<html><center>📁 Graphes UML chargés</center></html>"
		);

		label.setFont               (new Font("Segoe UI Emoji", Font.BOLD, 13));
		label.setBorder             (new EmptyBorder(15, 10, 15, 10)          );
		label.setHorizontalAlignment(SwingConstants.CENTER                    );
		label.setForeground         (Couleur.COULEUR_TEXTE.getColor()         );
		label.setBackground         (Couleur.COULEUR_FOND.getColor()          );
		label.setOpaque             (true                                     );

		return label;
	}

	/**
	 * Crée la liste graphique des fichiers/classes.
	 *
	 * @return la liste des fichiers
	 */
	private JList<String> creerListeFichiers()
	{
		JList<String> liste = new JList<>(this.modeleFichiers);

		liste.setSelectionMode(
			ListSelectionModel.MULTIPLE_INTERVAL_SELECTION
		);
		liste.setFont               (new Font("Consolas", Font.PLAIN, 12));
		liste.setBorder             (new EmptyBorder(5, 5, 5, 5)         );
		liste.setBackground         (Couleur.COULEUR_LISTE.getColor()    );
		liste.setForeground         (Couleur.COULEUR_TEXTE.getColor()    );
		liste.setSelectionBackground(Couleur.COULEUR_SELECTION.getColor());
		liste.setSelectionForeground(Couleur.BLANC.getColor()            );
		liste.setFixedCellHeight    (28                                  );

		return liste;
	}

	/**
	 * Retourne le modèle de la liste des fichiers.
	 *
	 * @return le modèle de la liste
	 */
	public DefaultListModel<String> getModeleFichiers()
	{
		return this.modeleFichiers;
	}

	/**
	 * Ajoute les classes du contrôleur à la liste si elles n'y figurent pas.
	 */
	public void ajouterFichier()
	{
		if (this.controleur.getLstClass() == null)
		{
			return;
		}

		for (CreeClass classe : this.controleur.getLstClass())
		{
			String nomFichier = classe.getNom();

			if (!this.modeleFichiers.contains(nomFichier))
			{
				this.modeleFichiers.addElement(nomFichier);
			}
		}
	}

	/**
	 * Vide complètement la liste des fichiers.
	 */
	public void viderListe()
	{
		this.modeleFichiers.clear();
	}

	/**
	 * Retourne les fichiers sélectionnés.
	 *
	 * @return la liste des fichiers sélectionnés
	 */
	public java.util.List<String> getFichiersSelectionnes()
	{
		return this.listeFichiers.getSelectedValuesList();
	}

	/**
	 * Retourne tous les fichiers présents dans la liste.
	 *
	 * @return la liste de tous les fichiers
	 */
	public java.util.List<String> getTousFichiers()
	{
		java.util.List<String> fichiers = new java.util.ArrayList<>();

		for (int i = 0; i < this.modeleFichiers.size(); i++)
		{
			fichiers.add(
				this.modeleFichiers.getElementAt(i)
			);
		}

		return fichiers;
	}

	/**
	 * Sélectionne un élément de la liste.
	 *
	 * @param index index à sélectionner (-1 pour désélectionner)
	 */
	public void selectionnerListe(int index)
	{
		if (index == -1)
		{
			this.listeFichiers.clearSelection();
		}
		else
		{
			this.listeFichiers.setSelectedIndex(index);
		}
	}

	/**
	 * Récupère une classe métier à partir de son nom.
	 *
	 * @param nom le nom de la classe
	 * @return la classe correspondante ou null
	 */
	private CreeClass getClasseParNom(String nom)
	{
		if (this.controleur.getLstClass() == null)
		{
			return null;
		}

		for (CreeClass classe : this.controleur.getLstClass())
		{
			if (classe.getNom().equals(nom))
			{
				return classe;
			}
		}

		return null;
	}

	@Override
	public void mouseClicked(MouseEvent evenement)
	{
		int index = this.listeFichiers.locationToIndex(
			evenement.getPoint()
		);

		if (index != -1)
		{
			String nomClasse =
				this.listeFichiers.getModel().getElementAt(index);

			CreeClass classe =
				this.getClasseParNom(nomClasse);

			if (classe != null && this.panneauInfo != null)
			{
				this.panneauInfo.majInfoClasse(nomClasse);
			}
		}
	}

	@Override
	public void mousePressed (MouseEvent evenement) {}

	@Override
	public void mouseReleased(MouseEvent evenement) {}

	@Override
	public void mouseEntered (MouseEvent evenement) {}

	@Override
	public void mouseExited  (MouseEvent evenement) {}
}
