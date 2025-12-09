package projet.metier;

import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.HashMap;

public class Lien
{
	private List<CreeClass> lienAttribut;
	private List<CreeClass> lienHeritage;
	private List<CreeClass> lienInterface;

	private CreeClass creeClass;
	private Map<CreeClass, String> multiplicitee;

	public Lien(List<CreeClass> lstClass, CreeClass creeClass)
	{
		this.creeClass = creeClass;
		this.multiplicitee = new HashMap<CreeClass, String>();
		this.lienAttribut = new ArrayList<CreeClass>();
		this.lienHeritage = new ArrayList<CreeClass>();
		this.lienInterface = new ArrayList<CreeClass>();
		this.lienClasseParAttribut(lstClass);
		this.lienClasseParMere(lstClass);
		this.lienClasseParInterface(lstClass);
	}

	private void lienClasseParAttribut(List<CreeClass> lstClass)
	{
		for (int cpt2 = 0; cpt2 < lstClass.size(); cpt2++)
		{
			List<Attribut> lstAtt = lstClass.get(cpt2).getLstAttribut();
			if (lstAtt == null) continue;
			
			int countAttributes = 0;
			String multiplicity = "";

			// iterate over a copy because we may remove elements
			for (Attribut att : new ArrayList<Attribut>(lstAtt))
			{
				if (this.creeClass.getNom().equals(att.getType()))
				{
					countAttributes++;
					// add the class that has an attribute of type creeClass
					if (!lienAttribut.contains(lstClass.get(cpt2)))
					{
						this.lienAttribut.add(lstClass.get(cpt2));

						// Determine multiplicity: 0..1 for single, 0..* for List/Collection
						multiplicity = att.getType().toLowerCase().contains("list")              ||
						                      att.getType().toLowerCase().contains("collection") ||
						                      att.getType().toLowerCase().contains("set")        ||
											  att.getType().              contains("[]")         ||
						                      att.getType().toLowerCase().contains("array") ? "0..*" : "0..1";
						multiplicitee.put(lstClass.get(cpt2), multiplicity);
					}
					

					lstClass.get(cpt2).supprimerAttribut(att);
					}
				}
				// If attributes were found, determine multiplicity based on count
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
		for (int cpt2 = 0; cpt2 < lstClass.size(); cpt2++)
		{
			List<Attribut> lstAtt = lstClass.get(cpt2).getLstAttribut();

			if (lstAtt == null || this.creeClass.getMere() == null)
				continue;

			if (this.creeClass.getMere().equals( lstClass.get(cpt2).getNom()))
			{
				// add the class that has an attribute of type creeClass
				if (!lienHeritage.contains(lstClass.get(cpt2)))
				{
					lienHeritage.add(lstClass.get(cpt2));
				}
			}
		}
	}

	private void lienClasseParInterface(List<CreeClass> lstClass)
	{
		for (int cpt2 = 0; cpt2 < lstClass.size(); cpt2++)
		{
			List<Attribut> lstAtt = lstClass.get(cpt2).getLstAttribut();

			if (lstAtt == null || this.creeClass.getInterfaces() == null)
				continue;

			for (String inter : this.creeClass.getInterfaces())
			{
				if (inter.equals( lstClass.get(cpt2).getNom()))
				{
					// add the class that has an attribute of type creeClass
					if (!lienInterface.contains(lstClass.get(cpt2)))
					{
						lienInterface.add(lstClass.get(cpt2));
					}
			}
			}
		}
	}

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