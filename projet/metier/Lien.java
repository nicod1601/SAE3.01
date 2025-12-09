package projet.metier;

import java.util.List;

public class Lien
{
	private List<CreeClass> lienAttribut;
	private List<CreeClass> lienHeritage;
	private List<CreeClass> lienInterface;

	public Lien(List<CreeClass> lstClass)
	{
		this.lienClasseParAttribut(lstClass);
		this.lienClasseParMere(lstClass);
		this.lienClasseParInterface(lstClass);
	}

	public void lienClasseParAttribut(List<CreeClass> lstClass)
	{
		for(int cpt = 0; cpt < lstClass.size(); cpt++ )
		{
			for(int cpt2 = 0; cpt2 < lstClass.size(); cpt2++ )
			{
				if(lstClass.get(cpt) != lstClass.get(cpt2))
				{
					List<Attribut> lstAtt = lstClass.get(cpt2).getLstAttribut();

					for(Attribut att : lstAtt)
					{
						if(lstClass.get(cpt).getNom().equals(att.getType()))
						{
							if(! lienAttribut.contains(lstClass.get(cpt)))
							{
								lienAttribut.add(lstClass.get(cpt));
							}
							
							lstClass.get(cpt2).supprimerAttribut(att);
						}
					}
				}
			}
		}
	}

	public void lienClasseParMere(List<CreeClass> lstClass)
	{
		for(int cpt = 0; cpt < lstClass.size(); cpt++ )
		{
			String mere = lstClass.get(cpt).getMere();
			if(mere != null)
			{
				for(int cpt2 = 0; cpt2 < lstClass.size(); cpt2++ )
				{
					if(lstClass.get(cpt) != lstClass.get(cpt2))
					{
						if(lstClass.get(cpt2).getNom().equals(mere))
						{
							if(! lienHeritage.contains(lstClass.get(cpt2)))
							{
								lienHeritage.add(lstClass.get(cpt2));
							}
							
							if(! lienHeritage.contains(lstClass.get(cpt)))
							{
								lienHeritage.add(lstClass.get(cpt));
							}
						}
					}
				}
			}
		}
	}
	
	public void lienClasseParInterface(List<CreeClass> lstClass)
	{
		for(int cpt = 0; cpt < lstClass.size(); cpt++ )
		{
			List<String> interfaces = lstClass.get(cpt).getInterfaces();
			if(interfaces != null)
			{
				for(int cpt2 = 0; cpt2 < lstClass.size(); cpt2++ )
				{
					if(lstClass.get(cpt) != lstClass.get(cpt2))
					{
						if(interfaces.contains(lstClass.get(cpt2).getNom()))
						{
							if(! lienInterface.contains(lstClass.get(cpt2)))
							{
								lienInterface.add(lstClass.get(cpt2));
							}
							if(! lienInterface.contains(lstClass.get(cpt)))
							{
								lienInterface.add(lstClass.get(cpt));
							}
						}
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
