package projet.ihm;

import java.util.ArrayList;
import java.util.HashMap;
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
				this.affichageNiv4();
				break;
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
			case "abstract":
				return "<<abstract>>";
			default:
				return "?";
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
		String typeClass = ctrl.getLstClass().get(id).getType();
		//System.out.println(classCourante);

		res += "-------------------------------------------------------\n";
		int centre = (48 + nomClass.length()) / 2;
		res += String.format("%" + centre + "s", nomClass);
		if( typeClass.equals("abstract"))
			res += "  <<abstract>>";
		else if ( typeClass.equals("interface") )
			res += "  <<interface>>";
		else if ( typeClass.equals("record"))
			res += "  <<record>>";
		res += "\n";
		res += "-------------------------------------------------------\n";

		if(!attributs.isEmpty())
		{
			// Affichage des attributs
			for (Attribut attribut : attributs)
			{
				String symbole = this.replaceVisibilite(attribut.getVisibilite());

				res += String.format("%s %s : %s\n", symbole, attribut.getNom(), attribut.getType());
			}

			// Séparateur entre attributs et méthodes
			res += "-------------------------------------------------------\n";
		}
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

			//Ajout ou non de <<abstract>>
			if(methode.estAbstract())
				signature += "  <<abstract>>";

			res += signature + "\n";
		}

		res += "-------------------------------------------------------\n";

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
		List<CreeClass> lstClass              = this.ctrl.getLstClass            ();

		List<List<List<String>>> associations = new ArrayList<List<List<String>>>();


		String strLiens                       = "";

		int    id                             = 0;
		int    cpt                            = 1;

		/* création des classes */
		for(CreeClass c : lstClass)
		{
			List<Methode>  methodes  = c.getLstMethode ();
			List<Attribut> attributs = c.getLstAttribut();

			Multiplicite   multiC       = c.getMultiplicite();

			// Class : [0..* , 1..1, ...]
			Map<CreeClass, List<List<String>>> mapMultiplC = multiC.getMapMultiplicites();

			
			System.out.println(structAffichageNiv2Niv3(attributs, methodes, id++));
			if (mapMultiplC != null)
			{
				for (CreeClass c2 : mapMultiplC.keySet())
				{
					for (List<String> multi : mapMultiplC.get(c2))
					{
						if (!multi.isEmpty())
						{
							List<String> lstClassMultiC = new ArrayList<>();
							lstClassMultiC.add(c.getNom());
							lstClassMultiC.add(multi.get(0));

							List<String> lstClassMultiClef = new ArrayList<>();
							lstClassMultiClef.add(c2.getNom());
							lstClassMultiClef.add(multi.get(1));

							if (!multi.get(0).equals("0..*") && verfiDoublon(lstClassMultiC, lstClassMultiClef, associations))
							{
								strLiens += String.format("Association %d : %s de %s(%s) vers %s(%s)\n", 
																				cpt++,
																				multi.get(0).equals("1..*") && multi.get(1).equals("1..*") ? "Bidirectionnel" : "Unidirectionnelle", 
																				c.getNom().trim(), multi.get(0),
																				c2.getNom().trim(), multi.get(1));

								List<List<String>> classMuLti         = new ArrayList<List<String>>      ();
										
								classMuLti.add(lstClassMultiC);
								classMuLti.add(lstClassMultiClef);

								associations.add(classMuLti);
							}
						}
					}
				}
			}
		}
		System.out.println(strLiens);
	}

	private boolean verfiDoublon(List<String> lstClassMultiC, List<String> lstCassMultiClef, List<List<List<String>>> association)
    {
        if (association != null && association.isEmpty()) { return true; }


         // l = [Disque; 1..], [[Point; 1..1]]
        //     [[     0     ], [      1     ]]
        //     [[ 0   ;  1  ], [  0   ;   1 ]]
        //
        // lstCassMultiC          = [Point ]; [1..1]
        // lstCassMultiClef       = [Disque]; [1..]


        for (List<List<String>> l : association)
        {
                //B -> A
            if (    (l.get(0).get(0).equals(lstCassMultiClef.get(0)) /*Disque*/&& 
                     l.get(0).get(1).equals(lstCassMultiClef.get(1)) /* 1..* */  ) && 
                    (l.get(1).get(0).equals(lstClassMultiC  .get(0)) /*Point */&& 
                     l.get(1).get(1).equals(lstClassMultiC  .get(1)) /*1..1  */  )   )

            {
                return false;
            }
        }
        return true; 
    }
	
	public void affichageNiv4()
	{
		String          heritage = "";
		String          inter    = "";

		List<CreeClass> classes = this.ctrl.getLstClass();


		this.affichageNiv3();
		
		/*Carre hérite de Rectangle 
		Disque implémente ISurface
		Rectangle implémente ISurface 
		*/

		for (CreeClass c : classes)
		{
			Lien   lien       = c.getLien();
			for (CreeClass c2 : lien.getLstLienHeritage())
			{
				heritage += String.format("%-10s hérite de %s\n",
							c.getNom().trim(),
							c2 .getNom().trim());
			}

			for (CreeClass c2 : lien.getLstLienInterface())
			{
				inter += String.format("%s implémente %s\n",
							c.getNom().trim(),
							c2 .getNom().trim());
			}
		}

		System.out.println("\n" + heritage + "\n" + inter);
	}


}