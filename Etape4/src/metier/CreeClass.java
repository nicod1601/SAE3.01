package src.metier;

import java.io.FileInputStream;
import java.util.*;

/**
 * Classe métier qui nous permet de creer une classe via la lecture de fichier(s) / un dossier.
 * Cette classe nous permet alors d'ajouter des méthodes, des attributs, ainsi que les liens qu'elle a :<br>
 * - Liens en fonction de ses attributs (par exemple si dans ses attributs elle a une (ou plus) autre classe (lienAttribut)).
 * - Liens en fonction de son héritage (par exemple si la dite classe hérite d'une autre classe (lienHeritage)).
 * - Liens en fonction de son interface (par exemple si la classe courante implemente d'autres classes (lienInterface)).
 * <br>
 * @author Équipe 3 SAE 3.01
 * @version 1.0
 */
public class CreeClass
{
	/** Nom de la classe */
	private String nom;
	/** type : class, interface, record*/
	private String type="";
	/** Si class est abstract*/
	private boolean estAbstract=false;
	/** Liste des attributs de la classe (int, String, double, etc...) */
	private List<Attribut> lstAttribut;
	/** Liste des attributs de classe ex : Point x; pour carré */
	private List<Attribut> lstClassAttribut;
	/** Liste des méthodes de la classe (comporte également le constructeur)*/
	private List<Methode> lstMethode;

	/** Nom de la classe mère, null si existe pas */
	private String mere = null;
	/** Liste des interfaces implémenté par la classe */
	private List<String> interfaces = null;
	/** Lien entre les classes selon la liste des attributs */
	private Lien lien;
	/** Multiplicité de la classe avec une autre classe avec la/les multiplicitée(s) */
	private Multiplicite multi;

	private int posX;
	private int posY;
	private int hauteur;
	private int largeur;

	/**
	 * Factory qui crée des objets {@link CreeClass}.
	 * <p>
	 * Ce pattern permet d'instancier différentes implémentations
	 * sans exposer leur logique de création.
	 * </p>
	 * @param Lien du fichier
	 * @return CreeClass
	 */
	public static CreeClass factoryCreeClass(String data)
	{
		CreeClass.verifdata(data);
		return new CreeClass(data);
	}

	/**
	 * Création d'un nouvel objet CreeClass.
	 *
	 * @param data le nom du fichier qu'on va traiter
	 */
	private CreeClass(String data)
	{
		this.posX = 0;
		this.posY = 0;
		this.hauteur = 0;
		this.largeur = 0;


		String nomComplet = new java.io.File(data).getName();
		if (nomComplet.endsWith(".java"))
		{
			this.nom = nomComplet.substring(0, nomComplet.length() - 5);
		}

		this.lstAttribut      = new ArrayList<Attribut>();
		this.lstMethode       = new ArrayList<Methode>();
		this.lstClassAttribut = new ArrayList<Attribut>();
		try(Scanner sc = new Scanner(new FileInputStream(data), "UTF8");)
		{
		
			while (sc.hasNext())
			{
				String line = sc.nextLine();

				if(line.equals(""))
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
				if (line.contains("class") || line.contains("interface") || (line.contains("abstract") && line.contains("class")) || line.contains("record"))
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
							this.type = mot;
						if (mot.equals("abstract"))
							this.estAbstract = true;

						//extends / implements / record
						switch (mot)
						{
							case "extends" -> { this.mere = lineSc.next(); }
							
							case "implements" ->
								{
									this.interfaces = new ArrayList<String>();
									while (lineSc.hasNext())
									{
										String inter = lineSc.next().replace(",", "").trim();
										if (!inter.isEmpty())
										{
											this.interfaces.add(inter);
										}
									}
								}
							case "record" -> { this.creeRecord(line); }
						}
					}
					lineSc.close();
					continue;
				}

				// Méthode / Constructeur / Attribut / Abstract
				if ((line.contains("private") || line.contains("protected") || line.contains("public")
					|| line.contains("abstract") ) && !line.contains("class"))
					// sans class car class peut être abstract
				{
					//Si dans la ligne il y a le nom de la classe ainsi qu'une visibilité alors c'est un constructeur
					if (line.contains(this.nom) && line.contains("("))
					{
						this.ajouterConstructeur(line);
					}
					else //sinon c'est pas un constructeur
					{
						//S'il y a une visibilité ainsi qu'une parenthèse
						//Mais pas le nom de la classe alors c'est une méthode
						if (line.contains("(") )
						{
							this.ajouterMethode(line);
						}
						//Sinon s'il y a pas de parenthèse et qu'il y a visibilité et un ';' alors c'est attribut
						else if (line.contains(";"))
						{
							this.ajouterAttribut(line);
						}
					}
				}
			}
			sc.close();
			
			this.lien = new Lien(this);
			this.multi = new Multiplicite();
			
			

		}
		catch (Exception e)
		{
			System.out.println("\u001B[31m Erreur : le fichier spécifié ( " + data + " ) n'existe pas.\u001B[0m");
		}
	}

	/**
	 * Ajoute le constructeur dans lstMethodes
	 *
	 * @param constructeur la ligne du constructeur
	 * @return void
	 * @throws NomException Problème de parenthèse (non trouvées)
	 */
	private void ajouterConstructeur(String constructeur)
	{
		constructeur = constructeur.trim();

		int posOuv  = constructeur.indexOf("(");
		int posFerm = constructeur.indexOf(")");

		if (posOuv == -1 || posFerm == -1)
		{
			System.out.println("Parenthèses non trouvées");
			System.out.println(constructeur);
			return;
		}

		// Parser la partie avant les parenthèses
		String avantParenthese = constructeur.substring(0, posOuv).trim();
		Scanner avantSc = new Scanner(avantParenthese);

		// premier mot = visibilité
		String visibilite = avantSc.next();
		String nom = "";

		// Mot après visibilité est forcément le nom
		nom = avantSc.next();
		// Si on a un suivant : public Disque methode()
		// Ca veut dire que c'est une méthode et pas un constructeur
		if ( avantSc.hasNext() )
		{
			this.ajouterMethode(constructeur);
			avantSc.close();
			return;
		}
		
		
		avantSc.close();

		// Parser les paramètres
		String paramBrut = constructeur.substring(posOuv + 1, posFerm).trim();
		//Tableau des informations des paramètres du constructeur
		String[] tabInfo = null;

		List<String[]> lstLstParamInfo = new ArrayList<>();
		
		if (!paramBrut.isEmpty())
		{
			Scanner sc = new Scanner(paramBrut);
			sc.useDelimiter(",");
			while (sc.hasNext())
			{
				String sh = sc.next().trim();
				tabInfo   = sh.split(" ");
				lstLstParamInfo.add(tabInfo);
			}
			sc.close();
		}

		Methode c = new Methode(visibilite, null, nom, false,false, lstLstParamInfo);
		this.lstMethode.add(c);
	}

	/**
	 * Ajoute l'Attribut dans lstAttribut.
	 *
	 * @param attribut ligne de l'attribut à traiter
	 * @return void
	 */
	private void ajouterAttribut(String attribut)
	{
		Scanner sc = new Scanner(attribut);

		String visibilite = sc.next();
		boolean estStatic = false;
		boolean estFinal  = false;
		String valeur="";

		String motSuivant = sc.next();

		// Vérifier les modificateurs
		if (motSuivant.equals("static"))
		{
			estStatic = true;
			if (sc.hasNext())
			{
				motSuivant = sc.next();
			}
		}
		if (motSuivant.equals("final"))
		{
			estFinal = true;
			if (sc.hasNext())
			{
				motSuivant = sc.next();
			}
		}

		String type = motSuivant;

		// Dernier mot forcément le nom
		String nom = sc.next();

		//Si attribut est final on récupère sa valeur
		//Ensuite dans attribut on verifie le type
		//et remplace la valeur par "..." si tableau ou list etc...
		if (estFinal)
		{
			// = valeur
			sc.next();
			while(sc.hasNext())
			{
				valeur += sc.next() + " ";
			}

			valeur = valeur.replace(";", "");

			Attribut attr = new Attribut(visibilite, type, nom, estStatic,valeur);
			this.lstAttribut.add(attr);
		}

		if (!nom.isEmpty() && !estFinal)
		{
			nom = nom.replace(";", "");
			Attribut attr = new Attribut(visibilite, type, nom, estStatic);
			this.lstAttribut.add(attr);
		}
		sc.close();
	}

	/**
	 * ajoute la Methode dans lstMethodes
	 *
	 * @param methode ligne de la Methode
	 * @return void
	 */
	private void ajouterMethode(String methode)
	{
		Scanner sc = new Scanner(methode);

		if (!sc.hasNext())
		{
			sc.close();
			return;
		}
		String visibilite   = sc.next();
		boolean estStatic   = false;
		boolean estAbstract = false;

		//Si visibilité non renseigné dans le code
		//On part du principe que si ce n'est pas renseigné c'est public par défaut
		if(visibilite.equals("abstract"))
		{
			visibilite = "public";
			sc.close();
			//on recommence de 0 car 1er mot différent d'une visibilité
			sc = new Scanner(methode);
		}

		if (!sc.hasNext())
		{
			sc.close();
			return;
		}
		//motSuivant = à soit [visibilité] soit abstract
		//Quand motSuivant = abstract alors pas de visibilité mise dans code
		//Quand mot suivant = [visibilité] alors mot après est soit static soit abstract

		String motSuivant = sc.next();

		if (motSuivant.equals("static") || motSuivant.equals("abstract"))
		{
			if (motSuivant.equals("static"))
				estStatic = true;
			else if (motSuivant.equals("abstract"))
				estAbstract = true;

			if (sc.hasNext())
			{
				motSuivant = sc.next();
			}
		}
		
		String type = motSuivant;

		// Reconstruire le reste pour trouver le nom et les paramètres
		StringBuilder reste = new StringBuilder();
		while (sc.hasNext())
		{
			reste.append(sc.next()).append(" ");
		}
		sc.close();

		String resteStr = reste.toString().trim();
		int posOuv = resteStr.indexOf("(");
		int posFerm = resteStr.indexOf(")");

		if (posOuv == -1 || posFerm == -1)
		{
			return;
		}

		String nom = resteStr.substring(0, posOuv).trim();
		String paramBrut = resteStr.substring(posOuv + 1, posFerm).trim();

		List<String[]> lstLstParamInfo = new ArrayList<>();
		String[] tabInfo = null;
		if (!paramBrut.isEmpty())
		{
			sc = new Scanner(paramBrut);
			sc.useDelimiter(",");
			while (sc.hasNext())
			{
				String sh= sc.next().trim();
				tabInfo = sh.split(" ");
				lstLstParamInfo.add(tabInfo);
			}
	
			sc.close();
		}

		Methode meth = new Methode(visibilite, type, nom, estStatic,estAbstract, lstLstParamInfo);
		this.lstMethode.add(meth);
	}

	/**
	 * ça ajoute le constructeur et ces methode cacher dans lstMethodes
	 * et ajoute tous ces attribut dans lstAttribut
	 *
	 * @param record ligne de la record
	 * @return void
	 */
	private void creeRecord(String record)
	{
		record = record.trim();
		int posOuv  = record.indexOf("(");
		int posFerm = record.indexOf(")");
		if (posOuv == -1 || posFerm == -1)
		{
			System.out.println("Problème de parenthèse");
			System.out.println(record);
			return;
		}

		// Parser les paramètres
		String paramBrut = record.substring(posOuv + 1, posFerm).trim();
		List<String[]> lstLstParamInfo = new ArrayList<>();

		String[] tabInfo = null;
		if (!paramBrut.isEmpty())
		{
			Scanner sc = new Scanner(paramBrut);
			sc.useDelimiter(",");
			while (sc.hasNext())
			{
				String sh= sc.next().trim();
				tabInfo = sh.split(" ");
				lstLstParamInfo.add(tabInfo);
			}

			sc.close();
		}
		//cree constructeur
		this.lstMethode.add(new Methode("public",null,this.nom,false,false,lstLstParamInfo) );
		// cree les attribut
		for (String[] sh : lstLstParamInfo)
		{
			this.lstAttribut.add(new Attribut("private",sh[0],sh[1],false) );
		}
		// crée les getter
		for (Attribut att : lstAttribut)
		{
			this.lstMethode.add(new Methode("public",att.getType(),att.getNom(),false,false,null ) );
			// et les setter
			List<String[]> lst = new ArrayList<String[]>();
			String[] tabSh = new String[] {att.getType(),att.getNom()};
			lst.add(tabSh);
			this.lstMethode.add(new Methode("public",att.getType(),att.getNom(),false,false,lst) );
		}
	}

	/**
	 * Cree tout les lien 
	 *
	 * @param lstClass toute les classCree
	 * @return void
	 */
	public void creelien(List<CreeClass> lstClass)
	{
		this.lien.initialiser(lstClass);
	}

	/**
	 * Cree toute les Multiplicite
	 *
	 * @param lstClass toute les classCree
	 * @return void
	 */
	public void creerMultiplicite(List<CreeClass> lstClass)
	{
		this.multi.creerMutiplisite(this, lstClass);
	}

	/**
	 * on vérifie si c'est bien un fichier java
	 *
	 * @param data le nom du fichier
	 * @return boolean
	 */
	private static void verifdata(String data) throws IllegalArgumentException
	{
		if (!data.substring(data.length() - 5).equals(".java"))
		{
			throw new IllegalArgumentException("Erreur " + data + " n'est pas un fichier .java");
		}
	}

	/**
	 * on depalce un attriut dans une autre list
	 *
	 * @param att l'attribut qu'on veux déplacer
	 * @return void
	 */
	public void deplacerAttribut(Attribut att)
	{
		this.lstClassAttribut.add(att);
		this.lstAttribut.remove(att);
	}

	/**
	 * Retourne le nom.
	 *
	 * @return le nom;
	 */
	public String getNom()
	{
		return this.nom;
	}

	/**
	 * Retourne le type (class,abstract,...).
	 *
	 * @return le type;
	 */
	public String getType()
	{
		return this.type;
	}

	/**
	 * 
	 */
	public boolean estAbstract()
	{
		return this.estAbstract;
	}

	/**
	 * Retourne la list de c'est attribut.
	 *
	 * @return la list de c'est attribut;
	 */
	public List<Attribut> getLstAttribut()
	{
		return lstAttribut;
	}

	public List<Attribut> getLstClassAttribut()
	{
		return this.lstClassAttribut;
	}

	/**
	 * Retourne la list de c'est Methodes.
	 *
	 * @return la list de c'est Methodes;
	 */
	public List<Methode> getLstMethode()
	{
		return lstMethode;
	}

	/**
	 * Retourne la mere.
	 *
	 * @return la mere;
	 */
	public String getMere()
	{
		return this.mere;
	}

	/**
	 * Retourne la list interface.
	 *
	 * @return une list interface;
	 */
	public List<String> getInterfaces()
	{
		return this.interfaces;
	}

	/**
	 * Retourne leurs lien.
	 *
	 * @return leurs lien;
	 */
	public Lien getLien()
	{
		return this.lien;
	}

	/**
	 * Retourne la Multiplicite.
	 *
	 * @return la Multiplicite;
	 */
	public Multiplicite getMultiplicite()
	{
		return this.multi;
	}

	public int getPosX()
	{
		return this.posX;
	}

	public int getPosY()
	{
		return this.posY;
	}

	public int getLargeur()
	{
		return this.largeur;
	}

	public int getHauteur()
	{
		return this.hauteur;
	}

	public void setPosX(int x)
	{
		this.posX = x;
	}

	public void setPosY(int y)
	{
		this.posY = y;
	}

	public void setHauteur(int h)
	{
		this.hauteur = h;
	}

	public void setLargeur(int l)
	{
		this.largeur = l;
	}
}