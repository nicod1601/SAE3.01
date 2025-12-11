/**
 * Cette classe représente une Methode.
 * Elle contient les informations de base et quelques utilitaires.
 */
package projet.metier;

import java.util.List;

public class Methode
{
	private String visibilite;
	private String type;
	private String nom;

	private boolean estStatic;
	private boolean estAbstract;
	private List<String[]> lstParametres;

	/**
	 * Crée d'une nouvelle methode.
	 *
	 * @param visibilite     la visibilite
	 * @param type           son type
	 * @param nom           son nom
	 * @param estStatic     savoir si elle est static
	 * @param estAbstract   savoir si elle est abstract
	 * @param lstParametres list de parametre 
	 */
	public Methode(String visibilite, String type, String nom, boolean estStatic,boolean estAbstract, List<String[]> lstParametres)
	{
		this.visibilite   = visibilite;
		this.type         = type;
		this.nom          = nom;
		this.estStatic    = estStatic;
		this.estAbstract  = estAbstract;
		
		this.lstParametres = lstParametres;
	}

	/**
	 * Retourne la visibilite.
	 *
	 * @return la visibilite
	 */
	public String getVisibilite()
	{
		return this.visibilite;
	}

	/**
	 * Retourne le type.
	 *
	 * @return le type
	 */
	public String getType()
	{
		return this.type;
	}

	/**
	 * Retourne le nom.
	 *
	 * @return le nom
	 */
	public String getNom()
	{
		return this.nom;
	}

	/**
	 * Retourne vrai si Static.
	 *
	 * @return un boolean
	 */
	public boolean isEstStatic()
	{
		return this.estStatic;
	}
  	/**
	 * Retourne vrai si abstract.
	 *
	 * @return un boolean
	 */
	public boolean estAbstract()
	{
		return this.estAbstract;
	}

	/**
	 * Retourne la list des Parametres .
	 *
	 * @return lstParametres
	 */
	public List<String[]> getLstParametres()
	{
		return this.lstParametres;
	}

}
