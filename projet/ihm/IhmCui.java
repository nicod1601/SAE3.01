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
				//break;
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

			System.out.println(String.format("méthode : %-10s visibilité : %-8s" + 
											(methode.getType() != null && !methode.getType().isEmpty() ? " type de retour : %s" : ""),
											nom,
											methode.getVisibilite().trim(),
											typeRetour));

			System.out.print("paramètres : ");

			if(methode.getLstParametre().isEmpty())
			{
				System.out.println("aucun");
			}
			else
			{
				for (int cpt = 0; cpt < methode.getLstParametre().size(); cpt++)
				{
					if(cpt == 0)
						System.out.println(String.format("p%d : %s", cpt + 1, methode.getLstParametre().get(cpt)));
					else
						System.out.println(String.format("             p%d : %s", cpt + 1, methode.getLstParametre().get(cpt)));
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
		res += String.format("%49s\n", ctrl.getNom());
		res += "------------------------------------------------\n";


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