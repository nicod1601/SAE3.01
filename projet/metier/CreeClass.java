package projet.metier;


import java.io.FileInputStream;
import java.util.*;

public class CreeClass
{
	private String nom;
	private List<Attribut> lstAttribut;
	private List<Methode> lstMethode;

	public static CreeClass factoryCreeClass(String data)
	{
		if (CreeClass.verifdata(data))
			return new CreeClass(data);
		else
			return null;
	}

	private CreeClass(String data )
	{
		this.nom = data.substring(data.lastIndexOf("/")+1, data.length()-5);
		this.lstAttribut = new ArrayList<Attribut>();
		this.lstMethode = new ArrayList<Methode>();
		try
		{
			Scanner sc = new Scanner(new FileInputStream(data), "UTF8");
			while (sc.hasNext())
			{
				String line = sc.nextLine();
				if (line.contains("private") || line.contains("protected")|| line.contains("public"))
				{
					if (line.contains(";"))
					{
						this.ajouterAttribut(line);
					}
					else if (line.contains(")"))
					{
						this.ajouterMethode(line);
					}
				}
			}

		}catch (Exception e)
		{
			System.out.println("Erreur lors de la lecture du fichier : " + e.getMessage() );
		}
	}

	private void ajouterAttribut(String attribut)
	{
		String[] line = attribut.split(" ");
		if (line.length >= 3)
		{
			String visibilite = line[0];
			boolean estStatic = false;
			boolean estFinal = false;
			int indexType = 1;

			if (line[1].equals("static"))
			{
				estStatic = true;
				indexType++;
			}
			if (line[1].equals("final"))
			{
				estFinal = true;
				indexType++;
			}

			String type = line[indexType];
			String nom = line[line.length -1].replace(";", "");

			Attribut attr = new Attribut(visibilite, type, nom, estStatic, estFinal);
			this.lstAttribut.add(attr);
		}
	}

	private void ajouterMethode(String methode)
	{
		String[] line = methode.split(" ");
		String visibilite = line[0];
		boolean estStatic = false;
		int indexType = 1;

		if(line.length >= 3)
		{
			if (line[1].equals("static"))
			{
				estStatic = true;
				indexType++;
			}

			String type = line[indexType];
			String reste = "";

			for (int i = indexType + 1; i < line.length; i++)
			{
				reste += line[i] + " ";
			}

			reste = reste.trim();
			String nom = reste.substring(0, reste.indexOf("("));
			String parametres = reste.substring(reste.indexOf("(") + 1, reste.indexOf(")")); // avoir le + 1
			List<String> lstParametre = new ArrayList<String>();
			
			if (!parametres.trim().isEmpty())
			{
				String[] params = parametres.split(",");
				for (String param : params)
				{
					lstParametre.add(param.trim());
				}
			}

			Methode meth = new Methode(visibilite, type, nom, estStatic, lstParametre);
			this.lstMethode.add(meth);
		}

		

	}

	private static boolean verifdata(String data)
	{
		// on vérifie que le ficher est en .java
		if (data.substring(data.length()-5).equals( ".java"))
		{
			return true;
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
}