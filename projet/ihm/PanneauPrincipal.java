package projet.ihm;

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
        this.ctrl = ctrl;
        this.lstClass = new ArrayList<CreeClass>();
        this.lstPanel = new ArrayList<JPanel>();
    }

    public void majListeClasses(boolean dossier , String nomFichier)
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

            // Vérifier si elle existe déjà
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
    }


    public void viderListeClasses()
    {
        this.lstClass.clear();
    }

    public void dessinerUML()
    {
        for(int cpt = 0; cpt < this.lstClass.size(); cpt++)
        {
            this.lstPanel.add(this.ctrl.UML(this.lstClass.get(cpt)));
        }
    }
}
