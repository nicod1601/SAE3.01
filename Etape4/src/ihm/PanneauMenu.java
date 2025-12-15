package src.ihm;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.*;

import java.io.File;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import src.Controleur;
import src.metier.Couleur;

public class PanneauMenu extends JPanel implements ActionListener
{
	private JMenuBar menuBar;
	private JMenu fileMenu;
	private JMenu editMenu;
	private JButton quitter;

	private Controleur ctrl;
	private File dossierOuvert;
	private FrameAppli frameAppli;

	public PanneauMenu(Controleur ctrl, FrameAppli frameAppli) 
	{
		this.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
		this.setBackground(Couleur.COULEUR_FOND.getColor());
		
		/*-------------------------*/
		/* Création des Composants */
		/*-------------------------*/
		this.frameAppli = frameAppli;
		this.ctrl = ctrl;
		
		this.creerMenus();
		this.appliquerStyle();
		
		/*-------------------------*/
		/* Position des Composants */
		/*-------------------------*/
		this.add(this.menuBar);
		
		/*-------------------------*/
		/* Activer  des Composants */
		/*-------------------------*/
		this.activerEcouteurs();
	}

	private void creerMenus()
	{
		this.menuBar = new JMenuBar();
		this.fileMenu = new JMenu("  📁 File  ");
		this.editMenu = new JMenu("  ✏️ Edit  ");
		this.quitter  = new JButton("✖ Quitter");

		/* Composants File */
		this.fileMenu.add(new JMenuItem("📄 Open File"));
		this.fileMenu.add(new JMenuItem("📂 Open Folder"));
		this.fileMenu.addSeparator();
		this.fileMenu.add(new JMenuItem("🗑️ Clear List"));

		/*Composants Edit */
		

		this.menuBar.add(fileMenu);
		this.menuBar.add(editMenu);
		this.menuBar.add(Box.createHorizontalGlue()); // Pousse le bouton quitter à droite
		this.menuBar.add(quitter);
	}

	private void appliquerStyle()
	{
		// Style de la barre de menu
		this.menuBar.setBackground(Couleur.COULEUR_FOND.getColor());
		this.menuBar.setBorder(new EmptyBorder(5, 10, 5, 10));
		this.menuBar.setBorderPainted(false);
		
		// Style des menus
		this.stylerMenu(this.fileMenu);
		this.stylerMenu(this.editMenu);
		
		// Style du bouton quitter
		this.stylerBoutonQuitter();
		
		// Style des items du menu File
		for (int i = 0; i < this.fileMenu.getItemCount(); i++) 
		{
			JMenuItem item = this.fileMenu.getItem(i);
			if (item != null)
			{
				this.stylerMenuItem(item);
			}
		}
	}

	private void stylerMenu(JMenu menu)
	{
		menu.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 14));
		menu.setForeground(Couleur.COULEUR_TEXTE.getColor());
		menu.setBackground(Couleur.COULEUR_FOND.getColor());
		menu.setOpaque(true);
		menu.setBorder(new EmptyBorder(8, 12, 8, 12));
		
		// Effet hover sur le menu
		menu.addMouseListener(new MouseAdapter() 
		{
			public void mouseEntered(MouseEvent e) 
			{
				menu.setBackground(Couleur.COULEUR_HOVER.getColor());
			}
			
			public void mouseExited(MouseEvent e) 
			{
				if (!menu.isSelected())
				{
					menu.setBackground(Couleur.COULEUR_FOND.getColor());
				}
			}
		});
		
		// Personnaliser le popup
		JPopupMenu popup = menu.getPopupMenu();
		popup.setBackground(Couleur.COULEUR_MENU.getColor());
		popup.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 10));
		popup.setBorder(BorderFactory.createLineBorder(Couleur.COULEUR_HOVER.getColor(), 1));
	}

	private void stylerMenuItem(JMenuItem item)
	{
		item.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 12));
		item.setBackground(Couleur.COULEUR_MENU.getColor());
		item.setForeground(Couleur.COULEUR_TEXTE.getColor());
		item.setBorder(new EmptyBorder(8, 20, 8, 20));
		item.setOpaque(true);
		
		// Effet hover avec animation
		item.addMouseListener(new MouseAdapter() 
		{
			public void mouseEntered(MouseEvent e) 
			{
				item.setBackground(Couleur.COULEUR_HOVER.getColor());
				item.setForeground(Couleur.BLANC.getColor());
			}
			
			public void mouseExited(MouseEvent e) 
			{
				item.setBackground(Couleur.COULEUR_MENU.getColor());
				item.setForeground(Couleur.COULEUR_TEXTE.getColor());
			}
		});
	}

	private void stylerBoutonQuitter()
	{
		this.quitter.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 12));
		this.quitter.setForeground(Couleur.BLANC.getColor());
		this.quitter.setBackground(Couleur.COULEUR_DANGER.getColor());
		this.quitter.setBorder(new EmptyBorder(8, 16, 8, 16));
		this.quitter.setFocusPainted(false);
		this.quitter.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
		this.quitter.setOpaque(true);
		this.quitter.setBorderPainted(false);
		
		// Effet hover
		this.quitter.addMouseListener(new MouseAdapter() 
		{
			public void mouseEntered(MouseEvent e) 
			{
				quitter.setBackground(new Color(192, 57, 43));
			}
			
			public void mouseExited(MouseEvent e) 
			{
				quitter.setBackground(Couleur.COULEUR_DANGER.getColor());
			}
		});
	}

	private void activerEcouteurs()
	{
		for (int i = 0; i < this.fileMenu.getItemCount(); i++) 
		{
			JMenuItem item = this.fileMenu.getItem(i);
			if (item != null)
			{
				item.addActionListener(this);
			}
		}

		this.quitter.addActionListener(this);
	}

	public void actionPerformed(ActionEvent e) 
	{
		String command = e.getActionCommand();
		
		// Nettoyer la commande (enlever les emojis)
		command = command.replaceAll("[^a-zA-Z\\s]", "").trim();
		
		switch (command) 
		{
			case "Open File":
				this.ouvrirFichier();
				break;
			case "Open Folder":
				this.ouvrirDossier();
				break;
			case "Clear List":
				this.viderListe();
				break;
			default:
				break;
		}

		if(e.getSource() == this.quitter)
		{
			System.exit(0);
		}
	}

	public void ouvrirFichier() 
	{
		JFileChooser chooser = this.creerFileChooser(JFileChooser.FILES_ONLY, "Sélectionner un fichier .java");
		
		int resultat = chooser.showOpenDialog(this);
		if (resultat == JFileChooser.APPROVE_OPTION) 
		{
			File fichierSelectionne = chooser.getSelectedFile();
			String nomFichier = "" + fichierSelectionne;
			
			if (this.estFichierJava(fichierSelectionne))
			{
				this.frameAppli.ajouterFichier(fichierSelectionne.getName());
				this.afficherMessageSucces("Fichier " + fichierSelectionne.getName() + " chargé avec succès", "Chargement du fichier");
				this.frameAppli.majListeClasses(false, nomFichier);
			}
			else
			{
				this.afficherMessageErreur("Veuillez sélectionner un fichier .java", "Format invalide");
			}
		}
	}

	private void ouvrirDossier() 
	{
		this.frameAppli.viderListe();

		JFileChooser chooser = this.creerFileChooser(JFileChooser.DIRECTORIES_ONLY, "Sélectionner un dossier contenant des fichiers .java");
		
		int resultat = chooser.showOpenDialog(this);
		if (resultat == JFileChooser.APPROVE_OPTION)
		{
			this.dossierOuvert = chooser.getSelectedFile();
			chargerFichiersDossier();
			this.ctrl.LectureRepertoire(this.dossierOuvert);
			this.frameAppli.majListeClasses(true, null);
		}
		else //si on annule il faut remettre les anciens fichiers
		{
			this.chargerFichiersDossier();
		}
	}

	private void chargerFichiersDossier()
	{        
		if (this.dossierOuvert != null && this.dossierOuvert.isDirectory())
		{
			File[] fichiers = this.dossierOuvert.listFiles();
			
			if (fichiers != null && fichiers.length > 0)
			{
				int compteur = 0;
				
				for (File f : fichiers)
				{
					if (this.estFichierJava(f))
					{
						this.frameAppli.ajouterFichier(f.getName());
						compteur++;
					}
				}
				
				if (compteur > 0)
				{
					this.afficherMessageSucces(compteur + " fichier(s) .java trouvé(s)", "Chargement du dossier");
				}
				else
				{
					this.afficherMessageAvertissement("Aucun fichier .java trouvé dans ce dossier", "Dossier vide");
				}
			}
			else
			{
				this.afficherMessageAvertissement("Le dossier est vide", "Dossier vide");
			}
		}
	}

	private void viderListe()
	{
		int reponse = JOptionPane.showConfirmDialog(this,
			"Voulez-vous vraiment vider la liste des fichiers ?",
			"Confirmation",
			JOptionPane.YES_NO_OPTION,
			JOptionPane.QUESTION_MESSAGE);
		
		if (reponse == JOptionPane.YES_OPTION)
		{
			this.frameAppli.viderListe();
			this.frameAppli.viderLstClass();
			this.afficherMessageSucces("Liste vidée avec succès", "Liste vidée");
		}
	}

	private JFileChooser creerFileChooser(int mode, String titre)
	{
		JFileChooser chooser = new JFileChooser();
		chooser.setCurrentDirectory(new File("../data"));
		chooser.setFileSelectionMode(mode);
		chooser.setDialogTitle(titre);
		return chooser;
	}

	private boolean estFichierJava(File fichier)
	{
		return fichier != null && fichier.isFile() && fichier.getName().endsWith(".java");
	}

	private void afficherMessageSucces(String message, String titre)
	{
		JOptionPane.showMessageDialog(this, message, titre, JOptionPane.INFORMATION_MESSAGE);
	}

	private void afficherMessageAvertissement(String message, String titre)
	{
		JOptionPane.showMessageDialog(this, message, titre, JOptionPane.WARNING_MESSAGE);
	}

	private void afficherMessageErreur(String message, String titre)
	{
		JOptionPane.showMessageDialog(this, message, titre, JOptionPane.ERROR_MESSAGE);
	}
}