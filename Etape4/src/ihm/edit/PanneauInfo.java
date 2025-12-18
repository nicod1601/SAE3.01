package src.ihm.edit;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import src.Controleur;
import src.metier.Couleur;
import src.metier.CreeClass;
import src.metier.Multiplicite;

public class PanneauInfo extends JPanel implements ActionListener
{
    private String nomClass;
    private JLabel[] tabTitre;
    private JPanel[] tabPanel;
    private JTextField[] tabTxtMult;
    private JPanel panelGrid;
    private CreeClass saveCle;
    private JButton btnModif;
    private JButton btnValid;
    private Controleur ctrl;

    public PanneauInfo(Controleur ctrl)
	{
        this.setLayout(new BorderLayout());
        this.ctrl = ctrl;
		this.appliquerStyle();
        this.creerComposant();
        this.addPosition();
        this.action();
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
                    int taille = 0;
                    ArrayList<String> lstInfo = new ArrayList<>();
                    CreeClass cle = null;

                    for (Map.Entry<CreeClass, List<List<String>>> entry : mult.getMapMultiplicites().entrySet())
					{
                        cle = entry.getKey();
                        this.saveCle = cle;
                        List<List<String>> liste = entry.getValue();
                        
                        for (List<String> valeur : liste) 
						{
                            for(String v : valeur) 
							{
                                lstInfo.add(v);
                            }
                            taille++;
                        }
                    }

                    this.panelGrid = new JPanel(new GridLayout(taille + 1, 1));
                    this.tabPanel = new JPanel[taille];
                    this.tabTitre = new JLabel[taille];
                    this.tabTxtMult = new JTextField[taille * 2];

                    for(int cpt2 =0; cpt2 < this.tabPanel.length; cpt2++) 
					{
                        this.tabPanel[cpt2] = new JPanel(new GridLayout(2, 1));
                    
						this.stylerPanelContenu(this.tabPanel[cpt2]);
					}

                    for(int cpt1 =0; cpt1 < this.tabTxtMult.length; cpt1++) 
					{
                        this.tabTxtMult[cpt1] = new JTextField();
                        this.tabTxtMult[cpt1].setText(lstInfo.get(cpt1));
                        this.tabTxtMult[cpt1].setHorizontalAlignment(JTextField.CENTER);
                        this.tabTxtMult[cpt1].setEnabled(false);
                    
						this.stylerTextField(this.tabTxtMult[cpt1]);
					}

                    for(int cpt2 =0; cpt2 < this.tabTitre.length; cpt2++) 
					{
                        this.tabTitre[cpt2] = new JLabel();
                        this.tabTitre[cpt2].setText(this.nomClass + " → " + this.saveCle.getNom());
                    
						this.stylerLabel(this.tabTitre[cpt2]);
					}

                    System.out.println(mult);
                }
            }

            this.btnModif = new JButton("Modifier");
            this.btnValid = new JButton("Valider");

			this.stylerBouton(this.btnModif, false);
            this.stylerBouton(this.btnValid, true);


        } 
		else 
		{
            this.panelGrid = new JPanel(new GridLayout());
            this.tabPanel = new JPanel[0];
            this.tabTitre = new JLabel[0];
            this.tabTxtMult = new JTextField[0];
            this.btnModif = new JButton("Modifier");
            this.btnValid = new JButton("Valider");
            this.nomClass = null;
        }
    }

    private void addPosition()
	{
        if(this.nomClass != null) 
		{
            for(int cpt =0; cpt < this.tabPanel.length; cpt++)
			{
                this.tabPanel[cpt].add(this.tabTitre[cpt]);
                
                JPanel panelChamps = new JPanel(new GridLayout(1, 2, 5, 0));
                panelChamps.add(this.tabTxtMult[cpt * 2]);     
                panelChamps.add(this.tabTxtMult[cpt * 2 + 1]);
                
                this.tabPanel[cpt].add(panelChamps);
                this.panelGrid.add(this.tabPanel[cpt]);
            }

            JPanel panelBtn = new JPanel();
            panelBtn.add(this.btnModif);
            panelBtn.add(this.btnValid);
            this.panelGrid.add(panelBtn);
        }

        this.add(this.panelGrid);
    }

    private void action()
	{
        if(this.nomClass != null)
		{
            for(int cpt1 =0; cpt1 < this.tabTxtMult.length; cpt1++)
			{
                this.tabTxtMult[cpt1].addActionListener(this);
            }

            this.btnModif.addActionListener(this);
            this.btnValid.addActionListener(this);
        }
    }

    public void actionPerformed(ActionEvent e) 
	{
        if(e.getSource() == this.btnValid) 
		{
            HashMap<CreeClass, List<List<String>>> nouvelleMap = new HashMap<>();
            List<List<String>> liste = new ArrayList<>();

            for (int i = 0; i < this.tabTxtMult.length; i += 2) 
			{
                List<String> pair = new ArrayList<>();
                pair.add(this.tabTxtMult[i].getText().trim());     
                pair.add(this.tabTxtMult[i + 1].getText().trim());
                liste.add(pair);
            }

            nouvelleMap.put(this.saveCle, liste);

            for(int cpt = 0; cpt < this.ctrl.getLstClass().size(); cpt++)
			{
                if(this.ctrl.getLstClass().get(cpt).getNom().equals(this.nomClass))
				{
                    this.ctrl.setHashMap(this.ctrl.getLstClass().get(cpt), nouvelleMap);
                }
            }
        }

        if(e.getSource() == this.btnModif) 
		{
            for(int cpt1 =0; cpt1 < this.tabTxtMult.length; cpt1++)
			{
                this.tabTxtMult[cpt1].setEnabled(true);
            }
        }
    }

    public void majInfoClasse(String nom) 
	{
        if(nom != null) 
		{
            this.clearInfo();
            this.nomClass = nom;
            this.creerComposant();
            this.addPosition();
            this.action();
            this.setVisible(true);
        }
    }

    public void clearInfo() 
	{
        this.removeAll();
        this.panelGrid = new JPanel(new GridLayout());
        this.tabPanel = new JPanel[0];
        this.tabTitre = new JLabel[0];
        this.tabTxtMult = new JTextField[0];
        this.nomClass = null;
        this.revalidate();
        this.repaint();
        this.setVisible(false);
    }

	/**
     * Applique le style global au panneau principal
     */
    private void appliquerStyle()
    {
        this.setBackground(Couleur.COULEUR_FOND.getColor());
        this.setBorder(new EmptyBorder(15, 15, 15, 15));
    }

    /**
     * Style pour les boutons
     */
    private void stylerBouton(JButton bouton, boolean estPrimaire)
    {
        bouton.setFont(new Font("Segoe UI", Font.PLAIN, 0xc));
        bouton.setFocusPainted(false);
        bouton.setBorderPainted(false);
        bouton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        if(estPrimaire)
        {
            bouton.setBackground(Couleur.COULEUR_PRIMAIRE.getColor());
            bouton.setForeground(Couleur.BLANC.getColor());
        }
        else
        {
            bouton.setBackground(Couleur.COULEUR_SECONDAIRE.getColor());
            bouton.setForeground(Couleur.BLANC.getColor());
        }
        
        bouton.setPreferredSize(new Dimension(120, 35));
        bouton.setBorder(BorderFactory.createCompoundBorder
		(
            BorderFactory.createLineBorder(Couleur.COULEUR_BORDURE.getColor(), 1, true),
            new EmptyBorder(5, 15, 5, 15)
        ));
        
        // Effet hover
        bouton.addMouseListener(new java.awt.event.MouseAdapter()
		{
            public void mouseEntered(java.awt.event.MouseEvent evt)
			{
                if(bouton.isEnabled())
				{
                    bouton.setBackground(Couleur.COULEUR_HOVER.getColor());
                }
            }
            public void mouseExited(java.awt.event.MouseEvent evt)
			{
                if(estPrimaire)
				{
                    bouton.setBackground(Couleur.COULEUR_PRIMAIRE.getColor());
                } 
				else
				{
                    bouton.setBackground(Couleur.COULEUR_SECONDAIRE.getColor());
                }
            }
        });
    }

    /**
     * Style pour les champs de texte
     */
    private void stylerTextField(JTextField textField)
    {
        textField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        textField.setHorizontalAlignment(JTextField.CENTER);
        textField.setBackground(Couleur.BLANC.getColor());
        textField.setForeground(Couleur.NOIR.getColor());
        textField.setBorder(BorderFactory.createCompoundBorder
		(
            BorderFactory.createLineBorder(Couleur.COULEUR_BORDURE.getColor(), 1, true),
            new EmptyBorder(5, 10, 5, 10)
        ));
        textField.setPreferredSize(new Dimension(150, 32));
    }

    /**
     * Style pour les labels de titre
     */
    private void stylerLabel(JLabel label)
    {
        label.setFont(new Font("Segoe UI", Font.BOLD, 14));
        label.setForeground(Couleur.NOIR.getColor());
        label.setBorder(new EmptyBorder(5, 0, 8, 0));
    }

    /**
     * Style pour les panneaux de contenu
     */
    private void stylerPanelContenu(JPanel panel)
    {
        panel.setBackground(Couleur.BLANC.getColor());
        panel.setBorder(BorderFactory.createCompoundBorder
		(
            BorderFactory.createLineBorder(Couleur.COULEUR_BORDURE.getColor(), 1, true),
            new EmptyBorder(12, 12, 12, 12)
        ));
    }
}