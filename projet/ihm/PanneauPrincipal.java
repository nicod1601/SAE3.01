package projet.ihm;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JPanel;
import projet.Controleur;
import projet.metier.Attribut;
import projet.metier.CreeClass;
import projet.metier.Methode;

public class PanneauPrincipal extends JPanel implements MouseListener, MouseMotionListener, MouseWheelListener
{
    private static final Color COULEUR_FOND = new Color(255,255,255);
    private static final Color ROUGE        = new Color(255, 0, 0);
    private static final Color BLEU         = new Color(0,122,255);
    private static final Color NOIR         = new Color(0,0,0);
    private static final Color VERT         = new Color(76, 175, 80);
    private static final Color ROSE         = new Color(255, 105, 180);
    private static final Color CYAN         = new Color(0, 188, 212);
    private static final Color TURQUOISE    = new Color(0, 150, 136);
    private static final Color BRUN         = new Color(121, 85, 72);
    private static final Color GRIS         = new Color(33, 33, 33);

    private static final Color[] TAB_COLOR = {ROUGE, BLEU,VERT,ROSE,CYAN,TURQUOISE,BRUN,GRIS};

    private FrameAppli frame;
    private Controleur ctrl;
    private List<CreeClass> lstClass;
    private int indexSelectionner;

    private int sourisX;
    private int sourisY;
    private boolean inClass;

    private double zoom = 1.0;
    private double offsetX = 0;
    private double offsetY = 0;

    private ArrayList<Integer[]> lstCordFleche;
    private int indexFlecheSelec;
    

    public PanneauPrincipal(Controleur ctrl, FrameAppli frame) 
    {
        this.setBackground(COULEUR_FOND);
        this.ctrl = ctrl;
        this.frame = frame;
        this.lstClass = new ArrayList<>();
        this.indexSelectionner = -1;
        this.indexFlecheSelec  = -1;
        this.sourisX = 0;
        this.sourisY = 0;
        this.inClass = false;

        this.lstCordFleche = new ArrayList<>();
        this.indexFlecheSelec = -1;

        this.addMouseListener(this);
        this.addMouseMotionListener(this);
        this.addMouseWheelListener(this);

    }

    public void majListeClasses(boolean dossier, String nomFichier)
    {
        if(dossier) 
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
        this.repaint();
    }

    public void viderListeClasses()
    {
        this.lstClass.clear();
        this.indexSelectionner = -1;
        this.sourisX = 0;
        this.sourisY = 0;
        this.inClass = false;
        this.offsetX = 0;
        this.offsetY = 0;
        this.zoom = 1.0;
        this.lstCordFleche.clear();
        this.repaint();
    }

    protected void paintComponent(Graphics g) 
    {
        super.paintComponent(g);
        this.lstCordFleche.clear();
        
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Sauvegarde du transform d'origine
        AffineTransform old = g2.getTransform();

        // Application du zoom
        g2.translate(offsetX, offsetY);
        g2.scale(zoom, zoom);

        int xOffset = 50;
        int yOffset = 50;
        int maxHeightLigne = 0;
        int espacementX = 50;
        int espacementY = 50;
        int largeurMax = this.getWidth() - 100;
        
        for(int cpt = 0; cpt < this.lstClass.size(); cpt++)
        {
            CreeClass classe = this.lstClass.get(cpt);
            List<Attribut> lstAttributs = classe.getLstAttribut();
            List<Methode> lstMethodes = classe.getLstMethode();

            int heightTitre = 40;
            int heightAttributs = lstAttributs.size() * 20 + 20;
            int heightMethodes = lstMethodes.size() * 20 + 20;
            int totalHeight = heightTitre + heightAttributs + heightMethodes;

            // Calculer la largeur nécessaire
            int width = 200;
            for (Methode meth : lstMethodes) 
            {
                String str = "";

                switch (meth.getVisibilite())
                {
                    case "public":    str += "+ "; break;
                    case "private":   str += "- "; break;
                    case "protected": str += "# "; break;
                    default:          str += "~ "; break;
                }

                str += meth.getNom() + " (";

                List<String[]> params = meth.getLstParametres();
                for (int i = 0; i < params.size(); i++)
                {
                    String[] p = params.get(i); 
                    str += p[1] + " : " + p[0];
                    if (i < params.size() - 1) 
                    {
                        str += ",   ";
                    }
                }

                str += ")";

                if (!classe.getNom().equals(meth.getNom()))
                {
                    str += " : " + meth.getType();
                }

                if(width < str.length() * 6)
                {
                    width = str.length() * 6;
                }
            }

            // Vérifier aussi la largeur des attributs
            for (Attribut attr : lstAttributs)
            {
                String str = attr.getNom() + " : " + attr.getType();
                if (attr.isEstFinal())
                {
                    str += " <<freeze>>";
                }
                if (width < str.length() * 8)
                {
                    width = str.length() * 8;
                }
            }

            // Mettre à jour la largeur et hauteur
            this.lstClass.get(cpt).setLargeur(width);
            this.lstClass.get(cpt).setHauteur(totalHeight); // CORRECTION : totalHeight au lieu de heightAttributs + heightMethodes

            // IMPORTANT : N'initialiser la position QUE si elle est à (0,0)
            // Sinon, utiliser la position actuelle de la classe
            if (classe.getPosX() == 0 && classe.getPosY() == 0)
            {
                // Si on dépasse la largeur max, on passe à la ligne suivante
                if (xOffset + width > largeurMax && cpt > 0)
                {
                    xOffset = 50;
                    yOffset += maxHeightLigne + espacementY;
                    maxHeightLigne = 0;
                }
                
                this.lstClass.get(cpt).setPosX(xOffset);
                this.lstClass.get(cpt).setPosY(yOffset);
                
                // Mettre à jour les positions pour la prochaine classe
                maxHeightLigne = Math.max(maxHeightLigne, totalHeight);
                xOffset += width + espacementX;
            }

            // Utiliser les positions ACTUELLES de la classe pour dessiner
            int posX = classe.getPosX();
            int posY = classe.getPosY();

            // Rectangle extérieur
            if(this.indexSelectionner == cpt )
            {
                g2.setColor(BLEU);
                g2.drawRect(posX, posY, width, totalHeight);
                g2.setColor(NOIR);

                g2.setColor(BLEU);
                g2.drawLine(posX, posY + heightTitre, posX + width, posY + heightTitre);
                g2.drawLine(posX, posY + heightTitre + heightAttributs, posX + width, posY + heightTitre + heightAttributs);
                g2.setColor(NOIR);
            }
            else
            {
                g2.drawRect(posX, posY, width, totalHeight);
                g2.drawLine(posX, posY + heightTitre, posX + width, posY + heightTitre);
                g2.drawLine(posX, posY + heightTitre + heightAttributs, posX + width, posY + heightTitre + heightAttributs);
            }
            

            // Texte : nom de la classe (centré)
            String nomClasse = classe.getNom();
            int largeurNom = g2.getFontMetrics().stringWidth(nomClasse);
            g2.drawString(nomClasse, posX + (width - largeurNom) / 2, posY + 25);

            // Attributs
            for(Attribut attr : lstAttributs) 
            {
                String symbole = "";
                String finale = "";

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

                String nomAttr = attr.getNom();
                String typeAttr = ": " + attr.getType() + finale;
                String AlligneGauche = " " + symbole + " " + nomAttr;

                int largeurType = g2.getFontMetrics().stringWidth(typeAttr);
                int xGauche = posX + 10; 
                int xType = posX + width - largeurType - 10; 

                g2.drawString(AlligneGauche, xGauche, posY + heightTitre + 20 + (lstAttributs.indexOf(attr) * 20));
                g2.drawString(typeAttr, xType, posY + heightTitre + 20 + (lstAttributs.indexOf(attr) * 20));

                if(attr.isEstStatic()) 
                {
                    int yUnderline = posY + heightTitre + 20 + (lstAttributs.indexOf(attr) * 20) + 2;
                    g2.setColor(GRIS);
                    g2.drawLine(xGauche, yUnderline, xGauche + g2.getFontMetrics().stringWidth(AlligneGauche), yUnderline);
                    g2.setColor(NOIR);
                }
            }

            // Méthodes
            int index = 0;
            for (Methode meth : lstMethodes) 
            {
                String symbole = "";

                switch (meth.getVisibilite())
                {
                    case "public":    symbole = "+ "; break;
                    case "private":   symbole = "- "; break;
                    case "protected": symbole = "# "; break;
                    default:          symbole = "~ "; break;
                }

                String nomMethode = meth.getNom();
                String debP = " (";

                List<String[]> params = meth.getLstParametres();
                String listP = "";
                for (int i = 0; i < params.size(); i++)
                {
                    String[] p = params.get(i); 
                    listP += p[1] + " : " + p[0];
                    if (i < params.size() - 1) 
                    {
                        listP += ",   ";
                    }
                }

                String finP = ")";
                String type = "";
                if (!classe.getNom().equals(meth.getNom()))
                {
                    type = " : " + meth.getType();
                }

                if(type.equals(" : void")){type = "";}

                String AlligneGauche = symbole + " " + nomMethode + debP + listP + finP;
                int largeurType = g2.getFontMetrics().stringWidth(type);
                int xGauche = posX + 10; 
                int xType = posX + width - largeurType - 10;

                int yPos = posY + heightTitre + heightAttributs + 20 + (index * 20);

                g2.drawString(AlligneGauche, xGauche, yPos);
                g2.drawString(type, xType, yPos);

                if(meth.isEstStatic()) 
                {
                    int yUnderline = posY + heightTitre + heightAttributs + 20 + (lstMethodes.indexOf(meth) * 20) + 2;
                    g2.setColor(GRIS);
                    g2.drawLine(xGauche, yUnderline, xGauche + g2.getFontMetrics().stringWidth(AlligneGauche), yUnderline);
                    g2.setColor(NOIR);
                }

                index++;
            }
            
        }

        for(CreeClass cl1 : this.lstClass)
        {
            int xDepFleche = cl1.getPosX();
            int yDepFleche = cl1.getPosY() + cl1.getHauteur() / 2;

            for(CreeClass cl2 : cl1.getLien().getLstLienAttribut())
            {
                int xFinFleche = cl2.getPosX();
                int yFinFleche = cl2.getPosY() + cl2.getHauteur() / 2;

                int dx = xFinFleche - xDepFleche;
                int dy = yFinFleche - yDepFleche;

                if(Math.abs(dx) > Math.abs(dy))
                {
                    if (dx > 0) 
                    {
                        // cl2 est à droite de cl1
                        xDepFleche = cl1.getPosX() + cl1.getLargeur();
                        xFinFleche = cl2.getPosX();
                    } 
                    else 
                    {
                        // cl2 est à gauche de cl1
                        xDepFleche = cl1.getPosX();
                        xFinFleche = cl2.getPosX() + cl2.getLargeur();
                    }
                }
                /*else
                {
                    if(dy > 0)
                    {
                        yDepFleche = cl1.getPosX() + cl1.getHauteur();
                        yFinFleche = cl2.getPosX();
                    } 
                    else 
                    {
                        yDepFleche = cl1.getPosX();
                        yFinFleche = cl2.getPosX() + cl2.getHauteur();
                    }
                }*/

                g2.setColor(GRIS);
                g2.drawLine(xDepFleche, yDepFleche, xFinFleche, yFinFleche);

                Integer[] tabInfo = {xFinFleche, yFinFleche};
                this.lstCordFleche.add(tabInfo);
                

                int taille = 10;

                double angle = Math.atan2(yFinFleche -yDepFleche, xFinFleche - xDepFleche);

                int xP1 = (int) (xFinFleche - taille * Math.cos(angle - Math.PI / 6));
                int yP1 = (int) (yFinFleche - taille * Math.sin(angle - Math.PI / 6));

                int xP2 = (int) (xFinFleche - taille * Math.cos(angle + Math.PI / 6));
                int yP2 = (int) (yFinFleche - taille * Math.sin(angle + Math.PI / 6));

                g2.drawLine(xFinFleche, yFinFleche, xP1, yP1);
                g2.drawLine(xFinFleche, yFinFleche, xP2, yP2);
                g2.setColor(NOIR);
            }

            

            for(CreeClass cl3 : cl1.getLien().getLstLienInterface())
            {
                int xFin = cl3.getPosX();
                int yFin = cl3.getPosY();

                Integer[] tabInfo = {xFin, yFin};
                this.lstCordFleche.add(tabInfo);

                float[] pattern = {6f, 6f};
                g2.setStroke(new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 1f, pattern, 0f));
                g2.setColor(NOIR);

                g2.setColor(ROUGE);
                g2.drawLine(xDepFleche, yDepFleche, xFin, yFin);
                

                int t = 10;

                g2.drawLine(xFin, yFin, xFin - t, yFin - t);
                g2.drawLine(xFin, yFin, xFin - t, yFin + t);
                g2.setColor(NOIR);
            }

            for(CreeClass cl4 : cl1.getLien().getLstLienHeritage())
            {
                int xFin = cl4.getPosX();
                int yFin = cl4.getPosY() + cl4.getHauteur() / 2;

                Integer[] tabInfo = {xFin, yFin};
                this.lstCordFleche.add(tabInfo);

                int dx = xFin - xDepFleche;
                int dy = yFin - yDepFleche;

                if(Math.abs(dx) > Math.abs(dy))
                {
                    if (dx > 0) 
                    {
                        // cl2 est à droite de cl1
                        xDepFleche = cl1.getPosX() + cl1.getLargeur();
                        xFin = cl4.getPosX();
                    } 
                    else 
                    {
                        // cl2 est à gauche de cl1
                        xDepFleche = cl1.getPosX();
                        xFin = cl4.getPosX() + cl4.getLargeur();
                    }
                }

                g2.setColor(CYAN);
                g2.drawLine(xDepFleche, yDepFleche, xFin, yFin);
                

                int taille = 10;

                double angle = Math.atan2(yFin -yDepFleche, xFin - xDepFleche);

                int xP1 = (int) (xFin - taille * Math.cos(angle - Math.PI / 6));
                int yP1 = (int) (yFin - taille * Math.sin(angle - Math.PI / 6));

                int xP2 = (int) (xFin - taille * Math.cos(angle + Math.PI / 6));
                int yP2 = (int) (yFin - taille * Math.sin(angle + Math.PI / 6));

                int[] xPoints = {xFin, xP1, xP2};
                int[] yPoints = {yFin, yP1, yP2};
                
                g2.fillPolygon(xPoints, yPoints, 3);
                g2.setColor(NOIR);
            }
        }
        // Restauration transform
        g2.setTransform(old);
    }


    public void mouseClicked(MouseEvent e)
    {
        this.inClass = false;
        double realX = (e.getX() - offsetX) / zoom ;
        double realY = (e.getY() - offsetY) / zoom ;

        int tolerance = 5;
        
        for(int cpt = 0; cpt < this.lstCordFleche.size(); cpt++) 
        {
            Integer[] tabInfo = this.lstCordFleche.get(cpt);
            
            int x = tabInfo[0];
            int y = tabInfo[1];
            
            // Calcul de la distance entre le clic et la pointe de la flèche
            double distance = Math.sqrt(Math.pow(realX - x, 2) + Math.pow(realY - y, 2));
            
            // CORRECTION : au lieu de if (realX >= x && realX <= x ...)
            if (distance <= tolerance)
            {
                System.out.println("Flèche cliquée à la position (" + x + ", " + y + ")");
                System.out.println("Index de la flèche : " + cpt);
                this.indexFlecheSelec = cpt;
                return;
            }
        }

        for(int cpt = 0; cpt < this.lstClass.size(); cpt++)
        {
            CreeClass c = this.lstClass.get(cpt);

            int x = c.getPosX();
            int y = c.getPosY();
            int w = c.getLargeur();
            int h = c.getHauteur();



            if (realX >= x && realX <= x + w && realY >= y && realY <= y + h)
            {
                this.indexSelectionner = cpt;
                this.frame.selectionnerList(this.indexSelectionner);
                break;
            }
            else
            {
                this.indexSelectionner = -1;
            }
        }
        this.repaint();
    }
    public void mousePressed(MouseEvent e)
    {
        this.inClass = false;

        for(int cpt = 0; cpt < this.lstClass.size(); cpt++)
        {
            CreeClass c = this.lstClass.get(cpt);

            int x = c.getPosX();
            int y = c.getPosY();
            int w = c.getLargeur();
            int h = c.getHauteur();

            double realX = (e.getX() - offsetX) / zoom ;
            double realY = (e.getY() - offsetY) / zoom ;

            if (realX >= x && realX <= x + w && realY >= y && realY <= y + h)
            {
                this.indexSelectionner = cpt;
                this.frame.selectionnerList(this.indexSelectionner);
                this.inClass = true;

                this.sourisX = (int)(realX - x);
                this.sourisY = (int)(realY - y);


                break;
            }
        }
        this.repaint();
    }

    public void mouseDragged(MouseEvent e) 
    {
        if (this.inClass == true && this.indexSelectionner >= 0)
        {
            double realX = (e.getX() - offsetX) / zoom ;
            double realY = (e.getY() - offsetY) / zoom ;

            int newX = (int)(realX - this.sourisX);
            int newY = (int)(realY - this.sourisY);

            
            this.lstClass.get(this.indexSelectionner).setPosX(newX);
            this.lstClass.get(this.indexSelectionner).setPosY(newY);
            repaint();
        }

        if(this.indexFlecheSelec >= 0)
        {
            double realX = (e.getX() - offsetX) / zoom ;
            double realY = (e.getY() - offsetY) / zoom ;

            Integer tabInfo[] = this.lstCordFleche.get(this.indexFlecheSelec);
            int x = tabInfo[0];
            int y = tabInfo[1];


            int newX = (int)(realX - x);
            int newY = (int)(realY - y);

            
        }
    }

    public void mouseWheelMoved(MouseWheelEvent e)
    {
        double oldZoom = this.zoom;
        
        // Modifier le zoom
        if (e.getWheelRotation() < 0)
            this.zoom *= 1.1;
        else
            this.zoom /= 1.1;
        
        double mouseX = e.getX();
        double mouseY = e.getY();
        
        this.offsetX = mouseX - (mouseX - offsetX) * this.zoom / oldZoom;
        this.offsetY = mouseY - (mouseY - offsetY) * this.zoom / oldZoom;
        
        repaint();
    }

    public void mouseReleased(MouseEvent e){}
    public void mouseEntered(MouseEvent e){}
    public void mouseExited(MouseEvent e){}
    public void mouseMoved(MouseEvent e){}

    public void selectionner(int index)
    {
        for(int cpt = 0; cpt < this.lstClass.size(); cpt++)
        {
            if(index == cpt)
            {
                this.indexSelectionner = cpt;
            }
        }

        this.repaint();
    }

    public void lstSelectionner(ArrayList<Integer> lst)
    {
        System.out.println("liste integer");
    }
}