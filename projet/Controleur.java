package projet;

import projet.metier.Attribut;
import projet.metier.CreeClass;
import projet.metier.Methode;

import projet.ihm.IhmCui;
import java.util.List;
import java.util.Scanner;



public class Controleur 
{

	private CreeClass metier;
	private IhmCui    ihm;


	public Controleur(String nomFichier, int niv) 
	{
		this.metier = CreeClass.factoryCreeClass(nomFichier);

		switch(niv)
		{
			case 1 : 
				
				this.ihm = new IhmCui(this, niv);
				break;
			case 2 :
				//this.ihm = new IhmCuilNiv2();
				//break;
			case 3 :
				//this.ihm = new IhmCuilNiv3();
				//break;
			case 4 :
				//this.ihm = new IhmCuilNiv4();
				//break;
			case 5 :
				//this.ihm = new IhmGuiNiv5();
				//break;
			case 6 :
				//this.ihm = new IhmGuiNiv6();
				//break;
			default :
		}
	}

	public List<Methode>  getMethode () {return this.metier.getLstMethode ();}
	public List<Attribut> getAttribut() {return this.metier.getLstAttribut();}

	public static void main(String[] args) 
	{
		int     niv;

		boolean saisieValide = false;

		Scanner sc           = new Scanner(System.in);

		do 
		{
			String blue     = "\u001B[0m" +"\u001B[36m";
			String vert     = "\u001B[32m";
			String yellow   = "\u001B[33m";
			String indispo  = "\u001B[9m" +"\u001B[31m";
			String reset    = "\u001B[0m";

			System.out.println(blue + "╔═══════════════════════════════════════════════╗\n" +
				                      "║       Choisissez un niveau d'affichage :      ║\n" +
				                      "╠═══════════════════════════════════════════════╣\n" +
				               blue + "║" + vert + "  1 = IHM CUI simple                           " + blue + "║\n" +
				               blue + "║" + indispo +"  2 = IHM CUI Formalisme UML                   " + blue + "║\n" +
				               blue + "║" + indispo + "  3 = IHM CUI Formalisme UML (Plusieur Classe) " + blue + "║\n" +
				               blue + "║" + indispo + "  4 = IHM CUI Héritage                         " + blue + "║\n" +
				                      "╚═══════════════════════════════════════════════╝" + reset);
			System.out.print(yellow + "Entrez un entier : " + reset);

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
