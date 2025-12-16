package src.metier;

import java.io.FileInputStream;
import java.io.File;
import java.util.Scanner;

public class LectureFichier
{

	private CreeClass classe;
	
	/**
	 * Factory qui crée des objets {@link CreeClass}.
	 * <p>
	 * Ce pattern permet d'instancier différentes implémentations
	 * sans exposer leur logique de création.
	 * </p>
	 * @param Lien du fichier
	 * @return CreeClass
	 */
	public static LectureFichier factoryLectureFichier(String data)
	{
		LectureFichier.verifdata(data);
		return new LectureFichier(data);
	}

	/**
	 * Création d'un nouvel objet CreeClass.
	 *
	 * @param data le nom du fichier qu'on va traiter
	 */
	private LectureFichier(String data)
	{
		this.classe = new CreeClass();

		String nomComplet = new File(data).getName();
		if (nomComplet.endsWith(".java"))
		{
			this.classe.setNom( nomComplet.substring(0, nomComplet.length() - 5) );
		}
		
		try(Scanner sc = new Scanner(new FileInputStream(data), "UTF8");)
		{
		
			while (sc.hasNext())
			{
				String line = sc.nextLine();

				if(line.equals("") || line.contains("{") || line.contains("]"))
					continue;
				
				/*--------------------------*/
				/* Gestion des commentaires */
				/*--------------------------*/
				
				// Si '/*'' Sur plusieurs lignes '*/''
				if (line.contains("/*"))
					while (!line.contains("*/"))
						line = sc.nextLine();

				// Si "this.x = x; //public String getX()""
				if (line.contains("//"))
				{
					line = line.substring(0, line.indexOf("//"));
				}

				// Si "this.x = x;/* public String getX()*/""
				if (line.contains("/*") && line.contains("*/"))
				{
					line = line.substring(0, line.indexOf("/*")) + line.substring(line.indexOf("*/") + 2);
				}

				// Si class / interface / class abstract / record
				if (line.contains("class") || line.contains("interface") || line.contains("record") ||
				   (line.contains("abstract") && line.contains("class")) )
				{
					/*------------------------------*/
					/* Ananlyse ligne : mot par mot */
					/*------------------------------*/
					
					// Utiliser Scanner pour parser la ligne
					Scanner lineSc = new Scanner(line);
					while (lineSc.hasNext())
					{
						String mot = lineSc.next();
						if(mot.equals(" "))
							continue;
						if(mot.equals("\t"))
							continue;

						if (mot.equals("class") || mot.equals("interface") || mot.equals("record"))
							this.classe.setType( mot );
						if (mot.equals("abstract"))
							this.classe.setEstAbstract(true);

						//extends / implements / record
						switch (mot)
						{
							case "extends" -> { this.classe.setMere(lineSc.next()); }
							
							case "implements" ->
								{
									this.classe.initLstInterface();
									while (lineSc.hasNext())
									{
										String inter = lineSc.next().replace(",", "").trim();
										if (!inter.isEmpty())
										{
											this.classe.addInterface(inter);
										}
									}
								}
							case "record" -> { this.classe.creeRecord(line); }
						}
					}
					lineSc.close();
					continue;
				}

				// Méthode / Constructeur / Attribut / Abstract
				if ((line.contains("private") || line.contains("protected") || line.contains("public")
					|| line.contains("abstract") ) && !line.contains("class"))
					// sans class car class peut être abstract
				{
					//Si dans la ligne il y a le nom de la classe ainsi qu'une visibilité
					// alors c'est un constructeur
					if (line.contains(this.classe.getNom()) && line.contains("("))
					{
						this.classe.ajouterConstructeur(line);
					}
					else //sinon c'est pas un constructeur
					{
						//S'il y a une visibilité ainsi qu'une parenthèse
						//Mais pas le nom de la classe alors c'est une méthode
						if (line.contains("(") )
						{
							this.classe.ajouterMethode(line);
						}
						//Sinon s'il y a pas de parenthèse et qu'il y a visibilité et un ';' alors c'est attribut
						else if (line.contains(";"))
						{
							this.classe.ajouterAttribut(line);
						}
					}
				}
			}
			sc.close();
			
			//
			this.classe.initLienMulti();

		}
		catch (Exception e)
		{
			System.out.println("\u001B[31m Erreur : le fichier spécifié ( " + data + " ) n'existe pas.\u001B[0m");
		}
	
	}

	/**
	 * on vérifie si c'est bien un fichier java
	 *
	 * @param data le nom du fichier
	 * @return boolean
	 */
	private static void verifdata(String data) throws IllegalArgumentException
	{
		if (!data.substring(data.length() - 5).equals(".java"))
		{
			throw new IllegalArgumentException("Erreur " + data + " n'est pas un fichier .java");
		}
	}

	public CreeClass getClasse()
	{
		return this.classe;
	}
}