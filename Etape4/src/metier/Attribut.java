/**
 * Cette classe représente un Attribut d'une classe Java.
 * Elle contient les informations de base sur un attribut (visibilité, type, nom, etc.)
 * et fournit des utilitaires pour accéder à ces informations.
 * 
 * Cette classe est utilisée dans le contexte de la rétro-conception UML pour analyser
 * les attributs des classes Java et générer des diagrammes de classes.
 * 
 * @author Équipe SAE 3.01
 * @version 1.0
 */
package src.metier;

public class Attribut
{
	/** La visibilité de l'attribut (public, private, protected, etc.) */
	private String visibilite;
	
	/** Le type de l'attribut (ex: int, String, etc.) */
	private String type;
	
	/** Le nom de l'attribut */
	private String nom;

	/** Indique si l'attribut est statique */
	private boolean estStatic;
	
	/** Indique si l'attribut est final (constante) */
	private boolean estFinal;

	/**
     * Crée un nouvel attribut.
     *
     * @param visibilite La visibilité de l'attribut (public, private, protected, etc.)
	 * @param type Le type de l'attribut (ex: int, String, etc.)
	 * @param nom Le nom de l'attribut
	 * @param estStatic Indique si l'attribut est statique
	 * @param estFinal Indique si l'attribut est final (constante)
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
	 * Retourne la visibilité de l'attribut.
	 *
	 * @return La visibilité (public, private, protected, etc.)
	 */
	public String getVisibilite()
	{
		return visibilite;
	}

	/**
	 * Retourne le type de l'attribut.
	 *
	 * @return Le type de l'attribut (ex: int, String, etc.)
	 */
	public String getType()
	{
		return type;
	}

	/**
	 * Retourne le nom de l'attribut.
	 *
	 * @return Le nom de l'attribut
	 */
	public String getNom()
	{
		return nom;
	}

	/**
	 * Indique si l'attribut est statique.
	 *
	 * @return true si l'attribut est statique, false sinon
	 */
	public boolean isEstStatic()
	{
		return estStatic;
	}

	/**
	 * Indique si l'attribut est final (constante).
	 *
	 * @return true si l'attribut est final, false sinon
	 */
	public boolean isEstFinal()
	{
		return estFinal;
	}

}
