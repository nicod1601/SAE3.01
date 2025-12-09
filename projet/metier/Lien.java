package projet.metier;

import java.util.List;
import java.util.Map;
import java.util.ArrayList;

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
		this.multiplicitee = null;
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
		
			// iterate over a copy because we may remove elements
			for (Attribut att : new ArrayList<Attribut>(lstAtt))
			{
				if (this.creeClass.getNom().equals(att.getType()))
				{
					// add the class that has an attribute of type creeClass
					if (!lienAttribut.contains(lstClass.get(cpt2)))
					{
						lienAttribut.add(lstClass.get(cpt2));
					}

					lstClass.get(cpt2).supprimerAttribut(att);
					}
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