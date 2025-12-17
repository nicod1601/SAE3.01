package src.metier;

import java.beans.XMLEncoder;
import java.beans.XMLDecoder;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class GererData
{
	private List<CreeClass> lstMetiers;
	
	// === SAUVEGARDE BINAIRE (.ser) ===
	public void sauvegarderBinaire(List<CreeClass> lstMetiers)
	{
		String fichierSer = "../data/sauvegardeSER.ser";
		try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(fichierSer)))
		{
			oos.writeObject(lstMetiers);
			System.out.println("✅ Sauvegarde binaire réussie : " + fichierSer);
		}
		catch (IOException e)
		{
			System.out.println("❌ Erreur sauvegarde binaire : " + e.getMessage());
		}
	}
	
	public List<CreeClass> chargerBinaire(String fichierSer)
	{
		try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(fichierSer)))
		{
			List<CreeClass> result = (List<CreeClass>) ois.readObject();
			System.out.println("✅ Chargement binaire réussi : " + fichierSer);
			return result;
		}
		catch (IOException | ClassNotFoundException e)
		{
			System.out.println("❌ Erreur chargement binaire : " + e.getMessage());
			return null;
		}
	}
	
	// === EXPORT XML (pour édition) ===
	public void exporterVersXML(List<CreeClass> lstMetiers)
	{
		String fichierXml = "../data/SauvegardeXML.xml";
		try {
			XMLEncoder encoder = new XMLEncoder(
					new BufferedOutputStream(
						new FileOutputStream(fichierXml)));
			
			// Écrire chaque classe séparément (plus simple pour XML)
			encoder.writeObject(lstMetiers.size());
			for (CreeClass c : lstMetiers)
			{
				encoder.writeObject(c.getNom());
				encoder.writeObject(c.getType());
				encoder.writeObject(c.getLstAttribut());
				encoder.writeObject(c.getLstMethode());
				encoder.writeObject(c.getMere());
				encoder.writeObject(c.getInterfaces());
				encoder.writeObject(c.getPosX());
				encoder.writeObject(c.getPosY());
				encoder.writeObject(c.getLargeur());
				encoder.writeObject(c.getHauteur());
			}
			
			encoder.close();
			System.out.println("✅ Export XML réussi : " + fichierXml);
		} catch (Exception e) {
			System.out.println("❌ Erreur export XML : " + e.getMessage());
		}
	}
	
	// === IMPORT XML (après édition) ===
	public List<CreeClass> importerDepuisXML(String fichierXml)
	{
		try {
			XMLDecoder decoder = new XMLDecoder(
				new BufferedInputStream(
					new FileInputStream(fichierXml)));
			
			int nbClasses = (Integer) decoder.readObject();
			List<CreeClass> result = new ArrayList<>();
			
			for (int i = 0; i < nbClasses; i++) {
				CreeClass c = new CreeClass();
				c.setNom((String) decoder.readObject());
				c.setType((String) decoder.readObject());
				c.setLstAttribut((List<Attribut>) decoder.readObject());
				c.setLstMethode((List<Methode>) decoder.readObject());
				c.setMere((String) decoder.readObject());
				c.setInterfaces((List<String>) decoder.readObject());
				c.setPosX((Integer) decoder.readObject());
				c.setPosY((Integer) decoder.readObject());
				c.setLargeur((Integer) decoder.readObject());
				c.setHauteur((Integer) decoder.readObject());
				
				result.add(c);
			}
			
			decoder.close();
			System.out.println("✅ Import XML réussi : " + fichierXml);
			return result;
			
		} catch (Exception e) {
			System.out.println("❌ Erreur import XML : " + e.getMessage());
			return null;
		}
	}
	
	// === MÉTHODE COMBINÉE : Sauvegarde automatique des deux ===
	public void sauvegarderProjet(List<CreeClass> lstMetiers, String nomFichier)
	{
		// 1. Sauvegarde binaire (principale)
		String fichierSer = nomFichier + ".ser";
		sauvegarderBinaire(lstMetiers);
		
		// 2. Export XML (pour édition)
		String fichierXml = nomFichier + ".xml";
		exporterVersXML(lstMetiers);
		
		System.out.println("📁 Projet sauvegardé :");
		System.out.println("   • " + fichierSer + " (binaire - principal)");
		System.out.println("   • " + fichierXml + " (XML - édition)");
	}
	
	// === MÉTHODE COMBINÉE : Chargement intelligent ===
	public List<CreeClass> chargerProjet(String nomFichier)
	{
		String fichierSer = nomFichier + ".ser";
		String fichierXml = nomFichier + ".xml";
		
		File fileSer = new File(fichierSer);
		File fileXml = new File(fichierXml);
		
		// Priorité 1 : Charger depuis .ser (si existe)
		if (fileSer.exists())
		{
			System.out.println("🔍 Chargement depuis .ser (binaire)");
			return chargerBinaire(fichierSer);
		}
		// Priorité 2 : Charger depuis .xml (si .ser manquant)
		else if (fileXml.exists())
		{
			System.out.println("🔍 Chargement depuis .xml (édition)");
			return importerDepuisXML(fichierXml);
		}
		else
		{
			System.out.println("❌ Aucun fichier de sauvegarde trouvé : " + nomFichier);
			return null;
		}
	}

	public List<CreeClass> getLstMetiers()
	{
		return lstMetiers;
	}
}