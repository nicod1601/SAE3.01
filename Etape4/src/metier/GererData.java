package src.metier;

import java.beans.*;
import java.io.*;
import java.util.List;


public class GererData
{
	private List<CreeClass> lstMetiers;
	
	public GererData()
	{
		this.lstMetiers = null;
	}

	public void sauvegarderXML(List<CreeClass> lstMetiers)
	{
		try {
			XMLEncoder encoder = new XMLEncoder(
					new BufferedOutputStream(
						new FileOutputStream("../data/savegarde_01.xml")));

			encoder.writeObject(lstMetiers);
			encoder.close();
		}
		catch (Exception e) {
			System.out.println("Erreur lors de la création du fichier XML");
			e.printStackTrace();
		}
	}

	public void lireXML()
	{
		try {
			XMLDecoder decoder = new XMLDecoder(
				new BufferedInputStream(
					new FileInputStream("../data/savegarde_01.xml")));
			
			List<CreeClass> newLstMetiers = (List<CreeClass>) decoder.readObject();
			this.lstMetiers = newLstMetiers;

			for ( CreeClass cccccccccccc : newLstMetiers)
				System.out.println(cccccccccccc.getNom());

			decoder.close();
			
		} catch (Exception e) {
			System.out.println("Erreur lors de la lecture du fichier");
			e.printStackTrace();
		}
	}


	public List<CreeClass> getLstCreeClassesXML() { return this.lstMetiers; }
}
