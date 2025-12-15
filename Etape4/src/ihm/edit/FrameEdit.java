package src.ihm;

import java.awt.BorderLayout;
import javax.swing.*;
import src.Controleur;

public class FrameEdit extends JFrame 
{
	private final PanneauChoix panneauChoix;
	private final PanneauInfo panneauInfo;
	private final Controleur ctrl;

	public FrameEdit(Controleur ctrl) 
	{
		this.setTitle("Edit du graph UML");
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setSize(1000, 600);
		this.setLocationRelativeTo(null);
		this.setLayout(new BorderLayout());

		/*-------------------------*/
		/* Création des Composants */
		/*-------------------------*/
		this.ctrl = ctrl;
		this.panneauInfo = new PanneauInfo();
		this.panneauChoix = new PanneauChoix(ctrl, this, this.panneauInfo);

		/*-------------------------*/
		/* Position des Composants */
		/*-------------------------*/
		this.add(this.panneauChoix, BorderLayout.WEST);
		this.add(this.panneauInfo, BorderLayout.CENTER);

		this.setVisible(true);
	}

	public static void main(String[] args) 
	{
		// Test avec un contrôleur valide si nécessaire
		// new FrameEdit(new Controleur());
	}
}
