package src.metier;

import java.io.Serializable;

/**
 * Cette classe représente un Attribut d'une classe Java.
 * <p>
 * Elle contient les informations de base sur un attribut (visibilité, type, nom, etc.)
 * et fournit des utilitaires pour accéder à ces informations.
 * Cette classe est utilisée dans le contexte de la rétro-conception UML pour analyser
 * les attributs des classes Java et générer des diagrammes de classes.
 * * @author Équipe SAE 3.01
 * @version 1.0
 */
public class Attribut implements Serializable
{
	private static final long serialVersionUID = 1L;
	
	/*----------------------------------------------------------------------------------------------------------------*/
	/* ATTRIBUTS                                                                                                      */
	/*----------------------------------------------------------------------------------------------------------------*/

	/** La visibilité de l'attribut (public, private, protected, etc.). */
	private String  visibilite;
	
	/** Le type de l'attribut (ex: int, String, List, etc.). */
	private String  type;
	
	/** Le nom de l'attribut. */
	private String  nom;

	/** Indique si l'attribut est statique. */
	private boolean estStatic;
	
	/** Propriété spécifique (ex: "frozen" pour final). */
	private String  propriete;

	/** Valeur par défaut ou valeur de la constante (si final). */
	private String  valeurFinal;

	/*----------------------------------------------------------------------------------------------------------------*/
	/* CONSTRUCTEURS                                                                                                  */
	/*----------------------------------------------------------------------------------------------------------------*/

	/**
	 * Constructeur vide requis pour la sérialisation.
	 */
	public Attribut() 
	{
		this.valeurFinal = null;
	}
	
	/**
	 * Crée un nouvel attribut standard.
	 *
	 * @param visibilite La visibilité de l'attribut.
	 * @param type       Le type de l'attribut.
	 * @param nom        Le nom de l'attribut.
	 * @param estStatic  Indique si l'attribut est statique.
	 */
	public Attribut(String visibilite, String type, String nom, boolean estStatic)
	{
		this.visibilite  = visibilite;
		this.type        = type;
		this.nom         = nom;
		this.estStatic   = estStatic;
		this.propriete   = "";
		this.valeurFinal = null;
	}

	/**
	 * Crée un nouvel attribut avec une valeur (généralement pour les constantes).
	 *
	 * @param visibilite La visibilité de l'attribut.
	 * @param type       Le type de l'attribut.
	 * @param nom        Le nom de l'attribut.
	 * @param estStatic  Indique si l'attribut est statique.
	 * @param valeur     La valeur initiale (sera traitée par checkValeur).
	 */
	public Attribut(String visibilite, String type, String nom, boolean estStatic, String valeur)
	{
		this(visibilite, type, nom, estStatic);
		this.propriete   = "frozen";
		this.valeurFinal = this.checkValeur(valeur);
	}

	/*----------------------------------------------------------------------------------------------------------------*/
	/* MÉTHODES PRIVÉES                                                                                               */
	/*----------------------------------------------------------------------------------------------------------------*/

	/**
	 * Vérifie et formate la valeur de l'attribut (notamment pour les collections).
	 * @param valeur La valeur brute extraite.
	 * @return La valeur formatée ou "..." pour les collections complexes.
	 */
	private String checkValeur(String valeur)
	{
		if (this.type == null) return valeur;

		String typeCheck = this.type.toLowerCase();
		
		if (typeCheck.contains("list")       || 
			typeCheck.contains("set")        || 
			typeCheck.contains("collection") || 
			typeCheck.contains("[]"))
		{
			return "...";
		}

		return valeur;
	}

	/*----------------------------------------------------------------------------------------------------------------*/
	/* GETTERS                                                                                                        */
	/*----------------------------------------------------------------------------------------------------------------*/

	/**
	 * Retourne la visibilité de l'attribut.
	 * @return La visibilité.
	 */
	public String getVisibilite()
	{
		return visibilite;
	}

	/**
	 * Retourne le type de l'attribut.
	 * @return Le type.
	 */
	public String getType()
	{
		return type;
	}

	/**
	 * Retourne le nom de l'attribut.
	 * @return Le nom.
	 */
	public String getNom()
	{
		return nom;
	}

	/**
	 * Indique si l'attribut est statique.
	 * @return true si statique.
	 */
	public boolean isEstStatic()
	{
		return estStatic;
	}

	/**
	 * Retourne la propriété de l'attribut (ex: {frozen}).
	 * (Note: Nommé 'ispropriete' pour compatibilité ascendante).
	 * @return La propriété.
	 */
	public String ispropriete()
	{
		return this.propriete;
	}

	/**
	 * Retourne la propriété de l'attribut (Alias de ispropriete).
	 * @return La propriété.
	 */
	public String getPropriete()
	{
		return this.propriete;
	}

	/**
	 * Retourne la valeur associée à l'attribut (si final).
	 * @return La valeur.
	 */
	public String getValeur()
	{
		return this.valeurFinal;
	}

	/*----------------------------------------------------------------------------------------------------------------*/
	/* SETTERS                                                                                                        */
	/*----------------------------------------------------------------------------------------------------------------*/

	/**
	 * Définit la visibilité de l'attribut.
	 * @param visibilite La nouvelle visibilité.
	 */
	public void setVisibilite(String visibilite)
	{
		this.visibilite = visibilite;
	}

	/**
	 * Définit le type de l'attribut.
	 * @param type Le nouveau type.
	 */
	public void setType(String type)
	{
		this.type = type;
	}

	/**
	 * Définit le nom de l'attribut.
	 * @param nom Le nouveau nom.
	 */
	public void setNom(String nom)
	{
		this.nom = nom;
	}

	/**
	 * Définit si l'attribut est statique.
	 * @param estStatic true pour statique.
	 */
	public void setEstStatic(boolean estStatic)
	{
		this.estStatic = estStatic;
	}

	/**
	 * Définit la propriété de l'attribut.
	 * @param propriete La nouvelle propriété.
	 */
	public void setPropriete(String propriete)
	{
		this.propriete = propriete;
	}

	/**
	 * Définit la valeur finale de l'attribut.
	 * @param valeurFinal La nouvelle valeur.
	 */
	public void setValeurFinal(String valeurFinal)
	{
		this.valeurFinal = valeurFinal;
	}
}