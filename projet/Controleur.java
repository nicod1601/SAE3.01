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

		boolean saisieValide = false;

		Scanner sc           = new Scanner(System.in);

		do 
		{
			System.out.print("Entrez un entier : ");

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

		System.out.println("Vous avez saisi : " + niv);
		Controleur app = new Controleur("../data/Point.java", niv);
		sc.close();
	}
	
}
