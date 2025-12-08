package projet.metier;


import java.io.FileInputStream;
import java.lang.reflect.Method;
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
					if(line.contains(nom))
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

		}catch (Exception e)
		{
			System.out.println("Erreur lors de la lecture du fichier : " + e.getMessage() );
			e.printStackTrace();
		}
	}

	private void ajouterConstructeur(String constructeur)
	{
		constructeur = constructeur.trim();

		String[] mots = constructeur.split(" ");
		String visibilite = mots[0];

		int posOuv = constructeur.indexOf("(");
		int posFerm = constructeur.indexOf(")");

		if (posOuv == -1 || posFerm == -1) 
		{
			System.out.println("Constructeur mal formaté : " + constructeur);
			return;
		}


		String avantParenthese = constructeur.substring(0, posOuv).trim();
		String[] morceaux = avantParenthese.split(" ");
		String nom = morceaux[morceaux.length - 1];

		// paramBrut -> "typeParam1 nomPram1, typeParam2 nomPram2, ..."
		String paramBrut = constructeur.substring(posOuv + 1, posFerm).trim();

		// lstLstParamInfo -> [param1 -> ["type", nom]; param2 -> [type, nom]; ...]"
		List<String[]> lstLstParamInfo  = new ArrayList<>();

		if (!paramBrut.isEmpty()) {
			//tabParams -> ["param1", "param2", ...] param -> "typeParam1 nomPram1"
			String[] tabParams = paramBrut.split(",");

			//param -> "typeParam nomPram"
			for (String param : tabParams) {

				// infoParam -> [type, nom] type -> "typeParam1" et nom -> "nomPram1"
				String[] tabInfoParam = param.trim().split(" ");

				lstLstParamInfo.add(tabInfoParam);
			}
		}


		Methode c = new Methode(visibilite, null, nom, false, lstLstParamInfo);
		this.lstMethode.add(c);
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

			int posOuv = reste.indexOf("(");
			int posFerm = reste.indexOf(")");

			String nom = reste.substring(0, posOuv);

			// paramBrut -> "typeParam1 nomPram1, typeParam2 nomPram2, ..."
			String paramBrut = reste.substring(posOuv + 1, posFerm).trim();

			// lstLstParamInfo -> [param1 -> ["type", nom]; param2 -> [type, nom]; ...]"
			List<String[]> lstLstParamInfo  = new ArrayList<>();

			if (!paramBrut.isEmpty()) {
				//tabParams -> ["param1", "param2", ...] param -> "typeParam1 nomPram1"
				String[] tabParams = paramBrut.split(",");

				//param -> "typeParam nomPram"
				for (String param : tabParams) {

					// infoParam -> [type, nom] type -> "typeParam1" et nom -> "nomPram1"
					String[] tabInfoParam = param.trim().split(" ");

					lstLstParamInfo.add(tabInfoParam);
				}
			}

			Methode meth = new Methode(visibilite, type, nom, estStatic, lstLstParamInfo);
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

	public void supprimerAttribut(Attribut att)
	{
		this.lstAttribut.remove(att);
	}
}