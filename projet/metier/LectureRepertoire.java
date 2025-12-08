package projet.metier;

import java.io.File;
import java.util.List;


public class LectureRepertoire
{
	private List<CreeClass> lstClass;

	public LectureRepertoire(File rep)
	{
		File[] fichiers = rep.listFiles();

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

}
