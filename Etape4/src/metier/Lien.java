package src.metier;

import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Classe Lien
 * 
 * Cette classe est responsable de l'établissement et de la gestion des liens entre les classes
 * dans le contexte de la rétro-conception UML. Elle identifie les relations d'attributs,
 * d'héritage et d'implémentation d'interfaces entre les classes Java analysées.
 * 
 * @author Équipe 3 SAE 3.01
 * @version 1.0
 */
public class Lien
{
	/*╔════════════════════════╗*/
	/*║       Attribut         ║*/
	/*╚════════════════════════╝*/
	
	/** Liste des classes liées par attributs (classes référencées via les attributs de la classe courante) */
	private List<CreeClass>        lstLienAttribut;
	
	/** Liste des classes liées par héritage (classes mères) */
	private List<CreeClass>        lstLienHeritage;
	
	/** Liste des classes liées par interfaces (interfaces implémentées) */
	private List<CreeClass>        lstLienInterface;
	
	/** Référence à l'objet CreeClass représentant la classe courante */
	private CreeClass              creeClass;

	/*╔════════════════════════╗*/
	/*║     Constructeur       ║*/
	/*╚════════════════════════╝*/
	
	/**
	 * Constructeur de la classe Lien.
	 * 
	 * Initialise la classe avec l'objet CreeClass fourni et crée les listes vides
	 * pour stocker les différents types de liens.
	 * 
	 * @param creeClass L'objet CreeClass représentant la classe pour laquelle établir les liens
	 */
	public Lien(CreeClass creeClass)
	{
		this.creeClass   = creeClass;
		this.lstLienAttribut  = new ArrayList<CreeClass>();
		this.lstLienHeritage  = new ArrayList<CreeClass>();
		this.lstLienInterface = new ArrayList<CreeClass>();

	}

	/*╔════════════════════════╗*/
	/*║       Methode          ║*/
	/*╚════════════════════════╝*/

	/**
	 * Méthode d'initialisation des liens.
	 * 
	 * Cette méthode publique appelle les trois méthodes privées pour établir
	 * tous les types de liens (attributs, héritage, interfaces) pour la classe courante.
	 * 
	 * @param lstClass La liste de toutes les classes analysées pour établir les liens
	 */
	public void initialiser(List<CreeClass> lstClass)
	{
		this.lienClasseParAttribut(lstClass);
		this.lienClasseParMere(lstClass);
		this.lienClasseParInterface(lstClass);
	}

	/**
	 * Méthode lienClasseParAttribut des liens.
	 * 
	 * Cette méthode privé permet de gérer les liens au niveau des attributs de la
	 * class.
	 * 
	 * @param lstClass La liste de toutes les classes analysées pour établir les liens
	 */
	private void lienClasseParAttribut(List<CreeClass> lstClass)
	{
		// Ajouter les classes que creeClass référence (liens sortants)
		List<Attribut> lstAtt = this.creeClass.getLstAttribut();
		for(Attribut att : new ArrayList<Attribut>(lstAtt))
		{
			for(CreeClass c : lstClass)
			{
				if(c.getNom().equals(att.getType()))
				{
					this.lstLienAttribut.add(c);
					creeClass.deplacerAttribut(att);
				}
			}
		}
	}

	/**
	 * Méthode lienClasseParMere des liens.
	 * 
	 * Cette méthode privé permet de gérer les liens à propo de l'héritage
	 * 
	 * @param lstClass La liste de toutes les classes analysées pour établir les liens
	 */
	private void lienClasseParMere(List<CreeClass> lstClass)
	{
		// Parcourir toutes les classes pour trouver des liens d'héritage
		for (int cpt2 = 0; cpt2 < lstClass.size(); cpt2++)
		{
			if (this.creeClass.getMere() == null)
				continue;

			// Vérifier si la classe actuelle hérite de la classe parcourue
			if (this.creeClass.getMere().equals( lstClass.get(cpt2).getNom()))
			{
				// ajouter la classe qui a un attribut de type creeClass
				if (!this.lstLienHeritage.contains(lstClass.get(cpt2)))
				{
					this.lstLienHeritage.add(lstClass.get(cpt2));
				}
			}
		}
	}
	
	/**
	 * Méthode lienClasseParInterface des liens.
	 * 
	 * Cette méthode privé permet de gérer les liens à propo d'un interface
	 * 
	 * @param lstClass La liste de toutes les classes analysées pour établir les liens
	 */
	private void lienClasseParInterface(List<CreeClass> lstClass)
	{
		// Parcourir toutes les classes pour trouver des liens d'interface
		for (int cpt2 = 0; cpt2 < lstClass.size(); cpt2++)
		{
			List<Attribut> lstAtt = lstClass.get(cpt2).getLstAttribut();

			if (lstAtt == null || this.creeClass.getInterfaces() == null)
				continue;

			// Vérifier si la classe actuelle implémente l'interface parcourue
			for (String inter : this.creeClass.getInterfaces())
			{
				if (inter.equals( lstClass.get(cpt2).getNom()))
				{
					// ajouter la classe qui a un attribut de type creeClass
					if (!this.lstLienInterface.contains(lstClass.get(cpt2)))
					{
						this.lstLienInterface.add(lstClass.get(cpt2));
					}
				}
			}
		}
	}

	/*╔════════════════════════╗*/
	/*║      Accesseur         ║*/
	/*╚════════════════════════╝*/
	
	/**
	 * Retourne la liste des classes liées par attributs.
	 * 
	 * @return La liste des classes référencées via les attributs de la classe courante
	 */
	public List<CreeClass> getLstLienAttribut()
	{
		return this.lstLienAttribut;
	}

	/**
	 * Retourne la liste des classes liées par héritage.
	 * 
	 * @return La liste des classes mères de la classe courante
	 */
	public List<CreeClass> getLstLienHeritage()
	{
		return this.lstLienHeritage;
	}

	/**
	 * Retourne la liste des classes liées par interfaces.
	 * 
	 * @return La liste des interfaces implémentées par la classe courante
	 */
	public List<CreeClass> getLstLienInterface()
	{
		return this.lstLienInterface;
	}
	
}
