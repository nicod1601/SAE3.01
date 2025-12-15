package src.metier;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Cette class permet de Lire tout un dossier
 * Cependant il permet de vérifier s'il y a des fichiers ".java" qui se trouve
 * pour ensuite creer nos Class
 * <br>
 * @author Équipe 3 SAE 3.01
 * @version 1.0
 */

public class LectureRepertoire
{
	/** Liste des Class */
	private List<CreeClass> lstClass;

	/**
	 * Création d'un nouvel objet LectureRepertoire.
	 *
	 * @param rep un repertoire qu'on va traiter
	 */
	public LectureRepertoire(File rep)
	{
		File[] fichiers = rep.listFiles();

		this.lstClass = new ArrayList<CreeClass>();

		//
		if (fichiers != null)
		{
			for (File f : fichiers)
			{
				if (f.getName().endsWith(".java"))
				{
					LectureFichier lecture = LectureFichier.factoryLectureFichier(f.getPath());
					if (lecture != null)
					{
						this.lstClass.add(lecture.getClasse());
					}
				}
			}
			this.creeLien();
			this.creerMultiplicite();
		}
		
	}

	/**
	 * cree tout les lien entre les CreeClass
	 *
	 * @return void
	 */
	public void creeLien()
	{
		for(int cpt = 0; cpt < this.lstClass.size(); cpt++ )
		{
			this.lstClass.get(cpt).creelien(this.lstClass);
		}
	}

	/**
	 * cree toute les Multiplicite entre les CreeClass
	 *
	 * @return void
	 */
	public void creerMultiplicite()
	{
		for(int cpt = 0; cpt < this.lstClass.size(); cpt++ )
		{
			this.lstClass.get(cpt).creerMultiplicite(this.lstClass);
		}
	}
	
	/**
	 * retourne toute les CreeClass qui on pus etre Cree
	 *
	 * @return List<CreeClass>
	 */
	public List<CreeClass> getLstClass()
	{
		return this.lstClass;
	}

}
