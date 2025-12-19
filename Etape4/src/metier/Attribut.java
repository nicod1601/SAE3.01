package src.metier;

import java.io.Serializable;

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
public class Attribut implements Serializable
{
	/** Version pour Serializable */
	private static final long serialVersionUID = 1L;
	
	/** La visibilité de l'attribut (public, private, protected, etc.) */
	private String visibilite;
	
	/** Le type de l'attribut (ex: int, String, etc.) */
	private String type;
	
	/** Le nom de l'attribut */
	private String nom;

	/** Indique si l'attribut est statique */
	private boolean estStatic;
	
	/** Indique si l'attribut est final (constante) */
	private String propriete;

	/** Valeur du final*/
	private String valeurFinal = null;

	/**
	 * Constructeur sans paramètres pour Serializable
	 */
	public Attribut() { }
	/**
	 * Crée un nouvel attribut.
	 *
	 * @param visibilite La visibilité de l'attribut (public, private, protected, etc.)
	 * @param type Le type de l'attribut (ex: int, String, etc.)
	 * @param nom Le nom de l'attribut
	 * @param estStatic Indique si l'attribut est statique
	 * @param propriete Indique si l'attribut est final (constante)
	 */
	public Attribut(String visibilite, String type, String nom, boolean estStatic)
	{
		this.visibilite = visibilite;
		this.type = type;
		this.nom = nom;
		this.estStatic = estStatic;
		this.propriete = "";
	}

	public Attribut(String visibilite, String type, String nom, boolean estStatic, String valeur)
	{
		this(visibilite, type, nom, estStatic);
		this.propriete = "frozen";
		this.valeurFinal = this.checkValeur(valeur);
	}

	

	private String checkValeur(String valeur)
	{
		String typeCheck = this.type.toLowerCase();
		
		if ( typeCheck.contains("list") || typeCheck.contains("set") || typeCheck.contains("collection") || typeCheck.contains("[]"))
		{
			return "...";
		}

		return valeur;
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
	public String ispropriete()
	{
		return propriete;
	}

	/**
	 * Retourne la valeur du final.
	 *
	 * @return la valeur du final
	 */
	public String getValeur()
	{
		return this.valeurFinal;
	}

	public String getPropriete()
	{
		return this.propriete;
	}
	public void setVisibilite(String visibilite) {
		this.visibilite = visibilite;
	}
	public void setType(String type) {
		this.type = type;
	}
	public void setNom(String nom) {
		this.nom = nom;
	}
	public void setEstStatic(boolean estStatic) {
		this.estStatic = estStatic;
	}
	public void setPropriete(String propriete) {
		this.propriete = propriete;
	}
	public void setValeurFinal(String valeurFinal) {
		this.valeurFinal = valeurFinal;
	}

	

}
