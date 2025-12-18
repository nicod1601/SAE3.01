package src.metier;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class GererData
{
	// === SAUVEGARDER .ser ===
	public void sauvegarderSER(List<CreeClass> classes)
	{
		try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("../data/sauvegarde01.ser")))
		{
			oos.writeObject(classes);
			System.out.println("✅ Sauvegarde .ser réussie");
		}
		catch (IOException e)
		{
			System.out.println("❌ Erreur sauvegarde .ser : " + e.getMessage());
		}
	}
	
	// === CHARGER .ser ===
	public List<CreeClass> chargerSER()
	{
		try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream("../data/sauvegarde01.ser")))
		{
			List<CreeClass> classes = (List<CreeClass>) ois.readObject();
			System.out.println("✅ Chargement .ser réussi : " + classes.size() + " classes");
			return classes;
		}
		catch (IOException | ClassNotFoundException e)
		{
			System.out.println("❌ Erreur chargement .ser : " + e.getMessage());
			return null;
		}
	}
	
	// === LIRE FICHIER .uml ===
	public List<CreeClass> chargerUML()
	{
		List<CreeClass> classes = new ArrayList<>();
		
		try (BufferedReader reader = new BufferedReader(new FileReader("../data/sauvegarde01.uml")))
		{
			String ligne;
			CreeClass classeCourante = null;
			
			while ((ligne = reader.readLine()) != null)
			{
				if (ligne.trim().isEmpty())
				{
					if (classeCourante != null)
					{
						classes.add(classeCourante);
						classeCourante = null;
					}
					continue;
				}
				
				Scanner sc = new Scanner(ligne);
				String premierMot = sc.next();
				
				if (premierMot.equals("Classe"))
				{
					classeCourante = new CreeClass();
					classeCourante.setNom(sc.next());
					
					while (sc.hasNext())
					{
						String prop = sc.next();
						Scanner propSc = new Scanner(prop);
						propSc.useDelimiter("=");
						
						String cle = propSc.next();
						String valeur = propSc.next();
						propSc.close();
						
						if (cle.equals("type")) classeCourante.setType(valeur);
						else if (cle.equals("abstract")) classeCourante.setEstAbstract(valeur.equals("true"));
						else if (cle.equals("posX")) classeCourante.setPosX(Integer.parseInt(valeur));
						else if (cle.equals("posY")) classeCourante.setPosY(Integer.parseInt(valeur));
						else if (cle.equals("largeur")) classeCourante.setLargeur(Integer.parseInt(valeur));
						else if (cle.equals("hauteur")) classeCourante.setHauteur(Integer.parseInt(valeur));
					}
				}
				else if (premierMot.equals("Extends"))
				{
					String mere = sc.next();
					if (!mere.equals("aucune")) classeCourante.setMere(mere);
				}
				else if (premierMot.equals("Implements"))
				{
					String inter = sc.next();
					if (!inter.equals("aucune"))
					{
						classeCourante.initLstInterface();
						classeCourante.addInterface(inter);
						while (sc.hasNext()) classeCourante.addInterface(sc.next());
					}
				}
				else if (premierMot.equals("Attribut") || premierMot.equals("Attribut:"))
				{
					String visibilite = sc.next();
					boolean estStatic = false;
					boolean estFinal = false;
					
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
					
					String type = next;
					String nom = sc.next();
					String valeur = null;
					
					if (sc.hasNext())
					{
						String reste = sc.nextLine().trim();
						if (reste.startsWith("=")) valeur = reste.substring(1);
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
				else if (premierMot.equals("Methode"))
				{
					String visibilite = sc.next();
					boolean estStatic = false;
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
					
					// Lire les paramètres
					String paramsStr = "";
					while (sc.hasNext()) paramsStr += sc.next() + " ";
					paramsStr = paramsStr.trim();
					
					// Enlever les parenthèses
					if(paramsStr.length() > 0)
						paramsStr = paramsStr.substring(1, paramsStr.length() - 1);
					
					List<String[]> lstParams = new ArrayList<>();
					if (!paramsStr.isEmpty())
					{
						Scanner paramSc = new Scanner(paramsStr);
						paramSc.useDelimiter(", ");
						
						while (paramSc.hasNext())
						{
							String param = paramSc.next();
							Scanner pSc = new Scanner(param);
							String typeParam = pSc.next();
							String nomParam = pSc.hasNext() ? pSc.next() : "param";
							lstParams.add(new String[]{typeParam, nomParam});
							pSc.close();
						}
						paramSc.close();
					}
					
					Methode meth = new Methode(visibilite, typeRetour, nomMethode, estStatic, estAbstract, lstParams);
					classeCourante.getLstMethode().add(meth);
				}
				
				sc.close();
			}
			
			if (classeCourante != null) classes.add(classeCourante);
			
			// Initialiser liens et multiplicités
			for (CreeClass c : classes)
			{
				c.initLienMulti();
				c.creelien(classes);
				c.creerMultiplicite(classes);
			}
			
		}
		catch (IOException e)
		{
			System.out.println("❌ Erreur lecture fichier : " + e.getMessage());
		}
		
		return classes;
	}
	
	// === SAUVEGARDER DANS FICHIER .uml ===
	public void sauvegarderUML(List<CreeClass> classes)
	{
		try (PrintWriter writer = new PrintWriter(new FileWriter("../data/sauvegarde01.uml")))
		{
			for (CreeClass c : classes)
			{
				// Ligne Classe
				writer.println("Classe " + c.getNom() + 
							   " type=" + c.getType() + 
							   " abstract=" + c.estAbstract() + 
							   " posX=" + c.getPosX() + 
							   " posY=" + c.getPosY() + 
							   " largeur=" + c.getLargeur() + 
							   " hauteur=" + c.getHauteur());
				
				// Extends (sans "mere=")
				writer.println("\tExtends " + (c.getMere() == null ? "aucune" : c.getMere()));
				
				// Implements
				writer.print("\tImplements ");
				List<String> interfaces = c.getInterfaces();
				if (interfaces == null || interfaces.isEmpty())
					writer.println("aucune");
				else
				{
					for (String i : interfaces)
						writer.print(i + " ");
					writer.println();
				}
				
				// Attributs normaux
				for (Attribut a : c.getLstAttribut())
				{
					writer.print("\t\tAttribut " + a.getVisibilite() + " ");
					if (a.isEstStatic()) writer.print("static ");
					if (a.getPropriete().equals("frozen")) writer.print("final ");
					writer.print(a.getType() + " " + a.getNom());
					if (a.getPropriete().equals("frozen") && a.getValeur() != null)
						writer.print("=" + a.getValeur());
					writer.println();
				}
				
				// Attributs de classe
				for (Attribut a : c.getLstClassAttribut())
				{
					writer.print("\t\tAttribut: " + a.getVisibilite() + " ");
					if (a.isEstStatic()) writer.print("static ");
					if (a.getPropriete().equals("frozen")) writer.print("final ");
					writer.print(a.getType() + " " + a.getNom());
					if (a.getPropriete().equals("frozen") && a.getValeur() != null)
						writer.print("=" + a.getValeur());
					writer.println();
				}
				
				// Méthodes
				for (Methode m : c.getLstMethode())
				{
					writer.print("\t\tMethode " + m.getVisibilite() + " ");
					if (m.isEstStatic()) writer.print("static ");
					if (m.estAbstract()) writer.print("abstract ");
					writer.print(m.getType() + " " + m.getNom() + " ()\n");
				}
				
				// Ligne vide entre les classes
				writer.println();
			}
			
			writer.close();
		}
		catch (IOException e)
		{
			System.out.println("❌ Erreur écriture fichier : " + e.getMessage());
		}
	}
}