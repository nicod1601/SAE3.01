package src.ihm.edit;

import javax.swing.JOptionPane;

import src.Controleur;
import src.metier.CreeClass;
import src.ihm.Fleche;

/**
 * Fenêtre contextuelle permettant la saisie
 * d'un rôle associé à une flèche du graphe UML.
 */
public class PopUp
{
	/** Contrôleur principal de l'application */
	private final Controleur controleur;

	/**
	 * Construit la fenêtre popup.
	 *
	 * @param controleur le contrôleur principal
	 */
	public PopUp(Controleur controleur)
	{
		this.controleur = controleur;
	}

	/**
	 * Affiche la boîte de dialogue de saisie du rôle
	 * pour une flèche donnée.
	 *
	 * @param idFleche identifiant de la flèche
	 * @param classeSource classe source associée à la flèche
	 */
	public void afficher(int idFleche, CreeClass classeSource)
	{
		String role = JOptionPane.showInputDialog
		(
			null                        ,
			"Entrez le rôle :"          ,
			"Saisie utilisateur"        ,
			JOptionPane.QUESTION_MESSAGE
		);

		if (role != null && !role.isEmpty())
		{
			this.controleur.setRole(idFleche, role, classeSource);

			JOptionPane.showMessageDialog
			(
				null,
				"Vous avez saisi : " + role
			);
		}
		else
		{
			JOptionPane.showMessageDialog
			(
				null,
				"Aucune saisie"
			);
		}
	}

	/**
	 * Définit la flèche sélectionnée et
	 * déclenche l'affichage de la popup.
	 *
	 * @param idFleche identifiant de la flèche
	 * @param classeSource classe source associée
	 */
	public void definirIndexFleche(int idFleche, CreeClass classeSource)
	{
		// Récupération de la flèche (utile si extension future)
		Fleche fleche = this.controleur.getFleche(idFleche, classeSource);

		if (fleche != null)
		{
			this.afficher(idFleche, classeSource);
		}
	}
}
