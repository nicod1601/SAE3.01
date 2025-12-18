package src.metier;

import java.beans.XMLEncoder;
import java.beans.XMLDecoder;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.HashMap;

public class GererData
{
	private List<CreeClass> lstMetiers;
	private static final String FICHIER_SER = "../data/sauvegarde01.ser";
	private static final String FICHIER_UML = "../data/sauvegarde01.uml";
	
	public GererData()
	{
		this.lstMetiers = null;
	}
	
	// === SAUVEGARDE BINAIRE (.ser) ===
	public void sauvegarderBinaire(List<CreeClass> lstMetiers)
	{
		try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FICHIER_SER)))
		{
			oos.writeObject(lstMetiers);
			System.out.println("✅ Sauvegarde binaire réussie : " + FICHIER_SER);
		}
		catch (IOException e)
		{
			System.out.println("❌ Erreur sauvegarde binaire : " + e.getMessage());
			e.printStackTrace();
		}
	}
	
	public List<CreeClass> chargerBinaire()
	{
		try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(FICHIER_SER)))
		{
			List<CreeClass> result = (List<CreeClass>) ois.readObject();
			this.lstMetiers = result;
			System.out.println("✅ Chargement binaire réussi : " + FICHIER_SER);
			return result;
		}
		catch (IOException | ClassNotFoundException e)
		{
			System.out.println("❌ Erreur chargement binaire : " + e.getMessage());
			e.printStackTrace();
			return null;
		}
	}
	
	// === EXPORT UML (format texte simple) ===
	public void exporterVersUML(List<CreeClass> lstMetiers)
	{
		try (PrintWriter writer = new PrintWriter(new FileWriter(FICHIER_UML)))
		{
			for (CreeClass c : lstMetiers)
			{
				// Écrire la classe (niveau 0) avec toutes ses propriétés
				writer.println("Classe:" + c.getNom());
				
				// Écrire les propriétés de la classe entre []
				StringBuilder propsClasse = new StringBuilder();
				propsClasse.append("[");
				propsClasse.append("type=").append(c.getType() != null ? c.getType() : "class").append(";");
				propsClasse.append("abstract=").append(c.estAbstract()).append(";");
				propsClasse.append("mere=").append(c.getMere() != null ? c.getMere() : "").append(";");
				propsClasse.append("posX=").append(c.getPosX()).append(";");
				propsClasse.append("posY=").append(c.getPosY()).append(";");
				propsClasse.append("largeur=").append(c.getLargeur()).append(";");
				propsClasse.append("hauteur=").append(c.getHauteur());
				
				// Interfaces
				List<String> interfaces = c.getInterfaces();
				if (interfaces != null && !interfaces.isEmpty())
				{
					propsClasse.append(";interfaces=");
					for (int i = 0; i < interfaces.size(); i++)
					{
						propsClasse.append(interfaces.get(i));
						if (i < interfaces.size() - 1) propsClasse.append(",");
					}
				}
				propsClasse.append("]");
				writer.println(propsClasse.toString());
				
				// Écrire les attributs normaux (niveau 1 - un tab)
				List<Attribut> attributs = c.getLstAttribut();
				if (attributs != null && !attributs.isEmpty())
				{
					for (Attribut a : attributs)
					{
						writer.println("\t" + formatAttribut(a));
					}
				}
				
				// Écrire les attributs de classe (niveau 1) avec un marqueur
				List<Attribut> classAttributs = c.getLstClassAttribut();
				if (classAttributs != null && !classAttributs.isEmpty())
				{
					writer.println("\t# Attributs de classe:");
					for (Attribut a : classAttributs)
					{
						writer.println("\t" + formatAttribut(a));
					}
				}
				
				// Écrire les méthodes (niveau 1)
				List<Methode> methodes = c.getLstMethode();
				if (methodes != null && !methodes.isEmpty())
				{
					for (Methode m : methodes)
					{
						writer.println("\t" + formatMethode(m));
					}
				}
				
				// Ligne vide entre les classes
				writer.println();
			}
			
			writer.close();
			System.out.println("✅ Export UML réussi : " + FICHIER_UML);
			
		}
		catch (Exception e)
		{
			System.out.println("❌ Erreur export UML : " + e.getMessage());
			e.printStackTrace();
		}
	}
	
	// Méthode pour formater un attribut
	private String formatAttribut(Attribut a)
	{
		StringBuilder sb = new StringBuilder();
		sb.append("Attribut:");
		sb.append(a.getVisibilite()).append(" ");
		
		if (a.isEstStatic()) sb.append("static ");
		if (a.isEstFinal()) sb.append("final ");
		
		sb.append(a.getType()).append(" ");
		sb.append(a.getNom());
		
		if (a.isEstFinal() && a.getValeur() != null && !a.getValeur().isEmpty())
		{
			sb.append("=").append(a.getValeur());
		}
		
		return sb.toString();
	}
	
	// Méthode pour formater une méthode
	private String formatMethode(Methode m)
	{
		StringBuilder sb = new StringBuilder();
		sb.append("Methode:");
		sb.append(m.getVisibilite()).append(" ");
		
		if (m.isEstStatic()) sb.append("static ");
		if (m.estAbstract()) sb.append("abstract ");
		
		sb.append(m.getType() != null ? m.getType() : "void").append(" ");
		sb.append(m.getNom()).append("(");
		
		// Ajouter les paramètres
		List<String[]> params = m.getLstParametres();
		if (params != null && !params.isEmpty())
		{
			for (int i = 0; i < params.size(); i++)
			{
				String[] param = params.get(i);
				if (param.length >= 2)
				{
					sb.append(param[0]).append(" ").append(param[1]);
					if (i < params.size() - 1)
					{
						sb.append(", ");
					}
				}
			}
		}
		
		sb.append(")");
		return sb.toString();
	}
	
	// === IMPORT UML (après édition) ===
	public List<CreeClass> importerDepuisUML()
	{
		try (BufferedReader reader = new BufferedReader(new FileReader(FICHIER_UML)))
		{
			List<CreeClass> result = new ArrayList<>();
			String line;
			CreeClass currentClass = null;
			boolean inClassAttributs = false;
			boolean skipEmptyLine = false;
			
			while ((line = reader.readLine()) != null)
			{
				// NE PAS TRIMMER ICI - garder les tabs pour détection
				String originalLine = line;
				line = line.trim();
				
				// Ignorer les lignes vides entre les classes
				if (line.isEmpty())
				{
					if (skipEmptyLine)
					{
						skipEmptyLine = false;
					}
					else if (currentClass != null)
					{
						result.add(currentClass);
						currentClass = null;
						inClassAttributs = false;
					}
					continue;
				}
				
				// Si c'est une nouvelle classe (pas de tab au début)
				if (!originalLine.startsWith("\t") && originalLine.startsWith("Classe:"))
				{
					if (currentClass != null)
					{
						result.add(currentClass);
					}
					
					String nomClasse = originalLine.substring(7).trim();
					currentClass = new CreeClass();
					currentClass.setNom(nomClasse);
					currentClass.setLstAttribut(new ArrayList<>());
					currentClass.setLstClassAttribut(new ArrayList<>());
					currentClass.setLstMethode(new ArrayList<>());
					currentClass.setInterfaces(new ArrayList<>());
					inClassAttributs = false;
					skipEmptyLine = true; // On s'attend à la ligne des propriétés
				}
				// Si c'est la ligne des propriétés de la classe (pas de tab)
				else if (!originalLine.startsWith("\t") && originalLine.startsWith("[") && originalLine.endsWith("]") && currentClass != null)
				{
					parserProprietesClasse(originalLine.trim(), currentClass);
				}
				// Si c'est un indicateur d'attributs de classe (avec tab)
				else if (originalLine.startsWith("\t") && originalLine.trim().equals("# Attributs de classe:") && currentClass != null)
				{
					inClassAttributs = true;
				}
				// Si c'est un attribut (avec tab)
				else if (originalLine.startsWith("\t") && originalLine.trim().startsWith("Attribut:") && currentClass != null)
				{
					String attributStr = originalLine.trim().substring(9).trim(); // Enlever "Attribut:"
					Attribut attr = parserAttribut(attributStr);
					if (attr != null)
					{
						if (inClassAttributs)
						{
							currentClass.getLstClassAttribut().add(attr);
						}
						else
						{
							currentClass.getLstAttribut().add(attr);
						}
					}
				}
				// Si c'est une méthode (avec tab)
				else if (originalLine.startsWith("\t") && originalLine.trim().startsWith("Methode:") && currentClass != null)
				{
					String methodeStr = originalLine.trim().substring(8).trim(); // Enlever "Methode:"
					Methode meth = parserMethode(methodeStr);
					if (meth != null)
					{
						currentClass.getLstMethode().add(meth);
					}
				}
			}
			
			// Ajouter la dernière classe si elle existe
			if (currentClass != null)
			{
				result.add(currentClass);
			}
			
			this.lstMetiers = result;
			System.out.println("✅ Import UML réussi. Classes chargées: " + result.size());
			
			// Recréer les liens et multiplicités
			if (!result.isEmpty())
			{
				for (CreeClass c : result)
				{
					// Initialiser les objets Lien et Multiplicite
					c.initLienMulti();
				}
				
				// Recréer les liens entre les classes
				for (CreeClass c : result)
				{
					c.creelien(result);
					c.creerMultiplicite(result);
				}
			}
			
			return result;
			
		}
		catch (Exception e)
		{
			System.out.println("❌ Erreur import UML : " + e.getMessage());
			e.printStackTrace();
			return null;
		}
	}
	
	// Parser les propriétés de la classe
	private void parserProprietesClasse(String ligne, CreeClass currentClass)
	{
		// Enlever les crochets
		ligne = ligne.substring(1, ligne.length() - 1);
		
		String[] proprietes = ligne.split(";");
		for (String prop : proprietes)
		{
			String[] keyValue = prop.split("=", 2);
			if (keyValue.length == 2)
			{
				String key = keyValue[0].trim();
				String value = keyValue[1].trim();
				
				switch (key)
				{
					case "type":
						currentClass.setType(value);
						break;
					case "abstract":
						currentClass.setEstAbstract(Boolean.parseBoolean(value));
						break;
					case "mere":
						if (!value.isEmpty())
							currentClass.setMere(value);
						break;
					case "posX":
						currentClass.setPosX(Integer.parseInt(value));
						break;
					case "posY":
						currentClass.setPosY(Integer.parseInt(value));
						break;
					case "largeur":
						currentClass.setLargeur(Integer.parseInt(value));
						break;
					case "hauteur":
						currentClass.setHauteur(Integer.parseInt(value));
						break;
					case "interfaces":
						if (!value.isEmpty())
						{
							String[] interfaces = value.split(",");
							for (String inter : interfaces)
							{
								if (!inter.trim().isEmpty())
									currentClass.getInterfaces().add(inter.trim());
							}
						}
						break;
				}
			}
		}
	}
	
	// Parser une ligne d'attribut - retourne un Attribut - VERSION SIMPLIFIÉE
	private Attribut parserAttribut(String ligne)
	{
		try
		{
			// Séparer en mots
			String[] mots = ligne.split("\\s+");
			if (mots.length < 3)
			{
				return null;
			}
			
			int index = 0;
			String visibilite = mots[index++];
			
			boolean estStatic = false;
			boolean estFinal = false;
			
			// Vérifier static/final
			if (index < mots.length && mots[index].equals("static"))
			{
				estStatic = true;
				index++;
			}
			if (index < mots.length && mots[index].equals("final"))
			{
				estFinal = true;
				index++;
			}
			
			// Type
			if (index >= mots.length)
			{
				return null;
			}
			String type = mots[index++];
			
			// Nom (peut contenir des espaces si valeur avec guillemets)
			StringBuilder nomBuilder = new StringBuilder();
			for (int i = index; i < mots.length; i++)
			{
				nomBuilder.append(mots[i]);
				if (i < mots.length - 1) nomBuilder.append(" ");
			}
			
			String nomComplet = nomBuilder.toString().trim();
			String nom = nomComplet;
			String valeur = null;
			
			// Chercher une valeur
			if (nomComplet.contains("="))
			{
				int egalIndex = nomComplet.indexOf("=");
				nom = nomComplet.substring(0, egalIndex).trim();
				valeur = nomComplet.substring(egalIndex + 1).trim();
				
				// Enlever les guillemets si présents
				if (valeur.startsWith("\"") && valeur.endsWith("\""))
				{
					valeur = valeur.substring(1, valeur.length() - 1);
				}
			}
			
			// Créer l'attribut
			if (estFinal && valeur != null)
			{
				return new Attribut(visibilite, type, nom, estStatic, valeur);
			}
			else
			{
				Attribut attr = new Attribut(visibilite, type, nom, estStatic);
				return attr;
			}
		}
		catch (Exception e)
		{
			System.out.println("❌ Erreur parsing attribut: " + ligne);
			e.printStackTrace();
			return null;
		}
	}
	
	// Parser une ligne de méthode - VERSION SIMPLIFIÉE
	private Methode parserMethode(String ligne)
	{
		try
		{
			// Chercher les parenthèses
			int parenOuvrante = ligne.indexOf("(");
			int parenFermante = ligne.indexOf(")");
			
			if (parenOuvrante == -1 || parenFermante == -1)
			{
				return null;
			}
			
			// Partie avant parenthèses
			String avantParen = ligne.substring(0, parenOuvrante).trim();
			// Partie paramètres
			String paramsStr = ligne.substring(parenOuvrante + 1, parenFermante).trim();
			
			// Parser avant parenthèses
			String[] motsAvant = avantParen.split("\\s+");
			if (motsAvant.length < 2)
			{
				return null;
			}
			
			int index = 0;
			String visibilite = motsAvant[index++];
			
			boolean estStatic = false;
			boolean estAbstract = false;
			
			// Modificateurs
			while (index < motsAvant.length)
			{
				if (motsAvant[index].equals("static"))
				{
					estStatic = true;
					index++;
				}
				else if (motsAvant[index].equals("abstract"))
				{
					estAbstract = true;
					index++;
				}
				else
				{
					break;
				}
			}
			
			// Type retour
			if (index >= motsAvant.length)
			{
				return null;
			}
			String typeRetour = motsAvant[index++];
			
			// Nom méthode
			if (index >= motsAvant.length)
			{
				return null;
			}
			String nomMethode = motsAvant[index++];
			
			// Parser paramètres
			List<String[]> lstParametres = new ArrayList<>();
			if (!paramsStr.isEmpty())
			{
				String[] params = paramsStr.split(",");
				for (String param : params)
				{
					param = param.trim();
					if (!param.isEmpty())
					{
						String[] parts = param.split("\\s+");
						if (parts.length >= 2)
						{
							lstParametres.add(new String[]{parts[0], parts[1]});
						}
						else if (parts.length == 1)
						{
							lstParametres.add(new String[]{parts[0], "param"});
						}
					}
				}
			}
			
			return new Methode(visibilite, typeRetour, nomMethode, estStatic, estAbstract, lstParametres);
		}
		catch (Exception e)
		{
			System.out.println("❌ Erreur parsing méthode: " + ligne);
			e.printStackTrace();
			return null;
		}
	}
	
	// === MÉTHODE COMBINÉE : Sauvegarde automatique des deux ===
	public void sauvegarderProjet(List<CreeClass> lstMetiers)
	{
		// 1. Sauvegarde binaire (principale) - garde les objets complets
		sauvegarderBinaire(lstMetiers);
		
		// 2. Export UML (pour édition) - format lisible
		exporterVersUML(lstMetiers);
		
		System.out.println("📁 Projet sauvegardé :");
		System.out.println("   • " + FICHIER_SER + " (binaire - objets complets)");
		System.out.println("   • " + FICHIER_UML + " (UML - format éditable)");
	}
	
	// === MÉTHODE COMBINÉE : Chargement intelligent ===
	public List<CreeClass> chargerProjet()
	{
		File fileSer = new File(FICHIER_SER);
		File fileUml = new File(FICHIER_UML);
		
		// Priorité 1 : Charger depuis .ser (si existe et n'est pas corrompu)
		if (fileSer.exists() && fileSer.length() > 0)
		{
			try
			{
				System.out.println("🔍 Chargement depuis .ser (binaire)");
				return chargerBinaire();
			}
			catch (Exception e)
			{
				System.out.println("⚠️ Fichier .ser corrompu, tentative avec UML");
			}
		}
		
		// Priorité 2 : Charger depuis .uml (si .ser manquant ou corrompu)
		if (fileUml.exists() && fileUml.length() > 0)
		{
			System.out.println("🔍 Chargement depuis .uml (édition)");
			List<CreeClass> result = importerDepuisUML();
			if (result != null)
			{
				System.out.println("✅ " + result.size() + " classes chargées depuis .uml");
			}
			return result;
		}
		
		System.out.println("❌ Aucun fichier de sauvegarde valide trouvé");
		return null;
	}
	
	public List<CreeClass> getLstMetiers()
	{
		return lstMetiers;
	}
}