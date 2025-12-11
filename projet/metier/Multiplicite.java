package projet.metier;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Multiplicite {

	HashMap<CreeClass, List<List<String>>> mapMultiplicites;

	public Multiplicite()
	{
		this.mapMultiplicites = new HashMap<CreeClass, List<List<String>>>();
	}

	public void creerMutiplisite(CreeClass creeClass, List<CreeClass> lstClass)
	{
		//verifie si la class quon traite a des attributs
		for (CreeClass autreClass : lstClass)
		{
			List<Attribut> lstAtt             = creeClass.getLstAttribut();
			List<Attribut> lstAttAutre        = autreClass.getLstAttribut();
			List<List<String>> lstToutMultipl = new ArrayList<List<String>>();
			List<String> lstMultiplC          = new ArrayList<String>();
			List<String> lstMultiplAutre      = new ArrayList<String>();
			

			int cptLiaison      = 0;
			int cptLiaisonAutre = 0;

			for (Attribut att : lstAtt)
			{
				// Si l'attribut pointe vers la classe courante
				if (att.getType().contains(autreClass.getNom()) )
				{
					lstMultiplC.add(determinerMultiplicite(att));
					cptLiaison++;
				}
			}

			for (Attribut attAutre : lstAttAutre)
			{
				
				if (attAutre.getType().contains(creeClass.getNom()))
				{
					lstMultiplAutre.add(determinerMultiplicite(attAutre));
					cptLiaisonAutre++;
				}
			}

			if (cptLiaison == 0 && cptLiaisonAutre == 0)
			{
				continue;
			}
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
				while (cptLiaison > cptLiaisonAutre)
				{
					lstMultiplAutre.add("0..*");
					cptLiaisonAutre++;
				}
			}
			
			for (int cpt = 0; cpt < lstMultiplC.size(); cpt++)
			{
				List<String> lstMultiplPair = new ArrayList<String>();


				lstMultiplPair.add( lstMultiplC.get(cpt) );
				lstMultiplPair.add( lstMultiplAutre.get(cpt) );
				lstToutMultipl.add( lstMultiplPair );
			}
			
			if (this.mapMultiplicites != null)
			{
				// Ajouter à la map seulement si des multiplicités ont été trouvées
				this.mapMultiplicites.put(autreClass, lstToutMultipl);
			}
			
		}
	}

	private String determinerMultiplicite(Attribut att)
	{
		String type = att.getType().toLowerCase();

		if (type.contains("list")       ||
			type.contains("set")        ||
			type.contains("collection") ||
			type.contains("[]"))
		{
			return "1..*";
		}

		return "1..1";
	}

	public HashMap<CreeClass, List<List<String>>> getMapMultiplicites() 
	{
		return mapMultiplicites;
	}
}
