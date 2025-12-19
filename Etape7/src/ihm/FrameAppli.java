package src.ihm;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.File;
import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import src.Controleur;
import src.metier.CreeClass;


/**
 * Fenêtre principale de l'application UML.
 *
 * Cette classe assemble les différents panneaux (`PanneauFichier`,
 * `PanneauMenu`, `PanneauPrincipal`) et gère quelques opérations de
 * mise à jour et de calcul de taille pour le scroll.
 */
public class FrameAppli extends JFrame
{
	/** Panneau central affichant les classes et les liaisons. */
	private PanneauPrincipal panneauPrincipal;

	/** Panneau à gauche listant les fichiers/imports. */
	private PanneauFichier   panneauFichier;

	/** Panneau de menu en haut (boutons, actions). */
	private PanneauMenu      panneauMenu;

	/** Conteneur scrollant le panneau principal. */
	private JScrollPane      scrollFrame;

	/** Contrôleur principal de l'application. */
	private Controleur       ctrl;

	/**
	 * Constructeur.
	 *
	 * @param ctrl instance de `Controleur` partagée avec les panneaux
	 */
	public FrameAppli(Controleur ctrl)
	{
		this.setTitle("Création UML Java");
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		Dimension tailleEcran = Toolkit.getDefaultToolkit().getScreenSize();
		this.setSize(tailleEcran.width, tailleEcran.height);
		this.setLocationRelativeTo(null);
		this.setExtendedState(JFrame.MAXIMIZED_BOTH);
		this.setLayout(new BorderLayout());

		/*-------------------------*/
		/* Création des composants */
		/*-------------------------*/
		this.ctrl = ctrl;
		this.panneauFichier   = new PanneauFichier  (ctrl, this);
		this.panneauMenu      = new PanneauMenu     (ctrl, this);
		this.panneauPrincipal = new PanneauPrincipal(ctrl, this);

		this.panneauPrincipal.setPreferredSize(new Dimension(tailleEcran.width + 10, tailleEcran.height + 10));

		this.scrollFrame = new JScrollPane(this.panneauPrincipal,
										   JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
										   JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

		scrollFrame.getVerticalScrollBar().setUnitIncrement(50);
		scrollFrame.getHorizontalScrollBar().setUnitIncrement(50);

		/*-------------------------*/
		/* Position des composants */
		/*-------------------------*/
		this.add(this.panneauFichier, BorderLayout.WEST);
		this.add(scrollFrame, BorderLayout.CENTER);
		this.add(this.panneauMenu, BorderLayout.NORTH);

		this.setVisible(true);
	}

	/**
	 * Délègue la définition d'un rôle sur une classe source.
	 *
	 * @param id identifiant de l'association
	 * @param role texte du rôle
	 * @param source classe source
	 */
	public void setRole(int id, String role, CreeClass source)
	{
		this.panneauPrincipal.setRole(id, role, source);
	}

	/**
	 * Calcule une estimation de la largeur totale nécessaire
	 * pour afficher toutes les classes (utilisée pour dimensionner
	 * le panneau scrollé).
	 *
	 * @return largeur estimée
	 */
	public int largeurTotal()
	{
		int largeurTotal = 0;
		for (CreeClass c : this.ctrl.getLstClass())
		{
			largeurTotal += this.panneauPrincipal.calculerLargeur(c, c.getLstAttribut(), c.getLstMethode(), 1);
		}
		return largeurTotal / (int) (Math.sqrt(this.ctrl.getLstClass().size()));
	}

	/**
	 * Calcule une estimation de la hauteur moyenne des classes
	 * (utilisée pour dimensionner le panneau scrollé).
	 *
	 * @return hauteur estimée
	 */
	public int hauteurTotal()
	{
		int hauteurMax = 0;
		for (CreeClass c : this.ctrl.getLstClass())
		{
			int heightTitre = !c.getType().equals("class")
							  ? 2 * PanneauPrincipal.ECART_BORD + panneauPrincipal.ESPACE_Y
							  : 2 * panneauPrincipal.ECART_BORD;

			int heightAttributs = panneauPrincipal.ESPACE_Y;
			int heightMethodes = panneauPrincipal.ESPACE_Y;

			if (c.getLstAttribut() != null)
			{
				heightAttributs = c.getLstAttribut().size() > 3
								  ? 4 * panneauPrincipal.ESPACE_Y + panneauPrincipal.ECART_BORD
								  : c.getLstAttribut().size() * panneauPrincipal.ESPACE_Y + panneauPrincipal.ECART_BORD;
			}

			if (c.getLstMethode() != null)
			{
				heightMethodes = c.getLstMethode().size() > 3
								 ? 4 * panneauPrincipal.ESPACE_Y + panneauPrincipal.ECART_BORD
								 : c.getLstMethode().size() * panneauPrincipal.ESPACE_Y + panneauPrincipal.ECART_BORD;
			}

			int totalHeight = heightTitre + heightAttributs + heightMethodes;
			hauteurMax += totalHeight;
		}
		return hauteurMax / (int) (Math.sqrt(this.ctrl.getLstClass().size()));
	}

	/**
	 * Met à jour la taille du panneau scrollable en fonction
	 * des estimations de largeur/hauteur.
	 */
	public void majTaileScroll()
	{
		if (this.panneauPrincipal == null) return;

		Dimension tailleEcran = Toolkit.getDefaultToolkit().getScreenSize();

		int lar = this.largeurTotal();
		int hau = this.hauteurTotal();

		this.panneauPrincipal.setPreferredSize(new Dimension(tailleEcran.width + lar, tailleEcran.height + hau));

		this.panneauPrincipal.revalidate(); // recalcul layout + scrollbars
	}

	/**
	 * Met à jour la liste des classes du panneau principal puis
	 * ajuste le scroll et redessine.
	 *
	 * @param dossier true si l'ajout provient d'un dossier
	 * @param nomFichier nom du fichier ajouté
	 */
	public void majListeClasses(boolean dossier, String nomFichier)
	{
		this.panneauPrincipal.majListeClasses(dossier, nomFichier);
		this.majTaileScroll();
		this.panneauPrincipal.revalidate();
		this.panneauPrincipal.repaint();
	}

	/** Demande au panneau principal de rafraîchir le dessin. */
	public void majIHM()
	{
		this.panneauPrincipal.majDessin();
	}

	/** Vide la liste des classes et désactive l'édition. */
	public void viderLstClass()
	{
		this.panneauPrincipal.viderListeClasses();
		this.desactiverEdit();
	}

	/**
	 * Ajoute un fichier à la liste et active l'édition.
	 *
	 * @param nomFichier nom du fichier à ajouter
	 */
	public void ajouterFichier(String nomFichier)
	{
		this.panneauFichier.ajouterFichier(nomFichier);
		this.activerEdit();
	}

	/** Vide la liste de fichiers et désactive l'édition. */
	public void viderListe()
	{
		this.panneauFichier.viderListe();
		this.desactiverEdit();
	}

	/** Sélectionne une classe dans le panneau principal. */
	public void selectionner(int index)
	{
		this.panneauPrincipal.selectionner(index);
	}

	/** Sélectionne un élément dans la liste de fichiers. */
	public void selectionnerList(int index)
	{
		this.panneauFichier.selectionnerList(index);
	}

	/**
	 * Exporte l'affichage en image via le panneau principal.
	 *
	 * @param chemin chemin de destination
	 * @param nomFichier fichier cible
	 */
	public void exporterEnImage(String chemin, File nomFichier)
	{
		this.panneauPrincipal.exporterEnImage(chemin, nomFichier);
	}

	/**
	 * Retourne le modèle de la liste de fichiers (utilisé par l'IHM).
	 *
	 * @return modèle de liste
	 */
	public DefaultListModel<String> getModeleFichiers()
	{
		return this.panneauFichier.getModeleFichiers();
	}

	/**
	 * Active les commandes d'édition dans le menu.
	 */
	public void activerEdit()
	{
		this.panneauMenu.activerEdit();
	}

	/**
	 * Désactive les commandes d'édition dans le menu.
	 */
	public void desactiverEdit()
	{
		this.panneauMenu.desactiverEdit();
	}

	/**
	 * Accesseur vers la liste globale des flèches (liaisons) affichées.
	 *
	 * @return liste des `Fleche`
	 */
	public java.util.List<Fleche> getLstFlecheGlobal()
	{
		return this.panneauPrincipal.getLstFlecheGlobal();
	}
}
