package projet.ihm;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JPanel;
import projet.Controleur;
import projet.metier.CreeClass;

public class PanneauPrincipal extends JPanel 
{
    private Controleur ctrl;
    private List<CreeClass> lstClass;
    private List<JPanel> lstPanel;

    public PanneauPrincipal(Controleur ctrl) 
    {
        this.setLayout(new FlowLayout());
        this.ctrl = ctrl;
        this.lstClass = new ArrayList<>();
        this.lstPanel = new ArrayList<>();
    }

    public void majListeClasses(boolean dossier, String nomFichier)
    {
        if (dossier) 
        {
            this.lstClass = this.ctrl.getLstClass();
            for (CreeClass classe : this.lstClass) 
            {
                System.out.println(classe.getNom());
            }
        } 
        else
        {
            System.out.println("Nom Fichier reçu : " + nomFichier);
            CreeClass nouvelleClasse = this.ctrl.CreerClass(nomFichier);
            
            for (CreeClass classe : this.lstClass)
            {
                if (classe.getNom().equals(nouvelleClasse.getNom()))
                {
                    System.out.println("Classe déjà existante : " + nouvelleClasse.getNom());
                    return;
                }
            }
            this.lstClass.add(nouvelleClasse);
            for (CreeClass classe : this.lstClass)
            {
                System.out.println(classe.getNom());
            }
        }
        this.dessinerUML();
    }

    public void viderListeClasses()
    {
        this.lstClass.clear();
        this.lstPanel.clear();
        this.removeAll();
        this.revalidate();
        this.repaint();
    }

    public void dessinerUML()
    {
        this.lstPanel.clear();
        this.removeAll();
        
        for (CreeClass classe : this.lstClass) 
        {
            this.lstPanel.add(this.ctrl.UML(classe));
        }

        for(JPanel p : this.lstPanel)
        {
            p.setPreferredSize(new Dimension(500, 500));
        }
        
        this.posComposant();
    }

    public void posComposant() 
    {
        for (JPanel panel : this.lstPanel)
        {
            this.add(panel);
        }
        
        this.revalidate();
        this.repaint();
    }
}