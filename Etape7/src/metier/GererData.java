package src.metier;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

/**
 * Classe de gestion de la persistance des données.
 * <p>
 * Cette classe permet de sauvegarder et de charger l'état de l'application (les classes UML)
 * sous deux formats :
 * <ul>
 * <li>Format binaire Java (.ser) via la sérialisation.</li>
 * <li>Format texte personnalisé (.uml) lisible et éditable.</li>
 * </ul>
 */
public class GererData
{
	/*----------------------------------------------------------------------------------------------------------------*/
	/* CONSTANTES                                                                                                     */
	/*----------------------------------------------------------------------------------------------------------------*/

	private static final String FILE_PATH_SER = "../data/sauvegarde01.ser";
	private static final String FILE_PATH_UML = "../data/sauvegarde01.uml";

	/*----------------------------------------------------------------------------------------------------------------*/
	/* MÉTHODES SÉRIALISATION (.SER)                                                                                  */
	/*----------------------------------------------------------------------------------------------------------------*/

	/**
	 * Sauvegarde la liste des classes dans un fichier binaire (.ser).
	 * * @param classes La liste des objets CreeClass à sérialiser.
	 */
	public void sauvegarderSER(List<CreeClass> classes)
	{
		try (ObjectOutputStream objectOutputStream = new ObjectOutputStream(new FileOutputStream(FILE_PATH_SER)))
		{
			objectOutputStream.writeObject(classes);
			System.out.println("Sauvegarde .ser réussie");
		}
		catch (IOException e)
		{
			System.out.println("Erreur sauvegarde .ser : " + e.getMessage());
		}
	}
	
	/**
	 * Charge la liste des classes depuis le fichier binaire (.ser).
	 * * @return La liste des objets CreeClass chargée, ou null en cas d'erreur.
	 */
	@SuppressWarnings("unchecked") // Suppression du warning "unchecked cast" inévitable lors de la désérialisation de List
	public List<CreeClass> chargerSER()
	{
		try (ObjectInputStream objectInputStream = new ObjectInputStream(new FileInputStream(FILE_PATH_SER)))
		{
			// Le cast est nécessaire ici car readObject retourne un Object
			List<CreeClass> classes = (List<CreeClass>) objectInputStream.readObject();
			return classes;
		}
		catch (IOException | ClassNotFoundException e)
		{
			System.out.println("Erreur chargement .ser : " + e.getMessage());
			return null;
		}
	}
	
	/*----------------------------------------------------------------------------------------------------------------*/
	/* MÉTHODES TEXTE (.UML)                                                                                          */
	/*----------------------------------------------------------------------------------------------------------------*/

	/**
	 * Charge la liste des classes depuis le fichier texte personnalisé (.uml).
	 * * @return Une liste d'objets CreeClass reconstruits à partir du fichier texte.
	 */
	public List<CreeClass> chargerUML()
	{
		List<CreeClass> classes = new ArrayList<>();
		
		try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH_UML)))
		{
			String    ligne;
			CreeClass classeCourante = null;
			
			while ((ligne = reader.readLine()) != null)
			{
				// Gestion des lignes vides (séparateurs de classes)
				if (ligne.trim().isEmpty())
				{
					if (classeCourante != null)
					{
						classes.add(classeCourante);
						classeCourante = null;
					}
					continue;
				}
				
				Scanner sc       = new Scanner(ligne);
				String  premierMot = sc.next();
				
				/* --- Détection du mot clé --- */
				
				// 1. Définition de la CLASSE
				if (premierMot.equals("Classe"))
				{
					classeCourante = new CreeClass();
					classeCourante.setNom(sc.next());
					
					while (sc.hasNext())
					{
						String  prop   = sc.next();
						Scanner propSc = new Scanner(prop);
						propSc.useDelimiter("=");
						
						String cle    = propSc.next();
						String valeur = propSc.next();
						propSc.close();
						
						if      (cle.equals("type"))     classeCourante.setType(valeur);
						else if (cle.equals("abstract")) classeCourante.setEstAbstract(valeur.equals("true"));
						else if (cle.equals("posX"))     classeCourante.setPosX(Integer.parseInt(valeur));
						else if (cle.equals("posY"))     classeCourante.setPosY(Integer.parseInt(valeur));
						else if (cle.equals("largeur"))  classeCourante.setLargeur(Integer.parseInt(valeur));
						else if (cle.equals("hauteur"))  classeCourante.setHauteur(Integer.parseInt(valeur));
					}
				}
				// 2. Héritage (EXTENDS)
				else if (premierMot.equals("Extends"))
				{
					String mere = sc.next();
					if (!mere.equals("aucune")) 
						classeCourante.setMere(mere);
				}
				// 3. Implémentation (IMPLEMENTS)
				else if (premierMot.equals("Implements"))
				{
					String inter = sc.next();
					if (!inter.equals("aucune"))
					{
						classeCourante.initLstInterface();
						classeCourante.addInterface(inter);
						while (sc.hasNext()) 
							classeCourante.addInterface(sc.next());
					}
				}
				// 4. ATTRIBUTS
				else if (premierMot.equals("Attribut") || premierMot.equals("Attribut:"))
				{
					String  visibilite = sc.next();
					boolean estStatic  = false;
					boolean estFinal   = false;
					
					String next = sc.next();
					if (next.equals("static"))
					{
						estStatic = true;
						next = sc.next();
					}
					if (next.equals("final"))
					{
						estFinal = true;
						next = sc.next();
					}
					
					String type   = next;
					String nom    = sc.next();
					String valeur = null;
					
					if (sc.hasNext())
					{
						String reste = sc.nextLine().trim();
						if (reste.startsWith("=")) 
							valeur = reste.substring(1);
					}
					
					Attribut attr;
					if (estFinal && valeur != null)
						attr = new Attribut(visibilite, type, nom, estStatic, valeur);
					else
						attr = new Attribut(visibilite, type, nom, estStatic);
					
					if (premierMot.equals("Attribut:"))
						classeCourante.getLstClassAttribut().add(attr);
					else
						classeCourante.getLstAttribut().add(attr);
				}
				// 5. MÉTHODES
				else if (premierMot.equals("Methode"))
				{
					String  visibilite  = sc.next();
					boolean estStatic   = false;
					boolean estAbstract = false;
					
					String next = sc.next();
					if (next.equals("static"))
					{
						estStatic = true;
						next = sc.next();
					}
					if (next.equals("abstract"))
					{
						estAbstract = true;
						next = sc.next();
					}
					
					String typeRetour = next;
					String nomMethode = sc.next();
					
					// Construction de la chaîne des paramètres
					StringBuilder paramsBuilder = new StringBuilder();
					while (sc.hasNext()) 
					{
						paramsBuilder.append(sc.next()).append(" ");
					}
					String paramsStr = paramsBuilder.toString().trim();
					
					// Enlever les parenthèses
					if(paramsStr.length() > 1)
						paramsStr = paramsStr.substring(1, paramsStr.length() - 1);
					
					List<String[]> lstParams = new ArrayList<>();
					if (!paramsStr.isEmpty())
					{
						Scanner paramSc = new Scanner(paramsStr);
						paramSc.useDelimiter(", ");
						
						while (paramSc.hasNext())
						{
							String  param     = paramSc.next();
							Scanner pSc       = new Scanner(param);
							String  typeParam = pSc.next();
							String  nomParam  = pSc.hasNext() ? pSc.next() : "param";
							
							lstParams.add(new String[]{typeParam, nomParam});
							pSc.close();
						}
						paramSc.close();
					}
					
					Methode meth = new Methode(visibilite, typeRetour, nomMethode, estStatic, estAbstract, lstParams);
					classeCourante.getLstMethode().add(meth);
				}
				// 6. RÔLES
				else if (premierMot.equals("Role"))
				{
					int    idRole  = Integer.parseInt(sc.next());
					String nomRole = sc.next();

					classeCourante.getMapRole().put(idRole, nomRole);
				}
				
				sc.close();
			}
			
			// Ajout de la dernière classe lue si elle existe
			if (classeCourante != null) 
				classes.add(classeCourante);
			
			// Initialiser les liens et multiplicités après avoir chargé toutes les classes
			for (CreeClass c : classes)
			{
				c.initLienMulti();
				c.creelien(classes);
				c.creerMultiplicite(classes);
			}
			
		}
		catch (IOException e)
		{
			System.out.println("Erreur lecture fichier : " + e.getMessage());
		}
		
		return classes;
	}
	
	/**
	 * Sauvegarde la liste des classes dans le fichier texte personnalisé (.uml).
	 * * @param classes La liste des classes à sauvegarder.
	 */
	public void sauvegarderUML(List<CreeClass> classes)
	{
		try (PrintWriter writer = new PrintWriter(new FileWriter(FILE_PATH_UML)))
		{
			for (CreeClass c : classes)
			{
				// --- En-tête Classe ---
				writer.println("Classe " + c.getNom() + 
							   " type=" + c.getType() + 
							   " abstract=" + c.estAbstract() + 
							   " posX=" + c.getPosX() + 
							   " posY=" + c.getPosY() + 
							   " largeur=" + c.getLargeur() + 
							   " hauteur=" + c.getHauteur());
				
				// --- Extends ---
				writer.println("\tExtends " + (c.getMere() == null ? "aucune" : c.getMere()));
				
				// --- Implements ---
				writer.print("\tImplements ");
				List<String> interfaces = c.getInterfaces();
				if (interfaces == null || interfaces.isEmpty())
				{
					writer.println("aucune");
				}
				else
				{
					for (String i : interfaces)
						writer.print(i + " ");
					writer.println();
				}
				
				// --- Attributs d'instance ---
				for (Attribut a : c.getLstAttribut())
				{
					ecrireAttribut(writer, a, "Attribut");
				}
				
				// --- Attributs de classe (Static) ---
				for (Attribut a : c.getLstClassAttribut())
				{
					ecrireAttribut(writer, a, "Attribut:");
				}
				
				// --- Méthodes ---
				for (Methode m : c.getLstMethode())
				{
					writer.print("\t\t\tMethode " + m.getVisibilite() + " ");
					if (m.isEstStatic()) writer.print("static ");
					if (m.estAbstract()) writer.print("abstract ");
					writer.print(m.getType() + " " + m.getNom() + " (");

					List<String[]> lstPram = m.getLstParametres();

					if(lstPram != null)
					{
						for (int i = 0; i < lstPram.size(); i++)
						{
							String[] tabS = lstPram.get(i);
							writer.print(tabS[0] + " " + tabS[1]);
							if (i < lstPram.size() - 1)
								writer.print(", ");
						}
					}
						
					writer.print(")\n");
				}
				
				// --- Rôles ---
				for (Map.Entry<Integer, String> entry : c.getMapRole().entrySet())
				{
					Integer id  = entry.getKey();
					String  nom = entry.getValue();
					writer.println("\tRole " + id + " " + nom);
				}

				// Ligne vide entre les classes
				writer.println();
			}
			
			writer.close();
		}
		catch (IOException e)
		{
			System.out.println("Erreur écriture fichier : " + e.getMessage());
		}
	}

	/**
	 * Méthode utilitaire pour écrire un attribut dans le fichier.
	 */
	private void ecrireAttribut(PrintWriter writer, Attribut a, String prefixe)
	{
		writer.print("\t\t" + prefixe + " " + a.getVisibilite() + " ");
		if (a.isEstStatic()) writer.print("static ");
		if (a.getPropriete().equals("frozen")) writer.print("final ");
		writer.print(a.getType() + " " + a.getNom());
		if (a.getPropriete().equals("frozen") && a.getValeur() != null)
			writer.print("=" + a.getValeur());
		writer.println();
	}
}