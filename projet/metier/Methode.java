package projet.metier;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class Methode
{
	private String visibilite;
	private String type;
	private String nom;

	private boolean estStatic;
	private Map<String, String> lstParametre;

	public Methode(String visibilite, String type, String nom, boolean estStatic, List<String> lstParametre)
	{
		this.visibilite = visibilite;
		this.type = type;
		this.nom = nom;
		this.estStatic = estStatic;
		this.lstParametre = new HashMap<>();
		for (String parametre : lstParametre)
		{
			String[] parts = parametre.trim().split(" ");
			if (parts.length == 2)
			{
				this.lstParametre.put(parts[0], parts[1]);
			}
		}
	}

	public String getVisibilite()
	{
		return visibilite;
	}

	public String getType()
	{
		return type;
	}

	public String getNom()
	{
		return nom;
	}

	public boolean isEstStatic()
	{
		return estStatic;
	}

	public List<String> getLstParametre()
	{
		return lstParametre;
	}

}
