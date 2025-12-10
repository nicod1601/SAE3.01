package projet.metier;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.List;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class ClassUMLPanel extends JPanel 
{
    private CreeClass class;

    public ClassUMLPanel(CreeClass class) 
    {
        this.class = class;
    }

    protected void paintComponent(Graphics g) 
    {
        super.paintComponent(g);
        List<Attribut> lstAttributs = this.class.getLstAttribut();
        List<Methode> lstMethodes = this.class.getLstMethode();

        // Pour un meilleur rendu (lissage du texte)
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Dimensions de la classe
        int x = 50;
        int y = 50;
        int heightTitre = 40;
        int heightAttributs = lstAttributs.size() * 20 + 20;
        int heightMethodes = lstMethodes.size() * 20 + 20;

        // Mise en forme du rectangle UML
        int width = 0;

        for (Methode meth : lstMethodes) 
        {
            String str = "";

            // --- Visibilité ---
            switch (meth.getVisibilite())
            {
                case "public":    str += "+ "; break;
                case "private":   str += "- "; break;
                case "protected": str += "# "; break;
                default:          str += "~ "; break;
            }

            // --- Nom de la méthode ---
            str += meth.getNom();
            str += " (";

            // --- Paramètres ---
            List<String[]> params = meth.getLstParametres();

            for (int i = 0; i < params.size(); i++)
            {
                String[] p = params.get(i); 
                str += p[1];
                str += " : ";
                str += p[0];

                if (i < params.size() - 1) 
                {
                    str += ",   ";
                }
            }

            str += ")";

            if (!this.class.getNom().equals(meth.getNom()))
            {
                str += " : " + meth.getType();
            }

            if(width < str.length() * 6)
            {
                width = str.length() * 6;
            }
        }

        


        // Rectangle extérieur
        g2.drawRect(x, y, width, heightTitre + heightAttributs + heightMethodes);

        // Séparations
        g2.drawLine(x, y + heightTitre, x + width, y + heightTitre);                                       // ligne du titre
        g2.drawLine(x, y + heightTitre + heightAttributs, x + width, y + heightTitre + heightAttributs); // ligne attributs

        // Texte : nom de la classe
        g2.drawString(this.class.getNom(), x + width / 2, y + 25);

        // Attributs
        for(Attribut attr : lstAttributs) 
        {
            String symbole = "";
            String finale = "";
            String str = "";

            // --- Visibilité ---
            switch (attr.getVisibilite())
            {
                case "public":    symbole = "+ "; break;
                case "private":   symbole = "- "; break;
                case "protected": symbole = "# "; break;
                default:          symbole = "~ "; break;
            }

            if(attr.isEstFinal()) 
            {
                finale = " <<freeze>>";
            }

            // --- Nom de l'attribut ---
            String nomAttr = attr.getNom();
            String typeAttr = ": " + attr.getType() + finale;

            //Allignement gauche 
            String AlligneGauche = " "+symbole + " " + nomAttr ;

            // Largeurs en pixels
            int largeurGauche = g2.getFontMetrics().stringWidth(AlligneGauche);
            int largeurType   = g2.getFontMetrics().stringWidth(typeAttr + finale);


            // Position du texte
            int xGauche = x + 10; 
            int xType = x + width - largeurType - 10; 

            g2.drawString(AlligneGauche, xGauche, y + heightTitre + 20 + (lstAttributs.indexOf(attr) * 20));
            g2.drawString(typeAttr, xType, y + heightTitre + 20 + (lstAttributs.indexOf(attr) * 20));
           
        }


        // Méthodes
        int index = 0;

        for (Methode meth : lstMethodes) 
        {
            String str = "";
            String symbole = "";

            // --- Visibilité ---
            switch (meth.getVisibilite())
            {
                case "public":    symbole = "+ "; break;
                case "private":   symbole = "- "; break;
                case "protected": symbole = "# "; break;
                default:          symbole = "~ "; break;
            }

            // --- Nom de la méthode ---
            String nomMethode  = meth.getNom();
            String debP = " (";

            // --- Paramètres ---
            List<String[]> params = meth.getLstParametres();

            String listP = "";
            for (int i = 0; i < params.size(); i++)
            {
                String[] p = params.get(i); 
                listP += p[1];
                listP += " : ";
                listP += p[0];

                if (i < params.size() - 1) 
                {
                    listP += ",   ";
                }
            }

            String finP = ")";

            String type = "";
            if (!this.class.getNom().equals(meth.getNom()))
            {
                type = " : " + meth.getType();
            }

            //Allignement gauche 
            String AlligneGauche = " "+symbole + " " + nomMethode + debP + listP + finP;

            // Largeurs en pixels
            int largeurGauche = g2.getFontMetrics().stringWidth(AlligneGauche);
            int largeurType   = g2.getFontMetrics().stringWidth(type);


            // Position du texte
            int xGauche = x + 10; 
            int xType = x + width - largeurType - 10;

            int yPos = y + heightTitre + heightAttributs + 20 + (index * 20);

            g2.drawString(AlligneGauche, xGauche, yPos);
            g2.drawString(type, xType, yPos);

            index++;
        }
        
    }

    public static void main(String[] args)
    {
        JFrame frame = new JFrame("Diagramme UML");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(420, 300);

        CreeClass exempleClass = CreeClass.factoryCreeClass("Methode.java");
        CreeClass exemple2 = CreeClass.factoryCreeClass("Attribut.java");

        JPanel p = new ClassUMLPanel(exempleClass);


        frame.add(p);
        frame.setVisible(true);
    }
}