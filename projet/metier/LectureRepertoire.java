package projet.metier;

import java.io.File;
import java.util.ArrayList;
import java.util.List;



public class LectureRepertoire
{
	private List<CreeClass> lstClass;

	public LectureRepertoire(File rep)
	{
		File[] fichiers = rep.listFiles();
		System.out.println("");

		this.lstClass = new ArrayList<CreeClass>();

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
		this.creeLien();
	}
	
	public void creeLien()
	{
		for(int cpt = 0; cpt < this.lstClass.size(); cpt++ )
		{
			List<String> interfaces = this.lstClass.get(cpt).getInterfaces();
			if(interfaces != null)
			{
				for(int cpt2 = 0; cpt2 < this.lstClass.size(); cpt2++ )
				{
					if(this.lstClass.get(cpt) != this.lstClass.get(cpt2))
					{
						if(interfaces.contains(this.lstClass.get(cpt2).getNom()))
						{
							this.lstClass.get(cpt2).creelien(this.lstClass);
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

}
