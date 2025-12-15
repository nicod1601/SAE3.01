package src;

import src.ihm.IhmCui;

import src.metier.Attribut;
import src.metier.CreeClass;
import src.metier.LectureFichier;
import src.metier.LectureRepertoire;
import src.metier.Methode;

import src.ihm.FrameAppli;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Classe Controleur
 * 
 * Cette classe représente le contrôleur principal de l'application de rétro-conception UML.
 * Elle gère l'initialisation des classes, la lecture des fichiers ou répertoires selon le niveau
 * choisi par l'utilisateur, et coordonne les interactions entre la logique métier et l'interface utilisateur.
 * 
 * Le contrôleur permet de traiter différents niveaux d'analyse :
 * - Niveau 1-2 : Analyse d'un fichier unique
 * - Niveau 3-4 : Analyse d'un répertoire complet
 * 
 * @author Équipe SAE 3.01
 * @version 1.0
 */
public class Controleur 
{
	/*╔════════════════════════╗*/
	/*║       Attribut         ║*/
	/*╚════════════════════════╝*/
	
	/** Le niveau d'analyse choisi par l'utilisateur (1 à 4) */
	private int             niv;
	
	/** Liste des classes métier analysées */
	private List<CreeClass> lstMetiers;
	
	/** Référence vers l'interface utilisateur en mode console */
	private IhmCui          ihmCui;

	private FrameAppli      frame;

	/*╔════════════════════════╗*/
	/*║     Constructeur       ║*/
	/*╚════════════════════════╝*/

	/**
	 * Constructeur du contrôleur.
	 * 
	 * Initialise le contrôleur en demandant le niveau d'analyse à l'utilisateur,
	 * charge les classes selon ce niveau, et crée l'interface utilisateur.
	 * Si le niveau est inférieur à 5, procède à l'initialisation complète.
	 */
	public Controleur()
	{
		do
		{
			this.niv = IhmCui.choixNiv();
			
			if (this.niv == 0)
			{
				break; //Quitter le programme
			}
			
			if (niv < 5)
			{
				do 
				{
					this.lstMetiers = initCreeClass(this.niv);
				}
				while (this.lstMetiers.isEmpty() || this.lstMetiers.get(0) == null);

				this.ihmCui = new IhmCui(this, this.niv);
			}

			if(niv == 5 )
			{
				this.frame = new FrameAppli(this);
			}

		} while (this.niv != 0);
	}


	/*╔════════════════════════╗*/
	/*║         Methodes       ║*/
	/*╚════════════════════════╝*/

	/**
	 * Initialise la liste des classes selon le niveau d'analyse.
	 * 
	 * Pour les niveaux 1 et 2 : demande un chemin de fichier unique à analyser.
	 * Pour les niveaux 3 et 4 : demande un chemin de répertoire à analyser.
	 * 
	 * @param niv Le niveau d'analyse (1-4)
	 * @return La liste des classes analysées
	 */
	public List<CreeClass> initCreeClass(int niv)
	{
		String data = "";
		Scanner sc  = new Scanner(System.in);
		
		List<CreeClass> lstMetiers = new ArrayList<CreeClass>();
		

		if (niv == 1 || niv== 2)
		{
			System.out.print("Nous sommes dans le dossier class. Veuillez entrer le chemin du fichier (ex: ../data/Point.java) : ");
			data = sc.next();
			LectureFichier lf = LectureFichier.factoryLectureFichier(data);
			lstMetiers.add(lf.getClasse());
		}
		else if (niv > 2 && niv < 5)
		{
			System.out.print("Nous sommes dans le dossier class. Veuillez entrer le chemin du dossier (ex: ../data) : ");
			data = sc.next();

			LectureRepertoire lectureRepertoire = new LectureRepertoire(new File(data));

			for ( CreeClass c :lectureRepertoire.getLstClass() )
			{
				lstMetiers.add(c);
			}
		}
		return lstMetiers;
	}

	/*╔════════════════════════╗*/
	/*║       Getters          ║*/
	/*╚════════════════════════╝*/
	
	/**
	 * Retourne la liste des méthodes d'une classe spécifique.
	 * 
	 * @param id L'indice de la classe dans la liste
	 * @return La liste des méthodes de la classe
	 */
	public List<Methode>   getMethode (int id)    { return new ArrayList<Methode>(this.lstMetiers.get(id).getLstMethode ()); }
	
	/**
	 * Retourne la liste des attributs d'une classe spécifique.
	 * 
	 * @param id L'indice de la classe dans la liste
	 * @return La liste des attributs de la classe
	 */
	public List<Attribut>  getAttribut(int id)    { return new ArrayList<Attribut>(this.lstMetiers.get(id).getLstAttribut()); }
	
	/**
	 * Retourne la liste complète des classes analysées.
	 * 
	 * @return La liste des classes métier
	 */
	public List<CreeClass> getLstClass(      )    { return new ArrayList<CreeClass>(this.lstMetiers);                          }
	
	/**
	 * Retourne la liste des noms des classes analysées.
	 * 
	 * @return La liste des noms des classes
	 */
	public List<String>    getNoms    (      )
	{
		List<String> lstNomCreeClass = new ArrayList<String>();

		for (CreeClass metier : lstMetiers)
		{
			lstNomCreeClass.add(metier.getNom());
		}

		return lstNomCreeClass;
	}

	/**
	 * Effectue la lecture d'un répertoire et met à jour la liste des classes.
	 * 
	 * @param dossier Le répertoire à analyser
	 */
	public void LectureRepertoire(File dossier)
	{
		LectureRepertoire lecture = new LectureRepertoire(dossier);
		this.lstMetiers           = lecture.getLstClass();
	}

	/**
	 * Crée une nouvelle instance de CreeClass à partir d'un chemin de fichier.
	 * 
	 * @param chemin Le chemin du fichier à analyser
	 * @return L'objet CreeClass créé
	 */
	public CreeClass CreerClass(String chemin)
	{
		LectureFichier lf = LectureFichier.factoryLectureFichier(chemin);
		return lf.getClasse();
	}

	/*╔════════════════════════╗*/
	/*║          Main          ║*/
	/*╚════════════════════════╝*/
	
	/**
	 * Méthode principale de l'application.
	 * 
	 * Crée une instance du contrôleur pour lancer l'application de rétro-conception UML.
	 * 
	 * @param args Arguments de ligne de commande (non utilisés)
	 */
	public static void main(String[] args) 
	{
		Controleur c = new Controleur();
	}

	
}
