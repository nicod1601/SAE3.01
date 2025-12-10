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
	private Map<CreeClass, List<String>>    mapMultiplicites;

	/*╔════════════════════════╗*/
	/*║     Constructeur       ║*/
	/*╚════════════════════════╝*/
	public Lien(CreeClass creeClass)
	{
		this.creeClass     = creeClass;
		this.mapMultiplicites = new HashMap<CreeClass, List<String> >();
		this.lstLienAttribut  = new ArrayList<CreeClass>();
		this.lstLienHeritage  = new ArrayList<CreeClass>();
		this.lstLienInterface = new ArrayList<CreeClass>();

	}

	/*╔════════════════════════╗*/
	/*║       Methode          ║*/
	/*╚════════════════════════╝*/

	public void initialiser(List<CreeClass> lstClass)
	{
		this.creerMutiplisite(lstClass);
		this.lienClasseParMere(lstClass);
		this.lienClasseParInterface(lstClass);
	}


	private void lienClasseParAttribut(List<CreeClass> lstClass)
	{
		for (int cpt2 = 0; cpt2 < lstClass.size(); cpt2++)
		{
			List<Attribut> lstAtt = lstClass.get(cpt2).getLstAttribut();
			if (lstAtt == null) continue;

			for (Attribut att : new ArrayList<Attribut>(lstAtt))
			{
				if (this.creeClass.getNom().equals(att.getType()))
				{
					// add the class that has an attribute of type creeClass
					if (!this.lstLienAttribut.contains(lstClass.get(cpt2)))
					{
						this.lstLienAttribut.add(lstClass.get(cpt2));
					}
					lstClass.get(cpt2).supprimerAttribut(att);
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
	public Map<CreeClass, List<String>> getMapMultiplicites()
	{
		return this.mapMultiplicites;
	}

	/*╔════════════════════════╗*/
	/*║      Multiplicité      ║*/
	/*╚════════════════════════╝*/
	public void creerMutiplisite(List<CreeClass> lstClass)
	{
		List<String> lst = new ArrayList<String>();
		
		for (CreeClass autreClass : lstClass)
		{
			List<Attribut> lstAtt = autreClass.getLstAttribut();
			if (lstAtt == null) continue;

			int nbLiens = 0;

			for (Attribut att : lstAtt)
			{
				// Si l'attribut pointe vers la classe courante
				if (att.getType().contains(this.creeClass.getNom()))
				{
					nbLiens++;
					lst.add( determinerMultiplicite(att));
				}
			}

			// vérifi si d'autre 
			if (nbLiens == 0 && autreClass.getLien() != null && autreClass.getLien().getLstLienAttribut().contains(creeClass))
			{
				lst.add("(1..1)");
			}
			
			this.mapMultiplicites.put(autreClass, lst);
		}
	}

	private String determinerMultiplicite(Attribut att)
	{
		String type = att.getType().toLowerCase();

		if (type.contains("list")       ||
			type.contains("set")        ||
			type.contains("collection") ||
			type.contains("[]"))
		{
			return "0..*";
		}

		return "0..1";
	}
}
