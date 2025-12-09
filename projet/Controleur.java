package projet;

import java.util.List;
import java.util.Map;
import java.util.Scanner;
import projet.ihm.IhmCui;
import projet.metier.Attribut;
import projet.metier.CreeClass;
import projet.metier.LectureRepertoire;
import projet.metier.Methode;

public class Controleur 
{
	/*╔════════════════════════╗*/
	/*║       Attribut         ║*/
	/*╚════════════════════════╝*/
	private CreeClass metier;

	private IhmCui    ihmCui;
	//private IhmGui    ihmGui;

	private LectureRepertoire lectureRepertoire;

	/*╔════════════════════════╗*/
	/*║     Constructeur       ║*/
	/*╚════════════════════════╝*/
	public Controleur(String nomFichier, int niv) 
	{
		this.metier = CreeClass.factoryCreeClass(nomFichier);

		if (niv >= 1 && niv <= 4) 
		{
			this.ihmCui = new IhmCui(this, niv);
		} 
		else
		{
			if (niv >= 5 && niv <= 7) 
			{
				//this.ihmGui = new IhmGui(this, niv);
			}
		}
	}
	/*╔════════════════════════╗*/
	/*║       Getters          ║*/
	/*╚════════════════════════╝*/
	//Getter Metier
	public List<Methode>   getMethode () { return this.metier.getLstMethode (); }
	public List<Attribut>  getAttribut() { return this.metier.getLstAttribut(); }
	public String          getNom()      { return this.metier.getNom();         }

	//Getter LectureRepertoire
	public List<CreeClass>                 getLstClass() { return this.lectureRepertoire.getLstClass(); };
	public Map<CreeClass, List<CreeClass>> getLien()     { return this.lectureRepertoire.getLien();     };

	/*╔════════════════════════╗*/
	/*║          Main          ║*/
	/*╚════════════════════════╝*/
	public static void main(String[] args) 
	{
		int     niv;

		boolean saisieValide = false;

		Scanner sc           = new Scanner(System.in);

		do 
		{
			String blue     = "\u001B[0m" +"\u001B[36m";
			String vert     = "\u001B[32m";
			String jaune    = "\u001B[33m";
			String indispo  = "\u001B[9m" +"\u001B[31m";
			String reset    = "\u001B[0m";

			System.out.println(blue + "╔═══════════════════════════════════════════════╗\n" +
				                      "║       Choisissez un niveau d'affichage :      ║\n" +
				                      "╠═══════════════════════════════════════════════╣\n" +
				               blue + "║" + vert    + "  1 = IHM CUI simple                           " + blue + "║\n" +
				               blue + "║" + vert    +"  2 = IHM CUI Formalisme UML                   "  + blue + "║\n" +
				               blue + "║" + indispo + "  3 = IHM CUI Formalisme UML (Plusieur Classe) " + blue + "║\n" +
				               blue + "║" + indispo + "  4 = IHM CUI Héritage                         " + blue + "║\n" +
				                      "╚═══════════════════════════════════════════════╝" + reset);
			System.out.print(jaune + "Entrez un entier : " + reset);

			if (sc.hasNextInt()) 
			{
				niv = sc.nextInt();
				saisieValide = true;
			} 
			else 
			{
				niv = -1;
				System.out.println("Ce n'est pas un entier valide. Réessayez.");
				
				sc.next();
			}
		} while (!saisieValide);

		Controleur app = new Controleur("../data/Point.java", niv);
		sc.close();
	}
	
}
