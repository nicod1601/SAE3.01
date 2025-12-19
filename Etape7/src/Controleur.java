package src;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

import src.ihm.Fleche;
import src.ihm.FrameAppli;
import src.ihm.IhmCui;
import src.metier.Attribut;
import src.metier.CreeClass;
import src.metier.GererData;
import src.metier.LectureFichier;
import src.metier.LectureRepertoire;
import src.metier.Methode;

/**
 * Classe Controleur
 * * Cette classe représente le contrôleur principal de l'application de rétro-conception UML.
 * Elle gère l'initialisation des classes, la lecture des fichiers ou répertoires selon le niveau
 * choisi par l'utilisateur, et coordonne les interactions entre la logique métier et l'interface utilisateur.
 * * Le contrôleur permet de traiter différents niveaux d'analyse :
 * - Niveau 1-2 : Analyse d'un fichier unique
 * - Niveau 3-4 : Analyse d'un répertoire complet
 * * @author Équipe SAE 3.01
 * @version 1.0
 */
public class Controleur 
{
	/*╔══════════════════════════════════════════════════════════════════════════════════════════════════════════════╗*/
	/*║ ATTRIBUTS                                                                                                    ║*/
	/*╚══════════════════════════════════════════════════════════════════════════════════════════════════════════════╝*/
	
	/** Le niveau d'analyse choisi par l'utilisateur (1 à 4). */
	private int             niv       ;
	
	/** Liste des classes métier analysées. */
	private List<CreeClass> lstMetiers;
	
	/** Référence vers l'interface utilisateur en mode console. */
	private IhmCui          ihmCui    ;

	/** Référence vers la fenêtre principale de l'application (GUI). */
	private FrameAppli      frame     ;

	/*╔══════════════════════════════════════════════════════════════════════════════════════════════════════════════╗*/
	/*║ CONSTRUCTEUR                                                                                                 ║*/
	/*╚══════════════════════════════════════════════════════════════════════════════════════════════════════════════╝*/

	/**
	 * Constructeur du contrôleur.
	 * * Initialise le contrôleur en demandant le niveau d'analyse à l'utilisateur via la console,
	 * charge les classes selon ce niveau, et lance l'interface utilisateur appropriée.
	 */
	public Controleur()
	{
		do
		{
			this.niv = IhmCui.choixNiv();
			
			if (this.niv == 0)
			{
				break; // Quitter le programme
			}
			
			// Mode Console ou hybride (Niveaux 1 à 4)
			if (niv < 5)
			{
				do 
				{
					this.lstMetiers = initCreeClass(this.niv);
				}
				while (this.lstMetiers.isEmpty() || this.lstMetiers.get(0) == null);

				this.ihmCui = new IhmCui(this, this.niv);
			}

			// Mode Graphique complet (Niveau 5)
			if(niv == 5 )
			{
				this.frame = new FrameAppli(this);
			}

		} while (this.niv != 0);
	}

	/*╔══════════════════════════════════════════════════════════════════════════════════════════════════════════════╗*/
	/*║ MÉTHODES INITIALISATION & LECTURE                                                                            ║*/
	/*╚══════════════════════════════════════════════════════════════════════════════════════════════════════════════╝*/

	/**
	 * Initialise la liste des classes selon le niveau d'analyse.
	 * <p>
	 * Pour les niveaux 1 et 2 : demande un chemin de fichier unique à analyser.
	 * Pour les niveaux 3 et 4 : demande un chemin de répertoire à analyser.
	 * * @param niv Le niveau d'analyse (1-4)
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

			for (CreeClass c : lectureRepertoire.getLstClass())
			{
				lstMetiers.add(c);
			}
		}
		return lstMetiers;
	}

	/**
	 * Effectue la lecture d'un répertoire et met à jour la liste des classes du contrôleur.
	 * * @param dossier Le répertoire à analyser
	 */
	public void LectureRepertoire(File dossier)
	{
		LectureRepertoire lecture = new LectureRepertoire(dossier);
		this.lstMetiers           = lecture.getLstClass();
	}

	/**
	 * Crée une nouvelle instance de CreeClass à partir d'un chemin de fichier.
	 * Gère également le chargement depuis des fichiers XML si détectés.
	 * * @param chemin Le chemin du fichier à analyser
	 * @return L'objet CreeClass créé ou null si une liste XML a été chargée
	 */
	public CreeClass CreerClass(String chemin)
	{
		LectureFichier lf = LectureFichier.factoryLectureFichier(chemin);
		
		if (lf.getLstCreeClassesXML() == null)
		{
			return lf.getClasse();
		}
		else
		{
			this.lstMetiers = lf.getLstCreeClassesXML();
			return null;
		}
	}

	/*╔══════════════════════════════════════════════════════════════════════════════════════════════════════════════╗*/
	/*║ PERSISTANCE (XML / SER)                                                                                      ║*/
	/*╚══════════════════════════════════════════════════════════════════════════════════════════════════════════════╝*/

	/**
	 * Sauvegarde la liste des classes métier au format XML.
	 *
	 */
	public void sauvegarderXML()
	{
		GererData gererData = new GererData();
		gererData.sauvegarderUML(this.lstMetiers);
	}

	/** * Sauvegarde la liste des classes métier au format sérialisé (.ser).
	 *
	 */
	public void sauvegarderSER()
	{
		GererData gererData = new GererData();
		gererData.sauvegarderSER(this.lstMetiers);
	}

	/** * Charge une liste de classes depuis un fichier UML (XML).
	 * * @return Liste de CreeClass chargée
	 */
	public List<CreeClass> chargerUML()
	{
		GererData gererData = new GererData();
		return gererData.chargerUML();
	}

	/** * Charge une liste de classes depuis un fichier sérialisé (.ser).
	 * * @return Liste de CreeClass chargée
	 */
	public List<CreeClass> chargerSER()
	{
		GererData gererData = new GererData();
		return gererData.chargerSER();
	}

	/*╔══════════════════════════════════════════════════════════════════════════════════════════════════════════════╗*/
	/*║ GESTION DES FLÈCHES ET IHM                                                                                   ║*/
	/*╚══════════════════════════════════════════════════════════════════════════════════════════════════════════════╝*/

	/**
	 * Fabrique et retourne un objet Fleche.
	 */
	public Fleche ajouterFleche(CreeClass classeDepart, CreeClass classeArrivee, String typeLien, String roleDepart, String roleArrivee, boolean isNavigable, int id)
	{
		Fleche fleche = new Fleche(classeDepart, classeArrivee, typeLien, roleDepart, roleArrivee, isNavigable, id);
		return fleche;
	}

	/**
	 * Demande la mise à jour de l'affichage de la fenêtre principale.
	 */
	public void majIHM()
	{
		this.frame.majIHM();
	}

	/**
	 * Vide la liste des classes métier en mémoire.
	 */
	public void viderLstMetier()
	{
		this.lstMetiers = new ArrayList<>();
	}

	/*╔══════════════════════════════════════════════════════════════════════════════════════════════════════════════╗*/
	/*║ GETTERS                                                                                                      ║*/
	/*╚══════════════════════════════════════════════════════════════════════════════════════════════════════════════╝*/
	
	/**
	 * Retourne la liste des méthodes d'une classe spécifique par son index.
	 * * @param id L'indice de la classe dans la liste
	 * @return La liste des méthodes de la classe
	 */
	public List<Methode> getMethode(int id)    
	{ 
		return new ArrayList<Methode>(this.lstMetiers.get(id).getLstMethode()); 
	}
	
	/**
	 * Retourne la liste des attributs d'une classe spécifique par son index.
	 * * @param id L'indice de la classe dans la liste
	 * @return La liste des attributs de la classe
	 */
	public List<Attribut> getAttribut(int id)    
	{ 
		return new ArrayList<Attribut>(this.lstMetiers.get(id).getLstAttribut()); 
	}
	
	/**
	 * Retourne la liste complète des classes analysées.
	 * * @return Une copie de la liste des classes métier
	 */
	public List<CreeClass> getLstClass()    
	{ 
		return this.lstMetiers == null ? new ArrayList<CreeClass>() : new ArrayList<CreeClass>(this.lstMetiers); 
	}
	
	/**
	 * Retourne la liste des noms des classes analysées.
	 * * @return La liste des noms des classes
	 */
	public List<String> getNoms()
	{
		List<String> lstNomCreeClass = new ArrayList<String>();

		for (CreeClass metier : lstMetiers)
		{
			lstNomCreeClass.add(metier.getNom());
		}

		return lstNomCreeClass;
	}

	/**
	 * Récupère une flèche globale via son index dans l'IHM.
	 * @param index Index de la flèche
	 * @return L'objet Fleche correspondant
	 */
	public Fleche getIndexFleche(int index)
	{
		return this.frame.getLstFlecheGlobal().get(index);
	}

	/**
	 * Recherche une flèche spécifique associée à une classe source.
	 * @param id Identifiant de la flèche
	 * @param source Classe source
	 * @return La flèche trouvée ou null
	 */
	public Fleche getFleche(int id, CreeClass source)
	{
		for (CreeClass c : this.lstMetiers)
		{
			Fleche flecheId = c.getFleche(id, source);
			if(flecheId != null)
				return flecheId;
		}
		return null;
	}

	/**
	 * Retourne la liste des flèches associées à une classe.
	 * @param c La classe concernée
	 * @return Liste des flèches
	 */
	public List<Fleche> getLstFleches(CreeClass c)
	{
		return c.getLstFleches();
	}

	/*╔══════════════════════════════════════════════════════════════════════════════════════════════════════════════╗*/
	/*║ SETTERS                                                                                                      ║*/
	/*╚══════════════════════════════════════════════════════════════════════════════════════════════════════════════╝*/

	/**
	 * Met à jour la map de multiplicité d'une classe spécifique et rafraîchit l'IHM.
	 * * @param c La classe dont la map doit être mise à jour
	 * @param nouvelleMap La nouvelle map de multiplicité
	 */
	public void setHashMap(CreeClass c, HashMap<CreeClass, List<List<String>>> nouvelleMap)
	{
		for(CreeClass cl : this.lstMetiers)
		{
			if(c.getNom().equals(cl.getNom()))
			{
				c.getMultiplicite().setHashMap(nouvelleMap);
			}
		}

		this.majIHM();
	}

	/**
	 * Définit le rôle sur une flèche spécifique.
	 * @param id Identifiant de la flèche
	 * @param role Le texte du rôle
	 * @param source La classe source
	 */
	public void setRole(int id, String role, CreeClass source)
	{
		this.frame.setRole(id, role, source);
		this.majIHM();
	}

	/**
	 * Affecte les flèches globales aux classes correspondantes (Source ou Cible).
	 * * @param lstFleches Liste globale des flèches
	 */
	public void setLstFleche(List<Fleche> lstFleches)
	{
		if (this.lstMetiers != null)
		{
			for (CreeClass c : this.lstMetiers)
			{
				List<Fleche> lstFlechesClass = new ArrayList<Fleche>();
				for (Fleche fl : lstFleches)
				{
					if (fl.getSource() == c || fl.getCible() == c)
					{
						lstFlechesClass.add(fl);
					}
				}
				
				if (!lstFlechesClass.isEmpty())
				{
					c.setLstFleche(lstFlechesClass);
				}
			}
		}
	}

	/*╔══════════════════════════════════════════════════════════════════════════════════════════════════════════════╗*/
	/*║ MAIN                                                                                                         ║*/
	/*╚══════════════════════════════════════════════════════════════════════════════════════════════════════════════╝*/
	
	/**
	 * Point d'entrée de l'application.
	 * Crée une instance du contrôleur pour lancer l'application.
	 * * @param args Arguments de ligne de commande (non utilisés)
	 */
	public static void main(String[] args) 
	{
		new Controleur();
	}
}