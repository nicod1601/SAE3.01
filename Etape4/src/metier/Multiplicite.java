package src.metier;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Gère le calcul des multiplicités entre différentes classes du modèle.
 * <p>
 * La map associe à chaque classe une liste de couples de multiplicités 
 * représentant la relation entre la classe traitée et une autre classe.
 * </p>
 * @author Équipe 3 SAE 3.01
 * @version 1.0
 */
public class Multiplicite 
{

	/**
	 * Map contenant, pour chaque classe liée, une liste de paires de multiplicités.
	 * Chaque paire représente la multiplicité côté classe principale puis côté autre classe.
	 */
	HashMap<CreeClass, List<List<String>>> mapMultiplicites;

	/**
	 * Constructeur de la classe Multiplicite.
	 * Initialise simplement la HashMap stockant les multiplicités.
	 */
	public Multiplicite() 
	{
		this.mapMultiplicites = new HashMap<CreeClass, List<List<String>>>();
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

		// Vérifie si la classe qu'on traite a des attributs
		for (CreeClass autreClass : lstClass) 
		{

			List<Attribut> lstAtt             = creeClass.getLstClassAttribut();
			List<Attribut> lstAttAutre        = autreClass.getLstClassAttribut();
			List<List<String>> lstToutMultipl = new ArrayList<List<String>>();
			List<String> lstMultiplC          = new ArrayList<String>();
			List<String> lstMultiplAutre      = new ArrayList<String>();

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
				if (attAutre.getType().contains(creeClass.getNom())) 
				{
					lstMultiplAutre.add(determinerMultiplicite(attAutre));
					cptLiaisonAutre++;
				}
			}

			// Si aucune liaison dans un sens ou l'autre, passer
			if (cptLiaison == 0 && cptLiaisonAutre == 0) 
				continue;
			
			// Compléter les multiplicités pour équilibrer le nombre de liaisons
			else if (cptLiaisonAutre - cptLiaison > 0) 
			{
				while (cptLiaisonAutre > cptLiaison) 
				{
					lstMultiplC.add("0..*");
					cptLiaison++;
				}
			}
			else if (cptLiaison - cptLiaisonAutre > 0) 
			{
				while (cptLiaison > cptLiaisonAutre) {
					lstMultiplAutre.add("0..*");
					cptLiaisonAutre++;
				}
			}

			// Création de la liste de paires finalisée
			for (int cpt = 0; cpt < lstMultiplC.size(); cpt++) 
			{
				List<String> lstMultiplPair = new ArrayList<String>();

				lstMultiplPair.add(lstMultiplC.get(cpt));
				lstMultiplPair.add(lstMultiplAutre.get(cpt));
				lstToutMultipl.add(lstMultiplPair);
			}

			// Ajouter à la map seulement si des multiplicités ont été trouvées
			if (this.mapMultiplicites != null) 
				this.mapMultiplicites.put(autreClass, lstToutMultipl);
		}
	}

	/**
	 * Détermine la multiplicité associée à un attribut selon son type.
	 * <p>
	 * Si le type comporte une collection (List, Set, Collection, tableau),
	 * la multiplicité est considérée comme "1..*".
	 * Sinon, elle est considérée comme "1..1".
	 * </p>
	 *
	 * @param att l'attribut analysé
	 * @return une chaîne représentant la multiplicité ("1..1" ou "1..*")
	 */
	private String determinerMultiplicite(Attribut att) 
	{
		String type = att.getType().toLowerCase();

		if (type.contains("list")       ||
			type.contains("set")        ||
			type.contains("collection") ||
			type.contains("[]")           ) 
			return "1..*";

		return "1..1";
	}

	/**
	 * Retourne la map contenant les multiplicités calculées.
	 *
	 * @return une HashMap associant une classe à ses relations de multiplicités
	 */
	public HashMap<CreeClass, List<List<String>>> getMapMultiplicites() {return mapMultiplicites;}
}
