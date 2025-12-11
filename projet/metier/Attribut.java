/**
 * Cette classe représente une Attribut.
 * Elle contient les informations de base et quelques utilitaires.
 */
package projet.metier;

public class Attribut
{
	private String visibilite;
	private String type;
	private String nom;

	private boolean estStatic;
	private boolean estFinal;

	/**
     * Crée d'une nouvelle methode.
     *
     * @param visibilite     la visibilite
	 * @param type           son type
	 * @param nom           son nom
	 * @param estStatic     savoir si elle est static
	 * @param estFinal   savoir si c'est une constante 
     */
	public Attribut(String visibilite, String type, String nom, boolean estStatic, boolean estFinal)
	{
		this.visibilite = visibilite;
		this.type = type;
		this.nom = nom;
		this.estStatic = estStatic;
		this.estFinal = estFinal;
	}

	/**
	 * Retourne la visibilite.
	 *
	 * @return la visibilite
	 */
	public String getVisibilite()
	{
		return visibilite;
	}

	/**
	 * Retourne la type.
	 *
	 * @return la type
	 */
	public String getType()
	{
		return type;
	}

	/**
	 * Retourne le nom.
	 *
	 * @return le nom
	 */
	public String getNom()
	{
		return nom;
	}

	/**
	 * Retourne vrai si Static.
	 *
	 * @return un boolean
	 */
	public boolean isEstStatic()
	{
		return estStatic;
	}

	/**
	 * Retourne vrai si final.
	 *
	 * @return un boolean
	 */
	public boolean isEstFinal()
	{
		return estFinal;
	}

}
