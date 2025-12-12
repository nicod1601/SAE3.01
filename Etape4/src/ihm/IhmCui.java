package src.ihm;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import src.Controleur;
import src.metier.*;

/**
 * Classe IHM (Interface Homme Machine) en mode CUI (Console User Interface).
 * Elle gère l'affichage des informations selon différents niveaux de détails :
 * - Niveau 1 : Affichage simple des méthodes et attributs.
 * - Niveau 2 : Affichage avec formalisme UML.
 * - Niveau 3 : Affichage UML avec gestion des associations.
 * - Niveau 4 : Affichage complet avec héritage et interfaces.
 * 
 * @author Équipe 3 SAE 3.01
 * @version 1.0
 */
public class IhmCui
{
	/*╔════════════════════════╗*/
	/*║       Attributs        ║*/
	/*╚════════════════════════╝*/
	
	/** Le contrôleur qui fait le lien avec le métier */
	private Controleur ctrl;
	
	/*╔════════════════════════╗*/
	/*║      Constructeur      ║*/
	/*╚════════════════════════╝*/

	/**
	 * Constructeur de l'IHM CUI.
	 * Lance l'affichage correspondant au niveau demandé.
	 *
	 * @param ctrl Le contrôleur de l'application.
	 * @param niv  Le niveau d'affichage choisi (1 à 4).
	 */
	public IhmCui(Controleur ctrl, int niv) 
	{
		this.ctrl = ctrl;

		switch(niv)
		{
			case 1 -> this.affichageNiv1();
			case 2 -> this.affichageNiv2();
			case 3 -> this.affichageNiv3();
			case 4 -> this.affichageNiv4();
		}
	}

	/*╔════════════════════════╗*/
	/*║         Methodes       ║*/
	/*╚════════════════════════╝*/

	/**
	 * Remplace le mot-clé de visibilité Java par son symbole UML correspondant.
	 *
	 * @param visi La visibilité sous forme de chaîne (private, public, protected).
	 * @return Le symbole UML (+, -, #, ?).
	 */
	public String replaceVisibilite(String visi)
	{
		return switch (visi.trim())
		{
			case "private"   -> "-";
			case "public"    -> "+";
			case "protected" -> "#";
			default          -> "?";
		};
	}

	/*╔════════════════════════╗*/
	/*║    Static Selection    ║*/
	/*╚════════════════════════╝*/

	/**
	 * Affiche le menu de sélection du niveau d'affichage et récupère le choix de l'utilisateur.
	 * Gère la saisie pour s'assurer qu'elle est valide.
	 *
	 * @return L'entier correspondant au niveau choisi (0 à 4).
	 */
	public static int choixNiv()
	{
		Scanner sc           = new Scanner(System.in);
		int     niv          = -1;
		boolean saisieValide = false;

		String  blue         = "\u001B[0m" +"\u001B[36m";
		String  vert         = "\u001B[32m";
		String  jaune        = "\u001B[33m";
		String  indispo      = "\u001B[9m" +"\u001B[31m";
		String  reset        = "\u001B[0m";

		System.out.println(blue + "╔═════════════════════════════════════════════════╗\n" +
		                          "║       Choisissez un niveau d'affichage :        ║\n" +
		                          "╠═════════════════════════════════════════════════╣\n" +
		                   blue + "║" + vert    + "  1 = IHM CUI simple                             " + blue + "║\n" +
		                   blue + "║" + vert    + "  2 = IHM CUI Formalisme UML                     " + blue + "║\n" +
		                   blue + "║" + vert    + "  3 = IHM CUI Formalisme UML (Plusieurs Classes) " + blue + "║\n" +
		                   blue + "║" + vert    + "  4 = IHM CUI Héritage                           " + blue + "║\n" +
		                          "╠-------------------------------------------------╣\n" +
		                   blue + "║" + vert + "  5 = IHM GUI                                    " + blue + "║\n" +
		                          "╠-------------------------------------------------╣\n" +

		                   blue + "║" + reset   + "  0 = Quitter le programme                       " + blue + "║\n" +
		                          "╚═════════════════════════════════════════════════╝" + reset);
		
		do 
		{
			System.out.print(jaune + "Entrez un entier : " + reset);
			try 
			{
				niv = sc.nextInt();
				saisieValide = true;
			} 
			catch (Exception e) 
			{
				System.out.println("Ce n'est pas un entier valide. Réessayez.");
				sc.next();
			}
		} 
		while (!(saisieValide && niv >= 0 && niv < 6));

		return niv;
	}

	/*╔════════════════════════╗*/
	/*║        Affichages      ║*/
	/*╚════════════════════════╝*/

	/**
	 * Affiche les informations de base (Niveau 1).
	 * Liste simplement les attributs et les méthodes avec leurs détails.
	 */
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
			String nom        = methode.getNom().equals(this.ctrl.getNoms().get(0)) ? "Constructeur" : methode.getNom();
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
					{
						System.out.println(String.format("p%d : %-10s type : %s", cpt + 1, nomParam, typeParam));
					}
					else
					{
						System.out.println(String.format("             p%d : %-10s type : %s", cpt + 1, nomParam, typeParam));
					}
				}
			}
			System.out.println();
		}
	}

	/**
	 * Construit la chaîne de caractères représentant une classe en format UML (boîte).
	 * Utilisé pour les niveaux 2 et 3.
	 *
	 * @param attributs La liste des attributs de la classe.
	 * @param methodes  La liste des méthodes de la classe.
	 * @param id        L'identifiant de la classe dans le contrôleur.
	 * @return Une chaîne formatée représentant la classe en UML.
	 */
	public String structAffichageNiv2Niv3(List<Attribut> attributs, List<Methode> methodes, int id)
	{
		String res       = "";
		String nomClass  = ctrl.getNoms().get(id); 
		String typeClass = ctrl.getLstClass().get(id).getType();
		int    centre    = (48 + nomClass.length()) / 2;

		res += "-------------------------------------------------------\n";
		res += String.format("%" + centre + "s", nomClass);

		switch (typeClass)
		{
			case "abstract"  -> res += "  <<abstract>>";
			case "interface" -> res += "  <<interface>>";
			case "record"    -> res += "  <<record>>";
		}

		res += "\n";
		res += "-------------------------------------------------------\n";

		if(!attributs.isEmpty())
		{
			// Affichage des attributs
			for (Attribut attribut : attributs)
			{
				String symbole   = this.replaceVisibilite(attribut.getVisibilite());

				String  reset    = "\u001B[0m";
				String  souligne = "\u001B[4m";

				if(attribut.isEstStatic())
				{
					res += souligne;
				}

				res += String.format("%s %s : %s", symbole, attribut.getNom(), attribut.getType());

				if(attribut.isEstFinal())
				{
					res += " <<freeze>>";
				}
				res += reset + "\n";
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
				String nomParam  = params.get(i)[1];
				String typeParam = params.get(i)[0];
				
				signature += nomParam + " : " + typeParam;
				
				if (i < params.size() - 1)
				{
					signature += ", ";
				}
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
			{
				signature += "  <<abstract>>";
			}

			res += signature + "\n";
		}

		res += "-------------------------------------------------------\n";

		return res;
	}

	/**
	 * Affiche les informations avec le formalisme UML pour une seule classe (Niveau 2).
	 */
	public void affichageNiv2()
	{
		List<Methode>  methodes  = ctrl.getMethode (0);
		List<Attribut> attributs = ctrl.getAttribut(0);

		String res = structAffichageNiv2Niv3(attributs, methodes, 0);

		System.out.println(res);
	}

	/**
	 * Affiche les informations avec le formalisme UML pour plusieurs classes (Niveau 3).
	 * Gère également l'affichage des associations entre les classes.
	 */
	public void affichageNiv3()
	{
		// reprendre la liste des classes qui se trouvent dans le répertoire
		List<CreeClass>          lstClass     = this.ctrl.getLstClass();
		List<List<List<String>>> associations = new ArrayList<List<List<String>>>();

		boolean aLiens   = false;
		String  strLiens = "-------------------------------------------------------" + "\n" + 
		                   "                      Associations                     " + "\n" + 
		                   "-------------------------------------------------------" + "\n";

		int     id       = 0;
		int     cpt      = 1;

		/* création des classes */
		for(CreeClass c : lstClass)
		{
			List<Methode>  methodes  = c.getLstMethode ();
			List<Attribut> attributs = c.getLstAttribut();
			Multiplicite   multiC    = c.getMultiplicite();

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
								strLiens += String.format("Association %2d : %-17s de %-15s vers %s\n", 
								            cpt++,
								            multi.get(0).equals("1..*") && multi.get(1).equals("1..*") ? "Bidirectionnel" : "Unidirectionnelle",
								            c .getNom().trim() + "(" + multi.get(0) + ")",
								            c2.getNom().trim() + "(" + multi.get(1) + ")");

								List<List<String>> classMuLti = new ArrayList<List<String>>();

								classMuLti.add(lstClassMultiC);
								classMuLti.add(lstClassMultiClef);

								associations.add(classMuLti);

								aLiens = true;
							}
						}
					}
				}
			}
		}

		if (aLiens) 
		{
			System.out.println(strLiens);
		}
	}

	/**
	 * Vérifie si une association a déjà été enregistrée pour éviter les doublons.
	 *
	 * @param lstClassMultiC    Liste contenant le nom de la classe source et sa multiplicité.
	 * @param lstCassMultiClef  Liste contenant le nom de la classe cible et sa multiplicité.
	 * @param association       La liste de toutes les associations déjà enregistrées.
	 * @return true si l'association n'existe pas encore, false sinon.
	 */
	private boolean verfiDoublon(List<String> lstClassMultiC, List<String> lstCassMultiClef, List<List<List<String>>> association)
	{
		if (association != null && association.isEmpty()) 
		{ 
			return true; 
		}

		// l = [Disque; 1..], [[Point; 1..1]]
		//     [[     0     ], [      1     ]]
		//     [[ 0   ;  1  ], [  0   ;   1 ]]
		//
		// lstCassMultiC          = [Point ]; [1..1]
		// lstCassMultiClef       = [Disque]; [1..]

		for (List<List<String>> l : association)
		{
			//B -> A
			if ( (l.get(0).get(0).equals(lstCassMultiClef.get(0)) /*Disque*/   && 
				  l.get(0).get(1).equals(lstCassMultiClef.get(1)) /* 1..* */ ) && 
				 (l.get(1).get(0).equals(lstClassMultiC  .get(0)) /*Point */   && 
				  l.get(1).get(1).equals(lstClassMultiC  .get(1)) /*1..1  */ ) )
			{
				return false;
			}
		}
		return true; 
	}
	
	/**
	 * Affiche les informations complètes (Niveau 4).
	 * Inclut l'affichage niveau 3 ainsi que les relations d'héritage et d'implémentation d'interfaces.
	 */
	public void affichageNiv4()
	{
		boolean aHeritage  = false;
		boolean aInterface = false;

		String  heritage   = "-------------------------------------------------------" + "\n" + 
		                     "                       Héritages                       " + "\n" + 
		                     "-------------------------------------------------------" + "\n";

		String  inter      = "-------------------------------------------------------" + "\n" + 
		                     "                       Interfaces                      " + "\n" + 
		                     "-------------------------------------------------------" + "\n";

		List<CreeClass> classes = this.ctrl.getLstClass();

		this.affichageNiv3();
		
		/*Carre hérite de Rectangle 
		Disque implémente ISurface
		Rectangle implémente ISurface 
		*/

		for (CreeClass c : classes)
		{
			Lien lien = c.getLien();
			for (CreeClass c2 : lien.getLstLienHeritage())
			{
				heritage += String.format("%-10s hérite de %s\n",
				            c .getNom().trim(),
				            c2.getNom().trim());

				aHeritage = true;
			}

			for (CreeClass c2 : lien.getLstLienInterface())
			{
				inter += String.format("%-10s implémente %s\n",
				         c .getNom().trim(),
				         c2.getNom().trim());

				aInterface = true;
			}
		}
		
		if (aHeritage  == false ) heritage = "";
		if (aInterface == false ) inter    = "";
			
		System.out.println("\n" + heritage + "\n" + inter);
	}
}