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
    private Controleur ctrl;
    private JList<String> listeFichiers;
    private DefaultListModel<String> modeleFichiers;
    
    public PanneauFichier(Controleur ctrl) 
    {
        this.ctrl = ctrl;
        
        this.configurerPanneau();
        this.creerComposants();
    }
    
    private void configurerPanneau()
    {
        this.setLayout(new BorderLayout());
        this.setPreferredSize(new Dimension(250, 0));
        this.setBackground(Color.WHITE);
        this.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 2, new Color(200, 200, 200)));
    }
    
    private void creerComposants()
    {
        // Label d'en-tête
        JLabel lblFichiers = this.creerLabelEnTete();
        
        // Liste des fichiers
        this.modeleFichiers = new DefaultListModel<>();
        this.listeFichiers = this.creerListeFichiers();
        
        JScrollPane scrollFichiers = new JScrollPane(listeFichiers);
        scrollFichiers.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        scrollFichiers.getViewport().setBackground(Color.WHITE);
        
        // Ajout des composants
        this.add(lblFichiers, BorderLayout.NORTH);
        this.add(scrollFichiers, BorderLayout.CENTER);
    }
    
    private JLabel creerLabelEnTete()
    {
        JLabel label = new JLabel("<html><center>Affichage des fichiers<br>" +
                                  "qui se trouvent dans le<br>" +
                                  "dossier qu'on a<br>" +
                                  "ouvert</center></html>");
        label.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        label.setBorder(new EmptyBorder(10, 10, 10, 10));
        label.setHorizontalAlignment(SwingConstants.CENTER);
        label.setForeground(new Color(70, 70, 70));
        
        return label;
    }
    
    private JList<String> creerListeFichiers()
    {
        JList<String> liste = new JList<>(modeleFichiers);
        liste.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        liste.setFont(new Font("Consolas", Font.PLAIN, 12));
        liste.setBorder(new EmptyBorder(5, 5, 5, 5));
        liste.setBackground(Color.WHITE);
        liste.setSelectionBackground(new Color(230, 240, 255));
        liste.setSelectionForeground(new Color(50, 50, 50));
        
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
}