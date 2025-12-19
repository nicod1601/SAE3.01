package src.ihm;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.*;
import java.io.File;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import src.Controleur;
import src.ihm.edit.FrameEdit;
import src.metier.Couleur;

/**
 * Panneau contenant la barre de menus de l'application.
 *
 * Fournit les éléments de menu pour l'ouverture, l'édition,
 * l'export et la gestion des fichiers, ainsi que les styles
 * et les écouteurs associés.
 */
public class PanneauMenu extends JPanel implements ActionListener
{
	private JMenuBar menuBar;
	private JMenu    fileMenu;
	private JMenu    editMenu;
	private JButton  quitter;
	private JScrollPane scroll;

	private Controleur ctrl;
	private File       dossierOuvert;
	private FrameAppli frameAppli;
	private FrameEdit  frameEdit;

	/**
	 * Crée un nouveau panneau de menu lié au contrôleur et à la frame principale.
	 *
	 * @param ctrl        instance du contrôleur applicatif
	 * @param frameAppli  fenêtre principale de l'application
	 */
	public PanneauMenu(Controleur ctrl, FrameAppli frameAppli)
	{
		this.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
		this.setBackground(Couleur.COULEUR_FOND.getColor());

		/*-------------------------*/
		/* Création des Composants */
		/*-------------------------*/
		this.frameAppli = frameAppli;
		this.ctrl       = ctrl;

		this.creerMenus();
		this.appliquerStyle();

		this.frameEdit = new FrameEdit(this.ctrl);

		/*-------------------------*/
		/* Position des Composants */
		/*-------------------------*/
		this.add(this.menuBar);

		/*-------------------------*/
		/* Activer  des Composants */
		/*-------------------------*/
		this.activerEcouteurs();
	}

	/**
	 * Initialise la barre de menus et les éléments associés.
	 */
	private void creerMenus()
	{
		this.menuBar = new JMenuBar();
		this.fileMenu = new JMenu  ("  📁 fichier  ");
		this.editMenu = new JMenu  ("  ✏️ Editer  " );
		this.quitter  = new JButton("✖ Quitter"  );

		/* Composants File */
		this.fileMenu.add         (new JMenuItem("📄 Ouvrir Fichier"));
		this.fileMenu.add         (new JMenuItem("📂 Ouvrir Dossier"));
		this.fileMenu.addSeparator();
		this.fileMenu.add         (new JMenuItem("📤 Exporter"));
		this.fileMenu.addSeparator();
		this.fileMenu.add         (new JMenuItem("🗑️ Suprimmer"));

		/*Composants Edit */
		this.editMenu.add(new JMenuItem("📄 Modif Fichier"));
		this.editMenu.add(new JMenuItem("💾 Sauvegarder")  );

		this.menuBar.add(this.fileMenu );
		this.menuBar.add(this.editMenu );
		this.menuBar.add(this.quitter  );
	}

	/**
	 * Applique le style (couleurs, polices) aux composants de la barre de menu.
	 */
	private void appliquerStyle()
	{
		// Style de la barre de menu
		this.menuBar.setBackground   (Couleur.COULEUR_FOND.getColor());
		this.menuBar.setBorder       (new EmptyBorder(5, 10, 5, 10));
		this.menuBar.setBorderPainted(false);
		
		// Style des menus
		this.stylerMenu(this.fileMenu);
		this.stylerMenu(this.editMenu);
		
		// Style du bouton quitter
		this.stylerBoutonQuitter();
		
		// Style des items du menu File
		for (int cpt = 0; cpt < this.fileMenu.getItemCount(); cpt++) 
		{
			JMenuItem item = this.fileMenu.getItem(cpt);
			
			if (item != null)
			{
				this.stylerMenuItem(item);
			}
		}

		for(int cpt =0; cpt < this.editMenu.getItemCount(); cpt++)
		{
			JMenuItem item = this.editMenu.getItem(cpt);
			
			if (item != null)
			{
				this.stylerMenuItem(item);
				item.setEnabled(false);
			}
		}
	}

	/**
	 * Enregistre les écouteurs d'événements sur les éléments de menu.
	 */
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

		for (int i = 0; i < this.editMenu.getItemCount(); i++) 
		{
			JMenuItem item = this.editMenu.getItem(i);
			if (item != null)
			{
				item.addActionListener(this);
			}
		}

		this.quitter.addActionListener(this);
	}

	/**
	 * Gère les actions des éléments de menu.
	 *
	 * @param e événement d'action
	 */
	public void actionPerformed(ActionEvent e)
	{
		String command = e.getActionCommand();
		
		// Nettoyer la commande (enlever les emojis)
		command = command.replaceAll("[^a-zA-Z\\s]", "").trim();
		
		switch (command) 
		{
			case "Ouvrir Fichier":
				this.ouvrirFichier();
				break;
			case "Ouvrir Dossier":
				this.ouvrirDossier();
				break;
			case "Exporter":
				this.exporterEnImage();
				break;
			case "Suprimmer":
				this.viderListe();
				break;
			case "Modif Fichier":
				this.ouvrirFrameEdit();
				break;
			case "Sauvegarder":
				this.ctrl.sauvegarderXML();
				this.ctrl.sauvegarderSER();
				JOptionPane.showMessageDialog(
					null,
					"Savegarde Reussite.",
					"Succès",
					JOptionPane.INFORMATION_MESSAGE );
				break;
			default:
				break;
		}

		if(e.getSource() == this.quitter)
		{
			System.exit(0);
		}
	}

	/**
	 * Ouvre ou ferme la fenêtre d'édition.
	 */
	private void ouvrirFrameEdit()
	{
		if(! this.frameEdit.isVisible())
		{
			this.frameEdit.setVisible(true);
		}
		else
		{
			this.frameEdit.setVisible(false);
		}
		this.frameEdit.mettreAJourMajListeClasses();
	}

	/**
	 * Ouvre une boîte de dialogue pour exporter le diagramme en image.
	 */
	public void exporterEnImage()
	{
		JFileChooser dialogueEnregistrement = new JFileChooser();

		dialogueEnregistrement.setDialogTitle     ("Enregistrer les positions sous..."                      );
		dialogueEnregistrement.setCurrentDirectory(new File( System.getProperty("user.home") + "/Downloads"));
	
		dialogueEnregistrement.setSelectedFile    (new File("Export.png")                                   );
	
		int choixUtilisateur = dialogueEnregistrement.showSaveDialog(this.getParent());
	
		if (choixUtilisateur == JFileChooser.APPROVE_OPTION) 
		{
			File   fichierAEnregistrer = dialogueEnregistrement.getSelectedFile();
			String cheminAbsolu        = fichierAEnregistrer   .getAbsolutePath();
			
			this.frameAppli.exporterEnImage(cheminAbsolu, fichierAEnregistrer);

		} 
	}


	/**
	 * Ouvre un fichier .java sélectionné par l'utilisateur et le charge.
	 */
	public void ouvrirFichier()
	{    
		if (this.viderListe())
		{
			JFileChooser chooser = this.creerFileChooser(JFileChooser.FILES_ONLY, "Sélectionner un fichier .java");
			
			int resultat = chooser.showOpenDialog(this);
			if (resultat == JFileChooser.APPROVE_OPTION) 
			{
				File fichierSelectionne = chooser.getSelectedFile();
				String nomFichier = "" + fichierSelectionne;
				
				this.frameAppli.ajouterFichier(fichierSelectionne.getName());
				this.afficherMessageSucces("Fichier " + fichierSelectionne.getName() + " chargé avec succès", "Chargement du fichier");
				this.frameAppli.majListeClasses(false, nomFichier);
			}
		}
	}

	/**
	 * Ouvre un dossier et charge tous les fichiers .java qu'il contient.
	 */
	private void ouvrirDossier()
	{
		if (this.viderListe())
		{
			
			JFileChooser chooser = this.creerFileChooser(JFileChooser.FILES_AND_DIRECTORIES, "Sélectionner un dossier contenant des fichiers .java");
			chooser.setCurrentDirectory(new File("../"));
			chooser.setSelectedFile(new File("data"));
			
			int resultat = chooser.showOpenDialog(this);

			if (resultat == JFileChooser.APPROVE_OPTION) 
			{
				this.dossierOuvert = chooser.getSelectedFile();
				this.chargerFichiersDossier();
				this.ctrl.LectureRepertoire(this.dossierOuvert);
				this.frameAppli.majListeClasses(true, null);
			}
		}
	}

	/**
	 * Parcourt le dossier ouvert et ajoute les fichiers .java à la liste.
	 */
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

	/**
	 * Vide la liste des fichiers après confirmation utilisateur.
	 *
	 * @return true si la liste a été vidée ou était déjà vide
	 */
	private boolean viderListe()
	{
		if (this.frameAppli.getModeleFichiers().isEmpty())
		{
			return true;
		}

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
			this.frameEdit.effacerInformations();
			this.frameEdit.dispose();
			return true;
		}
		else 
		{
			return false;
		}
	}

	/**
	 * Crée et configure un `JFileChooser` avec le mode et le titre donnés.
	 *
	 * @param mode  mode de sélection (`JFileChooser.FILES_ONLY` ou autre)
	 * @param titre titre de la boîte de dialogue
	 * @return un `JFileChooser` configuré
	 */
	private JFileChooser creerFileChooser(int mode, String titre)
	{
		JFileChooser chooser = new JFileChooser();
		chooser.setCurrentDirectory(new File("../data"));
		chooser.setFileSelectionMode(mode);
		chooser.setDialogTitle(titre);
		return chooser;
	}

	/**
	 * Vérifie qu'un fichier est bien un fichier Java.
	 *
	 * @param fichier fichier à tester
	 * @return true si le fichier n'est pas nul, est un fichier et termine par ".java"
	 */
	private boolean estFichierJava(File fichier)
	{
		return fichier != null && fichier.isFile() && fichier.getName().endsWith(".java");
	}

	/**
	 * Affiche un message d'information.
	 */
	private void afficherMessageSucces(String message, String titre)
	{
		JOptionPane.showMessageDialog(this, message, titre, JOptionPane.INFORMATION_MESSAGE);
	}

	/**
	 * Affiche un message d'avertissement.
	 */
	private void afficherMessageAvertissement(String message, String titre)
	{
		JOptionPane.showMessageDialog(this, message, titre, JOptionPane.WARNING_MESSAGE);
	}

	/**
	 * Affiche un message d'erreur.
	 */
	private void afficherMessageErreur(String message, String titre)
	{
		JOptionPane.showMessageDialog(this, message, titre, JOptionPane.ERROR_MESSAGE);
	}

	/**
	 * Applique le style et les animations à un élément de menu.
	 */
	private void stylerMenuItem(JMenuItem item)
	{
		item.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 12));
		item.setBackground(Couleur.COULEUR_MENU.getColor());
		item.setForeground(Couleur.COULEUR_TEXTE.getColor());
		item.setBorder(new EmptyBorder(8, 20, 8, 20));
		item.setOpaque(true);
		
		// animation des couleurs
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

	/**
	 * Style le bouton "quitter" et gère son comportement au survol.
	 */
	private void stylerBoutonQuitter()
	{
		this.quitter.setFont         (new Font("Segoe UI Emoji", Font.PLAIN, 12));
		this.quitter.setForeground   (Couleur.BLANC.getColor());
		this.quitter.setBackground   (Couleur.COULEUR_DANGER.getColor());
		this.quitter.setBorder       (new EmptyBorder(8, 16, 8, 16));
		this.quitter.setFocusPainted (false);
		this.quitter.setCursor       (new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
		this.quitter.setOpaque       (true);
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

	/**
	 * Applique le style à un `JMenu` et personnalise son popup.
	 */
	private void stylerMenu(JMenu menu)
	{
		menu.setFont      (new Font("Segoe UI Emoji", Font.PLAIN, 14));
		menu.setForeground(Couleur.COULEUR_TEXTE.getColor());
		menu.setBackground(Couleur.COULEUR_FOND.getColor());
		menu.setOpaque    (true);
		menu.setBorder    (new EmptyBorder(8, 12, 8, 12));
		
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
		popup.setFont      (new Font("Segoe UI Emoji", Font.PLAIN, 10));
		popup.setBorder    (BorderFactory.createLineBorder(Couleur.COULEUR_HOVER.getColor(), 1));
	}

	public void activerEdit()
	{
		for(int cpt=0; cpt < this.editMenu.getItemCount(); cpt++)
		{
			this.editMenu.getItem(cpt).setEnabled(true);
		}
	}

	public void desactiverEdit()
	{
		for(int cpt=0; cpt < this.editMenu.getItemCount(); cpt++)
		{
			this.editMenu.getItem(cpt).setEnabled(false);
		}
	}
}