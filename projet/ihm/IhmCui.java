package projet.ihm;

import java.util.List;
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
	public void affichageNiv1() 
	{
		List<Methode>  methodes  = ctrl.getMethode ();
		List<Attribut> attributs = ctrl.getAttribut();

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
			String nom = methode.getNom().equals(this.ctrl.getNom()) ? "Constructeur" : methode.getNom();
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


	public void affichageNiv2()
	{
		List<Methode>  methodes  = ctrl.getMethode ();
		List<Attribut> attributs = ctrl.getAttribut();

		String res = "";

		res += "------------------------------------------------\n";
		int centre = (48 + ctrl.getNom().length()) / 2;
		res += String.format("%" + centre + "s\n", ctrl.getNom());
		res += "------------------------------------------------\n";

		// Affichage des attributs
		for (Attribut attribut : attributs)
		{
			String symbole = "";

			switch (attribut.getVisibilite().trim()) 
			{
				case "private": 
					symbole = "-";
					break;

				case "public":
					symbole = "+";
					break;

				case "protected":
					symbole = "#";
					break;
	
				default:
					symbole = "~";
					break;
			}

			res += String.format("%s %s : %s\n", symbole, attribut.getNom(), attribut.getType());
		}

		// Séparateur entre attributs et méthodes
		res += "------------------------------------------------\n";

		// Affichage des méthodes
		for (Methode methode : methodes)
		{
			String signature = "";
			String symbole;

			switch (methode.getVisibilite().trim()) 
			{
				case "private": 
					symbole = "-";
					break;

				case "public":
					symbole = "+";
					break;

				case "protected":
					symbole = "#";
					break;
	
				default:
					symbole = "~";
					break;
			}

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

		res += "------------------------------------------------";

		System.out.println(res);
	}

	public void affichageNiv3()
	{
		// reprendre la liste des classes qui se trouvent dans le répertoire
		List<CreeClass> classes = this.ctrl.getLstClass();


		/* création des classes */
		for(CreeClass c : classes)
		{
			String res = "";
			List<Methode>  methodes  = c.getLstMethode ();
			List<Attribut> attributs = c.getLstAttribut();

			res += "------------------------------------------------\n";
			int centre = (48 + c.getNom().length()) / 2;
			res += String.format("%" + centre + "s\n", c.getNom());
			res += "------------------------------------------------\n";

			// Affichage des attributs
			for (Attribut attribut : attributs)
			{
				String symbole = "";
				

				switch (attribut.getVisibilite().trim()) 
				{
					case "private": 
						symbole = "-";
						break;

					case "public":
						symbole = "+";
						break;

					case "protected":
						symbole = "#";
						break;
		
					default:
						symbole = "~";
						break;
				}

				res += String.format("%s %s : %s\n", symbole, attribut.getNom(), attribut.getType());
			}

			// Séparateur entre attributs et méthodes
			res += "------------------------------------------------\n";

			// Affichage des méthodes
			for (Methode methode : methodes)
			{
				String symbole;
				String signature = "";

				switch (methode.getVisibilite().trim()) 
				{
					case "private": 
						symbole = "-";
						break;

					case "public":
						symbole = "+";
						break;

					case "protected":
						symbole = "#";
						break;
		
					default:
						symbole = "~";
						break;
				}

				// Construction de la signature de la méthode
				signature += symbole + " " + methode.getNom() + " (";

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
			System.out.println(res);
		}
	}
}