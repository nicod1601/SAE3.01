package projet.ihm;

import projet.Controleur;
import projet.metier.Attribut;
import projet.metier.Methode;

import java.util.List;

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
				//this.affichageNiv3();
				//break;
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
		int centre = (49 + ctrl.getNom().length()) / 2;
		res += String.format("%" + centre + "s\n", ctrl.getNom());
		res += "------------------------------------------------\n";

		for (Attribut attribut : attributs)
		{
			String type = "";

			switch (attribut.getVisibilite().trim()) 
			{
				case "private": 
					type ="-";
					break;

				case "public":
					type = "+";
					break;
	
				default:
					type = "?";
					break;
			}

			res += String.format("%s %s : %s\n",type, attribut.getNom(), attribut.getType());
		}

		for (Methode methode : methodes)
		{
			
		}


		System.out.println(res);
		/*
		------------------------------------------------
		                        Point
		------------------------------------------------
		- x : int
		- y : int
		------------------------------------------------
		+ Point ( nom : String, x : int , y : int )
		+ getX () : int
		+ getY () : int
		+ setX ( x : int )
		+ setY ( y : int )
		------------------------------------------------ 
		*/
	}
}