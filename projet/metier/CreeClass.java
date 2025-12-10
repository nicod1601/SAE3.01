/**
 * Classe métier qui nous permet de creer une classe via la lecture de fichier(s) / un dossier.
 * Cette classe nous permet alors d'ajouter des méthodes, des attributs, ainsi que les liens qu'elle a :<br>
 * - Liens en fonction de ses attributs (par exemple si dans ses attributs elle a une autre classe (lienAttribut)).
 * - Liens en fonction de son héritage (par exemple si la dite classe hérite d'une autre classe (lienHeritage)).
 * - Liens en fonction de son interface (par exemple si la classe courante implemente une autre class (lienInterface)).
 * <br>
 * @author : MARTIN Erwan, DELPECH Nicolas, GRICOURT Paul, PREVOST Donovan, MILLEREUX-BIENVAULT William  
 * 
 */
package projet.metier;

import java.io.FileInputStream;
import java.util.*;

public class CreeClass
{
	private String nom;
	private List<Attribut> lstAttribut;
	private List<Methode> lstMethode;

	private String mere = null;
	private List<String> interfaces = null;
	private Lien lien;

	public static CreeClass factoryCreeClass(String data)
	{
		if (CreeClass.verifdata(data))
			return new CreeClass(data);
		else
			return null;
	}

	private CreeClass(String data)
	{
		String nomComplet = new java.io.File(data).getName();
		if (nomComplet.endsWith(".java"))
		{
			this.nom = nomComplet.substring(0, nomComplet.length() - 5);
		}

		this.lstAttribut = new ArrayList<Attribut>();
		this.lstMethode = new ArrayList<Methode>();
		try
		{
			Scanner sc = new Scanner(new FileInputStream(data), "UTF8");
			while (sc.hasNext())
			{
				String line = sc.nextLine();

				if (line.contains("class ") && !line.contains("("))
				{
					if(line.contains("extends") || line.contains("implements"))
					{
						// Utiliser Scanner pour parser la ligne
						Scanner lineSc = new Scanner(line);
						while (lineSc.hasNext())
						{
							String mot = lineSc.next();
							
							if (mot.equals("extends") && lineSc.hasNext())
							{
								this.mere = lineSc.next();
							}
							else if (mot.equals("implements"))
							{
								this.interfaces = new ArrayList<String>();
								while (lineSc.hasNext())
								{
									String inter = lineSc.next().replace(",", "").trim();
									if (!inter.isEmpty())
									{
										this.interfaces.add(inter);
									}
								}
							}
						}
						lineSc.close();
					}
					continue;
				}

				if (line.contains("private") || line.contains("protected")|| line.contains("public"))
				{
					if(line.contains(this.nom))
					{
						this.ajouterConstructeur(line);
					}
					else
					{
						if (line.contains(")"))
						{
							this.ajouterMethode(line);
						}
					}

					if (line.contains(";"))
					{
						this.ajouterAttribut(line);
					}
				}
			}
			sc.close();

		}catch (Exception e)
		{
			System.out.println("\u001B[31m Erreur : le fichier spécifié ( " + data + " ) n'existe pas.\u001B[0m");
		}
	}

	private void ajouterConstructeur(String constructeur)
	{
		constructeur = constructeur.trim();

		int posOuv = constructeur.indexOf("(");
		int posFerm = constructeur.indexOf(")");

		if (posOuv == -1 || posFerm == -1) 
		{
			System.out.println("Problème de parenthèse");
			return;
		}

		// Parser la partie avant les parenthèses
		String avantParenthese = constructeur.substring(0, posOuv).trim();
		Scanner avantSc = new Scanner(avantParenthese);
		
		String visibilite = avantSc.next(); // premier mot = visibilité
		String nom = "";
		
		// Lire jusqu'au dernier mot (le nom du constructeur)
		while (avantSc.hasNext())
		{
			nom = avantSc.next();
		}
		avantSc.close();

		// Parser les paramètres
		String paramBrut = constructeur.substring(posOuv + 1, posFerm).trim();
		List<String[]> lstLstParamInfo = new ArrayList<>();

		if (!paramBrut.isEmpty())
		{
			// Utiliser Scanner avec délimiteur virgule
			Scanner paramSc = new Scanner(paramBrut);
			paramSc.useDelimiter(",");
			
			while (paramSc.hasNext())
			{
				String param = paramSc.next().trim();
				
				// Parser chaque paramètre (type nom)
				Scanner motSc = new Scanner(param);
				List<String> infos = new ArrayList<>();
				
				while (motSc.hasNext())
				{
					infos.add(motSc.next());
				}
				motSc.close();
				
				// Convertir en tableau pour compatibilité
				if (infos.size() >= 2)
				{
					String[] tabInfo = new String[infos.size()];
					for (int i = 0; i < infos.size(); i++)
					{
						tabInfo[i] = infos.get(i);
					}
					lstLstParamInfo.add(tabInfo);
				}
			}
			paramSc.close();
		}

		Methode c = new Methode(visibilite, null, nom, false, lstLstParamInfo);
		this.lstMethode.add(c);
	}

	private void ajouterAttribut(String attribut)
	{
		Scanner sc = new Scanner(attribut);
		
		if (!sc.hasNext()) 
		{
			sc.close();
			return;
		}
		
		String visibilite = sc.next();
		boolean estStatic = false;
		boolean estFinal = false;
		
		if (!sc.hasNext()) 
		{
			sc.close();
			return;
		}
		
		String motSuivant = sc.next();
		
		// Vérifier les modificateurs
		if (motSuivant.equals("static"))
		{
			estStatic = true;
			if (sc.hasNext())
			{
				motSuivant = sc.next();
			}
		}
		else if (motSuivant.equals("final"))
		{
			estFinal = true;
			if (sc.hasNext())
			{
				motSuivant = sc.next();
			}
		}
		
		String type = motSuivant;
		
		// Lire jusqu'au dernier mot (le nom de l'attribut)
		String nom = "";
		while (sc.hasNext())
		{
			nom = sc.next();
		}
		
		if (!nom.isEmpty())
		{
			nom = nom.replace(";", "");
			Attribut attr = new Attribut(visibilite, type, nom, estStatic, estFinal);
			this.lstAttribut.add(attr);
		}
		
		sc.close();
	}

	private void ajouterMethode(String methode)
	{
		Scanner sc = new Scanner(methode);
		
		if (!sc.hasNext()) 
		{
			sc.close();
			return;
		}
		
		String visibilite = sc.next();
		boolean estStatic = false;
		
		if (!sc.hasNext()) 
		{
			sc.close();
			return;
		}
		
		String motSuivant = sc.next();
		
		if (motSuivant.equals("static"))
		{
			estStatic = true;
			if (sc.hasNext())
			{
				motSuivant = sc.next();
			}
		}
		
		String type = motSuivant;
		
		// Reconstruire le reste pour trouver le nom et les paramètres
		StringBuilder reste = new StringBuilder();
		while (sc.hasNext())
		{
			reste.append(sc.next()).append(" ");
		}
		sc.close();
		
		String resteStr = reste.toString().trim();
		int posOuv = resteStr.indexOf("(");
		int posFerm = resteStr.indexOf(")");
		
		if (posOuv == -1 || posFerm == -1)
		{
			return;
		}
		
		String nom = resteStr.substring(0, posOuv).trim();
		String paramBrut = resteStr.substring(posOuv + 1, posFerm).trim();
		
		List<String[]> lstLstParamInfo = new ArrayList<>();
		
		if (!paramBrut.isEmpty())
		{
			// Utiliser Scanner avec délimiteur virgule
			Scanner paramSc = new Scanner(paramBrut);
			paramSc.useDelimiter(",");
			
			while (paramSc.hasNext())
			{
				String param = paramSc.next().trim();
				
				// Parser chaque paramètre (type nom)
				Scanner motSc = new Scanner(param);
				List<String> infos = new ArrayList<>();
				
				while (motSc.hasNext())
				{
					infos.add(motSc.next());
				}
				motSc.close();
				
				// Convertir en tableau pour compatibilité
				if (infos.size() >= 2)
				{
					String[] tabInfo = new String[infos.size()];
					for (int i = 0; i < infos.size(); i++)
					{
						tabInfo[i] = infos.get(i);
					}
					lstLstParamInfo.add(tabInfo);
				}
			}
			paramSc.close();
		}
		
		Methode meth = new Methode(visibilite, type, nom, estStatic, lstLstParamInfo);
		this.lstMethode.add(meth);
	}

	public void creelien(List<CreeClass> lstClass)
	{
		this.lien = new Lien(lstClass, this);
	}
	public void creerMultiplicite(List<CreeClass> lstClass)
	{
		this.lien.creerMutiplisite(lstClass);
	}

	private static boolean verifdata(String data)
	{
		try
		{
			if (data.substring(data.length()-5).equals(".java"))
			{
				return true;
			}
		} catch (Exception e)
		{
			System.out.println("\u001B[31m Erreur : le fichier spécifié ( " + data + " ) n'existe pas ou n'est pas un fichier .Java.\u001B[0m");
		}
		return false;
	}

	public String getNom() {
		return nom;
	}

	public List<Attribut> getLstAttribut() {
		return lstAttribut;
	}

	public List<Methode> getLstMethode() {
		return lstMethode;
	}

	public void supprimerAttribut(Attribut att)
	{
		this.lstAttribut.remove(att);
	}

	public String getMere()
	{
		return this.mere;
	}

	public List<String> getInterfaces()
	{
		return this.interfaces;
	} 

	public Lien getLien()
	{
		return this.lien;
	}
}