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
		String nomComplet = data.substring(data.lastIndexOf("/") + 1);
		if (nomComplet.indexOf("/") == -1) 
		{
			nomComplet = data.substring(data.lastIndexOf("\\") + 1);
		}
		this.nom = nomComplet.substring(0, nomComplet.length() - 5);
		this.lstAttribut = new ArrayList<Attribut>();
		this.lstMethode = new ArrayList<Methode>();
		try
		{
			Scanner sc = new Scanner(new FileInputStream(data), "UTF8");
			while (sc.hasNext())
			{
				String line = sc.nextLine().trim();

				if (line.contains("class ") && !line.contains("("))
				{
					//if(line.contains("extends") || line.contains("implements"))
					//{
					//	infosClass(line);
					//}
				}
				else
				{
					if (line.contains("private") || line.contains("protected")|| line.contains("public"))
					{
						if(line.contains(this.nom+"("))
						{
							this.ajouterConstructeur(line);
							System.err.println("Constructeur ajouté : " + line);
						}
						else
						{
							if (line.contains("(") && line.contains(")"))
							{
								this.ajouterMethode(line);
								System.err.println("Méthode ajoutée : " + line);
							}
							else if (line.contains(";"))
							{
								this.ajouterAttribut(line);
							}
						}
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

		int posOuv = constructeur.indexOf("(");
		int posFerm = constructeur.indexOf(")");

		if (posOuv == -1 || posFerm == -1 || posFerm <= posOuv) 
		{
			System.out.println("Problème de parenthèse dans le constructeur : " + constructeur);
			return;
		}

		// Extraire la partie avant la parenthèse pour avoir visibilité et nom
		String avantParenthese = constructeur.substring(0, posOuv).trim();
		String[] morceaux = avantParenthese.split(" ");
		
		String visibilite = morceaux[0];
		String nom = morceaux[morceaux.length - 1];

		// Extraire les paramètres
		String paramBrut = constructeur.substring(posOuv + 1, posFerm).trim();

		List<String[]> lstLstParamInfo = new ArrayList<>();

		if (!paramBrut.isEmpty()) {
			// Séparer les paramètres par la virgule
			String[] tabParams = paramBrut.split(",");

			for (String param : tabParams)
			{
				param = param.trim();
				
				// Trouver le dernier espace pour séparer type et nom
				int dernierEspace = param.lastIndexOf(" ");
				
				if (dernierEspace != -1) {
					String typeParam = param.substring(0, dernierEspace).trim();
					String nomParam = param.substring(dernierEspace + 1).trim();
					
					lstLstParamInfo.add(new String[]{typeParam, nomParam});
				}
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
			if (line[indexType].equals("final"))
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
		methode = methode.trim();
		
		int posOuv = methode.indexOf("(");
		int posFerm = methode.indexOf(")");

		if (posOuv == -1 || posFerm == -1 || posFerm <= posOuv) 
		{
			System.out.println("Problème de parenthèse dans la méthode : " + methode);
			return;
		}

		// Extraire la partie avant la parenthèse
		String avantParenthese = methode.substring(0, posOuv).trim();
		String[] morceaux = avantParenthese.split(" ");
		
		// Récupérer visibilité
		String visibilite = morceaux[0];
		
		// Vérifier si static
		boolean estStatic = false;
		int indexType = 1;
		
		if (morceaux.length > 1 && morceaux[1].equals("static")) {
			estStatic = true;
			indexType = 2;
		}
		
		// Type de retour
		String type = (morceaux.length > indexType) ? morceaux[indexType] : "";
		
		// Nom de la méthode (dernier élément avant la parenthèse)
		String nom = morceaux[morceaux.length - 1];

		// Extraire les paramètres
		String paramBrut = methode.substring(posOuv + 1, posFerm).trim();

		List<String[]> lstLstParamInfo = new ArrayList<>();

		if (!paramBrut.isEmpty()) {
			// Séparer les paramètres par la virgule
			String[] tabParams = paramBrut.split(",");

			for (String param : tabParams)
			{
				param = param.trim();
				
				// Trouver le dernier espace pour séparer type et nom
				int dernierEspace = param.lastIndexOf(" ");
				
				if (dernierEspace != -1) {
					String typeParam = param.substring(0, dernierEspace).trim();
					String nomParam = param.substring(dernierEspace + 1).trim();
					
					lstLstParamInfo.add(new String[]{typeParam, nomParam});
				}
			}
		}

		Methode meth = new Methode(visibilite, type, nom, estStatic, lstLstParamInfo);
		this.lstMethode.add(meth);
	}

	private static boolean verifdata(String data)
	{
		if (data.length() >= 5 && data.substring(data.length() - 5).equals(".java"))
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