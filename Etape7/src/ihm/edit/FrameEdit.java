package src.ihm.edit;

import java.awt.BorderLayout;
import javax.swing.*;
import src.Controleur;

/**
 * Fenêtre d'édition des propriétés du diagramme UML.
 * <p>
 * Cette fenêtre contient deux panneaux principaux :
 * <ul>
 * <li>{@link PanneauChoix} : Pour sélectionner les classes à éditer.</li>
 * <li>{@link PanneauInfo} : Pour modifier les détails (multiplicités, rôles) de la classe sélectionnée.</li>
 * </ul>
 */
public class FrameEdit extends JFrame 
{
	/*----------------------------------------------------------------------------------------------------------------*/
	/* ATTRIBUTS                                                                                                      */
	/*----------------------------------------------------------------------------------------------------------------*/

	private final PanneauChoix    panneauChoix;
	private final PanneauInfo     panneauInfo;
	private final Controleur      ctrl;

	/*----------------------------------------------------------------------------------------------------------------*/
	/* CONSTRUCTEUR                                                                                                   */
	/*----------------------------------------------------------------------------------------------------------------*/

	/**
	 * Constructeur de la FrameEdit.
	 * Initialise la fenêtre, ses dimensions et ses composants internes.
	 * * @param ctrl Le contrôleur principal de l'application.
	 */
	public FrameEdit(Controleur ctrl) 
	{
		this.setTitle("Édition du graphe UML");
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		this.setSize(700, 600);
		this.setLocationRelativeTo(null);
		this.setLayout(new BorderLayout());

		/*-------------------------*/
		/* Création des Composants */
		/*-------------------------*/
		this.ctrl         = ctrl;
		this.panneauInfo  = new PanneauInfo(ctrl);
		this.panneauChoix = new PanneauChoix(ctrl, this, this.panneauInfo);

		/*-------------------------*/
		/* Position des Composants */
		/*-------------------------*/
		this.add(this.panneauChoix, BorderLayout.WEST);
		this.add(this.panneauInfo,  BorderLayout.CENTER);

		this.setVisible(false);
	}

	/*----------------------------------------------------------------------------------------------------------------*/
	/* MÉTHODES                                                                                                       */
	/*----------------------------------------------------------------------------------------------------------------*/

	/**
	 * Met à jour la liste des classes affichée dans le panneau de choix.
	 * Appelé lorsque de nouveaux fichiers sont chargés ou créés.
	 */
	public void mettreAJourMajListeClasses() 
	{
		this.panneauChoix.ajouterFichier();
	}

	/**
	 * Demande au panneau d'information d'afficher les détails d'une classe spécifique.
	 * * @param nomClasse Le nom de la classe à éditer.
	 */
	public void mettreAJourInfoClasse(String nomClasse) 
	{
		this.panneauInfo.majInfoClasse(nomClasse);
	}

	/**
	 * Efface les informations affichées dans le panneau d'édition.
	 * Utilisé lorsqu'aucune classe n'est sélectionnée.
	 */
	public void effacerInformations()
	{
		this.panneauInfo.clearInfo();
	}
}