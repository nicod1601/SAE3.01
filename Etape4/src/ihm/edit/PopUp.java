package src.ihm.edit;

import javax.swing.JOptionPane;
import src.Controleur;


public class PopUp
{

	private Controleur ctrl;

	private Integer id;
	private String role;

	public PopUp(Controleur ctrl)
	{
		this.ctrl = ctrl;
		this.role = null;
		this.id  = null;
	}

	public void afficher()
	{
		if(this.id != null)
		{
			this.role = JOptionPane.showInputDialog
			(
					null,
					"Entrez votre role :",
					"Saisie utilisateur",
					JOptionPane.QUESTION_MESSAGE
			);

			if (this.role != null && !this.role.isEmpty())
			{
				this.ctrl.setRole(this.id, this.role);
				JOptionPane.showMessageDialog(null,
						"Vous avez saisi : " + this.role);
			}
			else
			{
				JOptionPane.showMessageDialog(null,
						"Aucune saisie");
			}
		}
	}

	/*public void getFleche(int id, String source, String cible)
	{
		System.out.println("flèche recherche");

		this.fleche = this.ctrl.getFleche(id, source, cible);

		if(this.fleche != null)
			System.out.println("flèche trouver      " + this.fleche);
			
		this.afficher();
	}*/

	public void setIndexFleche(int index)
	{
		this.id = index;
		this.afficher();
	}
}
