package src.metier;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Classe responsable de la lecture et du traitement d'un répertoire contenant des fichiers Java.
 * <p>
 * Cette classe permet de :
 * - Lire tous les fichiers d'un répertoire
 * - Filtrer les fichiers .java
 * - Créer des objets {@link CreeClass} pour chaque fichier Java trouvé
 * - Établir les liens entre les classes (héritage, implémentation, attributs)
 * - Calculer les multiplicités entre les classes
 * </p>
 * <br>
 * @author Équipe 3 SAE 3.01
 * @version 1.0
 */

public class LectureRepertoire
{
	/** Liste de toutes les classes Java trouvées et traitées depuis le répertoire */
	private List<CreeClass> lstClass;

	/**
	 * Crée un nouvel objet LectureRepertoire en traitant tous les fichiers Java d'un répertoire.
	 * <p>
	 * Parcourt le répertoire spécifié, identifie tous les fichiers .java,
	 * les traite via {@link LectureFichier}, puis établit les liens et multiplicités
	 * entre les classes trouvées.
	 * </p>
	 *
	 * @param rep le répertoire (File) contenant les fichiers Java à traiter
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
					if (lecture != null && lecture.getClasse() != null)
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
	 * Crée tous les liens entre les classes (héritage, interfaces, attributs).
	 * <p>
	 * Initialise les relations d'héritage et d'implémentation d'interfaces
	 * ainsi que les liens créés par les attributs de type classe.
	 * </p>
	 */
	public void creeLien()
	{
		for(int cpt = 0; cpt < this.lstClass.size(); cpt++ )
		{
			this.lstClass.get(cpt).creelien(this.lstClass);
		}
	}

	/**
	 * Crée toutes les multiplicités entre les classes.
	 * <p>
	 * Calcule et établit les relations de multiplicité (1..1, 0..1, *, 1..*, etc.)
	 * entre les classes selon leurs attributs et associations.
	 * </p>
	 */
	public void creerMultiplicite()
	{
		for(int cpt = 0; cpt < this.lstClass.size(); cpt++ )
		{
			this.lstClass.get(cpt).creerMultiplicite(this.lstClass);
		}
	}
	
	/**
	 * Retourne la liste de toutes les classes Java trouvées et traitées.
	 *
	 * @return la liste des objets {@link CreeClass} créés à partir du répertoire
	 */
	public List<CreeClass> getLstClass()
	{
		return this.lstClass;
	}

}
