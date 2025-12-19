package src.metier;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.io.Serializable;

/**
 * Gère le calcul des multiplicités entre différentes classes du modèle.
 * <p>
 * La map associe à chaque classe une liste de couples de multiplicités 
 * représentant la relation entre la classe traitée et une autre classe.
 * </p>
 * @author Équipe 3 SAE 3.01
 * @version 1.0
 */
public class Multiplicite implements Serializable
{
	/** Version pour Serializable */
	private static final long serialVersionUID = 1L;
	
	/**
	 * Map contenant, pour chaque classe liée, une liste de paires de multiplicités.
	 * Chaque paire représente la multiplicité côté classe principale puis côté autre classe.
	 */
	private HashMap<CreeClass, List<List<String>>> mapMultiplicites;

	/** Liste des identifiants uniques pour chaque association */
	private ArrayList<Integer> lstIdMulti;
	
	/** Compteur pour générer les identifiants uniques des associations */
	private int compteurId;

	/**
	 * Constructeur de la classe Multiplicite.
	 * Initialise la HashMap stockant les multiplicités, la liste des identifiants
	 * et le compteur d'identifiants à 0.
	 */
	public Multiplicite() 
	{
		this.mapMultiplicites = new HashMap<CreeClass, List<List<String>>>();
		this.lstIdMulti = new ArrayList<Integer>();
		this.compteurId = 0;
	}

	/**
	 * Analyse les attributs d'une classe et de celles qui lui sont liées afin de
	 * déterminer les multiplicités UML entre elles.
	 *
	 * @param creeClass la classe principale dont on analyse les relations
	 * @param lstClass  la liste des autres classes du modèle
	 */
	public void creerMutiplisite(CreeClass creeClass, List<CreeClass> lstClass) 
	{
		// Parcourt toutes les autres classes pour chercher des relations
		for (CreeClass autreClass : lstClass) 
		{
			// Récupération des listes d'attributs
			List<Attribut> lstAtt             = creeClass.getLstClassAttribut();
			List<Attribut> lstAttAutre        = autreClass.getLstClassAttribut();
			List<List<String>> lstToutMultipl = new ArrayList<List<String>>();
			List<String> lstMultiplC          = new ArrayList<String>();
			List<String> lstMultiplAutre      = new ArrayList<String>();

			// Compteurs de liaisons trouvées dans chaque sens
			int cptLiaison      = 0;
			int cptLiaisonAutre = 0;

			// Analyse des attributs de la classe principale
			for (Attribut att : new ArrayList<Attribut>(lstAtt)) 
			{
				// Si l'attribut pointe vers l'autre classe
				if (att.getType().contains(autreClass.getNom())) 
				{
					lstMultiplC.add(determinerMultiplicite(att));
					cptLiaison++;
				}
			}

			// Analyse des attributs de l'autre classe
			for (Attribut attAutre : lstAttAutre) 
			{
				// Si l'attribut pointe vers la classe principale
				if (attAutre.getType().contains(creeClass.getNom())) 
				{
					lstMultiplAutre.add(determinerMultiplicite(attAutre));
					cptLiaisonAutre++;
				}
			}

			// Si aucune liaison dans un sens ou l'autre, passer à la classe suivante
			if (cptLiaison == 0 && cptLiaisonAutre == 0) 
				continue;
			
			// Compléter les multiplicités pour équilibrer le nombre de liaisons
			else if (cptLiaisonAutre - cptLiaison > 0) 
			{
				// Plus de liaisons depuis l'autre classe : ajouter des "0..*" côté classe principale
				while (cptLiaisonAutre > cptLiaison) 
				{
					lstMultiplC.add("0..*");
					cptLiaison++;
				}
			}
			else if (cptLiaison - cptLiaisonAutre > 0) 
			{
				// Plus de liaisons depuis la classe principale : ajouter des "0..*" côté autre classe
				while (cptLiaison > cptLiaisonAutre) 
				{
					lstMultiplAutre.add("0..*");
					cptLiaisonAutre++;
				}
			}

			// Création de la liste de paires finalisée et génération des identifiants
			for (int cpt = 0; cpt < lstMultiplC.size(); cpt++) 
			{
				List<String> lstMultiplPair = new ArrayList<String>();

				// Ajout de la paire [multiplicité classe principale, multiplicité autre classe]
				lstMultiplPair.add(lstMultiplC.get(cpt));
				lstMultiplPair.add(lstMultiplAutre.get(cpt));
				lstToutMultipl.add(lstMultiplPair);

				// Génération d'un identifiant unique pour cette association
				this.lstIdMulti.add(compteurId++);
			}

			// Ajouter à la map seulement si des multiplicités ont été trouvées
			if (this.mapMultiplicites != null) 
				this.mapMultiplicites.put(autreClass, lstToutMultipl);
		}
	}

	/**
	 * Détermine la multiplicité associée à un attribut selon son type.
	 *
	 * @param att l'attribut analysé
	 * @return une chaîne représentant la multiplicité ("1..1" ou "1..*")
	 */
	private String determinerMultiplicite(Attribut att) 
	{
		String type = att.getType().toLowerCase();

		// Vérifie si le type est une collection ou un tableau
		if (type.contains("list")       ||
			type.contains("set")        ||
			type.contains("collection") ||
			type.contains("[]")           ) 
			return "1..*";

		// Type simple : relation un-à-un
		return "1..1";
	}

	/**
	 * Retourne la map contenant les multiplicités calculées.
	 *
	 * @return une HashMap associant une classe à ses relations de multiplicités
	 */
	public HashMap<CreeClass, List<List<String>>> getMapMultiplicites() 
	{
		return mapMultiplicites;
	}

	/**
	 * Remplace la HashMap des multiplicités par une nouvelle.
	 *
	 * @param nouvelleMap la nouvelle HashMap à utiliser (si null, aucune modification)
	 */
	public void setHashMap(HashMap<CreeClass,List<List<String>>> nouvelleMap)
	{
		// Affichage de la map actuelle pour débogage
		System.out.println("=== mapMultiplicites ===");
		for (Map.Entry<CreeClass, List<List<String>>> entry : this.mapMultiplicites.entrySet())
		{
			System.out.println("Classe liée : " + entry.getKey().getNom());

			for (List<String> pair : entry.getValue())
			{
				System.out.println("   [" + pair.get(0) + ", " + pair.get(1) + "]");
			}
		}

		// Affichage de la nouvelle map pour débogage
		System.out.println("=== nouvelleMap ===");
		for (Map.Entry<CreeClass, List<List<String>>> entry : nouvelleMap.entrySet())
		{
			System.out.println("Classe liée : " + entry.getKey().getNom());

			for (List<String> pair : entry.getValue())
			{
				System.out.println("   [" + pair.get(0) + ", " + pair.get(1) + "]");
			}
		}

		// Si la nouvelle map est null, ne rien faire
		if (nouvelleMap == null)
			return;

		// Remplacement de la map actuelle
		this.mapMultiplicites.clear();
		this.mapMultiplicites.putAll(nouvelleMap);

		// Vérification après mise à jour
		System.out.println("=== Verif de la mise nouvelle mapMultiplicites ===");
		for (Map.Entry<CreeClass, List<List<String>>> entry : this.mapMultiplicites.entrySet())
		{
			System.out.println("Classe liée : " + entry.getKey().getNom());

			for (List<String> pair : entry.getValue())
			{
				System.out.println("   [" + pair.get(0) + ", " + pair.get(1) + "]");
			}
		}
	}

	/**
	 * Retourne le nombre de classes liées (taille de la map).
	 *
	 * @return le nombre d'entrées dans la map des multiplicités
	 */
	public int getNb()
	{
		return this.mapMultiplicites.size();
	}

	/**
	 * Retourne l'identifiant d'une association à l'index donné.
	 *
	 * @param index l'index de l'association dans la liste des identifiants
	 * @return l'identifiant de l'association
	 */
	public int getIdAssociation(int index)
	{
		return this.lstIdMulti.get(index);
	}

	/**
	 * Retourne la liste de tous les identifiants d'associations.
	 *
	 * @return la liste des identifiants uniques des associations
	 */
	public ArrayList<Integer> getLstIdAsso()
	{
		return this.lstIdMulti;
	}

	/**
	 * Recherche et retourne une association par son identifiant.
	 *
	 * @param id l'identifiant de l'association recherchée
	 * @return une chaîne contenant les deux multiplicités concaténées, ou null si non trouvé
	 */
	public String getAssociationParId(int id)
	{
		// Recherche de l'index correspondant à l'identifiant
		int index = this.lstIdMulti.indexOf(id);
		if (index == -1) return null;

		// Parcours de toutes les listes de multiplicités
		for (List<List<String>> lst : mapMultiplicites.values())
		{
			// Si l'index est dans cette liste
			if (index < lst.size())
			{
				List<String> pair = lst.get(index);
				return pair.get(0) + pair.get(1);
			}
			// Sinon, décrémenter l'index pour chercher dans la liste suivante
			index -= lst.size();
		}
		return null;
	}
}