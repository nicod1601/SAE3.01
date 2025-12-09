package projet.metier;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;



public class LectureRepertoire
{
	private List<CreeClass> lstClass;
	private Map<CreeClass, List<CreeClass>> lien;

	public LectureRepertoire(File rep)
	{
		File[] fichiers = rep.listFiles();

		this.lstClass = new ArrayList<>();
		this.lien = new HashMap<>();

		if (fichiers != null)
		{
			for (File f : fichiers)
			{
				if (f.getName().endsWith(".java"))
				{
					CreeClass c = CreeClass.factoryCreeClass(f.getPath());
					if (c != null)
					{
						this.lstClass.add(c);
					}
				}
			}
		}

	}

	
	public void lienClasse()
	{
		for(int cpt = 0; cpt < this.lstClass.size(); cpt++ )
		{
			for(int cpt2 = 0; cpt2 < this.lstClass.size(); cpt2++ )
			{
				if(this.lstClass.get(cpt) != this.lstClass.get(cpt2))
				{
					List<Attribut> lstAtt = this.lstClass.get(cpt2).getLstAttribut();

					for(Attribut att : lstAtt)
					{
						if(this.lstClass.get(cpt).getNom().equals(att.getType()))
						{
							if(! this.lien.containsKey(this.lstClass.get(cpt)))
							{
								this.lien.put(this.lstClass.get(cpt), new ArrayList<CreeClass>());
							}
							
							this.lien.get(this.lstClass.get(cpt)).add(this.lstClass.get(cpt2));

							this.lstClass.get(cpt2).supprimerAttribut(att);
						}
					}
				}
			}
		}
	}

	public List<CreeClass> getLstClass()
	{
		return this.lstClass;
	}

	public Map<CreeClass, List<CreeClass>> getLien()
	{
		return this.lien;
	}

}
