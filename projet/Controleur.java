package projet;

import java.awt.Frame;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import projet.ihm.IhmCui;
import projet.metier.Attribut;
import projet.metier.CreeClass;
import projet.metier.LectureRepertoire;
import projet.metier.Methode;
import projet.ihm.FrameAppli;

public class Controleur 
{
	/*╔════════════════════════╗*/
	/*║       Attribut         ║*/
	/*╚════════════════════════╝*/
	private int             niv;
	private List<CreeClass> lstMetiers;
	private IhmCui          ihmCui;
	private FrameAppli      frameAppli;

	/*╔════════════════════════╗*/
	/*║     Constructeur       ║*/
	/*╚════════════════════════╝*/

	public Controleur()
	{
		this.frameAppli = new FrameAppli(this);
		this.niv = choixNiv();
		
		do 
		{
			this.lstMetiers = initCreeClass(this.niv);
		}
		while (this.lstMetiers.get(0) == null);

		this.ihmCui     = new IhmCui(this, this.niv);

		
	}

	public List<CreeClass> initCreeClass(int niv)
	{
		String data = "";
		Scanner sc = new Scanner(System.in);
		
		List<CreeClass> lstMetiers = new ArrayList<CreeClass>();
		

		if (niv == 1 || niv== 2)
		{
			System.out.println("Quel fichier choisis tu ?");
			data = sc.next();
			lstMetiers.add(CreeClass.factoryCreeClass(data));
		}
		else if (niv > 2 && niv < 5)
		{
			System.out.println("Quel dossier choisis tu ?");
			data = sc.next();
			LectureRepertoire lectureRepertoire = new LectureRepertoire(new File(data));
			for ( CreeClass c :lectureRepertoire.getLstClass() )
					lstMetiers.add(c);
		}
		return lstMetiers;
	}

	/*╔════════════════════════╗*/
	/*║       Getters          ║*/
	/*╚════════════════════════╝*/
	//Getter Metier
	public List<Methode>   getMethode (int id)    { return this.lstMetiers.get(id).getLstMethode (); }
	public List<Attribut>  getAttribut(int id)    { return this.lstMetiers.get(id).getLstAttribut(); }
	public List<String>    getNoms()
	{
		List<String> lstNomCreeClass = new ArrayList<String>();

		for (CreeClass metier : lstMetiers)
			lstNomCreeClass.add(metier.getNom());

		return lstNomCreeClass;         
	}
	public List<CreeClass>                 getLstClass() { return this.lstMetiers; };
	
	public void LectureRepertoire(File dossier)
	{
		LectureRepertoire lecture = new LectureRepertoire(dossier);
		this.lstMetiers = lecture.getLstClass();
	}

	public CreeClass CreerClass(String chemin)
	{
		return CreeClass.factoryCreeClass(chemin);
	}


	/*╔════════════════════════╗*/
	/*║          Main          ║*/
	/*╚════════════════════════╝*/
	public int choixNiv()
	{
		Scanner sc = new Scanner(System.in);
		int     niv = -1;
		boolean saisieValide = false;

		
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
			                   blue + "║" + vert    + "  3 = IHM CUI Formalisme UML (Plusieur Classe) " + blue + "║\n" +
			                   blue + "║" + indispo + "  4 = IHM CUI Héritage                         " + blue + "║\n" +
			                          "╚═══════════════════════════════════════════════╝" + reset);
		
		do 
		{
			System.out.print(jaune + "Entrez un entier : " + reset);
			try {
				niv = sc.nextInt();
				saisieValide = true;
			} catch (Exception e) {
				System.out.println("Ce n'est pas un entier valide. Réessayez.");
				sc.next();
			}
		} while (!(saisieValide && niv > 0 && niv < 8));

		return niv;
	}

	public static void main(String[] args) {
		new Controleur();
	}

	
}
