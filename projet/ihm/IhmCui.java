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

			System.out.println(	"attribut : "   + cpt                               +
								" nom : "       + attributs.get(cpt).getNom()       +
								" type : "      + attributs.get(cpt).getType()      +
								" visibilite : "+ attributs.get(cpt).getVisibilite()+
								" portee : "    + portee                             );
		}

		System.out.println("");

		for (Methode methode : methodes)
		{
			String nom = methode.getNom().equals(this.ctrl.getNom()) ? "Constructeur" : methode.getNom();

			System.out.print(	"methode : "     + nom                     +
								" visibilite : " + methode.getVisibilite() + "\n" +
								"parametres : "                                  );

			if(methode.getLstParametre().isEmpty())
			{
				System.out.println("aucun \n");
			}
			else
			{
				for (int cpt= 0; cpt < methode.getLstParametre().size(); cpt++)
				{
					if(cpt == 0)
						System.out.print(methode.getLstParametre().get(cpt) + "\n");
					else
						System.out.print("             " +methode.getLstParametre().get(cpt) + "\n");
				}
				System.out.println("");
			}
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