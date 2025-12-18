package src.ihm.edit;

import src.Controleur;

import javax.swing.JOptionPane;


public class PopUp
{

	private Controleur ctrl;

	public PopUp(Controleur ctrl)
	{
		this.ctrl = ctrl;
		String role = JOptionPane.showInputDialog(
				null,
				"Entrez votre role :",
				"Saisie utilisateur",
				JOptionPane.QUESTION_MESSAGE
		);

		if (role != null && !role.isEmpty())
		{
			this.ctrl.setRole(null, role);
			JOptionPane.showMessageDialog(null,
					"Vous avez saisi : " + role);
		}
		else
		{
			JOptionPane.showMessageDialog(null,
					"Aucune saisie");
		}
	}
}
