package src.ihm;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import src.Controleur;
import src.metier.CreeClass;
import java.util.List;

public class PanneauChoix extends JPanel
{
	private Controleur ctrl;
	private FrameEdit frame;

	private JCheckBox[] checkbox;
	private PanneauInfo panneauInfo;

	public PanneauChoix(Controleur ctrl, FrameEdit frame, PanneauInfo panneauInfo) 
	{
		this.ctrl = ctrl;
		this.frame = frame;
		this.panneauInfo = panneauInfo;

		// Créer un panneau pour contenir les éléments en vertical
		JPanel panelContenu = new JPanel();
		panelContenu.setLayout(new BoxLayout(panelContenu, BoxLayout.Y_AXIS));

		List<CreeClass> lstClasses = this.ctrl.getLstClass();
		this.checkbox = new JCheckBox[lstClasses.size()];
		int i = 0;
		for (CreeClass c : lstClasses) 
		{
			this.checkbox[i] = new JCheckBox(c.getNom());
			final int index = i;
			final CreeClass classe = c;
			
			// Ajouter un listener pour afficher les infos quand sélectionné
			this.checkbox[i].addChangeListener(new ChangeListener()
			{
				@Override
				public void stateChanged(ChangeEvent e)
				{
					if (checkbox[index].isSelected())
					{
						panneauInfo.afficherInfoClasse(classe);
					}
					else
					{
						panneauInfo.effacer();
					}
				}
			});
			
			panelContenu.add(new JLabel(c.getNom()));
			panelContenu.add(this.checkbox[i]);
			i++;
		}

		// Ajouter le panneau dans un JScrollPane
		JScrollPane scrollPane = new JScrollPane(panelContenu);
		this.add(scrollPane);

	}

}
