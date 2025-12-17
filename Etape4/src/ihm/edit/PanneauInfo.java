package src.ihm.edit;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.swing.*;
import src.Controleur;
import src.metier.CreeClass;
import src.metier.Multiplicite;

public class PanneauInfo extends JPanel implements ActionListener
{
	private String nomClass;

	private JLabel[] tabTitre;
	private JPanel[] tabPanel;
	private JTextField[] tabTxtMult;
	private JPanel panelGrid;

	private JButton btnModif;
	private JButton btnValid;

	private Controleur ctrl;

	public PanneauInfo(Controleur ctrl)
	{
		this.setLayout(new BorderLayout());

		this.ctrl = ctrl;
		this.creerComposant();
		this.addPosition();

		this.setVisible(false);
	}

	private void creerComposant()
	{
		if(this.nomClass != null)
		{

			for(int cpt =0; cpt < this.ctrl.getLstClass().size(); cpt++)
			{
				if(this.nomClass.equals(this.ctrl.getLstClass().get(cpt).getNom()))
				{
					Multiplicite mult = this.ctrl.getLstClass().get(cpt).getMultiplicite();

					int taille        = 0;
					ArrayList lstInfo = new ArrayList<String>();
					CreeClass cle = null;
					
					for (Map.Entry<CreeClass, List<List<String>>> entry : mult.getMapMultiplicites().entrySet())
					{
						cle = entry.getKey();
						List<List<String>> liste = entry.getValue();

						for(List<String> valeur : liste)
						{
							taille++;
							lstInfo.add(valeur);
						}
					}

					this.panelGrid = new JPanel(new GridLayout(taille, 1));
					this.tabPanel     = new JPanel[taille];
					this.tabTitre     = new JLabel[taille];
					this.tabTxtMult   = new JTextField[taille];

					for(int cpt2 =0; cpt2 < this.tabPanel.length; cpt2++)
					{
						this.tabPanel[cpt2] = new JPanel(new BorderLayout());
					}

					for(int cpt1 =0; cpt1 < this.tabTxtMult.length; cpt1++)
					{
						this.tabTxtMult[cpt1] = new JTextField();
						this.tabTxtMult[cpt1].setText("" + lstInfo.get(cpt1));
					}

					for(int cpt2 =0; cpt2 < this.tabTitre.length; cpt2++)
					{
						this.tabTitre[cpt2] = new JLabel();
						this.tabTitre[cpt2].setText(cle.getNom() + cpt2);
					}
				}
			}
		}
		else
		{
			this.panelGrid    = new JPanel(new GridLayout());
			this.tabPanel     = new JPanel[0];
			this.tabTitre     = new JLabel[0];
			this.tabTxtMult   = new JTextField[0];
			this.nomClass     = null;
		}
		
	}

	private void addPosition()
	{
		if(this.nomClass != null)
		{
			for(int cpt =0; cpt < this.tabPanel.length; cpt++)
			{
				this.tabPanel[cpt].add(this.tabTitre[cpt], BorderLayout.NORTH);
				this.tabPanel[cpt].add(this.tabTxtMult[cpt]);

				this.panelGrid.add(this.tabPanel[cpt]);
			}
		}

		this.add(this.panelGrid);
	}

	public void actionPerformed(ActionEvent e)
	{
	}

	public void majInfoClasse(String nom)
	{
		if(nom != null)
		{
			this.clearInfo();
			this.nomClass = nom;
			this.creerComposant();
			this.addPosition();
			this.setVisible(true);
			
		}
	}

	public void clearInfo()
	{
		this.removeAll();            // 🔴 OBLIGATOIRE

		this.panelGrid  = new JPanel(new GridLayout());
		this.tabPanel   = new JPanel[0];
		this.tabTitre   = new JLabel[0];
		this.tabTxtMult = new JTextField[0];
		this.nomClass   = null;

		this.revalidate();
		this.repaint();
		this.setVisible(false);
	}
}