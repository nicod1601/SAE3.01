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

public class PanneauInfo extends JPanel implements ActionListener
{
    // Polices de caractères
    private static final Font FONT_TITRE = new Font("Segoe UI", Font.BOLD, 14);
    private static final Font FONT_LABEL = new Font("Segoe UI", Font.PLAIN, 12);
    private static final Font FONT_CHAMP = new Font("Segoe UI", Font.PLAIN, 13);
    
    private String nomClass;
    private JLabel[] tabTitre;
    private JPanel[] tabPanel;
    private JTextField[] tabTxtMult;
    private JPanel panelGrid;
    
    // Stockage de toutes les clés et leurs indices correspondants
    private ArrayList<CreeClass> listeCles;
    private ArrayList<Integer> listeIndexDebut;
    private ArrayList<Integer> listeNbPaires;
    
    //private JButton btnModif;
    private JButton btnValid;
    private Controleur ctrl;

    public PanneauInfo(Controleur ctrl)
    {
        this.setLayout(new BorderLayout());
        this.ctrl = ctrl;
        this.listeCles = new ArrayList<>();
        this.listeIndexDebut = new ArrayList<>();
        this.listeNbPaires = new ArrayList<>();
        this.appliquerStyle();
        this.creerComposant();
        this.addPosition();
        this.action();
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
     * Style pour les champs de texte
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
     * Style pour les labels de titre
     */
    private void stylerLabel(JLabel label)
    {
        label.setFont(new Font("Segoe UI", Font.BOLD, 14));
        label.setForeground(Couleur.COULEUR_TEXTE.getColor());
        label.setBorder(new EmptyBorder(5, 0, 8, 0));
    }

    /**
     * Style pour les labels de description
     */
    private void stylerLabelDescription(JLabel label)
    {
        label.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        label.setForeground(Couleur.COULEUR_TEXTE_SECONDAIRE.getColor());
    }

    /**
     * Style pour les panneaux de contenu
     */
    private void stylerPanelContenu(JPanel panel)
    {
        panel.setBackground(Couleur.COULEUR_LISTE.getColor());
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Couleur.COULEUR_BORDURE.getColor(), 1, true),
            new EmptyBorder(12, 12, 12, 12)
        ));
    }

    private void creerComposant()
    {
        if(this.nomClass != null)
        {
            // Réinitialiser les listes
            this.listeCles.clear();
            this.listeIndexDebut.clear();
            this.listeNbPaires.clear();
            
            CreeClass classeActuelle = null;
            
            for(int cpt = 0; cpt < this.ctrl.getLstClass().size(); cpt++)
            {
                if(this.nomClass.equals(this.ctrl.getLstClass().get(cpt).getNom())) 
                {
                    classeActuelle = this.ctrl.getLstClass().get(cpt);
                    break;
                }
            }
            
            if(classeActuelle != null)
            {
                Multiplicite mult = classeActuelle.getMultiplicite();
                int tailleTotale = 0;
                ArrayList<String> lstInfoTotale = new ArrayList<>();
                ArrayList<String> lstTitres = new ArrayList<>();
                
                // 1. Parcourir les relations directes (classes vers lesquelles cette classe pointe)
                if(mult != null && mult.getMapMultiplicites() != null)
                {
                    for (Map.Entry<CreeClass, List<List<String>>> entry : mult.getMapMultiplicites().entrySet())
                    {
                        CreeClass cle = entry.getKey();
                        List<List<String>> liste = entry.getValue();
                        
                        // Stocker la clé et l'index de début
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
                            lstTitres.add(this.nomClass + " → " + cle.getNom());
                            tailleTotale++;
                            nbPaires++;
                        }
                        
                        // Stocker le nombre de paires pour cette clé
                        this.listeNbPaires.add(nbPaires);
                    }
                }
                
                // 2. Parcourir TOUTES les autres classes pour trouver celles qui pointent vers cette classe
                for(int i = 0; i < this.ctrl.getLstClass().size(); i++)
                {
                    CreeClass autreClasse = this.ctrl.getLstClass().get(i);
                    
                    // Ne pas traiter la classe actuelle
                    if(autreClasse.equals(classeActuelle)) continue;
                    
                    Multiplicite multAutre = autreClasse.getMultiplicite();
                    if(multAutre == null || multAutre.getMapMultiplicites() == null) continue;
                    
                    // Vérifier si cette autre classe pointe vers la classe actuelle
                    if(multAutre.getMapMultiplicites().containsKey(classeActuelle))
                    {
                        // Ne pas ajouter si on a déjà une relation directe avec cette classe
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
                            List<List<String>> listeInverse = multAutre.getMapMultiplicites().get(classeActuelle);
                            
                            this.listeCles.add(autreClasse);
                            this.listeIndexDebut.add(lstInfoTotale.size());
                            int nbPaires = 0;
                            
                            // Ajouter les multiplicités inversées
                            for (List<String> valeur : listeInverse) 
                            {
                                // Inverser : [source, cible] devient [cible, source]
                                lstInfoTotale.add(valeur.get(1));
                                lstInfoTotale.add(valeur.get(0));
                                lstTitres.add(this.nomClass + " ← " + autreClasse.getNom());
                                tailleTotale++;
                                nbPaires++;
                            }
                            
                            this.listeNbPaires.add(nbPaires);
                        }
                    }
                }

				// Créer les composants
				this.panelGrid = new JPanel(new GridLayout(tailleTotale + 1, 1, 0, 12));
				this.panelGrid.setBackground(Couleur.COULEUR_FOND.getColor());
				this.tabPanel = new JPanel[tailleTotale];
				this.tabTitre = new JLabel[tailleTotale];
				this.tabTxtMult = new JTextField[tailleTotale * 2];

				for(int cpt2 = 0; cpt2 < this.tabPanel.length; cpt2++) 
				{
					this.tabPanel[cpt2] = new JPanel(new BorderLayout(10, 8));
					this.stylerPanelContenu(this.tabPanel[cpt2]);
				}

				for(int cpt1 = 0; cpt1 < this.tabTxtMult.length; cpt1++) 
				{
					this.tabTxtMult[cpt1] = new JTextField();
					this.tabTxtMult[cpt1].setText(lstInfoTotale.get(cpt1));
					if(cpt1 %2 == 0)
						this.tabTxtMult[cpt1].setEnabled(false);
					this.stylerTextField(this.tabTxtMult[cpt1]);
				}

				for(int cpt2 = 0; cpt2 < this.tabTitre.length; cpt2++) 
				{
					this.tabTitre[cpt2] = new JLabel(lstTitres.get(cpt2));
					this.stylerLabel(this.tabTitre[cpt2]);
					this.stylerLabelDescription(this.tabTitre[cpt2]);
				}
            }

           // this.btnModif = new JButton("Modifier");
            this.btnValid = new JButton("Valider");
            //stylerBouton(this.btnModif, false);
            stylerBouton(this.btnValid, true);
        } 
        else 
        {
            this.panelGrid = new JPanel(new GridLayout());
            this.panelGrid.setBackground(Couleur.COULEUR_FOND.getColor());
            this.tabPanel = new JPanel[0];
            this.tabTitre = new JLabel[0];
            this.tabTxtMult = new JTextField[0];
            this.listeCles.clear();
            this.listeIndexDebut.clear();
            this.listeNbPaires.clear();
            //this.btnModif = new JButton("Modifier");
            this.btnValid = new JButton("Valider");
            //stylerBouton(this.btnModif, false);
            stylerBouton(this.btnValid, true);
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
			if(this.tabPanel.length != 0 )
			{
				JPanel panelBtn = new JPanel();
				//panelBtn.add(this.btnModif);
				panelBtn.add(this.btnValid);
				this.panelGrid.add(panelBtn);
			}

        }

        this.add(this.panelGrid);
    }

    private void action()
    {
        if(this.nomClass != null)
        {
            for(int cpt1 = 0; cpt1 < this.tabTxtMult.length; cpt1++)
            {
                this.tabTxtMult[cpt1].addActionListener(this);
            }

            //this.btnModif.addActionListener(this);
            this.btnValid.addActionListener(this);
        }
    }

    public void actionPerformed(ActionEvent e) 
    {
        if(e.getSource() == this.btnValid) 
        {
            if(validerChamps())
            {
                // Créer une map complète avec toutes les relations
                HashMap<CreeClass, List<List<String>>> nouvelleMap = new HashMap<>();
                
                // Pour chaque clé stockée
                for(int k = 0; k < this.listeCles.size(); k++)
                {
                    CreeClass cle = this.listeCles.get(k);
                    int indexDebut = this.listeIndexDebut.get(k);
                    int nbPaires = this.listeNbPaires.get(k);
                    
                    List<List<String>> listePourCetteCle = new ArrayList<>();
                    
                    // Récupérer les paires de multiplicités pour cette clé
                    for(int i = 0; i < nbPaires; i++)
                    {
                        int indexTexte = (indexDebut + i) * 2;
                        List<String> pair = new ArrayList<>();
                        pair.add(this.tabTxtMult[indexTexte].getText().trim());
                        pair.add(this.tabTxtMult[indexTexte + 1].getText().trim());
                        listePourCetteCle.add(pair);
                    }
                    
                    nouvelleMap.put(cle, listePourCetteCle);
                }

                // Mettre à jour dans le contrôleur (classe courante)
                CreeClass classeActuelle = null;
                for(int cpt = 0; cpt < this.ctrl.getLstClass().size(); cpt++)
                {
                    if(this.ctrl.getLstClass().get(cpt).getNom().equals(this.nomClass))
                    {
                        classeActuelle = this.ctrl.getLstClass().get(cpt);
                        this.ctrl.setHashMap(classeActuelle, nouvelleMap);
                        break;
                    }
                }
                
                // Mettre à jour les classes liées (inverser les multiplicités)
                mettreAJourClassesLiees(classeActuelle, nouvelleMap);
                
                // Désactiver les champs après validation
                for(int cpt1 = 0; cpt1 < this.tabTxtMult.length; cpt1++)
                {
                    this.tabTxtMult[cpt1].setEnabled(false);
                }
                
                afficherMessage("Modifications enregistrées avec succès !", "Succès", JOptionPane.INFORMATION_MESSAGE);
            }
        }

        /*if(e.getSource() == this.btnModif) 
        {
            for(int cpt1 = 0; cpt1 < this.tabTxtMult.length; cpt1++)
            {
                this.tabTxtMult[cpt1].setEnabled(true);
            }
        }*/
    }

    /**
     * Valide les champs avant l'enregistrement
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
     * Affiche un message à l'utilisateur
     */
    private void afficherMessage(String message, String titre, int type)
    {
        JOptionPane.showMessageDialog(this, message, titre, type);
    }

    /**
     * Met à jour les multiplicités inversées dans les classes liées
     * Si Disque → Point a "1..*" et "0..*", alors Point → Disque aura "0..*" et "1..*"
     */
    private void mettreAJourClassesLiees(CreeClass classeSource, HashMap<CreeClass, List<List<String>>> nouvelleMap)
    {
        if(classeSource == null) return;
        
        // Pour chaque classe liée dans la nouvelle map
        for(Map.Entry<CreeClass, List<List<String>>> entry : nouvelleMap.entrySet())
        {
            CreeClass classeCible = entry.getKey();
            List<List<String>> multiplicites = entry.getValue();
            
            // Récupérer la multiplicité de la classe cible
            Multiplicite multCible = classeCible.getMultiplicite();

            if(multCible == null) continue;

            HashMap<CreeClass, List<List<String>>> mapCible = multCible.getMapMultiplicites();
            
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
                
                // Ne mettre à jour que si les valeurs ont changé
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
     * Compare deux listes de multiplicités pour voir si elles sont identiques
     */
    private boolean sontIdentiques(List<List<String>> liste1, List<List<String>> liste2)
    {
        if(liste1 == null || liste2 == null) return false;
        if(liste1.size() != liste2.size()) return false;
        
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
        this.panelGrid.setBackground(Couleur.COULEUR_FOND.getColor());
        this.tabPanel = new JPanel[0];
        this.tabTitre = new JLabel[0];
        this.tabTxtMult = new JTextField[0];
        this.listeCles.clear();
        this.listeIndexDebut.clear();
        this.listeNbPaires.clear();
        this.nomClass = null;
        this.revalidate();
        this.repaint();
        this.setVisible(false);
    }
}