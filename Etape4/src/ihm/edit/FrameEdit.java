package src.ihm.edit;

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
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.setSize(500, 400);
        this.setLocationRelativeTo(null);
        this.setLayout(new BorderLayout());

		/*-------------------------*/
		/* Création des Composants */
		/*-------------------------*/
        this.ctrl = ctrl;
        this.panneauInfo = new PanneauInfo(ctrl);
        this.panneauChoix = new PanneauChoix(ctrl, this, this.panneauInfo);

        /*-------------------------*/
		/* Position des Composants */
		/*-------------------------*/
        this.add(this.panneauChoix, BorderLayout.WEST);
        this.add(this.panneauInfo, BorderLayout.CENTER);

        this.setVisible(false);
    }

    public void majListeClasses() 
	{
        this.panneauChoix.ajouterFichier();
    }

    public void majInfoClasse(String nomClasse) 
	{
        this.panneauInfo.majInfoClasse(nomClasse);
    }
}
