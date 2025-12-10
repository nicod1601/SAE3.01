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
		this.mapMultiplicites = new HashMap<CreeClass, List<String>>();
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
		// Ajouter les classes qui ont un attribut de type creeClass (liens entrants)
		for (int cpt2 = 0; cpt2 < lstClass.size(); cpt2++)
		{
			List<Attribut> lstAtt = lstClass.get(cpt2).getLstAttribut();
			if (lstAtt == null) continue;

			for (Attribut att : new ArrayList<Attribut>(lstAtt))
			{
				if (this.creeClass.getNom().equals(att.getType()))
				{
					if (!this.lstLienAttribut.contains(lstClass.get(cpt2)))
					{
						this.lstLienAttribut.add(lstClass.get(cpt2));
					}
				}
			}
		}

		// Ajouter les classes que creeClass référence (liens sortants)
		List<Attribut> lstAttCourant = this.creeClass.getLstAttribut();
		if (lstAttCourant != null)
		{
			for (Attribut att : new ArrayList<Attribut>(lstAttCourant))
			{
				for (CreeClass classe : lstClass)
				{
					if (classe.getNom().equals(att.getType()) && !this.lstLienAttribut.contains(classe))
					{
						this.lstLienAttribut.add(classe);
					}
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
		
		List<Attribut> lstAtt = this.creeClass.getLstAttribut();

		//verifie si la class quon traite a des attributs
		for (CreeClass autreClass : lstClass)
		{
			int cptLiaison = 0;

			List<String> lstMultipl = new ArrayList<String>();

			for (Attribut att : lstAtt)
			{
				// Si l'attribut pointe vers la classe courante
				if (att.getType().contains(autreClass.getNom()))
				{
					lstMultipl.add(determinerMultiplicite(att));
					cptLiaison ++;
				}
			}

			// Si aucun lien attribut trouvé, vérifier si elle a un lien vers nous
			if (cptLiaison == 0 && autreClass.getLien() != null && autreClass.getLien().getLstLienAttribut().contains(this.creeClass))
			{
				lstMultipl.add("1..1");
			}

			

			// Ajouter à la map seulement si des multiplicités ont été trouvées
			this.mapMultiplicites.put(autreClass, lstMultipl);
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
