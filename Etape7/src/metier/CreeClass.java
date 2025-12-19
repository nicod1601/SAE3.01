package src.metier;
import java.io.Serializable;
import java.util.*;
import src.ihm.Fleche;

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
public class CreeClass implements Serializable
{
	/** Version pour Serializable */
	private static final long serialVersionUID = 1L;
	
	/** Nom de la classe */
	private String nom;
	/** type : class, interface, record*/
	private String type;
	/** Si class est abstract*/
	private boolean estAbstract;
	/** Liste des attributs de la classe (int, String, double, etc...) */
	private List<Attribut> lstAttribut;
	/** Liste des attributs de classe ex : Point x; pour carré */
	private List<Attribut> lstClassAttribut;
	/** Liste des méthodes de la classe (comporte également le constructeur)*/
	private List<Methode> lstMethode;
	/** Liste des fleches associer a cette class */
	private List<Fleche> lstFleches;

	/** Map pour les rôle -> Map pour la clé unique.
	 * Id de rôle associé au nom du rôle
	 */
	private Map<Integer, String> mapRole;


	/** Nom de la classe mère, null si existe pas */
	private String mere;
	/** Liste des interfaces implémenté par la classe */
	private List<String> interfaces;
	/** Lien entre les classes selon la liste des attributs */
	private Lien lien;
	/** Multiplicité de la classe avec une autre classe avec la/les multiplicitée(s) */
	private Multiplicite multi;

	private int posX;
	private int posY;
	private int hauteur;
	private int largeur;

	/**
	 * Création d'un nouvel objet CreeClass.
	 */
	public CreeClass()
	{
		this.nom              = "";
		this.type             = "";

		this.estAbstract      = false;

		this.lstAttribut      = new ArrayList<>();
		this.lstClassAttribut = new ArrayList<>();
		this.lstMethode       = new ArrayList<>();
		this.lstFleches       = new ArrayList<>();
		this.mapRole          = new HashMap<Integer, String>();


		this.mere             = null;
		this.lien             = null;
		this.multi            = null;
	}

	/**
	 * Initialise la liste des interfaces implémentées par la classe.
	 */
	public void initLstInterface()
	{
		this.interfaces = new ArrayList<>();
	}
	
	/**
	 * Ajoute une interface à la liste des interfaces implémentées.
	 * @param inter le nom de l'interface à ajouter
	 */
	public void addInterface(String inter)
	{
		this.interfaces.add(inter);
	}

	/**
	 * Initialise les liens et les multiplicités de cette classe.
	 */
	public void initLienMulti()
	{
		this.lien = new Lien(this);
		this.multi = new Multiplicite();
	}

	/**
	 * Ajoute le constructeur dans lstMethodes
	 *
	 * @param constructeur la ligne du constructeur
	 * @return void
	 * @throws NomException Problème de parenthèse (non trouvées)
	 */
	public void ajouterConstructeur(String constructeur)
	{
		constructeur = constructeur.trim();

		int posOuv  = constructeur.indexOf("(");
		int posFerm = constructeur.indexOf(")");

		if (posOuv == -1 || posFerm == -1)
		{
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
			lstLstParamInfo = parserParametresAvecGeneriques(paramBrut);
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
	public void ajouterAttribut(String attribut)
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
	public void ajouterMethode(String methode)
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
		int finOuv = resteStr.lastIndexOf(")");

		if (posOuv == -1 || finOuv == -1)
		{
			return;
		}

		String nom = resteStr.substring(0, posOuv).trim();
		String paramBrut = resteStr.substring(posOuv + 1, finOuv).trim();

		List<String[]> lstLstParamInfo = new ArrayList<>();
		
		if (!paramBrut.isEmpty())
		{
			// Nouvelle méthode pour parser les paramètres qui gère les génériques
			lstLstParamInfo = parserParametresAvecGeneriques(paramBrut);
		}

		Methode meth = new Methode(visibilite, type, nom, estStatic, estAbstract, lstLstParamInfo);
		this.lstMethode.add(meth);
	}

	/**
	 * Parse les paramètres en gérant les types génériques (HashMap<K,V>, List<T>, etc.)
	 * 
	 * @param paramBrut la chaîne brute des paramètres
	 * @return la liste des paramètres parsés [type, nom]
	 */
	private List<String[]> parserParametresAvecGeneriques(String paramBrut)
	{
		List<String[]> lstLstParamInfo = new ArrayList<>();
		
		int index = 0;
		int longueur = paramBrut.length();
		int niveauChevrons = 0;
		StringBuilder paramCourant = new StringBuilder();
		
		while (index < longueur)
		{
			char c = paramBrut.charAt(index);
			
			if (c == '<')
			{
				niveauChevrons++;
				paramCourant.append(c);
			}
			else if (c == '>')
			{
				niveauChevrons--;
				paramCourant.append(c);
			}
			else if (c == ',' && niveauChevrons == 0)
			{
				// Vraie virgule séparant les paramètres (pas à l'intérieur HashMap)
				String param = paramCourant.toString().trim();
				if (!param.isEmpty())
				{
					String[] tabInfo = extraireTypeEtNom(param);
					if (tabInfo != null)
					{
						lstLstParamInfo.add(tabInfo);
					}
				}
				paramCourant = new StringBuilder();
			}
			else
			{
				paramCourant.append(c);
			}
			index++;
		}
		
		// Dernier paramètre
		String dernierParam = paramCourant.toString().trim();
		if (!dernierParam.isEmpty())
		{
			String[] tabInfo = extraireTypeEtNom(dernierParam);
			if (tabInfo != null)
			{
				lstLstParamInfo.add(tabInfo);
			}
		}
		
		return lstLstParamInfo;
	}

	/**
	 * Extrait le type et le nom d'un paramètre
	 * 
	 * @param param le paramètre complet (ex: "HashMap<String, Integer> map")
	 * @return tableau [type, nom] ou null si invalide
	 */
	private String[] extraireTypeEtNom(String param)
	{
		param = param.trim();
		if (param.isEmpty())
		{
			return null;
		}
		
		// Trouver le dernier espace pour séparer type et nom
		int dernierEspace = param.lastIndexOf(' ');
		if (dernierEspace == -1)
		{
			// Pas d'espace, seulement le type (cas rare mais possible)
			return new String[]{param, ""};
		}
		
		String type = param.substring(0, dernierEspace).trim();
		String nom = param.substring(dernierEspace + 1).trim();
		
		return new String[]{type, nom};
	}

	/**
	 * ça ajoute le constructeur et ces methode cacher dans lstMethodes
	 * et ajoute tous ces attribut dans lstAttribut
	 *
	 * @param record ligne de la record
	 * @return void
	 */
	public void creeRecord(String record)
	{
		record = record.trim();
		int posOuv  = record.indexOf("(");
		int posFerm = record.indexOf(")");
		if (posOuv == -1 || posFerm == -1)
		{
			return;
		}

		// Parser les paramètres
		String paramBrut = record.substring(posOuv + 1, posFerm).trim();
		List<String[]> lstLstParamInfo = new ArrayList<>();

		String[] tabInfo = null;
		if (!paramBrut.isEmpty())
		{
			lstLstParamInfo = parserParametresAvecGeneriques(paramBrut);
		}
		//cree constructeur
		this.lstMethode.add(new Methode("public",null,this.nom,false,false,lstLstParamInfo) );
		// cree les attribut
		for (String[] sh : lstLstParamInfo)
		{
			this.lstAttribut.add(new Attribut("private",sh[0],sh[1],false) );
		}
		// crée les getter
		for (Attribut att : this.lstAttribut)
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
	 * Crée tous les liens entre cette classe et les autres classes du projet.
	 *
	 * @param lstClass la liste de toutes les classes créées
	 */
	public void creelien(List<CreeClass> lstClass)
	{
		this.lien.initialiser(lstClass);
	}

	/**
	 * Crée toutes les multiplicités entre cette classe et les autres classes du projet.
	 *
	 * @param lstClass la liste de toutes les classes créées
	 */
	public void creerMultiplicite(List<CreeClass> lstClass)
	{
		this.multi.creerMutiplisite(this, lstClass);
	}

	/**
	 * Déplace un attribut de la liste des attributs à la liste des attributs de classe.
	 *
	 * @param att l'attribut à déplacer
	 */
	public void deplacerAttribut(Attribut att)
	{
		if(this.lstAttribut.contains(att))
		{
			this.lstClassAttribut.add(att);
			this.lstAttribut.remove(att);
		}
	}

	/**
	 * Récupère une flèche de la classe
	 * @param id indice de la flèche que l'on veut récupérer
	 * @param source CreeClass concernée
	 */
	public Fleche getFleche(int id, CreeClass source)
	{
		for(int cpt = 0; cpt < this.lstFleches.size(); cpt++)
		{
			if(this.lstFleches.get(cpt).getId() == id && this == source)
			{
				return this.lstFleches.get(cpt);
			}
		}
		return null;
	}

	/**
	 * Retourne le nom de la classe.
	 *
	 * @return le nom de la classe
	 */
	public String getNom() { return this.nom; }

	/**
	 * Retourne le type de la classe (class, interface, record, etc.).
	 *
	 * @return le type de la classe
	 */
	public String getType() { return this.type; }


	/**
	 * Récupère la map des rôles associés à cette classe.
	 * @return la map des rôles avec leur indice unique
	 */
	public Map<Integer, String> getMapRole() { return this.mapRole; }

	/**
	 * Vérifie si la classe est abstraite.
	 * @return true si la classe est abstraite, false sinon
	 */
	public boolean estAbstract() { return this.estAbstract; }

	/**
	 * Retourne la liste des attributs de la classe.
	 *
	 * @return la liste des attributs d'instance
	 */
	public List<Attribut> getLstAttribut() { return lstAttribut; }

	/**
	 * Retourne la liste des attributs de classe (static).
	 *
	 * @return la liste des attributs de classe
	 */
	public List<Attribut> getLstClassAttribut() { return this.lstClassAttribut; }

	/**
	 * Retourne la liste des méthodes de la classe.
	 *
	 * @return la liste des méthodes (incluant le constructeur)
	 */
	public List<Methode> getLstMethode() { return lstMethode; }

	/**
	 * Retourne le nom de la classe mère (classe parente).
	 *
	 * @return le nom de la classe mère, ou null si pas d'héritage
	 */
	public String getMere() { return this.mere; }

	/**
	 * Retourne la liste des interfaces implémentées par la classe.
	 *
	 * @return la liste des interfaces
	 */
	public List<String> getInterfaces() { return this.interfaces; }

	/**
	 * Retourne les liens de cette classe avec les autres classes.
	 *
	 * @return l'objet Lien contenant les liens d'attribut, d'héritage et d'interface
	 */
	public Lien getLien() { return this.lien; }

	/**
	 * Retourne les multiplicités de cette classe avec les autres classes.
	 *
	 * @return l'objet Multiplicite contenant les multiplicités
	 */
	public Multiplicite getMultiplicite() { return this.multi; }

	/**
	 * Retourne la coordonnée X de position de la classe.
	 * @return la position X
	 */
	public int getPosX() { return this.posX; }

	/**
	 * Retourne la coordonnée Y de position de la classe.
	 * @return la position Y
	 */
	public int getPosY() { return this.posY; }

	/**
	 * Retourne la largeur de la classe.
	 * @return la largeur
	 */
	public int getLargeur() { return this.largeur; }

	/**
	 * Retourne la hauteur de la classe.
	 * @return la hauteur
	 */
	public int getHauteur() { return this.hauteur; }

	/**
	 * Retourne la liste des flèches associées à cette classe.
	 * @return la liste des flèches
	 */
	public List<Fleche> getLstFleches()
	{
		return this.lstFleches;
	}

	/*-----------------------*/
	/*         SETTERS       */
	/*-----------------------*/

	/**
	 * Définit le nom de la classe.
	 * @param nom le nom à assigner
	 */
	public void setNom(String nom) {
		this.nom = nom;
	}

	/**
	 * Définit le type de la classe (class, interface, record, etc.).
	 * @param type le type à assigner
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * Définit si la classe est abstraite.
	 * @param estAbstract true si la classe est abstraite
	 */
	public void setEstAbstract(boolean estAbstract) {
		this.estAbstract = estAbstract;
	}

	/**
	 * Définit la liste des attributs d'instance de la classe.
	 * @param lstAttribut la liste des attributs à assigner
	 */
	public void setLstAttribut(List<Attribut> lstAttribut) {
		this.lstAttribut = lstAttribut;
	}

	/**
	 * Définit la liste des attributs de classe (static).
	 * @param lstClassAttribut la liste des attributs de classe à assigner
	 */
	public void setLstClassAttribut(List<Attribut> lstClassAttribut) {
		this.lstClassAttribut = lstClassAttribut;
	}

	/**
	 * Définit la liste des méthodes de la classe.
	 * @param lstMethode la liste des méthodes à assigner
	 */
	public void setLstMethode(List<Methode> lstMethode) {
		this.lstMethode = lstMethode;
	}

	/**
	 * Définit la classe mère (classe parente).
	 * @param mere le nom de la classe mère à assigner
	 */
	public void setMere(String mere) {
		this.mere = mere;
	}

	/**
	 * Définit la liste des interfaces implémentées.
	 * @param interfaces la liste des interfaces à assigner
	 */
	public void setInterfaces(List<String> interfaces) {
		this.interfaces = interfaces;
	}

	/**
	 * Définit les liens de cette classe avec les autres.
	 * @param lien l'objet Lien à assigner
	 */
	public void setLien(Lien lien) {
		this.lien = lien;
	}

	/**
	 * Définit les multiplicités de cette classe avec les autres.
	 * @param multi l'objet Multiplicite à assigner
	 */
	public void setMultiplicite(Multiplicite multi) {
		this.multi = multi;
	}

	/**
	 * Définit la coordonnée X de position.
	 * @param x la position X à assigner
	 */
	public void setPosX(int x)
	{
		this.posX = x;
	}

	/**
	 * Définit la coordonnée Y de position.
	 * @param y la position Y à assigner
	 */
	public void setPosY(int y)
	{
		this.posY = y;
	}

	/**
	 * Définit la hauteur de la classe.
	 * @param h la hauteur à assigner
	 */
	public void setHauteur(int h)
	{
		this.hauteur = h;
	}

	/**
	 * Définit la largeur de la classe.
	 * @param l la largeur à assigner
	 */
	public void setLargeur(int l)
	{
		this.largeur = l;
	}

	/**
	 * Définit la liste des flèches associées à cette classe.
	 * @param flechesActuelle la liste des flèches à assigner
	 */
	public void setLstFleche(List<Fleche> flechesActuelle)
	{
		this.lstFleches = flechesActuelle;
	}
	
	/**
	 * Associe un rôle à un identifiant unique.
	 * @param id l'identifiant unique du rôle
	 * @param role le nom du rôle à associer
	 */
	public void setRole(int id, String role)
	{
		this.mapRole.put(id, role);
	}

	@Override
	public String toString() 
	{
		StringBuilder sb = new StringBuilder();
		sb.append("CreeClass{");
		sb.append("nom=").append(nom);
		sb.append('}');
		return sb.toString();
	}

	

}