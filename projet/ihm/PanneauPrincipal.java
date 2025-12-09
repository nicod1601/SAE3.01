package projet.ihm;

import java.util.ArrayList;
import java.util.List;
import javax.swing.JPanel;
import projet.Controleur;
import projet.metier.CreeClass;

public class PanneauPrincipal extends JPanel 
{

    private Controleur ctrl;
    private List<CreeClass> listeClasses;


    public PanneauPrincipal(Controleur ctrl) 
    {
        this.ctrl = ctrl;
        this.listeClasses = new ArrayList<CreeClass>();
    }

    public void majListeClasses(boolean dossier , String nomFichier)
    {
        if (dossier)
        {
            this.listeClasses = this.ctrl.getLstClass();

            for (CreeClass classe : this.listeClasses)
            {
                System.out.println(classe.getNom());
            }
        }
        else
        {
            System.out.println("Nom Fichier reçu : " + nomFichier);

            CreeClass nouvelleClasse = this.ctrl.CreerClass(nomFichier);

            // Vérifier si elle existe déjà
            for (CreeClass classe : this.listeClasses)
            {
                if (classe.getNom().equals(nouvelleClasse.getNom()))
                {
                    System.out.println("Classe déjà existante : " + nouvelleClasse.getNom());
                    return;
                }
            }

            this.listeClasses.add(nouvelleClasse);

            for (CreeClass classe : this.listeClasses)
            {
                System.out.println(classe.getNom());
            }
        }
    }


    public void viderListeClasses()
    {
        this.listeClasses.clear();
    }
}
