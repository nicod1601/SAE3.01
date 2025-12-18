package src.ihm.edit;

import src.Controleur;

import javax.swing.JOptionPane;

public class PopUp
{
	public PopUp(Controlleur ctrl)
	{
		String role = JOptionPane.showInputDialog(
				null,
				"Entrez votre role :",
				"Saisie utilisateur",
				JOptionPane.QUESTION_MESSAGE
		);

		if (role != null && !role.isEmpty())
		{
			ctrl.setRoleUser(role);
			JOptionPane.showMessageDialog(null,
					"Vous avez saisi : " + role);
		}
		else
		{
			JOptionPane.showMessageDialog(null,
					"Aucune saisie");
		}
	}
	public static void main(String[] args)
	{
		PopUp pp = new PopUp();

	}
}
