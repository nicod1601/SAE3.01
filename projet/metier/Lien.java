package projet.metier;

import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.HashMap;

public class Lien
{
	/*╔════════════════════════╗*/
	/*║       Attribut         ║*/
	/*╚════════════════════════╝*/
	private List<CreeClass>        lienAttribut;
	private List<CreeClass>        lienHeritage;
	private List<CreeClass>        lienInterface;
	private CreeClass              creeClass;
	private Map<CreeClass, String> multiplicitee;

	/*╔════════════════════════╗*/
	/*║     Constructeur       ║*/
	/*╚════════════════════════╝*/
	public Lien(List<CreeClass> lstClass, CreeClass creeClass)
	{
		this.creeClass     = creeClass;
		this.multiplicitee = new HashMap<CreeClass, String>();
		this.lienAttribut  = new ArrayList<CreeClass>();
		this.lienHeritage  = new ArrayList<CreeClass>();
		this.lienInterface = new ArrayList<CreeClass>();
		
		this.lienClasseParAttribut(lstClass);
		this.lienClasseParMere(lstClass);
		this.lienClasseParInterface(lstClass);
	}

	/*╔════════════════════════╗*/
	/*║       Methode          ║*/
	/*╚════════════════════════╝*/
	private void lienClasseParAttribut(List<CreeClass> lstClass)
	{
		// Parcourir toutes les classes pour trouver des liens d'attributs
		for (int cpt2 = 0; cpt2 < lstClass.size(); cpt2++)
		{
			List<Attribut> lstAtt = lstClass.get(cpt2).getLstAttribut();
			if (lstAtt == null) continue;
			
			int countAttributes = 0;
			String multiplicity = "";

			// itérer sur une copie car nous pouvons supprimer des éléments
			for (Attribut att : new ArrayList<Attribut>(lstAtt))
			{
				// Vérifier si le type de l'attribut correspond à la classe actuelle
				if (this.creeClass.getNom().equals(att.getType()))
				{
					countAttributes++;
					// ajouter la classe qui a un attribut de type creeClass
					if (!lienAttribut.contains(lstClass.get(cpt2)))
					{
						this.lienAttribut.add(lstClass.get(cpt2));

						// Déterminer la multiplicité : 0..1 pour un seul, 0..* pour Liste/Collection
						multiplicity = att.getType().toLowerCase().contains("list")              ||
						               att.getType().toLowerCase().contains("collection") ||
						               att.getType().toLowerCase().contains("set")        ||
						               att.getType().              contains("[]")         ||
						               att.getType().toLowerCase().contains("array") ? "0..*" : "0..1";
						multiplicitee.put(lstClass.get(cpt2), multiplicity);
					}

					// Supprimer l'attribut de la liste pour éviter de le traiter à nouveau
					lstClass.get(cpt2).supprimerAttribut(att);
				}
			}
			
			// Si des attributs ont été trouvés, déterminer la multiplicité en fonction du nombre
			if (multiplicity.equals("0..1"))
			{
				if (countAttributes != 1)
				{
					multiplicity = "0.." + countAttributes;
				}
				multiplicitee.put(lstClass.get(cpt2), multiplicity);
			}
		}
	}
	private void lienClasseParMere(List<CreeClass> lstClass)
	{
		// Parcourir toutes les classes pour trouver des liens d'héritage
		for (int cpt2 = 0; cpt2 < lstClass.size(); cpt2++)
		{
			List<Attribut> lstAtt = lstClass.get(cpt2).getLstAttribut();

			if (lstAtt == null || this.creeClass.getMere() == null)
				continue;

			// Vérifier si la classe actuelle hérite de la classe parcourue
			if (this.creeClass.getMere().equals( lstClass.get(cpt2).getNom()))
			{
				// ajouter la classe qui a un attribut de type creeClass
				if (!lienHeritage.contains(lstClass.get(cpt2)))
				{
					lienHeritage.add(lstClass.get(cpt2));
				}
			}
		}
	}
	
	private void lienClasseParInterface(List<CreeClass> lstClass)
	{
		// Parcourir toutes les classes pour trouver des liens d'interface
		for (int cpt2 = 0; cpt2 < lstClass.size(); cpt2++)
		{
			List<Attribut> lstAtt = lstClass.get(cpt2).getLstAttribut();

			if (lstAtt == null || this.creeClass.getInterfaces() == null)
				continue;

			// Vérifier si la classe actuelle implémente l'interface parcourue
			for (String inter : this.creeClass.getInterfaces())
			{
				if (inter.equals( lstClass.get(cpt2).getNom()))
				{
					// ajouter la classe qui a un attribut de type creeClass
					if (!lienInterface.contains(lstClass.get(cpt2)))
					{
						lienInterface.add(lstClass.get(cpt2));
					}
				}
			}
		}
	}

	/*╔════════════════════════╗*/
	/*║      Accesseur         ║*/
	/*╚════════════════════════╝*/
	public List<CreeClass> getLienAttribut()
	{
		return lienAttribut;
	}

	public List<CreeClass> getLienHeritage()
	{
		return lienHeritage;
	}

	public List<CreeClass> getLienInterface()
	{
		return lienInterface;
	}
}
