package src.metier;

import java.io.File;
import java.io.FileInputStream;
import java.util.List;
import java.util.Scanner;

/**
 * Classe responsable de la lecture et du traitement des fichiers Java.
 * <p>
 * Cette classe permet de parser des fichiers Java pour extraire les informations
 * sur les classes (attributs, méthodes, héritage, interfaces, etc.) et créer
 * des objets {@link CreeClass} représentant la structure de ces classes.
 * </p>
 * <p>
 * Elle supporte également la désérialisation de fichiers .ser et la lecture
 * de fichiers .uml via le pattern Factory.
 * </p>
 * @author Équipe 3 SAE 3.01
 * @version 1.0
 */
public class LectureFichier
{

	private CreeClass classe;

	private List<CreeClass> lstMetiersXML = null;
	
	/**
	 * Factory qui crée des objets {@link LectureFichier} selon le type de fichier.
	 * <p>
	 * Ce pattern permet d'instancier différentes implémentations
	 * selon le type de fichier (.java, .ser ou .uml) sans exposer leur logique de création.
	 * </p>
	 * @param data le chemin du fichier à traiter (.java, .ser ou .uml)
	 * @return une instance de LectureFichier prête à traiter le fichier
	 * @throws IllegalArgumentException si le fichier n'est pas un fichier .java valide
	 */
	public static LectureFichier factoryLectureFichier(String data)
	{
		if (data.endsWith(".ser") )
		{
			return new LectureFichier(new GererData() ,0);
		}
		else if (data.endsWith(".uml"))
		{
			return new LectureFichier(new GererData() ,1);
		}
		
		LectureFichier.verifdata(data);
		return new LectureFichier(data);
		
	}
	
	/**
	 * Constructeur privé utilisé par la factory pour charger les fichiers sérialisés et UML.
	 *
	 * @param gererData l'instance de GererData pour accéder aux données
	 * @param dummy paramètre discriminant : 0 pour fichier .ser, 1 pour fichier .uml
	 */
	private LectureFichier(GererData gererData, int dummy)
	{
		if (dummy == 0)
			this.lstMetiersXML = gererData.chargerSER();
		else
			this.lstMetiersXML = gererData.chargerUML();
	}
	/**
	 * Constructeur privé qui parse un fichier Java et crée un objet CreeClass.
	 * <p>
	 * Analyse le fichier ligne par ligne pour extraire :
	 * - Le nom de la classe
	 * - Le type (class, interface, record, abstract)
	 * - La classe mère (si héritage)
	 * - Les interfaces implémentées
	 * - Les attributs, constructeurs et méthodes
	 * </p>
	 *
	 * @param data le chemin du fichier Java à traiter
	 */
	private LectureFichier(String data)
	{
		this.classe = new CreeClass();

		String nomComplet = new File(data).getName();
		if (nomComplet.endsWith(".java"))
		{
			this.classe.setNom( nomComplet.substring(0, nomComplet.length() - 5) );
		}
		
		try(Scanner sc = new Scanner(new FileInputStream(data), "UTF8");)
		{
		
			while (sc.hasNext())
			{
				String line = sc.nextLine();

				if(line.equals("") || (line.contains("\"") && !line.contains("final")))
					continue;
				
				/*--------------------------*/
				/* Gestion des commentaires */
				/*--------------------------*/
				
				// Si '/*'' Sur plusieurs lignes '*/''
				if (line.contains("/*"))
					while (!line.contains("*/"))
						line = sc.nextLine();

				// Si "this.x = x; //public String getX()""
				if (line.contains("//"))
				{
					line = line.substring(0, line.indexOf("//"));
				}

				// Si "this.x = x;/* public String getX()*/""
				if (line.contains("/*") && line.contains("*/"))
				{
					line = line.substring(0, line.indexOf("/*")) + line.substring(line.indexOf("*/") + 2);
				}

				// Si class / interface / class abstract / record
				if (line.contains("class") || line.contains("interface") || line.contains("record") ||
				   (line.contains("abstract") && line.contains("class")) )
				{
					/*------------------------------*/
					/* Ananlyse ligne : mot par mot */
					/*------------------------------*/
					
					// Utiliser Scanner pour parser la ligne
					Scanner lineSc = new Scanner(line);
					while (lineSc.hasNext())
					{
						String mot = lineSc.next();
						if(mot.equals(" "))
							continue;
						if(mot.equals("\t"))
							continue;

						if (mot.equals("class") || mot.equals("interface") || mot.equals("record"))
							this.classe.setType( mot );
						if (mot.equals("abstract"))
							this.classe.setEstAbstract(true);

						//extends / implements / record
						switch (mot)
						{
							case "extends" -> { this.classe.setMere(lineSc.next()); }
							
							case "implements" ->
								{
									this.classe.initLstInterface();
									while (lineSc.hasNext())
									{
										String inter = lineSc.next().replace(",", "").trim();
										if (!inter.isEmpty())
										{
											this.classe.addInterface(inter);
										}
									}
								}
							case "record" -> { this.classe.creeRecord(line); }
						}
					}
					lineSc.close();
					continue;
				}
				if (line.contains("enum"))
				{
					// Ne pas traiter les enums : marquer la classe comme ignorée
					this.classe = null;
					break; //On ne gère pas les enums pour l'instant
				}

				// Méthode / Constructeur / Attribut / Abstract
				if ((line.contains("private") || line.contains("protected") || line.contains("public")
					|| line.contains("abstract") ) && !line.contains("class"))
					// sans class car class peut être abstract
				{
					//Si dans la ligne il y a le nom de la classe ainsi qu'une visibilité
					// alors c'est un constructeur
					if (line.contains(this.classe.getNom()) && line.contains("("))
					{
						this.classe.ajouterConstructeur(line);
					}
					else //sinon c'est pas un constructeur
					{
						//S'il y a une visibilité ainsi qu'une parenthèse
						//Mais pas le nom de la classe alors c'est une méthode
						if (line.contains("(") )
						{
							this.classe.ajouterMethode(line);
						}
						//Sinon s'il y a pas de parenthèse et qu'il y a visibilité et un ';' alors c'est attribut
						else if (line.contains(";"))
						{
							this.classe.ajouterAttribut(line);
						}
					}
				}
			}
			sc.close();
			
			//
			this.classe.initLienMulti();

		}
		catch (Exception e)
		{
			System.out.println("\u001B[31m Erreur : le fichier spécifié ( " + data + " ) n'existe pas.\u001B[0m");
		}
	
	}

	/**
	 * Vérifie que le fichier spécifié est bien un fichier Java (.java).
	 *
	 * @param data le chemin du fichier à vérifier
	 * @throws IllegalArgumentException si le fichier n'est pas un fichier .java
	 */
	private static void verifdata(String data) throws IllegalArgumentException
	{
		if (!data.substring(data.length() - 5).equals(".java"))
		{
			throw new IllegalArgumentException("Erreur " + data + " n'est pas un fichier .java");
		}
	}

	/**
	 * Retourne la classe parsée depuis le fichier Java.
	 * 
	 * @return l'objet CreeClass représentant la classe extraite du fichier,
	 *         ou null si le fichier contenait une enum (non supportée)
	 */
	public CreeClass getClasse()
	{
		return this.classe;
	}

	/**
	 * Retourne la liste des classes chargées depuis un fichier sérialisé ou UML.
	 * <p>
	 * Cette méthode est utilisée quand le fichier traité est un fichier .ser ou .uml
	 * plutôt qu'un fichier Java classique.
	 * </p>
	 * 
	 * @return la liste des objets CreeClass chargés, ou null si le fichier
	 *         n'était pas un fichier sérialisé ou UML
	 */
	public List<CreeClass> getLstCreeClassesXML()
	{
		return this.lstMetiersXML;
	}
}