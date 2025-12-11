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
		this.creerMultiplicite();
	}
	
	public void creeLien()
	{
		for(int cpt = 0; cpt < this.lstClass.size(); cpt++ )
		{
			this.lstClass.get(cpt).creelien(this.lstClass);
		}
	}
	public void creerMultiplicite()
	{
		for(int cpt = 0; cpt < this.lstClass.size(); cpt++ )
		{
			System.out.println( this.lstClass.get(cpt).getNom() ); //dabug
			this.lstClass.get(cpt).creerMultiplicite(this.lstClass);
		}
	}
	

	public List<CreeClass> getLstClass()
	{
		return this.lstClass;
	}

}
