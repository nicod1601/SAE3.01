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
	private List<CreeClass>        lienAttribut;
	private List<CreeClass>        lienHeritage;
	private List<CreeClass>        lienInterface;
	private CreeClass              creeClass;
	private Map<CreeClass, String> multiplicitee;


	/*╔════════════════════════╗*/
	/*║     Constructeur       ║*/
	/*╚════════════════════════╝*/
	public Lien(List<CreeClass> lstClass, CreeClass creeClass)
	{
		this.creeClass     = creeClass;
		this.multiplicitee = new HashMap<CreeClass, String>();
		this.lienAttribut  = new ArrayList<CreeClass>();
		this.lienHeritage  = new ArrayList<CreeClass>();
		this.lienInterface = new ArrayList<CreeClass>();
		
		this.lienClasseParAttribut(lstClass);
		this.lienClasseParMere(lstClass);
		this.lienClasseParInterface(lstClass);
	}

	/*╔════════════════════════╗*/
	/*║       Methode          ║*/
	/*╚════════════════════════╝*/
	private void lienClasseParAttribut(List<CreeClass> lstClass)
	{
		for (CreeClass otherClass : lstClass)
		{
			List<Attribut> lstAtt = otherClass.getLstAttribut();
			if (lstAtt == null) continue;

			int nbLiens = 0;
			boolean multiple = false;

			for (Attribut att : lstAtt)
			{
				// Si l'attribut pointe vers la classe courante
				if (att.getType().contains(this.creeClass.getNom()))
				{
					nbLiens++;

					if (determinerMultiplicite(att).equals("0..*"))
					{
						multiple = true;
					}
				}
			}

			if (nbLiens > 0)
			{
				lienAttribut.add(otherClass);

				String multiplicite;
				if (multiple)
				{
					multiplicite = "0..*";
				}
				else if (nbLiens == 1)
				{
					multiplicite = "0..1";
				}
				else
				{
					multiplicite = "0.." + nbLiens;
				}

				multiplicitee.put(otherClass, multiplicite);
			}
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
				if (!this.lienHeritage.contains(lstClass.get(cpt2)))
				{
					this.lienHeritage.add(lstClass.get(cpt2));
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
					if (!this.lienInterface.contains(lstClass.get(cpt2)))
					{
						this.lienInterface.add(lstClass.get(cpt2));
					}
				}
			}
		}
	}

	/*╔════════════════════════╗*/
	/*║      Accesseur         ║*/
	/*╚════════════════════════╝*/
	public List<CreeClass> getLienAttribut()
	{
		return this.lienAttribut;
	}

	public List<CreeClass> getLienHeritage()
	{
		return this.lienHeritage;
	}

	public List<CreeClass> getLienInterface()
	{
		return this.lienInterface;
	}
	public Map<CreeClass,String> multiplicitee()
	{
		return this.multiplicitee;
	}
}
