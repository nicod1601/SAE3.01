package projet.ihm;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import projet.Controleur;

public class PanneauFichier extends JPanel
{
    private static final Color COULEUR_FOND = new Color(45, 52, 54);
    private static final Color COULEUR_LISTE = new Color(52, 73, 94);
    private static final Color COULEUR_TEXTE = new Color(236, 240, 241);
    private static final Color COULEUR_TEXTE_SECONDAIRE = new Color(189, 195, 199);
    private static final Color COULEUR_SELECTION = new Color(74, 105, 189);
    private static final Color COULEUR_HOVER = new Color(52, 152, 219);
    private static final Color COULEUR_BORDURE = new Color(55, 66, 77);
    

    private Controleur ctrl;
    private JList<String> listeFichiers;
    private DefaultListModel<String> modeleFichiers;
    private FrameAppli frame;

    public PanneauFichier(Controleur ctrl, FrameAppli frame) 
    {
        this.ctrl = ctrl;
        this.frame = frame;
        
        this.configurerPanneau();
        this.creerComposants();
    }
    
    private void configurerPanneau()
    {
        this.setLayout(new BorderLayout());
        this.setPreferredSize(new Dimension(250, 0));
        this.setBackground(COULEUR_FOND);
        this.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 2, COULEUR_BORDURE));
    }
    
    private void creerComposants()
    {
        // Label d'en-tête
        JLabel lblFichiers = this.creerLabelEnTete();
        
        // Liste des fichiers
        this.modeleFichiers = new DefaultListModel<>();
        this.listeFichiers = this.creerListeFichiers();
        
        JScrollPane scrollFichiers = new JScrollPane(listeFichiers);
        scrollFichiers.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        scrollFichiers.getViewport().setBackground(COULEUR_LISTE);
        scrollFichiers.setBackground(COULEUR_FOND);
        this.listeFichiers.setEnabled(false);
        
        // Ajout des composants
        this.add(lblFichiers, BorderLayout.NORTH);
        this.add(scrollFichiers, BorderLayout.CENTER);

        //Activation des composants

        //this.listeFichiers.addMouseListener(this);

    }
    
    private JLabel creerLabelEnTete()
    {
        JLabel label = new JLabel("<html><center>📁 Fichiers chargés<br><br>" +
                                  "<span style='font-size:10px; color:#bdc3c7;'>" +
                                  "Fichiers du dossier<br>sélectionné" +
                                  "</span></center></html>");
        label.setFont(new Font("Segoe UI Emoji", Font.BOLD, 13));
        label.setBorder(new EmptyBorder(15, 10, 15, 10));
        label.setHorizontalAlignment(SwingConstants.CENTER);
        label.setForeground(COULEUR_TEXTE);
        label.setBackground(COULEUR_FOND);
        label.setOpaque(true);
        
        return label;
    }
    
    private JList<String> creerListeFichiers()
    {
        JList<String> liste = new JList<>(modeleFichiers);
        liste.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        liste.setFont(new Font("Consolas", Font.PLAIN, 12));
        liste.setBorder(new EmptyBorder(5, 5, 5, 5));
        liste.setBackground(COULEUR_LISTE);
        liste.setForeground(COULEUR_TEXTE);
        liste.setSelectionBackground(COULEUR_SELECTION);
        liste.setSelectionForeground(Color.WHITE);
        liste.setFixedCellHeight(28);
        
        return liste;
    }

    
    /**
     * Retourne le modèle de liste pour permettre au PanneauMenu d'ajouter des fichiers
     */
    public DefaultListModel<String> getModeleFichiers()
    {
        return this.modeleFichiers;
    }
    
    /**
     * Ajoute un fichier à la liste
     */
    public void ajouterFichier(String nomFichier)
    {
        if (!this.modeleFichiers.contains(nomFichier))
        {
            this.modeleFichiers.addElement(nomFichier);
        }
    }
    
    /**
     * Vide la liste des fichiers
     */
    public void viderListe()
    {
        this.modeleFichiers.clear();
    }
    
    /**
     * Retourne les fichiers sélectionnés
     */
    public java.util.List<String> getFichiersSelectionnes()
    {
        return this.listeFichiers.getSelectedValuesList();
    }
    
    /**
     * Retourne tous les fichiers de la liste
     */
    public java.util.List<String> getTousFichiers()
    {
        java.util.List<String> fichiers = new java.util.ArrayList<>();
        for (int i = 0; i < this.modeleFichiers.size(); i++)
        {
            fichiers.add(this.modeleFichiers.getElementAt(i));
        }
        return fichiers;
    }

    public void selectionnerList(int index)
    {
        if(index == -1)
        {
            this.listeFichiers.clearSelection();
        }
        else
        {
            this.listeFichiers.setSelectedIndex(index);
        }

    }
}