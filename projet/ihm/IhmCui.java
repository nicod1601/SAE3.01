package projet.ihm;

import java.util.List;
import java.util.Map;

import projet.Controleur;
import projet.metier.*;

public class IhmCui
{
	/*╔════════════════════════╗*/
	/*║       Attributs        ║*/
	/*╚════════════════════════╝*/
	private Controleur ctrl;
	
	/*╔════════════════════════╗*/
	/*║       Controleur       ║*/
	/*╚════════════════════════╝*/
	public IhmCui(Controleur ctrl, int niv) 
	{
		this.ctrl = ctrl;

		switch(niv)
		{
			case 1 : 
				this.affichageNiv1();
				break;
			case 2 :
				this.affichageNiv2();
				break;
			case 3 :
				this.affichageNiv3();
				break;
			case 4 :
				//this.affichageNiv4();
				//break;
		}
	}
	/*╔════════════════════════╗*/
	/*║         Methodes       ║*/
	/*╚════════════════════════╝*/
	public String replaceVisibilite(String visi)
	{
		switch (visi.trim()) 
		{
			case "private": 
				return "-";
			case "public":
				return "+";
			case "protected":
				return "#";
			default:
				return "~";
		}
	}

	/*╔════════════════════════╗*/
	/*║        Affichages      ║*/
	/*╚════════════════════════╝*/
	public void affichageNiv1() 
	{
		List<Methode>  methodes  = ctrl.getMethode (0);
		List<Attribut> attributs = ctrl.getAttribut(0);

		for (int cpt= 0; cpt < attributs.size(); cpt++)
		{
			String portee = attributs.get(cpt).isEstStatic() ? "Class" : "Instance";

			System.out.println(String.format("attribut : %-2d  nom : %-10s  type : %-7s  visibilite : %-10s  portée : %s",
								cpt,
								attributs.get(cpt).getNom(),
								attributs.get(cpt).getType(),
								attributs.get(cpt).getVisibilite().trim(), 
								portee));
		}

		System.out.println("");

		for (Methode methode : methodes)
		{
			String nom = methode.getNom().equals(this.ctrl.getNoms().get(0)) ? "Constructeur" : methode.getNom();
			String typeRetour = methode.getType() != null && !methode.getType().isEmpty() ? methode.getType() : "aucun";

			System.out.println(String.format("méthode : %-10s visibilité : %-8s type de retour : %s",
											nom,
											methode.getVisibilite().trim(),
											typeRetour));

			System.out.print("paramètres : ");

			if(methode.getLstParametres().isEmpty())
			{
				System.out.println("aucun");
			}
			else
			{
				for (int cpt = 0; cpt < methode.getLstParametres().size(); cpt++)
				{
					String nomParam  = methode.getLstParametres().get(cpt)[1];
					String typeParam = methode.getLstParametres().get(cpt)[0];
					
					if(cpt == 0)
						System.out.println(String.format("p%d : %-10s type : %s", cpt + 1, nomParam, typeParam));
					else
						System.out.println(String.format("             p%d : %-10s type : %s", cpt + 1, nomParam, typeParam));
				}
			}
			System.out.println();
		}
	}

	public String structAffichageNiv2Niv3(List<Attribut> attributs, List<Methode> methodes, int id)
	{

		String res = "";
		String nomClass = ctrl.getNoms().get(id); 

		res += "------------------------------------------------\n";
		int centre = (48 + nomClass.length()) / 2;
		res += String.format("%" + centre + "s\n", nomClass);
		res += "------------------------------------------------\n";

		// Affichage des attributs
		for (Attribut attribut : attributs)
		{
			String symbole = this.replaceVisibilite(attribut.getVisibilite());

			res += String.format("%s %s : %s\n", symbole, attribut.getNom(), attribut.getType());
		}

		// Séparateur entre attributs et méthodes
		res += "------------------------------------------------\n";

		// Affichage des méthodes
		for (Methode methode : methodes)
		{
			String signature = "";

			String symbole   = this.replaceVisibilite(methode.getVisibilite());

			// Construction de la signature de la méthode
			signature += symbole + " " + methode.getNom() + "(";

			// Ajout des paramètres
			List<String[]> params = methode.getLstParametres();
			for (int i = 0; i < params.size(); i++)
			{
				String nomParam = params.get(i)[1];
				String typeParam = params.get(i)[0];
				
				signature += nomParam + " : " + typeParam;
				
				if (i < params.size() - 1)
					signature += ", ";
			}

			signature += ")";

			// Ajout du type de retour
			if (methode.getType() != null && !methode.getType().isEmpty())
			{

				int longeur = signature.length() < 30 ? 30 - signature.length() :  1;
				signature += String.format("%"+ longeur +"s : %-5s", "", methode.getType());
			}

			res += signature + "\n";
		}

		res += "------------------------------------------------\n";

		return res;
	}

	public void affichageNiv2()
	{
		List<Methode>  methodes  = ctrl.getMethode (0);
		List<Attribut> attributs = ctrl.getAttribut(0);

		String res = structAffichageNiv2Niv3(attributs, methodes, 0);

		System.out.println(res);
	}

	public void affichageNiv3()
	{
		// reprendre la liste des classes qui se trouvent dans le répertoire
		List<CreeClass> classes = this.ctrl.getLstClass();

		String strLiens         = "";

		int id                  = 0;
		int    cpt              = 1;

		/* création des classes */
		for(CreeClass c : classes)
		{
			List<Methode>  methodes  = c.getLstMethode ();
			List<Attribut> attributs = c.getLstAttribut();

			String res        = structAffichageNiv2Niv3(attributs, methodes, id++);
			
			Lien   lien       = c.getLien();
			Map<CreeClass,String> mapMultipl = lien.multiplicitee();

			
			System.out.println(res);
			// Affichage de la HashMap de CreeClass,String
			for (Map.Entry<CreeClass, String> entry : mapMultipl.entrySet())
			{
				System.out.println("Classe: " + entry.getKey().getNom() + " -> Multiplicité: " + entry.getValue());
			}

			//Exemple : Association 1 : unidirectionnelle de Disque(0..*) vers Point(1..1) 
			if (lien != null && lien.getLienAttribut() != null)
			{
				for (CreeClass c2 : lien.getLienAttribut())
				{
					String multiplC  = mapMultipl.get(c)  == null ? "1..1" : mapMultipl.get(c);
					String multiplC2 = mapMultipl.get(c2) == null ? "1..1" : mapMultipl.get(c2);

					strLiens += String.format("Association %d : %s de %s(%s) vers %s(%s)\n", 
											  cpt++,
											  "\"Unidirectionnelle\"", 
											  c2.getNom().trim(), multiplC2,
											  c .getNom().trim(), multiplC);
				}
			}    
		}
		System.out.println(strLiens);
	}
}