package src.ihm;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import src.Controleur;
import src.metier.Couleur;

/**
 * Panneau d'affichage des fichiers chargés dans l'application.
 * <p>
 * Ce panneau affiche une liste des fichiers actuellement chargés.
 * Il permet de visualiser, sélectionner et gérer les fichiers
 * associés au modèle UML de l'application.
 */
public class PanneauFichier extends JPanel
{
	/** Contrôleur principal de l'application. */
	private Controleur               ctrl;

	/** Liste des fichiers affichés dans le panneau. */
	private JList<String>            listeFichiers;

	/** Modèle de données pour la liste des fichiers. */
	private DefaultListModel<String> modeleFichiers;

	/** Fenêtre principale de l'application. */
	private FrameAppli               frame;

	/**
	 * Constructeur du panneau de fichiers.
	 * <p>
	 * Initialise le panneau avec le contrôleur et la fenêtre principale,
	 * puis configure l'interface utilisateur.
	 *
	 * @param ctrl Contrôleur principal de l'application
	 * @param frame Fenêtre principale contenant ce panneau
	 */
	public PanneauFichier(Controleur ctrl, FrameAppli frame) 
	{
		this.ctrl  = ctrl;
		this.frame = frame;
		
		this.configurerPanneau();
		this.creerComposants();
	}
	
	/**
	 * Configure les paramètres graphiques du panneau.
	 * <p>
	 * Définit la disposition, la dimension, la couleur de fond
	 * et la bordure du panneau.
	 */
	private void configurerPanneau()
	{
		this.setLayout       (new BorderLayout()                 );
		this.setPreferredSize(new Dimension(250, 0));
		this.setBackground   (Couleur.COULEUR_FOND.getColor()    );
		this.setBorder       (BorderFactory.createMatteBorder(0, 0, 0, 2, Couleur.COULEUR_BORDURE.getColor()));
	}
	
	/**
	 * Crée et initialise tous les composants du panneau.
	 * <p>
	 * Construit le label d'en-tête, la liste des fichiers avec
	 * sa barre de défilement, puis les ajoute au panneau.
	 */
	private void creerComposants()
	{
		// Label d'en-tête avec icône fichier
		JLabel lblFichiers         = this.creerLabelEnTete();
		
		// Modèle de données pour la liste
		this.modeleFichiers        = new DefaultListModel<>();
		this.listeFichiers         = this.creerListeFichiers();
		
		// Barre de défilement avec les fichiers
		JScrollPane scrollFichiers = new JScrollPane(listeFichiers);


		scrollFichiers.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		scrollFichiers.getViewport().setBackground(Couleur.COULEUR_LISTE.getColor());
		scrollFichiers.setBackground(Couleur.COULEUR_FOND.getColor());
		this.listeFichiers.setEnabled(false);
		
		// Ajout des composants au panneau
		this.add(lblFichiers   , BorderLayout.NORTH);
		this.add(scrollFichiers, BorderLayout.CENTER);
	}
	
	/**
	 * Crée le label d'en-tête du panneau.
	 * <p>
	 * Construit un label avec titre, icône et formatage personnalisé.
	 *
	 * @return label d'en-tête configuré
	 */
	private JLabel creerLabelEnTete()
	{
		JLabel label = new JLabel   ("<html><center>📁 Fichiers chargés</center></html>");
		label.setFont               (new Font("Segoe UI Emoji", Font.BOLD, 13));
		label.setBorder             (new EmptyBorder(15, 10, 15, 10));
		label.setHorizontalAlignment(SwingConstants.CENTER);
		label.setForeground         (Couleur.COULEUR_TEXTE.getColor());
		label.setBackground         (Couleur.COULEUR_FOND.getColor());
		label.setOpaque             (true);
		
		return label;
	}
	
	/**
	 * Crée la liste des fichiers avec formatage personnalisé.
	 * <p>
	 * Configure l'apparence, la police et les couleurs de la liste.
	 *
	 * @return liste des fichiers configurée
	 */
	private JList<String> creerListeFichiers()
	{
		JList<String> liste = new JList<>(modeleFichiers);

		liste.setSelectionMode      (ListSelectionModel.MULTIPLE_INTERVAL_SELECTION     );
		liste.setFont               (new Font("Consolas", Font.PLAIN, 12)    );
		liste.setBorder             (new EmptyBorder(5, 5, 5, 5));
		liste.setBackground         (Couleur.COULEUR_LISTE.getColor()                   );
		liste.setForeground         (Couleur.COULEUR_TEXTE.getColor()                   );
		liste.setSelectionBackground(Couleur.COULEUR_SELECTION.getColor()               );
		liste.setSelectionForeground(Couleur.BLANC.getColor()                           );
		liste.setFixedCellHeight    (28                                          );
		
		return liste;
	}

	
	/**
	 * Retourne le modèle de données de la liste.
	 * <p>
	 * Permet à d'autres composants (comme PanneauMenu) d'ajouter
	 * ou modifier les fichiers affichés.
	 *
	 * @return modèle de liste des fichiers
	 */
	public DefaultListModel<String> getModeleFichiers()
	{
		return this.modeleFichiers;
	}
	
	/**
	 * Ajoute un fichier à la liste s'il n'y existe pas déjà.
	 * <p>
	 * Vérifie l'absence du fichier avant son ajout pour éviter
	 * les doublons.
	 *
	 * @param nomFichier nom du fichier à ajouter
	 */
	public void ajouterFichier(String nomFichier)
	{
		if (!this.modeleFichiers.contains(nomFichier))
		{
			this.modeleFichiers.addElement(nomFichier);
		}
	}
	
	/**
	 * Vide complètement la liste des fichiers.
	 * <p>
	 * Supprime tous les fichiers affichés et vide également
	 * la liste métier associée via le contrôleur.
	 */
	public void viderListe()
	{
		this.modeleFichiers.clear();
		this.ctrl.viderLstMetier();
	}
	
	/**
	 * Retourne les fichiers actuellement sélectionnés dans la liste.
	 *
	 * @return liste des fichiers sélectionnés
	 */
	public java.util.List<String> getFichiersSelectionnes()
	{
		return this.listeFichiers.getSelectedValuesList();
	}
	
	/**
	 * Retourne tous les fichiers présents dans la liste.
	 *
	 * @return liste complète de tous les fichiers
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

	/**
	 * Sélectionne ou désélectionne un fichier dans la liste.
	 * <p>
	 * Si l'index vaut -1, désélectionne tous les fichiers.
	 * Sinon, sélectionne le fichier à l'index spécifié.
	 *
	 * @param index index du fichier à sélectionner, ou -1 pour désélectionner
	 */
	public void selectionnerList(int index)
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
}