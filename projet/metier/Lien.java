package projet.metier;

import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.HashMap;

public class Lien
{
	/*╔════════════════════════╗*/
	/*║       Attribut         ║*/
	/*╚════════════════════════╝*/
	private List<CreeClass>        lstLienAttribut;
	private List<CreeClass>        lstLienHeritage;
	private List<CreeClass>        lstLienInterface;
	private CreeClass              creeClass;

	/*╔════════════════════════╗*/
	/*║     Constructeur       ║*/
	/*╚════════════════════════╝*/
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

	public void initialiser(List<CreeClass> lstClass)
	{
		this.lienClasseParAttribut(lstClass);
		this.lienClasseParMere(lstClass);
		this.lienClasseParInterface(lstClass);
	}


	private void lienClasseParAttribut(List<CreeClass> lstClass)
	{
		// Ajouter les classes que creeClass référence (liens sortants)
		List<Attribut> lstAtt = this.creeClass.getLstAttribut();
		for(Attribut att : new ArrayList<Attribut>(lstAtt))
		{
			for(CreeClass c : lstClass)
			{
				if(c.getNom().equals(att.getType()) && !this.lstLienAttribut.contains(c))
				{
					this.lstLienAttribut.add(c);
				}
			}
		}
	}


	private void lienClasseParMere(List<CreeClass> lstClass)
	{
		// Parcourir toutes les classes pour trouver des liens d'héritage
		for (int cpt2 = 0; cpt2 < lstClass.size(); cpt2++)
		{
			List<Attribut> lstAtt = lstClass.get(cpt2).getLstAttribut();

			if (lstAtt == null || this.creeClass.getMere() == null)
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
	public List<CreeClass> getLstLienAttribut()
	{
		return this.lstLienAttribut;
	}

	public List<CreeClass> getLstLienHeritage()
	{
		return this.lstLienHeritage;
	}

	public List<CreeClass> getLstLienInterface()
	{
		return this.lstLienInterface;
	}
	
}
