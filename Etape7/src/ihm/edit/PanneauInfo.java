package src.ihm.edit;

import java.awt.*;
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

/**
 * Panneau d'informations affichant les détails des associations d'une classe.
 * <p>
 * Ce panneau permet de consulter et modifier les multiplicités et les rôles
 * des associations liées à la classe sélectionnée (liens entrants et sortants).
 */
public class PanneauInfo extends JPanel implements ActionListener
{
	/*----------------------------------------------------------------*/
	/* ATTRIBUTS                                                      */
	/*--------------------------------------------------------------- */

	private Controleur             ctrl         ;
	private CreeClass              classActuelle;
	
	// Composants IHM
	private JLabel[]               tabTitre  ;
	private JPanel[]               tabPanel  ;
	private JTextField[]           tabTxtMult;
	private JPanel                 panelGrid ;
	private ArrayList<JButton>     lstBtnRole;
	private PopUp                  pop       ;
	private JScrollPane            scrollPane;
	private JButton                btnValid  ;
	
	// Gestion des données (Mapping entre l'affichage plat et la structure de données)
	/** Liste des classes liées (clés de la Map). */
	private ArrayList<CreeClass>   listeCles;
	
	/** Index de départ dans le tableau de textfields pour chaque clé. */
	private ArrayList<Integer>     listeIndexDebut;
	
	/** Nombre de paires de multiplicités pour chaque clé. */
	private ArrayList<Integer>     listeNbPaires;


	/*----------------------------------------------------------------*/
	/* CONSTRUCTEUR                                                   */
	/*----------------------------------------------------------------*/

	/**
	 * Constructeur du PanneauInfo.
	 * * @param ctrl Le contrôleur de l'application.
	 */
	public PanneauInfo(Controleur ctrl)
	{
		this.setLayout(new BorderLayout());
		this.ctrl            = ctrl;
		this.listeCles       = new ArrayList<>();
		this.listeIndexDebut = new ArrayList<>();
		this.listeNbPaires   = new ArrayList<>();
		
		this.appliquerStyle();

		this.scrollPane = new JScrollPane();
		this.scrollPane.setBorder(null);
		this.scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		this.scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		this.scrollPane.getVerticalScrollBar().setUnitIncrement(16); // Scroll plus fluide

		this.creerComposant();
		this.addPosition();
		this.action();
		this.setVisible(false);
	}

	/*----------------------------------------------------------------*/
	/* MÉTHODES DE STYLE                                              */
	/*----------------------------------------------------------------*/

	/**
	 * Applique le style global au panneau principal.
	 */
	private void appliquerStyle()
	{
		this.setBackground(Couleur.COULEUR_FOND.getColor());
		this.setBorder(new EmptyBorder(15, 15, 15, 15));
	}

	/**
	 * Applique le style aux boutons.
	 * * @param bouton      Le bouton à styliser.
	 * @param estPrimaire Vrai si c'est un bouton d'action principale (ex: Valider).
	 */
	private void stylerBouton(JButton bouton, boolean estPrimaire)
	{
		bouton.setFont(new Font("Segoe UI", Font.BOLD, 14));
		bouton.setFocusPainted(false);
		bouton.setBorderPainted(false);
		bouton.setCursor(new Cursor(Cursor.HAND_CURSOR));
		
		if(estPrimaire)
		{
			bouton.setBackground(Couleur.VERT.getColor());
			bouton.setForeground(Couleur.BLANC.getColor());
		}
		else
		{
			bouton.setBackground(Couleur.COULEUR_ACCENT.getColor());
			bouton.setForeground(Couleur.BLANC.getColor());
		}
		
		bouton.setPreferredSize(new Dimension(120, 35));
		bouton.setBorder(BorderFactory.createCompoundBorder(
			BorderFactory.createLineBorder(Couleur.COULEUR_BORDURE.getColor(), 1, true),
			new EmptyBorder(5, 15, 5, 15)
		));
	}

	/**
	 * Applique le style aux champs de texte (Multiplicités).
	 * * @param textField Le champ de texte à styliser.
	 */
	private void stylerTextField(JTextField textField)
	{
		textField.setFont(new Font("Segoe UI", Font.BOLD, 20));
		textField.setHorizontalAlignment(JTextField.CENTER);
		textField.setForeground(Couleur.VERT.getColor());
		textField.setDisabledTextColor(Couleur.NOIR.getColor());

		textField.setCaretColor(Couleur.COULEUR_TEXTE.getColor());
		
		textField.setBorder(BorderFactory.createCompoundBorder
		(
			BorderFactory.createLineBorder(Couleur.COULEUR_BORDURE.getColor(), 1, true),
			new EmptyBorder(5, 10, 5, 10)
		));
		textField.setPreferredSize(new Dimension(150, 32));
	}

	/**
	 * Applique le style aux labels de titre.
	 */
	private void stylerLabel(JLabel label)
	{
		label.setFont(new Font("Segoe UI", Font.BOLD, 14));
		label.setForeground(Couleur.ROUGE.getColor());
		label.setBorder(new EmptyBorder(5, 0, 8, 0));
	}

	/**
	 * Applique le style aux labels de description.
	 */
	private void stylerLabelDescription(JLabel label)
	{
		label.setFont(new Font("Segoe UI", Font.PLAIN, 13));
		label.setForeground(Couleur.COULEUR_TEXTE_SECONDAIRE.getColor());
	}

	/**
	 * Applique le style aux sous-panneaux de contenu.
	 */
	private void stylerPanelContenu(JPanel panel)
	{
		panel.setBackground(Couleur.COULEUR_LISTE.getColor());
		panel.setBorder(BorderFactory.createCompoundBorder(
			BorderFactory.createLineBorder(Couleur.COULEUR_BORDURE.getColor(), 1, true),
			new EmptyBorder(12, 12, 12, 12)
		));
	}

	/*----------------------------------------------------------------*/
	/* CRÉATION DES COMPOSANTS                                        */
	/*----------------------------------------------------------------*/

	/**
	 * Initialise et crée les composants graphiques en fonction de la classe sélectionnée.
	 * Cette méthode rassemble les associations directes et inverses.
	 */
	private void creerComposant()
	{
		if(this.classActuelle != null)
		{
			// Réinitialiser les listes de mapping
			this.listeCles      .clear();
			this.listeIndexDebut.clear();
			this.listeNbPaires  .clear();
			
			Multiplicite mult = this.classActuelle.getMultiplicite();
			int tailleTotale  = 0;
			ArrayList<String> lstInfoTotale = new ArrayList<>();
			ArrayList<String> lstTitres     = new ArrayList<>();
				
			/* --------------------------------------------------------- */
			/* 1. Relations directes (La classe actuelle pointe vers X)  */
			/* --------------------------------------------------------- */
			if(mult != null && mult.getMapMultiplicites() != null)
			{
				for (Map.Entry<CreeClass, List<List<String>>> entry : mult.getMapMultiplicites().entrySet())
				{
					CreeClass cle = entry.getKey();
					List<List<String>> liste = entry.getValue();
						
					// Stocker la clé et l'index de début pour la reconstruction future
					this.listeCles.add(cle);
					this.listeIndexDebut.add(lstInfoTotale.size());
					int nbPaires = 0;
						
					// Ajouter les multiplicités de cette relation
					for (List<String> valeur : liste) 
					{
						for(String v : valeur) 
						{
							lstInfoTotale.add(v);
						}
						lstTitres.add(this.classActuelle.getNom() + " → " + cle.getNom());
						tailleTotale++;
						nbPaires++;
					}
						
					// Stocker le nombre de paires pour cette clé
					this.listeNbPaires.add(nbPaires);
				}
			}
				
			/* --------------------------------------------------------- */
			/* 2. Relations inverses (X pointe vers la classe actuelle)  */
			/* --------------------------------------------------------- */
			for(int i = 0; i < this.ctrl.getLstClass().size(); i++)
			{
				CreeClass autreClasse = this.ctrl.getLstClass().get(i);
					
				// Ne pas traiter la classe actuelle (déjà fait ou auto-référence gérée)
				if(autreClasse.equals(this.classActuelle)) continue;
					
				Multiplicite multAutre = autreClasse.getMultiplicite();
				if(multAutre == null || multAutre.getMapMultiplicites() == null) continue;
					
				// Vérifier si cette autre classe possède une relation vers la classe actuelle
				if(multAutre.getMapMultiplicites().containsKey(this.classActuelle))
				{
					// Eviter les doublons si une relation bidirectionnelle a déjà été traitée dans la boucle 1
					boolean dejaPresent = false;
					for(CreeClass cle : this.listeCles)
					{
						if(cle.equals(autreClasse))
						{
							dejaPresent = true;
							break;
						}
					}
						
					if(!dejaPresent)
					{
						// Ajouter la relation inverse
						List<List<String>> listeInverse = multAutre.getMapMultiplicites().get(this.classActuelle);
							
						this.listeCles.add(autreClasse);
						this.listeIndexDebut.add(lstInfoTotale.size());
						int nbPaires = 0;
							
						// Ajouter les multiplicités inversées
						for (List<String> valeur : listeInverse) 
						{
							// Inversion : [source, cible] devient [cible, source] visuellement
							lstInfoTotale.add(valeur.get(1));
							lstInfoTotale.add(valeur.get(0));
							lstTitres.add(this.classActuelle.getNom() + " ← " + autreClasse.getNom());
							tailleTotale++;
							nbPaires++;
						}
							
						this.listeNbPaires.add(nbPaires);
					}
				}
			}

			// Création des tableaux de composants
			this.panelGrid  = new JPanel(new GridLayout(tailleTotale + 1, 1, 0, 12));
			this.panelGrid.setBackground(Couleur.COULEUR_FOND.getColor());
			
			this.tabPanel   = new JPanel[tailleTotale];
			this.tabTitre   = new JLabel[tailleTotale];
			this.tabTxtMult = new JTextField[tailleTotale * 2];
			this.lstBtnRole = new ArrayList<JButton>();
			this.pop        = new PopUp(this.ctrl);

			// Initialisation des panels conteneurs
			for(int cpt2 = 0; cpt2 < this.tabPanel.length; cpt2++) 
			{
				this.tabPanel[cpt2] = new JPanel(new BorderLayout(10, 8));
				this.stylerPanelContenu(this.tabPanel[cpt2]);
			}

			// Initialisation des champs de texte
			for(int cpt1 = 0; cpt1 < this.tabTxtMult.length; cpt1++) 
			{
				this.tabTxtMult[cpt1] = new JTextField();
				this.tabTxtMult[cpt1].setText(lstInfoTotale.get(cpt1));
				
				// Désactiver le champ de gauche (source) car souvent fixe ou géré différemment
				if(cpt1 % 2 == 0)
					this.tabTxtMult[cpt1].setEnabled(false);
					
				this.stylerTextField(this.tabTxtMult[cpt1]);
			}

			// Initialisation des titres
			for(int cpt2 = 0; cpt2 < this.tabTitre.length; cpt2++) 
			{
				this.tabTitre[cpt2] = new JLabel(lstTitres.get(cpt2));
				this.stylerLabel(this.tabTitre[cpt2]);
				this.stylerLabelDescription(this.tabTitre[cpt2]);
			}

			// Initialisation des boutons "Role"
			for(int cpt5 = 0; cpt5 < this.tabPanel.length; cpt5++)
			{
				this.lstBtnRole.add(new JButton("+ Role"));
				this.stylerBouton(this.lstBtnRole.get(cpt5), false);
			}

			this.btnValid = new JButton("Valider");
			this.stylerBouton(this.btnValid, true);
		} 
		else 
		{
			// Si aucune classe n'est sélectionnée, nettoyer l'affichage
			this.panelGrid = new JPanel(new GridLayout());
			this.panelGrid.setBackground(Couleur.COULEUR_FOND.getColor());
			this.tabPanel        = new JPanel[0];
			this.tabTitre        = new JLabel[0];
			this.tabTxtMult      = new JTextField[0];
			this.listeCles      .clear();
			this.listeIndexDebut.clear();
			this.listeNbPaires  .clear();
			
			this.btnValid = new JButton("Valider");
			this.stylerBouton(this.btnValid, true);
			
			this.classActuelle = null;
			this.lstBtnRole    = new ArrayList<JButton>();
			this.pop           = new PopUp(this.ctrl);
		}
	}

	/**
	 * Assemble les composants dans le panneau.
	 */
	private void addPosition()
	{
		if(this.classActuelle != null) 
		{
			for(int cpt = 0; cpt < this.tabPanel.length; cpt++)
			{
				JPanel panelHaut = new JPanel(new GridLayout(1, 2));
				panelHaut.add(this.tabTitre[cpt]);

				JPanel panelBtn = new JPanel(new FlowLayout(FlowLayout.RIGHT));
				panelBtn.add(this.lstBtnRole.get(cpt));
				
				panelHaut.add(panelBtn);

				panelHaut.setOpaque(false);
				panelBtn.setOpaque(false);
				
				JPanel panelChamps = new JPanel(new GridLayout(1, 2, 5, 0));
				panelChamps.add(this.tabTxtMult[cpt * 2]    );     
				panelChamps.add(this.tabTxtMult[cpt * 2 + 1]);
				
				this.tabPanel[cpt].add(panelHaut, BorderLayout.NORTH);
				this.tabPanel[cpt].add(panelChamps                  );
				this.panelGrid    .add(this.tabPanel[cpt]           );
			}

			if(this.tabPanel.length != 0)
			{
				JPanel panelBtn = new JPanel();
				panelBtn.add(this.btnValid);
				panelBtn.setOpaque(false);
				this.panelGrid.add(panelBtn);
			}
		}

		// Mettre le panelGrid dans le JScrollPane
		this.scrollPane.setViewportView(this.panelGrid);
		this.add(this.scrollPane, BorderLayout.CENTER);
		
		// Forcer le recalcul de la taille préférée
		this.panelGrid.revalidate();
	}

	/**
	 * Ajoute les écouteurs d'événements aux composants.
	 */
	private void action()
	{
		if(this.classActuelle != null)
		{
			for(int cpt1 = 0; cpt1 < this.tabTxtMult.length; cpt1++)
			{
				this.tabTxtMult[cpt1].addActionListener(this);
			}
			for(int cpt = 0; cpt < this.lstBtnRole.size(); cpt++)
			{
				this.lstBtnRole.get(cpt).addActionListener(this);
			}
			this.btnValid.addActionListener(this);
		}
	}

	/*----------------------------------------------------------------*/
	/* GESTION ÉVÉNEMENTS                                             */
	/*----------------------------------------------------------------*/

	@Override
	public void actionPerformed(ActionEvent e) 
	{
		if(e.getSource() == this.btnValid) 
		{
			if(validerChamps())
			{
				// Créer une map complète avec toutes les relations
				HashMap<CreeClass, List<List<String>>> nouvelleMap = new HashMap<>();
				
				// Pour chaque clé stockée (classe liée)
				for(int k = 0; k < this.listeCles.size(); k++)
				{
					CreeClass cle  = this.listeCles      .get(k);
					int indexDebut = this.listeIndexDebut.get(k);
					int nbPaires   = this.listeNbPaires  .get(k);
					
					List<List<String>> listePourCetteCle = new ArrayList<>();
					
					// Récupérer les paires de multiplicités pour cette clé dans les champs de texte
					for(int i = 0; i < nbPaires; i++)
					{
						int indexTexte = (indexDebut + i) * 2;
						List<String> pair = new ArrayList<>();
						pair.add(this.tabTxtMult[indexTexte].getText().trim());
						pair.add(this.tabTxtMult[indexTexte + 1].getText().trim());

						// Validation métier spécifique
						if (pair.get(0).equals("0..*") && pair.get(1).equals("0..*") && pair.get(0).equals(pair.get(1)))
						{
							JOptionPane.showMessageDialog
							(
								this,
								"Attention : les deux multiplicités sont identiques (" + pair.get(0) + ")",
								"Avertissement",
								JOptionPane.WARNING_MESSAGE
							);
							this.majInfoClasse(this.classActuelle.getNom());
							return;
						}

						if (!pair.get(0).contains("..") || !pair.get(1).contains(".."))
						{
							JOptionPane.showMessageDialog
							(
								this,
								"Erreur : Au moins une multiplicité est invalide.\n" +
								"Format attendu : '0..*', '1..*', '1..1', etc.\n" +
								"Valeurs actuelles : " + pair.get(0) + " et " + pair.get(1),
								"Format de multiplicité invalide",
								JOptionPane.ERROR_MESSAGE
							);
							this.majInfoClasse(this.classActuelle.getNom());
							return;
						}

						listePourCetteCle.add(pair);
					}
					
					nouvelleMap.put(cle, listePourCetteCle);
				}

				// Mettre à jour dans le contrôleur pour la classe courante
				CreeClass classeActuelle = null;
				for(int cpt = 0; cpt < this.ctrl.getLstClass().size(); cpt++)
				{
					if(this.ctrl.getLstClass().get(cpt).getNom().equals(this.classActuelle.getNom()))
					{
						classeActuelle = this.ctrl.getLstClass().get(cpt);
						this.ctrl.setHashMap(classeActuelle, nouvelleMap);
						break;
					}
				}
				
				// Mettre à jour les classes liées (répercuter les changements sur les relations inverses)
				mettreAJourClassesLiees(classeActuelle, nouvelleMap);
				
				afficherMessage("Modifications enregistrées avec succès !", "Succès", JOptionPane.INFORMATION_MESSAGE);
			}
		}

		// Gestion des boutons Rôle
		for(int cpt = 0; cpt < this.lstBtnRole.size(); cpt++)
		{
			if(this.lstBtnRole.get(cpt) == e.getSource())
			{
				ctrl.majIHM();
				this.pop.definirIndexFleche(cpt, classActuelle);
			}
		}
	}

	/*----------------------------------------------------------------*/
	/* OUTILS / UTILITAIRES                                           */
	/*----------------------------------------------------------------*/

	/**
	 * Valide que tous les champs de texte sont remplis.
	 */
	private boolean validerChamps()
	{
		for(int i = 0; i < this.tabTxtMult.length; i++)
		{
			String texte = this.tabTxtMult[i].getText().trim();
			if(texte.isEmpty())
			{
				afficherMessage("Tous les champs doivent être remplis !", "Erreur de validation", JOptionPane.ERROR_MESSAGE);
				return false;
			}
		}
		return true;
	}

	/**
	 * Affiche une boîte de dialogue standard.
	 */
	private void afficherMessage(String message, String titre, int type)
	{
		JOptionPane.showMessageDialog(this, message, titre, type);
	}

	/**
	 * Met à jour les multiplicités inversées dans les classes liées.
	 * Ex: Si Disque → Point a "1..*" et "0..*", alors Point → Disque aura "0..*" et "1..*".
	 * * @param classeSource La classe dont on vient de modifier les relations.
	 * @param nouvelleMap  La nouvelle map des relations de la classe source.
	 */
	private void mettreAJourClassesLiees(CreeClass classeSource, HashMap<CreeClass, List<List<String>>> nouvelleMap)
	{
		if(classeSource == null) return;
		
		// Pour chaque classe avec laquelle la classe source a une relation
		for(Map.Entry<CreeClass, List<List<String>>> entry : nouvelleMap.entrySet())
		{
			CreeClass classeCible            = entry.getKey();
			List<List<String>> multiplicites = entry.getValue();
			
			// Récupérer la multiplicité de la classe cible
			Multiplicite multCible = classeCible.getMultiplicite();

			if(multCible == null) continue;

			HashMap<CreeClass, List<List<String>>> mapCible = multCible.getMapMultiplicites();
			
			// Si la classe cible a bien une référence retour vers la classe source
			if(mapCible.containsKey(classeSource))
			{
				List<List<String>> multiplicitesActuelles = mapCible.get(classeSource);
				List<List<String>> multiplicitesInversees = new ArrayList<>();
				
				for(List<String> paire : multiplicites)
				{
					// Inverser l'ordre : [source, cible] devient [cible, source]
					List<String> paireInversee = new ArrayList<>();
					paireInversee.add(paire.get(1));
					paireInversee.add(paire.get(0)); 
					multiplicitesInversees.add(paireInversee);
				}
				
				// Ne mettre à jour que si les valeurs ont changé pour éviter des boucles infinies ou refresh inutiles
				if(!sontIdentiques(multiplicitesActuelles, multiplicitesInversees))
				{
					// Mettre à jour la map de la classe cible
					mapCible.put(classeSource, multiplicitesInversees);
					
					// Informer le contrôleur de la mise à jour
					this.ctrl.setHashMap(classeCible, mapCible);
					
					System.out.println("Mise à jour inverse : " + classeCible.getNom() + " → " + classeSource.getNom());
				}
			}
		}
	}
	
	/**
	 * Compare deux listes de multiplicités pour vérifier l'égalité stricte.
	 */
	private boolean sontIdentiques(List<List<String>> liste1, List<List<String>> liste2)
	{
		if(liste1 == null || liste2 == null) return false;
		if(liste1.size() != liste2.size())   return false;
		
		for(int i = 0; i < liste1.size(); i++)
		{
			List<String> paire1 = liste1.get(i);
			List<String> paire2 = liste2.get(i);
			
			if(paire1.size() != paire2.size()) return false;
			
			for(int j = 0; j < paire1.size(); j++)
			{
				if(!paire1.get(j).equals(paire2.get(j)))
				{
					return false;
				}
			}
		}
		
		return true;
	}

	/**
	 * Met à jour le panneau avec les informations d'une nouvelle classe.
	 * * @param nom Le nom de la classe à afficher.
	 */
	public void majInfoClasse(String nom) 
	{
		if(nom != null) 
		{
			this.clearInfo();
			for(int cpt = 0; cpt < this.ctrl.getLstClass().size(); cpt++)
			{
				if(nom.equals(this.ctrl.getLstClass().get(cpt).getNom())) 
				{
					this.classActuelle = this.ctrl.getLstClass().get(cpt);
					break;
				}
			}
				
			this.creerComposant();
			this.addPosition();
			this.action();
			
			// Forcer le scroll en haut et recalculer l'affichage
			SwingUtilities.invokeLater(() -> {
				this.scrollPane.getVerticalScrollBar().setValue(0);
				this.panelGrid.revalidate();
				this.scrollPane.revalidate();
			});
			
			this.setVisible(true);
		}
	}

	/**
	 * Réinitialise et cache le panneau d'informations.
	 */
	public void clearInfo() 
	{
		this.removeAll();
		this.panelGrid = new JPanel(new GridLayout());
		this.panelGrid.setBackground(Couleur.COULEUR_FOND.getColor());
		
		this.tabPanel        = new JPanel[0];
		this.tabTitre        = new JLabel[0];
		this.tabTxtMult      = new JTextField[0];
		this.listeCles      .clear();
		this.listeIndexDebut.clear();
		this.listeNbPaires  .clear();
		this.classActuelle   = null;
		
		// Réinitialiser le JScrollPane
		this.scrollPane.setViewportView(null);
		
		this.revalidate()     ;
		this.repaint   ()     ;
		this.setVisible(false);
	}
}